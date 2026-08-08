package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.NhatKyChamSoc;
import url.DBConnect;

/**
 * UC-4.4 - Ghi nhật ký hoạt động chăm sóc.
 *
 * Đây là trung tâm tính toán của module canh tác:
 *   - Chi phí vật tư : xuất kho FIFO/FEFO theo LoHangVatTu, đơn giá lấy từ ChiTietPhieuNhapKho.
 *   - Chi phí dụng cụ: theo DungCu.gia_binh_quan, trừ DungCu.ton_kho_hien_tai.
 *   - Chi phí thiết bị: khấu hao theo ngày sử dụng, ghi PhieuDieuDongThietBi.
 *   - Diện tích tiêu hao: vật tư/dụng cụ dùng hết -> GIẢI PHÓNG diện tích ở kho
 *     (KhuVuc.dien_tich_chiem_dung của khu chứa lô hàng / dụng cụ).
 *
 * Toàn bộ chạy trong 1 transaction. Xóa nhật ký sẽ HOÀN TÁC đúng những gì đã trừ.
 *
 * Ghi chú quan trọng về schema (theo file DB):
 *   - NhatKyChamSoc.id KHÔNG phải IDENTITY  -> phải tự sinh id.
 *   - VatTu.dien_tich_chiem_dung và DungCu.dien_tich_chiem_dung là diện tích CHO MỘT ĐƠN VỊ
 *     (hằng số khai báo trong danh mục), TUYỆT ĐỐI không được trừ vào 2 cột này.
 *     Diện tích thực tế đang bị chiếm nằm ở KhuVuc.dien_tich_chiem_dung.
 *   - PhieuDieuDongThietBi.nhat_ky_bat_dau_id / nhat_ky_ket_thuc_id đều NOT NULL.
 */
public class NhatKyChamSocDAO {

    // Trạng thái thiết bị không cho phép điều động ra lô
    private static final String[] TRANG_THAI_THIET_BI_KHONG_DUNG_DUOC =
            {"Bảo trì", "Hỏng", "Đang bảo trì", "Thanh lý", "Ngừng sử dụng"};

    // ===================================================================
    // ĐỌC DỮ LIỆU
    // ===================================================================

    public List<NhatKyChamSoc> getAllWithLo() {
        List<NhatKyChamSoc> list = new ArrayList<>();
        String sql = "SELECT n.*, d.ten_don_vi AS ten_lo "
                   + "FROM NhatKyChamSoc n JOIN DonViQuanLy d ON n.lo_dat_id = d.id "
                   + "ORDER BY n.ngay_thuc_hien DESC, n.id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(doc(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<NhatKyChamSoc> getByLoDat(int loDatId) {
        List<NhatKyChamSoc> list = new ArrayList<>();
        String sql = "SELECT n.*, d.ten_don_vi AS ten_lo "
                   + "FROM NhatKyChamSoc n JOIN DonViQuanLy d ON n.lo_dat_id = d.id "
                   + "WHERE n.lo_dat_id = ? ORDER BY n.ngay_thuc_hien DESC, n.id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loDatId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(doc(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private NhatKyChamSoc doc(ResultSet rs) throws SQLException {
        NhatKyChamSoc n = new NhatKyChamSoc();
        n.setId(rs.getInt("id"));
        n.setLo_dat_id(rs.getInt("lo_dat_id"));
        n.setLoai_cong_viec(rs.getNString("loai_cong_viec"));
        n.setNgay_thuc_hien(rs.getDate("ngay_thuc_hien"));
        int nv = rs.getInt("nhac_viec_id");
        n.setNhac_viec_id(rs.wasNull() ? null : nv);
        n.setCo_su_dung_vat_tu(rs.getBoolean("co_su_dung_vat_tu"));
        n.setCo_su_dung_dung_cu(rs.getBoolean("co_su_dung_dung_cu"));
        n.setCo_su_dung_thiet_bi(rs.getBoolean("co_su_dung_thiet_bi"));
        n.setTong_chi_phi_vat_tu(rs.getDouble("tong_chi_phi_vat_tu"));
        n.setTong_chi_phi_dung_cu(rs.getDouble("tong_chi_phi_dung_cu"));
        n.setTong_chi_phi_thiet_bi(rs.getDouble("tong_chi_phi_thiet_bi"));
        n.setTong_chi_phi(rs.getDouble("tong_chi_phi"));
        n.setNguoi_ghi_nhan_id(rs.getInt("nguoi_ghi_nhan_id"));
        n.setMo_ta(rs.getNString("mo_ta"));
        n.setTen_lo_dat(rs.getNString("ten_lo"));
        return n;
    }

