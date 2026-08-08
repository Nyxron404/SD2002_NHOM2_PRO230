package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import models.NhacViecItem;
import models.VatTuTonKho;
import url.DBConnect;

/**
 * Cung cấp dữ liệu tồn kho KHẢ DỤNG và nhắc việc cho các form của module canh tác.
 *
 * Vì sao không dùng thẳng VatTu.ton_kho_hien_tai:
 *   - Cột đó là tổng cộng dồn, gồm cả lô đã hết hạn sử dụng.
 *   - Khi xuất kho, NhatKyChamSocDAO chỉ lấy lô còn hạn -> nếu form hiển thị
 *     tồn tổng thì người dùng nhập vượt và bị báo lỗi khi lưu.
 * Do đó tồn khả dụng được tính trực tiếp từ LoHangVatTu.
 */
public class TonKhoCanhTacDAO {

    /** Tên loại vật tư dùng để phòng trừ sâu bệnh (UC-4.6). */
    public static final String LOAI_THUOC_BVTV = "Thuốc bảo vệ thực vật";

    private static final String SQL_VAT_TU = """
        SELECT  v.id,
                v.ten_vat_tu,
                v.don_vi_tinh,
                v.dien_tich_chiem_dung,
                v.loai_vat_tu_id,
                lv.ten_loai,
                ISNULL(k.ton_kha_dung, 0)  AS ton_kha_dung,
                ISNULL(k.don_gia, 0)       AS don_gia,
                k.han_gan_nhat
        FROM VatTu v
        JOIN LoaiVatTu lv ON v.loai_vat_tu_id = lv.id
        LEFT JOIN (
            SELECT  c.vat_tu_id,
                    SUM(l.so_luong_con_lai) AS ton_kha_dung,
                    MIN(c.don_gia)          AS don_gia,
                    MIN(l.han_su_dung)      AS han_gan_nhat
            FROM LoHangVatTu l
            JOIN ChiTietPhieuNhapKho c ON l.chi_tiet_phieu_nhap_id = c.id
            WHERE l.so_luong_con_lai > 0
              AND (l.han_su_dung IS NULL OR l.han_su_dung >= CAST(GETDATE() AS DATE))
            GROUP BY c.vat_tu_id
        ) k ON k.vat_tu_id = v.id
        %s
        ORDER BY lv.ten_loai, v.ten_vat_tu
        """;

    /** Vật tư còn tồn khả dụng trong kho (dùng cho form Nhật ký chăm sóc). */
    public List<VatTuTonKho> getVatTuKhaDung() {
        return docVatTu(String.format(SQL_VAT_TU, "WHERE ISNULL(k.ton_kha_dung,0) > 0"), 0);
    }

    /** Riêng thuốc bảo vệ thực vật, dùng cho form Xử lý sâu bệnh (UC-4.6). */
    public List<VatTuTonKho> getThuocBaoVeThucVat() {
        String dieuKien = "WHERE ISNULL(k.ton_kha_dung,0) > 0 AND lv.ten_loai = ?";
        return docVatTu(String.format(SQL_VAT_TU, dieuKien), 1);
    }

