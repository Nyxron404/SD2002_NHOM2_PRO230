package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import models.ChiPhiLoDat;
import url.DBConnect;

/**
 * Tổng hợp theo dõi chi phí & tiêu hao của module canh tác (UC-4).
 *
 * Mỗi lô đất (DonViQuanLy loại "Lô đất") sẽ có:
 *   - Tiền: chi phí vật tư / dụng cụ / thiết bị và tổng chi phí.
 *   - Diện tích tiêu hao: diện tích kho được giải phóng khi vật tư/dụng cụ được dùng hết,
 *     và diện tích kho bị chiếm bởi nông sản sau thu hoạch.
 *   - Thiết bị: số lượt điều động và tổng số ngày sử dụng.
 *
 * Toàn bộ dùng LEFT JOIN từ các bảng con đã gom nhóm sẵn để tránh nhân bản dòng
 * (một lỗi rất dễ gặp nếu join thẳng nhiều bảng chi tiết cùng lúc).
 */
public class ThongKeCanhTacDAO {

    private static final String SQL_TONG_HOP = """
        SELECT  d.id                                   AS lo_dat_id,
                d.ten_don_vi                           AS ten_lo,
                d.dien_tich                            AS dien_tich_lo,
                g.ten_giong                            AS ten_giong,
                ISNULL(nk.so_lan, 0)                   AS so_lan,
                ISNULL(nk.cp_vat_tu, 0)                AS cp_vat_tu,
                ISNULL(nk.cp_dung_cu, 0)               AS cp_dung_cu,
                ISNULL(nk.cp_thiet_bi, 0)              AS cp_thiet_bi,
                ISNULL(nk.cp_tong, 0)                  AS cp_tong,
                ISNULL(vt.dt_vat_tu, 0)                AS dt_vat_tu,
                ISNULL(dc.dt_dung_cu, 0)               AS dt_dung_cu,
                ISNULL(tb.so_luot, 0)                  AS so_luot_tb,
                ISNULL(tb.so_ngay, 0)                  AS so_ngay_tb,
                ISNULL(th.san_luong, 0)                AS san_luong,
                ISNULL(th.dt_kho, 0)                   AS dt_kho
        FROM DonViQuanLy d
        LEFT JOIN VuonTrong v      ON v.lo_dat_id = d.id
        LEFT JOIN GiongSauRieng g  ON v.giong_id  = g.id
        LEFT JOIN (
                SELECT lo_dat_id,
                       COUNT(*)                    AS so_lan,
                       SUM(tong_chi_phi_vat_tu)    AS cp_vat_tu,
                       SUM(tong_chi_phi_dung_cu)   AS cp_dung_cu,
                       SUM(tong_chi_phi_thiet_bi)  AS cp_thiet_bi,
                       SUM(tong_chi_phi)           AS cp_tong
                FROM NhatKyChamSoc GROUP BY lo_dat_id
        ) nk ON nk.lo_dat_id = d.id
        LEFT JOIN (
                SELECT n.lo_dat_id, SUM(c.dien_tich_giai_phong) AS dt_vat_tu
                FROM ChiTietVatTuTieuHao c
                JOIN NhatKyChamSoc n ON c.nguon_tieu_hao_id = n.id
                GROUP BY n.lo_dat_id
        ) vt ON vt.lo_dat_id = d.id
        LEFT JOIN (
                SELECT n.lo_dat_id, SUM(c.so_luong_su_dung * dc0.dien_tich_chiem_dung) AS dt_dung_cu
                FROM ChiTietDungCuTieuHao c
                JOIN NhatKyChamSoc n ON c.nhat_ky_cham_soc_id = n.id
                JOIN DungCu dc0      ON c.dung_cu_id = dc0.id
                GROUP BY n.lo_dat_id
        ) dc ON dc.lo_dat_id = d.id
        LEFT JOIN (
                SELECT lo_dat_id, COUNT(*) AS so_luot, SUM(ISNULL(so_ngay_su_dung,0)) AS so_ngay
                FROM PhieuDieuDongThietBi GROUP BY lo_dat_id
        ) tb ON tb.lo_dat_id = d.id
        LEFT JOIN (
                SELECT v2.lo_dat_id,
                       SUM(t.tong_san_luong_kg)            AS san_luong,
                       SUM(t.tong_dien_tich_chiem_dung)    AS dt_kho
                FROM GhiNhanThuHoach t
                JOIN VuonTrong v2 ON t.vuon_trong_id = v2.id
                GROUP BY v2.lo_dat_id
        ) th ON th.lo_dat_id = d.id
        WHERE d.loai_don_vi = N'Lô đất'
        ORDER BY d.ten_don_vi
        """;