    // ===================================================================
    // GHI NHẬT KÝ (TÍNH TIỀN + DIỆN TÍCH + THIẾT BỊ)
    // ===================================================================

    /**
     * Ghi nhật ký kèm xuất kho, tính chi phí và cập nhật diện tích kho.
     * @return null nếu thành công, ngược lại là thông báo lỗi hiển thị cho người dùng.
     */
    public String insertFull(NhatKyChamSoc n) {
        // ---- Kiểm tra dữ liệu đầu vào trước khi mở transaction ----
        if (n == null) return "Dữ liệu nhật ký rỗng.";
        if (n.getLo_dat_id() <= 0) return "Vui lòng chọn lô đất.";
        if (n.getLoai_cong_viec() == null || n.getLoai_cong_viec().isBlank())
            return "Vui lòng nhập loại công việc.";
        if (n.getNgay_thuc_hien() == null) return "Vui lòng chọn ngày thực hiện.";

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return "Không kết nối được cơ sở dữ liệu.";
            conn.setAutoCommit(false);

            // Lô đất phải tồn tại và đúng loại "Lô đất"
            String loaiDonVi = layChuoi(conn, "SELECT loai_don_vi FROM DonViQuanLy WHERE id=?", n.getLo_dat_id());
            if (loaiDonVi == null) throw new SQLException("Lô đất không tồn tại.");
            if (!"Lô đất".equalsIgnoreCase(loaiDonVi))
                throw new SQLException("Đơn vị đã chọn không phải là lô đất (đang là: " + loaiDonVi + ").");

            // Người ghi nhận là FK tới TaiKhoan -> phải hợp lệ
            int nguoiGhiNhan = chuanHoaTaiKhoan(conn, n.getNguoi_ghi_nhan_id());
            if (nguoiGhiNhan <= 0) throw new SQLException("Không xác định được tài khoản người ghi nhận.");

            int newId = layIdMoi(conn, "NhatKyChamSoc");

            boolean coVatTu  = !n.getDongVatTu().isEmpty();
            boolean coDungCu = !n.getDongDungCu().isEmpty();
            boolean coThietBi = !n.getDongThietBi().isEmpty();

            // 1) Chèn header trước (chi phí = 0, cập nhật lại ở bước cuối)
            String sqlHeader = "INSERT INTO NhatKyChamSoc (id, lo_dat_id, loai_cong_viec, ngay_thuc_hien, nhac_viec_id, "
                    + "co_su_dung_vat_tu, co_su_dung_dung_cu, co_su_dung_thiet_bi, "
                    + "tong_chi_phi_vat_tu, tong_chi_phi_dung_cu, tong_chi_phi_thiet_bi, tong_chi_phi, "
                    + "nguoi_ghi_nhan_id, mo_ta, ngay_tao) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, 0, 0, 0, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sqlHeader)) {
                ps.setInt(1, newId);
                ps.setInt(2, n.getLo_dat_id());
                ps.setNString(3, n.getLoai_cong_viec().trim());
                ps.setDate(4, new Date(n.getNgay_thuc_hien().getTime()));
                if (n.getNhac_viec_id() != null && n.getNhac_viec_id() > 0) ps.setInt(5, n.getNhac_viec_id());
                else ps.setNull(5, java.sql.Types.INTEGER);
                ps.setBoolean(6, coVatTu);
                ps.setBoolean(7, coDungCu);
                ps.setBoolean(8, coThietBi);
                ps.setInt(9, nguoiGhiNhan);
                ps.setNString(10, n.getMo_ta());
                ps.executeUpdate();
            }

            // 2) Vật tư: xuất kho FIFO/FEFO -> tiền + diện tích giải phóng
            double chiPhiVatTu = 0, dienTichVatTu = 0;
            for (NhatKyChamSoc.DongVatTu dv : n.getDongVatTu()) {
                if (dv.vatTuId <= 0 || dv.soLuong <= 0) continue;
                double[] kq = xuatKhoVatTu(conn, newId, dv.vatTuId, dv.soLuong);
                chiPhiVatTu   += kq[0];
                dienTichVatTu += kq[1];
            }

