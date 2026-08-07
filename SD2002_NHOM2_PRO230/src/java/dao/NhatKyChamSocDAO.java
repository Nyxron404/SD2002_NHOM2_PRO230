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
 * Trung tâm module: xuất kho vật tư (FIFO/FEFO), tính chi phí vật tư + dụng cụ + hao mòn thiết bị,
 * cộng dồn chi phí theo lô. Toàn bộ chạy trong 1 giao dịch (transaction).
 */
public class NhatKyChamSocDAO {

    public List<NhatKyChamSoc> getAllWithLo() {
        List<NhatKyChamSoc> list = new ArrayList<>();
        String sql = "SELECT n.*, d.ten_don_vi AS ten_lo FROM NhatKyChamSoc n "
                + "JOIN DonViQuanLy d ON n.lo_dat_id = d.id ORDER BY n.id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhatKyChamSoc n = new NhatKyChamSoc();
                n.setId(rs.getInt("id"));
                n.setLo_dat_id(rs.getInt("lo_dat_id"));
                n.setLoai_cong_viec(rs.getNString("loai_cong_viec"));
                n.setNgay_thuc_hien(rs.getDate("ngay_thuc_hien"));
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
                list.add(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Ghi nhật ký kèm xuất kho, tính chi phí. Trả về thông báo lỗi (null nếu thành công).
     */
    public String insertFull(NhatKyChamSoc n) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return "Không kết nối được cơ sở dữ liệu";
            conn.setAutoCommit(false);

            int newId = layIdMoi(conn);

            boolean coVatTu = !n.getDongVatTu().isEmpty();
            boolean coDungCu = !n.getDongDungCu().isEmpty();
            boolean coThietBi = !n.getDongThietBi().isEmpty();

            // 1) Chèn header (chi phí = 0, sẽ cập nhật sau)
            String sqlHeader = "INSERT INTO NhatKyChamSoc (id, lo_dat_id, loai_cong_viec, ngay_thuc_hien, nhac_viec_id, "
                    + "co_su_dung_vat_tu, co_su_dung_dung_cu, co_su_dung_thiet_bi, "
                    + "tong_chi_phi_vat_tu, tong_chi_phi_dung_cu, tong_chi_phi_thiet_bi, tong_chi_phi, "
                    + "nguoi_ghi_nhan_id, mo_ta, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, 0, 0, 0, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sqlHeader)) {
                ps.setInt(1, newId);
                ps.setInt(2, n.getLo_dat_id());
                ps.setNString(3, n.getLoai_cong_viec());
                ps.setDate(4, new Date(n.getNgay_thuc_hien().getTime()));
                if (n.getNhac_viec_id() != null) ps.setInt(5, n.getNhac_viec_id()); else ps.setNull(5, java.sql.Types.INTEGER);
                ps.setBoolean(6, coVatTu);
                ps.setBoolean(7, coDungCu);
                ps.setBoolean(8, coThietBi);
                ps.setInt(9, n.getNguoi_ghi_nhan_id());
                ps.setNString(10, n.getMo_ta());
                ps.executeUpdate();
            }

            double chiPhiVatTu = 0;
            // 2) Xuất kho vật tư FIFO
            for (NhatKyChamSoc.DongVatTu dv : n.getDongVatTu()) {
                if (dv.vatTuId <= 0 || dv.soLuong <= 0) continue;
                double[] kq = xuatKhoVatTu(conn, newId, dv.vatTuId, dv.soLuong);
                chiPhiVatTu += kq[0];
            }

            double chiPhiDungCu = 0;
            // 3) Dụng cụ tiêu hao
            for (NhatKyChamSoc.DongDungCu dd : n.getDongDungCu()) {
                if (dd.dungCuId <= 0 || dd.soLuong <= 0) continue;
                chiPhiDungCu += tieuHaoDungCu(conn, newId, dd.dungCuId, dd.soLuong);
            }

            double chiPhiThietBi = 0;
            // 4) Hao mòn thiết bị (khấu hao theo số ngày sử dụng)
            for (NhatKyChamSoc.DongThietBi dt : n.getDongThietBi()) {
                if (dt.thietBiId <= 0) continue;
                int soNgay = dt.soNgaySuDung <= 0 ? 1 : dt.soNgaySuDung;
                chiPhiThietBi += phanBoKhauHaoThietBi(conn, newId, dt.thietBiId, soNgay,
                        n.getLo_dat_id(), n.getNgay_thuc_hien());
            }

            double tongChiPhi = chiPhiVatTu + chiPhiDungCu + chiPhiThietBi;

            // 5) Cập nhật lại chi phí trên header
            String sqlUpd = "UPDATE NhatKyChamSoc SET tong_chi_phi_vat_tu=?, tong_chi_phi_dung_cu=?, "
                    + "tong_chi_phi_thiet_bi=?, tong_chi_phi=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpd)) {
                ps.setDouble(1, chiPhiVatTu);
                ps.setDouble(2, chiPhiDungCu);
                ps.setDouble(3, chiPhiThietBi);
                ps.setDouble(4, tongChiPhi);
                ps.setInt(5, newId);
                ps.executeUpdate();
            }