    /** Bảng tổng hợp chi phí & tiêu hao của tất cả lô đất. */
    public List<ChiPhiLoDat> getTongHopTheoLo() {
        List<ChiPhiLoDat> list = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TONG_HOP);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ChiPhiLoDat c = new ChiPhiLoDat();
                c.setLo_dat_id(rs.getInt("lo_dat_id"));
                c.setTen_lo_dat(rs.getNString("ten_lo"));
                c.setTen_giong(rs.getNString("ten_giong"));
                c.setDien_tich_lo(rs.getDouble("dien_tich_lo"));
                c.setSo_lan_cham_soc(rs.getInt("so_lan"));
                c.setChi_phi_vat_tu(rs.getDouble("cp_vat_tu"));
                c.setChi_phi_dung_cu(rs.getDouble("cp_dung_cu"));
                c.setChi_phi_thiet_bi(rs.getDouble("cp_thiet_bi"));
                c.setTong_chi_phi(rs.getDouble("cp_tong"));
                c.setDien_tich_vat_tu_giai_phong(rs.getDouble("dt_vat_tu"));
                c.setDien_tich_dung_cu_giai_phong(rs.getDouble("dt_dung_cu"));
                c.setSo_luot_thiet_bi(rs.getInt("so_luot_tb"));
                c.setSo_ngay_thiet_bi(rs.getInt("so_ngay_tb"));
                c.setSan_luong_kg(rs.getDouble("san_luong"));
                c.setDien_tich_luu_kho(rs.getDouble("dt_kho"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Tổng chi phí canh tác toàn trang trại (dùng cho UC-6 báo cáo). */
    public double getTongChiPhiToanTrangTrai() {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT ISNULL(SUM(tong_chi_phi),0) FROM NhatKyChamSoc");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Chi tiết vật tư đã tiêu hao của một nhật ký (phục vụ xem lại/đối chiếu). */
    public List<String[]> getChiTietVatTu(int nhatKyId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT v.ten_vat_tu, c.so_luong_su_dung, v.don_vi_tinh, c.don_gia, c.thanh_tien, "
                   + "c.dien_tich_giai_phong, l.so_lo "
                   + "FROM ChiTietVatTuTieuHao c "
                   + "JOIN VatTu v ON c.vat_tu_id = v.id "
                   + "JOIN LoHangVatTu l ON c.lo_hang_id = l.id "
                   + "WHERE c.nguon_tieu_hao_id = ? ORDER BY c.id";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nhatKyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getNString("ten_vat_tu"),
                            String.valueOf(rs.getDouble("so_luong_su_dung")),
                            rs.getNString("don_vi_tinh"),
                            String.valueOf(rs.getDouble("don_gia")),
                            String.valueOf(rs.getDouble("thanh_tien")),
                            String.valueOf(rs.getDouble("dien_tich_giai_phong")),
                            rs.getString("so_lo")});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Chi tiết thiết bị đã điều động cho một nhật ký. */
    public List<String[]> getChiTietThietBi(int nhatKyId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT t.ten_thiet_bi, p.so_ngay_su_dung, p.chi_phi_khau_hao, p.ngay_dieu_dong, p.ngay_tra_ve "
                   + "FROM PhieuDieuDongThietBi p JOIN ThietBi t ON p.thiet_bi_id = t.id "
                   + "WHERE p.nhat_ky_bat_dau_id = ? ORDER BY p.id";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nhatKyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[]{
                            rs.getNString("ten_thiet_bi"),
                            String.valueOf(rs.getInt("so_ngay_su_dung")),
                            String.valueOf(rs.getDouble("chi_phi_khau_hao")),
                            String.valueOf(rs.getDate("ngay_dieu_dong")),
                            String.valueOf(rs.getDate("ngay_tra_ve"))});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