            // 3) Dụng cụ tiêu hao -> tiền + diện tích giải phóng
            double chiPhiDungCu = 0, dienTichDungCu = 0;
            for (NhatKyChamSoc.DongDungCu dd : n.getDongDungCu()) {
                if (dd.dungCuId <= 0 || dd.soLuong <= 0) continue;
                double[] kq = tieuHaoDungCu(conn, newId, dd.dungCuId, dd.soLuong);
                chiPhiDungCu   += kq[0];
                dienTichDungCu += kq[1];
            }

            // 4) Thiết bị: khấu hao theo số ngày sử dụng
            double chiPhiThietBi = 0;
            for (NhatKyChamSoc.DongThietBi dt : n.getDongThietBi()) {
                if (dt.thietBiId <= 0) continue;
                int soNgay = Math.max(1, dt.soNgaySuDung);
                chiPhiThietBi += phanBoKhauHaoThietBi(conn, newId, dt.thietBiId, soNgay,
                        n.getLo_dat_id(), n.getNgay_thuc_hien());
            }

            double tongChiPhi = chiPhiVatTu + chiPhiDungCu + chiPhiThietBi;

            // 5) Cập nhật chi phí lên header
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE NhatKyChamSoc SET tong_chi_phi_vat_tu=?, tong_chi_phi_dung_cu=?, "
                  + "tong_chi_phi_thiet_bi=?, tong_chi_phi=? WHERE id=?")) {
                ps.setDouble(1, lamTron(chiPhiVatTu));
                ps.setDouble(2, lamTron(chiPhiDungCu));
                ps.setDouble(3, lamTron(chiPhiThietBi));
                ps.setDouble(4, lamTron(tongChiPhi));
                ps.setInt(5, newId);
                ps.executeUpdate();
            }

            // 6) Đóng nhắc việc tương ứng (nếu nhật ký sinh ra từ lịch chăm sóc)
            if (n.getNhac_viec_id() != null && n.getNhac_viec_id() > 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE NhacViec SET trang_thai=N'Đã thực hiện', nhat_ky_cham_soc_id=? WHERE id=?")) {
                    ps.setInt(1, newId);
                    ps.setInt(2, n.getNhac_viec_id());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            n.setId(newId);
            n.setTong_chi_phi_vat_tu(chiPhiVatTu);
            n.setTong_chi_phi_dung_cu(chiPhiDungCu);
            n.setTong_chi_phi_thiet_bi(chiPhiThietBi);
            n.setTong_chi_phi(tongChiPhi);
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            rollback(conn);
            return e.getMessage() == null ? "Không thể ghi nhật ký." : e.getMessage();
        } finally {
            dong(conn);
        }
    }

    // ===================================================================
    // XÓA NHẬT KÝ (HOÀN TÁC TỒN KHO, DIỆN TÍCH, THIẾT BỊ)
    // ===================================================================

    /**
     * Xóa nhật ký và hoàn tác đúng những gì đã trừ:
     *   - Cộng lại tồn lô hàng vật tư + tồn tổng VatTu, chiếm lại diện tích kho.
     *   - Cộng lại tồn dụng cụ, chiếm lại diện tích kho.
     *   - Xóa phiếu điều động thiết bị, trả thiết bị về trạng thái sẵn sàng.
     */
    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            // 1) Hoàn tác vật tư
            String sqlVT = "SELECT c.vat_tu_id, c.lo_hang_id, c.so_luong_su_dung, c.dien_tich_giai_phong, "
                         + "l.vi_tri_luu_tru_id "
                         + "FROM ChiTietVatTuTieuHao c JOIN LoHangVatTu l ON c.lo_hang_id = l.id "
                         + "WHERE c.nguon_tieu_hao_id = ?";
            List<double[]> dongVT = new ArrayList<>(); // [vatTuId, loHangId, soLuong, dienTich, viTri]
            try (PreparedStatement ps = conn.prepareStatement(sqlVT)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        dongVT.add(new double[]{
                                rs.getInt("vat_tu_id"), rs.getInt("lo_hang_id"),
                                rs.getDouble("so_luong_su_dung"), rs.getDouble("dien_tich_giai_phong"),
                                rs.getInt("vi_tri_luu_tru_id")});
                    }
                }
            }
            for (double[] d : dongVT) {
                capNhatTonLoHang(conn, (int) d[1], d[2]);           // cộng lại tồn lô
                capNhatTonVatTu(conn, (int) d[0], d[2]);            // cộng lại tồn tổng
                capNhatDienTichKhuVuc(conn, (int) d[4], d[3]);      // chiếm lại diện tích kho
            }

            // 2) Hoàn tác dụng cụ
            String sqlDC = "SELECT c.dung_cu_id, c.so_luong_su_dung, d.dien_tich_chiem_dung, d.vi_tri_luu_tru_id "
                         + "FROM ChiTietDungCuTieuHao c JOIN DungCu d ON c.dung_cu_id = d.id "
                         + "WHERE c.nhat_ky_cham_soc_id = ?";
            List<double[]> dongDC = new ArrayList<>(); // [dungCuId, soLuong, dtcdDonVi, viTri]
            try (PreparedStatement ps = conn.prepareStatement(sqlDC)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        dongDC.add(new double[]{
                                rs.getInt("dung_cu_id"), rs.getDouble("so_luong_su_dung"),
                                rs.getDouble("dien_tich_chiem_dung"), rs.getInt("vi_tri_luu_tru_id")});
                    }
                }
            }
            for (double[] d : dongDC) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE DungCu SET ton_kho_hien_tai = ton_kho_hien_tai + ?, ngay_cap_nhat=GETDATE() WHERE id=?")) {
                    ps.setDouble(1, d[1]);
                    ps.setInt(2, (int) d[0]);
                    ps.executeUpdate();
                }
                capNhatDienTichKhuVuc(conn, (int) d[3], d[1] * d[2]);
            }

            // 3) Trả thiết bị về trạng thái sẵn sàng rồi xóa phiếu điều động
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ThietBi SET trang_thai = N'Sẵn sàng', ngay_cap_nhat=GETDATE() WHERE id IN "
                  + "(SELECT thiet_bi_id FROM PhieuDieuDongThietBi WHERE nhat_ky_bat_dau_id = ?)")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // 4) Xóa chi tiết rồi xóa header
            xoa(conn, "DELETE FROM ChiTietVatTuTieuHao WHERE nguon_tieu_hao_id=?", id);
            xoa(conn, "DELETE FROM ChiTietDungCuTieuHao WHERE nhat_ky_cham_soc_id=?", id);
            xoa(conn, "DELETE FROM PhieuDieuDongThietBi WHERE nhat_ky_bat_dau_id=?", id);
            // gỡ liên kết trước khi xóa để không vướng khóa ngoại
            xoa(conn, "UPDATE GhiNhanSauBenh SET nhat_ky_cham_soc_id = NULL WHERE nhat_ky_cham_soc_id=?", id);
            xoa(conn, "UPDATE NhacViec SET nhat_ky_cham_soc_id = NULL, trang_thai = N'Chưa thực hiện' WHERE nhat_ky_cham_soc_id=?", id);

            int r;
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM NhatKyChamSoc WHERE id=?")) {
                ps.setInt(1, id);
                r = ps.executeUpdate();
            }

            conn.commit();
            return r > 0;

        } catch (Exception e) {
            e.printStackTrace();
            rollback(conn);
            return false;
        } finally {
            dong(conn);
        }
    }

    // ===================================================================
    // LOGIC XUẤT KHO / CHI PHÍ (dùng chung, có thể gọi từ DAO khác)
    // ===================================================================

    /**
     * Xuất kho vật tư theo FIFO/FEFO (ưu tiên lô hết hạn sớm nhất, rồi tới lô nhập trước).
     * Ghi ChiTietVatTuTieuHao, trừ tồn LoHangVatTu, trừ tồn VatTu và GIẢI PHÓNG diện tích
     * ở KhuVuc chứa lô hàng.
     *
     * @return [tổng chi phí, tổng diện tích giải phóng]
     * @throws SQLException nếu không đủ tồn kho
     */
    public static double[] xuatKhoVatTu(Connection conn, int nguonTieuHaoId, int vatTuId, double soLuongCan)
            throws SQLException {

        // Diện tích chiếm dụng cho MỘT đơn vị vật tư (hằng số danh mục)
        double dtcdDonVi = 0;
        String tenVatTu = "#" + vatTuId;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ten_vat_tu, dien_tich_chiem_dung FROM VatTu WHERE id=?")) {
            ps.setInt(1, vatTuId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Vật tư #" + vatTuId + " không tồn tại.");
                tenVatTu  = rs.getNString("ten_vat_tu");
                dtcdDonVi = rs.getDouble("dien_tich_chiem_dung");
            }
        }

        // Chỉ lấy lô còn hạn và còn hàng
        String sqlLo = "SELECT l.id, l.so_luong_con_lai, l.vi_tri_luu_tru_id, c.don_gia "
                     + "FROM LoHangVatTu l JOIN ChiTietPhieuNhapKho c ON l.chi_tiet_phieu_nhap_id = c.id "
                     + "WHERE c.vat_tu_id = ? AND l.so_luong_con_lai > 0 "
                     + "AND (l.han_su_dung IS NULL OR l.han_su_dung >= CAST(GETDATE() AS DATE)) "
                     + "ORDER BY CASE WHEN l.han_su_dung IS NULL THEN 1 ELSE 0 END, l.han_su_dung ASC, l.id ASC";

        List<int[]> loKhoa = new ArrayList<>();   // [loId, viTriLuuTruId]
        List<double[]> loSo = new ArrayList<>();  // [conLai, donGia]
        double tongCon = 0;
        try (PreparedStatement ps = conn.prepareStatement(sqlLo)) {
            ps.setInt(1, vatTuId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    loKhoa.add(new int[]{rs.getInt("id"), rs.getInt("vi_tri_luu_tru_id")});
                    double conLai = rs.getDouble("so_luong_con_lai");
                    loSo.add(new double[]{conLai, rs.getDouble("don_gia")});
                    tongCon += conLai;
                }
            }
        }
        if (tongCon < soLuongCan) {
            throw new SQLException("Vật tư \"" + tenVatTu + "\" không đủ tồn kho khả dụng (còn "
                    + lamTron(tongCon) + ", cần " + lamTron(soLuongCan) + ").");
        }

        double conCan = soLuongCan, tongChiPhi = 0, tongDienTich = 0;

        for (int i = 0; i < loKhoa.size() && conCan > 0.000001; i++) {
            int loId    = loKhoa.get(i)[0];
            int viTri   = loKhoa.get(i)[1];
            double conLai = loSo.get(i)[0];
            double donGia = loSo.get(i)[1];

            double dung      = Math.min(conLai, conCan);
            double thanhTien = dung * donGia;
            double dienTich  = dung * dtcdDonVi;

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ChiTietVatTuTieuHao (nguon_tieu_hao_id, vat_tu_id, lo_hang_id, "
                  + "so_luong_su_dung, dien_tich_giai_phong, don_gia, thanh_tien) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, nguonTieuHaoId);
                ps.setInt(2, vatTuId);
                ps.setInt(3, loId);
                ps.setDouble(4, lamTron(dung));
                ps.setDouble(5, lamTron(dienTich));
                ps.setDouble(6, lamTron(donGia));
                ps.setDouble(7, lamTron(thanhTien));
                ps.executeUpdate();
            }

            capNhatTonLoHang(conn, loId, -dung);
            // Vật tư rời kho -> khu vực được giải phóng diện tích
            capNhatDienTichKhuVuc(conn, viTri, -dienTich);

            conCan       -= dung;
            tongChiPhi   += thanhTien;
            tongDienTich += dienTich;
        }

        capNhatTonVatTu(conn, vatTuId, -soLuongCan);
        return new double[]{tongChiPhi, tongDienTich};
    }

    /**
     * Tiêu hao dụng cụ theo giá bình quân. Trừ tồn DungCu và giải phóng diện tích kho.
     * @return [chi phí, diện tích giải phóng]
     */
    private static double[] tieuHaoDungCu(Connection conn, int nhatKyId, int dungCuId, double soLuong)
            throws SQLException {

        double giaBinhQuan = 0, tonKho = 0, dtcdDonVi = 0;
        int viTri = 0;
        String ten = "#" + dungCuId;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ten_dung_cu, gia_binh_quan, ton_kho_hien_tai, dien_tich_chiem_dung, vi_tri_luu_tru_id "
              + "FROM DungCu WHERE id=?")) {
            ps.setInt(1, dungCuId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Dụng cụ #" + dungCuId + " không tồn tại.");
                ten         = rs.getNString("ten_dung_cu");
                giaBinhQuan = rs.getDouble("gia_binh_quan");
                tonKho      = rs.getDouble("ton_kho_hien_tai");
                dtcdDonVi   = rs.getDouble("dien_tich_chiem_dung");
                viTri       = rs.getInt("vi_tri_luu_tru_id");
            }
        }
        if (tonKho < soLuong) {
            throw new SQLException("Dụng cụ \"" + ten + "\" không đủ tồn kho (còn "
                    + lamTron(tonKho) + ", cần " + lamTron(soLuong) + ").");
        }

        double thanhTien = giaBinhQuan * soLuong;
        double dienTich  = dtcdDonVi * soLuong;

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ChiTietDungCuTieuHao (nhat_ky_cham_soc_id, dung_cu_id, so_luong_su_dung, don_gia, thanh_tien) "
              + "VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, nhatKyId);
            ps.setInt(2, dungCuId);
            ps.setDouble(3, lamTron(soLuong));
            ps.setDouble(4, lamTron(giaBinhQuan));
            ps.setDouble(5, lamTron(thanhTien));
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE DungCu SET ton_kho_hien_tai = ton_kho_hien_tai - ?, "
              + "trang_thai = CASE WHEN ton_kho_hien_tai - ? <= ton_kho_toi_thieu THEN 'Sap het' ELSE trang_thai END, "
              + "ngay_cap_nhat = GETDATE() WHERE id = ?")) {
            ps.setDouble(1, soLuong);
            ps.setDouble(2, soLuong);
            ps.setInt(3, dungCuId);
            ps.executeUpdate();
        }

        capNhatDienTichKhuVuc(conn, viTri, -dienTich);
        return new double[]{thanhTien, dienTich};
    }

    /**
     * Khấu hao thiết bị theo số ngày sử dụng thực tế trên lô.
     *   khấu hao/ngày = nguyên giá / (thời gian khấu hao năm * 365)
     *   chi phí       = khấu hao/ngày * số ngày sử dụng
     * Ghi 1 phiếu điều động thiết bị để lưu vết (đi và về trong cùng đợt công việc).
     *
     * @return chi phí khấu hao phân bổ cho lô
     */
    private static double phanBoKhauHaoThietBi(Connection conn, int nhatKyId, int thietBiId, int soNgay,
                                               int loDatId, java.util.Date ngay) throws SQLException {

        int khauHaoNam = 0;
        String tenTB = "#" + thietBiId, trangThai = "";
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ten_thiet_bi, thoi_gian_khau_hao_nam, trang_thai FROM ThietBi WHERE id=?")) {
            ps.setInt(1, thietBiId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Thiết bị #" + thietBiId + " không tồn tại.");
                tenTB      = rs.getNString("ten_thiet_bi");
                khauHaoNam = rs.getInt("thoi_gian_khau_hao_nam");
                trangThai  = rs.getNString("trang_thai");
            }
        }
        for (String tt : TRANG_THAI_THIET_BI_KHONG_DUNG_DUOC) {
            if (tt.equalsIgnoreCase(trangThai))
                throw new SQLException("Thiết bị \"" + tenTB + "\" đang ở trạng thái \"" + trangThai
                        + "\" nên không thể điều động ra lô.");
        }

        // Nguyên giá lấy từ phiếu nhập thiết bị (mỗi thiết bị chỉ có 1 dòng - UNIQUE thiet_bi_id)
        double nguyenGia = 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT don_gia FROM ChiTietPhieuNhapThietBi WHERE thiet_bi_id=?")) {
            ps.setInt(1, thietBiId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) nguyenGia = rs.getDouble(1); }
        }

        double khauHaoNgay = (khauHaoNam > 0 && nguyenGia > 0) ? nguyenGia / (khauHaoNam * 365.0) : 0;
        double chiPhi = khauHaoNgay * soNgay;

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(ngay);
        java.sql.Date ngayDieuDong = new Date(cal.getTimeInMillis());
        cal.add(java.util.Calendar.DAY_OF_MONTH, soNgay);
        java.sql.Date ngayTraVe = new Date(cal.getTimeInMillis());

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO PhieuDieuDongThietBi (thiet_bi_id, lo_dat_id, nhat_ky_bat_dau_id, nhat_ky_ket_thuc_id, "
              + "ngay_dieu_dong, ngay_tra_ve, so_ngay_su_dung, chi_phi_khau_hao, trang_thai, ngay_tao) "
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, N'Đã trả về', GETDATE())")) {
            ps.setInt(1, thietBiId);
            ps.setInt(2, loDatId);
            ps.setInt(3, nhatKyId);
            ps.setInt(4, nhatKyId); // cột NOT NULL -> đợt sử dụng khép kín trong 1 nhật ký
            ps.setDate(5, ngayDieuDong);
            ps.setDate(6, ngayTraVe);
            ps.setInt(7, soNgay);
            ps.setDouble(8, lamTron(chiPhi));
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE ThietBi SET trang_thai = N'Đang sử dụng', ngay_cap_nhat = GETDATE() WHERE id = ?")) {
            ps.setInt(1, thietBiId);
            ps.executeUpdate();
        }

        return chiPhi;
    }

    // ===================================================================
    // TIỆN ÍCH
    // ===================================================================

    /** Sinh id cho bảng không dùng IDENTITY, có khóa để tránh trùng khi ghi đồng thời. */
    static int layIdMoi(Connection conn, String bang) throws SQLException {
        String sql = "SELECT ISNULL(MAX(id),0)+1 FROM " + bang + " WITH (UPDLOCK, HOLDLOCK)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 1;
    }

    /** Cộng (delta > 0) hoặc trừ (delta < 0) diện tích đang bị chiếm của khu vực chứa đơn vị này. */
    static void capNhatDienTichKhuVuc(Connection conn, int donViQuanLyId, double delta) throws SQLException {
        if (donViQuanLyId <= 0 || Math.abs(delta) < 0.000001) return;
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE KhuVuc SET dien_tich_chiem_dung = "
              + "CASE WHEN dien_tich_chiem_dung + ? < 0 THEN 0 ELSE dien_tich_chiem_dung + ? END, "
              + "ngay_cap_nhat = GETDATE() "
              + "WHERE id = (SELECT khu_vuc_id FROM DonViQuanLy WHERE id = ?)")) {
            ps.setDouble(1, delta);
            ps.setDouble(2, delta);
            ps.setInt(3, donViQuanLyId);
            ps.executeUpdate();
        }
    }

    private static void capNhatTonLoHang(Connection conn, int loHangId, double delta) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE LoHangVatTu SET so_luong_con_lai = "
              + "CASE WHEN so_luong_con_lai + ? < 0 THEN 0 ELSE so_luong_con_lai + ? END, "
              + "trang_thai = CASE WHEN so_luong_con_lai + ? <= 0 THEN N'Đã dùng hết' ELSE N'Còn hàng' END "
              + "WHERE id = ?")) {
            ps.setDouble(1, delta);
            ps.setDouble(2, delta);
            ps.setDouble(3, delta);
            ps.setInt(4, loHangId);
            ps.executeUpdate();
        }
    }

    private static void capNhatTonVatTu(Connection conn, int vatTuId, double delta) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE VatTu SET ton_kho_hien_tai = "
              + "CASE WHEN ton_kho_hien_tai + ? < 0 THEN 0 ELSE ton_kho_hien_tai + ? END, "
              + "ngay_cap_nhat = GETDATE() WHERE id = ?")) {
            ps.setDouble(1, delta);
            ps.setDouble(2, delta);
            ps.setInt(3, vatTuId);
            ps.executeUpdate();
        }
    }

    /** Trả về id tài khoản hợp lệ (FK), nếu id truyền vào không tồn tại thì lấy tài khoản đầu tiên. */
    static int chuanHoaTaiKhoan(Connection conn, int taiKhoanId) throws SQLException {
        if (taiKhoanId > 0) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM TaiKhoan WHERE id=?")) {
                ps.setInt(1, taiKhoanId);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement("SELECT TOP 1 id FROM TaiKhoan ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private static String layChuoi(Connection conn, String sql, int thamSo) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, thamSo);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getNString(1) : null; }
        }
    }

    private static void xoa(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private static double lamTron(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static void rollback(Connection conn) {
        if (conn != null) try { conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void dong(Connection conn) {
        if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