            conn.commit();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            return e.getMessage();
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    public boolean delete(int id) {
        // Xóa các dòng tiêu hao trước để tránh vướng khóa ngoại (không hoàn tồn kho vì đã tiêu hao thực tế)
        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement("DELETE FROM ChiTietVatTuTieuHao WHERE nguon_tieu_hao_id=?");
                 PreparedStatement p2 = conn.prepareStatement("DELETE FROM ChiTietDungCuTieuHao WHERE nhat_ky_cham_soc_id=?");
                 PreparedStatement p3 = conn.prepareStatement("DELETE FROM PhieuDieuDongThietBi WHERE nhat_ky_bat_dau_id=?");
                 PreparedStatement p4 = conn.prepareStatement("DELETE FROM NhatKyChamSoc WHERE id=?")) {
                p1.setInt(1, id); p1.executeUpdate();
                p2.setInt(1, id); p2.executeUpdate();
                p3.setInt(1, id); p3.executeUpdate();
                p4.setInt(1, id);
                int r = p4.executeUpdate();
                conn.commit();
                return r > 0;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // ===================== LOGIC XUẤT KHO / CHI PHÍ (tái sử dụng) =====================

    /** Lấy id mới cho bảng NhatKyChamSoc (không dùng IDENTITY). */
    static int layIdMoi(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT ISNULL(MAX(id),0)+1 FROM NhatKyChamSoc");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 1;
    }

    /**
     * Xuất kho vật tư theo FIFO/FEFO (ưu tiên lô hết hạn sớm nhất).
     * Ghi ChiTietVatTuTieuHao, trừ tồn LoHangVatTu và VatTu.
     * Trả về [tổng chi phí, tổng diện tích giải phóng]. Ném lỗi nếu không đủ tồn.
     */
    public static double[] xuatKhoVatTu(Connection conn, int nguonTieuHaoId, int vatTuId, double soLuongCan)
            throws SQLException {
        // Diện tích chiếm dụng / đơn vị của vật tư
        double dtcdDonVi = 0;
        String tenVatTu = "#" + vatTuId;
        try (PreparedStatement ps = conn.prepareStatement("SELECT ten_vat_tu, dien_tich_chiem_dung, ton_kho_hien_tai FROM VatTu WHERE id=?")) {
            ps.setInt(1, vatTuId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tenVatTu = rs.getNString("ten_vat_tu");
                    dtcdDonVi = rs.getDouble("dien_tich_chiem_dung");
                }
            }
        }

        // Kiểm tra tổng tồn theo các lô hàng
        double tongCon = 0;
        String sqlSum = "SELECT ISNULL(SUM(l.so_luong_con_lai),0) FROM LoHangVatTu l "
                + "JOIN ChiTietPhieuNhapKho c ON l.chi_tiet_phieu_nhap_id=c.id "
                + "WHERE c.vat_tu_id=? AND l.so_luong_con_lai>0";
        try (PreparedStatement ps = conn.prepareStatement(sqlSum)) {
            ps.setInt(1, vatTuId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) tongCon = rs.getDouble(1); }
        }
        if (tongCon < soLuongCan) {
            throw new SQLException("Vật tư \"" + tenVatTu + "\" không đủ tồn kho (còn " + tongCon + ", cần " + soLuongCan + ")");
        }

        double conCan = soLuongCan;
        double tongChiPhi = 0;
        double tongDienTich = 0;