    private List<VatTuTonKho> docVatTu(String sql, int soThamSo) {
        List<VatTuTonKho> list = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (soThamSo == 1) ps.setNString(1, LOAI_THUOC_BVTV);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VatTuTonKho t = new VatTuTonKho();
                    t.setId(rs.getInt("id"));
                    t.setTen(rs.getNString("ten_vat_tu"));
                    t.setDon_vi_tinh(rs.getNString("don_vi_tinh"));
                    t.setDien_tich_don_vi(rs.getDouble("dien_tich_chiem_dung"));
                    t.setLoai_id(rs.getInt("loai_vat_tu_id"));
                    t.setTen_loai(rs.getNString("ten_loai"));
                    t.setTon_kha_dung(rs.getDouble("ton_kha_dung"));
                    t.setDon_gia_gan_nhat(rs.getDouble("don_gia"));
                    java.sql.Date han = rs.getDate("han_gan_nhat");
                    t.setHan_su_dung_gan_nhat(han == null ? "" : han.toString());
                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Dụng cụ còn tồn (tính theo DungCu.ton_kho_hien_tai vì dụng cụ không quản lý theo lô). */
    public List<VatTuTonKho> getDungCuKhaDung() {
        List<VatTuTonKho> list = new ArrayList<>();
        String sql = "SELECT id, ten_dung_cu, don_vi_tinh, ton_kho_hien_tai, gia_binh_quan, "
                   + "dien_tich_chiem_dung, trang_thai FROM DungCu "
                   + "WHERE ton_kho_hien_tai > 0 ORDER BY ten_dung_cu";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VatTuTonKho t = new VatTuTonKho();
                t.setId(rs.getInt("id"));
                t.setTen(rs.getNString("ten_dung_cu"));
                t.setDon_vi_tinh(rs.getNString("don_vi_tinh"));
                t.setTon_kha_dung(rs.getDouble("ton_kho_hien_tai"));
                t.setDon_gia_gan_nhat(rs.getDouble("gia_binh_quan"));
                t.setDien_tich_don_vi(rs.getDouble("dien_tich_chiem_dung"));
                t.setTen_loai(rs.getString("trang_thai"));
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Thiết bị đang ở trạng thái điều động được (loại trừ bảo trì / hỏng / thanh lý). */
    public List<VatTuTonKho> getThietBiKhaDung() {
        List<VatTuTonKho> list = new ArrayList<>();
        String sql = "SELECT t.id, t.ten_thiet_bi, t.trang_thai, t.thoi_gian_khau_hao_nam, "
                   + "ISNULL(c.don_gia,0) AS nguyen_gia "
                   + "FROM ThietBi t LEFT JOIN ChiTietPhieuNhapThietBi c ON c.thiet_bi_id = t.id "
                   + "WHERE t.trang_thai NOT IN (N'Bảo trì', N'Đang bảo trì', N'Hỏng', N'Thanh lý', N'Ngừng sử dụng') "
                   + "ORDER BY t.ten_thiet_bi";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VatTuTonKho t = new VatTuTonKho();
                t.setId(rs.getInt("id"));
                t.setTen(rs.getNString("ten_thiet_bi"));
                t.setTen_loai(rs.getNString("trang_thai"));
                t.setDon_vi_tinh("ngày");
                int nam = rs.getInt("thoi_gian_khau_hao_nam");
                double nguyenGia = rs.getDouble("nguyen_gia");
                // Đơn giá ở đây là khấu hao MỘT NGÀY -> để form ước tính chi phí.
                t.setDon_gia_gan_nhat(nam > 0 ? Math.round(nguyenGia / (nam * 365.0) * 100.0) / 100.0 : 0);
                t.setTon_kha_dung(365);   // giới hạn mềm số ngày nhập vào 1 lần
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===================================================================
    // NHẮC VIỆC (UC-4.3 -> UC-4.4)
    // ===================================================================

    /** Nhắc việc chưa gắn nhật ký, dùng cho dropdown khi ghi nhật ký chăm sóc. */
    public List<NhacViecItem> getNhacViecChoXuLy() {
        return docNhacViec("WHERE nv.nhat_ky_cham_soc_id IS NULL AND l.trang_thai = N'Đang áp dụng' "
                         + "ORDER BY nv.ngay_nhac ASC");
    }

    /** Toàn bộ nhắc việc gần đây để hiển thị bảng theo dõi. */
    public List<NhacViecItem> getNhacViecGanDay() {
        return docNhacViec("ORDER BY nv.ngay_nhac DESC");
    }

    private List<NhacViecItem> docNhacViec(String duoiCau) {
        List<NhacViecItem> list = new ArrayList<>();
        String sql = "SELECT nv.id, nv.lich_lo_dat_id, nv.ngay_nhac, nv.trang_thai, nv.nhat_ky_cham_soc_id, "
                   + "ld.lich_cham_soc_id, ld.lo_dat_id, d.ten_don_vi, l.loai_cong_viec "
                   + "FROM NhacViec nv "
                   + "JOIN LichChamSoc_LoDat ld ON nv.lich_lo_dat_id = ld.id "
                   + "JOIN LichChamSoc l ON ld.lich_cham_soc_id = l.id "
                   + "JOIN DonViQuanLy d ON ld.lo_dat_id = d.id " + duoiCau;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            long homNay = System.currentTimeMillis();
            while (rs.next()) {
                NhacViecItem n = new NhacViecItem();
                n.setId(rs.getInt("id"));
                n.setLich_lo_dat_id(rs.getInt("lich_lo_dat_id"));
                n.setLich_cham_soc_id(rs.getInt("lich_cham_soc_id"));
                n.setLo_dat_id(rs.getInt("lo_dat_id"));
                n.setTen_lo_dat(rs.getNString("ten_don_vi"));
                n.setLoai_cong_viec(rs.getNString("loai_cong_viec"));
                n.setNgay_nhac(rs.getDate("ngay_nhac"));
                n.setTrang_thai(rs.getNString("trang_thai"));
                int nk = rs.getInt("nhat_ky_cham_soc_id");
                n.setNhat_ky_cham_soc_id(rs.wasNull() ? null : nk);
                n.setQua_han(n.getNhat_ky_cham_soc_id() == null
                        && n.getNgay_nhac() != null && n.getNgay_nhac().getTime() < homNay);
                list.add(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