        String sqlLo = "SELECT l.id, l.so_luong_con_lai, c.don_gia FROM LoHangVatTu l "
                + "JOIN ChiTietPhieuNhapKho c ON l.chi_tiet_phieu_nhap_id=c.id "
                + "WHERE c.vat_tu_id=? AND l.so_luong_con_lai>0 ORDER BY l.han_su_dung ASC, l.id ASC";
        List<int[]> loIds = new ArrayList<>();
        List<double[]> loData = new ArrayList<>(); // [con_lai, don_gia]
        try (PreparedStatement ps = conn.prepareStatement(sqlLo)) {
            ps.setInt(1, vatTuId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    loIds.add(new int[]{rs.getInt("id")});
                    loData.add(new double[]{rs.getDouble("so_luong_con_lai"), rs.getDouble("don_gia")});
                }
            }
        }

        for (int i = 0; i < loIds.size() && conCan > 0; i++) {
            int loId = loIds.get(i)[0];
            double conLai = loData.get(i)[0];
            double donGia = loData.get(i)[1];
            double dung = Math.min(conLai, conCan);
            double thanhTien = dung * donGia;
            double dienTich = dung * dtcdDonVi;

            // Ghi chi tiết tiêu hao
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ChiTietVatTuTieuHao (nguon_tieu_hao_id, vat_tu_id, lo_hang_id, so_luong_su_dung, dien_tich_giai_phong, don_gia, thanh_tien) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, nguonTieuHaoId);
                ps.setInt(2, vatTuId);
                ps.setInt(3, loId);
                ps.setDouble(4, dung);
                ps.setDouble(5, dienTich);
                ps.setDouble(6, donGia);
                ps.setDouble(7, thanhTien);
                ps.executeUpdate();
            }
            // Trừ tồn lô hàng
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE LoHangVatTu SET so_luong_con_lai = so_luong_con_lai - ?, "
                    + "trang_thai = CASE WHEN so_luong_con_lai - ? <= 0 THEN N'Đã dùng hết' ELSE trang_thai END WHERE id=?")) {
                ps.setDouble(1, dung);
                ps.setDouble(2, dung);
                ps.setInt(3, loId);
                ps.executeUpdate();
            }
            conCan -= dung;
            tongChiPhi += thanhTien;
            tongDienTich += dienTich;
        }

        // Trừ tồn tổng của vật tư + giải phóng diện tích chiếm dụng
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE VatTu SET ton_kho_hien_tai = ton_kho_hien_tai - ?, "
                + "dien_tich_chiem_dung = CASE WHEN dien_tich_chiem_dung - ? < 0 THEN 0 ELSE dien_tich_chiem_dung - ? END, "
                + "ngay_cap_nhat=GETDATE() WHERE id=?")) {
            ps.setDouble(1, soLuongCan);
            ps.setDouble(2, tongDienTich);
            ps.setDouble(3, tongDienTich);
            ps.setInt(4, vatTuId);
            ps.executeUpdate();
        }

        return new double[]{tongChiPhi, tongDienTich};
    }

    /** Tiêu hao dụng cụ theo giá bình quân, trừ tồn kho dụng cụ. Trả về chi phí. */
    private static double tieuHaoDungCu(Connection conn, int nhatKyId, int dungCuId, double soLuong)
            throws SQLException {
        double giaBinhQuan = 0;
        try (PreparedStatement ps = conn.prepareStatement("SELECT gia_binh_quan FROM DungCu WHERE id=?")) {
            ps.setInt(1, dungCuId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) giaBinhQuan = rs.getDouble(1); }
        }
        double thanhTien = giaBinhQuan * soLuong;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO ChiTietDungCuTieuHao (nhat_ky_cham_soc_id, dung_cu_id, so_luong_su_dung, don_gia, thanh_tien) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, nhatKyId);
            ps.setInt(2, dungCuId);
            ps.setDouble(3, soLuong);
            ps.setDouble(4, giaBinhQuan);
            ps.setDouble(5, thanhTien);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE DungCu SET ton_kho_hien_tai = ton_kho_hien_tai - ?, ngay_cap_nhat=GETDATE() WHERE id=?")) {
            ps.setDouble(1, soLuong);
            ps.setInt(2, dungCuId);
            ps.executeUpdate();
        }
        return thanhTien;
    }

    /**
     * Phân bổ chi phí khấu hao thiết bị cho lô đất.
     * Khấu hao ngày = nguyên giá / (thời gian khấu hao năm * 365).
     * Ghi 1 phiếu điều động thiết bị (dùng trong ngày) để lưu vết chi phí.
     */
    private static double phanBoKhauHaoThietBi(Connection conn, int nhatKyId, int thietBiId, int soNgay,
                                               int loDatId, java.util.Date ngay) throws SQLException {
        int khauHaoNam = 0;
        try (PreparedStatement ps = conn.prepareStatement("SELECT thoi_gian_khau_hao_nam FROM ThietBi WHERE id=?")) {
            ps.setInt(1, thietBiId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) khauHaoNam = rs.getInt(1); }
        }
        double nguyenGia = 0;
        try (PreparedStatement ps = conn.prepareStatement("SELECT don_gia FROM ChiTietPhieuNhapThietBi WHERE thiet_bi_id=?")) {
            ps.setInt(1, thietBiId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) nguyenGia = rs.getDouble(1); }
        }
        double khauHaoNgay = (khauHaoNam > 0) ? nguyenGia / (khauHaoNam * 365.0) : 0;
        double chiPhi = khauHaoNgay * soNgay;

        Date d = new Date(ngay.getTime());
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO PhieuDieuDongThietBi (thiet_bi_id, lo_dat_id, nhat_ky_bat_dau_id, nhat_ky_ket_thuc_id, "
                + "ngay_dieu_dong, ngay_tra_ve, so_ngay_su_dung, chi_phi_khau_hao, trang_thai, ngay_tao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, N'Đã trả về', GETDATE())")) {
            ps.setInt(1, thietBiId);
            ps.setInt(2, loDatId);
            ps.setInt(3, nhatKyId);
            ps.setInt(4, nhatKyId);
            ps.setDate(5, d);
            ps.setDate(6, d);
            ps.setInt(7, soNgay);
            ps.setDouble(8, chiPhi);
            ps.executeUpdate();
        }
        return chiPhi;
    }
}
