package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.GhiNhanSauBenh;
import models.NhatKyChamSoc;
import url.DBConnect;

/**
 * UC-4.6 - Ghi nhận và xử lý sâu bệnh; include UC-3.2 (xuất kho thuốc).
 * Đồng bộ trạng thái lô (co_sau_benh) - liên kết UC-4.5.
 */
public class GhiNhanSauBenhDAO {

    public List<GhiNhanSauBenh> getAllWithVuon() {
        List<GhiNhanSauBenh> list = new ArrayList<>();
        String sql = "SELECT s.*, d.ten_don_vi AS ten_lo FROM GhiNhanSauBenh s "
                + "JOIN VuonTrong v ON s.vuon_trong_id = v.id "
                + "JOIN DonViQuanLy d ON v.lo_dat_id = d.id ORDER BY s.id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GhiNhanSauBenh s = new GhiNhanSauBenh();
                s.setId(rs.getInt("id"));
                s.setVuon_trong_id(rs.getInt("vuon_trong_id"));
                s.setTen_sau_benh(rs.getNString("ten_sau_benh"));
                s.setMuc_do_nghiem_trong(rs.getNString("muc_do_nghiem_trong"));
                s.setNgay_phat_hien(rs.getDate("ngay_phat_hien"));
                s.setBien_phap_xu_ly(rs.getNString("bien_phap_xu_ly"));
                int nk = rs.getInt("nhat_ky_cham_soc_id");
                s.setNhat_ky_cham_soc_id(rs.wasNull() ? null : nk);
                s.setTrang_thai(rs.getNString("trang_thai"));
                s.setNguoi_ghi_nhan_id(rs.getInt("nguoi_ghi_nhan_id"));
                s.setTen_lo_dat(rs.getNString("ten_lo"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Ghi nhận sâu bệnh, xuất kho thuốc (nếu có), cập nhật cờ sâu bệnh của lô.
     * Trả về null nếu thành công, hoặc thông báo lỗi.
     */
    public String insert(GhiNhanSauBenh s) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            int newId;
            String sql = "INSERT INTO GhiNhanSauBenh (vuon_trong_id, ten_sau_benh, muc_do_nghiem_trong, ngay_phat_hien, "
                    + "bien_phap_xu_ly, trang_thai, nguoi_ghi_nhan_id, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, s.getVuon_trong_id());
                ps.setNString(2, s.getTen_sau_benh());
                ps.setNString(3, s.getMuc_do_nghiem_trong());
                if (s.getNgay_phat_hien() != null) ps.setDate(4, new Date(s.getNgay_phat_hien().getTime()));
                else ps.setDate(4, new Date(System.currentTimeMillis()));
                ps.setNString(5, s.getBien_phap_xu_ly());
                ps.setNString(6, s.getTrang_thai() != null ? s.getTrang_thai() : "Chưa xử lý");
                ps.setInt(7, s.getNguoi_ghi_nhan_id());
                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) { gk.next(); newId = gk.getInt(1); }
            }

            // Xuất kho thuốc (include UC-3.2) - nguồn tiêu hao = bản ghi sâu bệnh
            for (NhatKyChamSoc.DongVatTu dv : s.getDongThuoc()) {
                if (dv.vatTuId <= 0 || dv.soLuong <= 0) continue;
                NhatKyChamSocDAO.xuatKhoVatTu(conn, newId, dv.vatTuId, dv.soLuong);
            }

            // Đồng bộ trạng thái lô sang "có sâu bệnh"
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE VuonTrong SET co_sau_benh=1, ngay_cap_nhat=GETDATE() WHERE id=?")) {
                ps.setInt(1, s.getVuon_trong_id());
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

    public boolean update(GhiNhanSauBenh s) {
        String sql = "UPDATE GhiNhanSauBenh SET ten_sau_benh=?, muc_do_nghiem_trong=?, bien_phap_xu_ly=?, trang_thai=? WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, s.getTen_sau_benh());
            ps.setNString(2, s.getMuc_do_nghiem_trong());
            ps.setNString(3, s.getBien_phap_xu_ly());
            ps.setNString(4, s.getTrang_thai());
            ps.setInt(5, s.getId());
            boolean ok = ps.executeUpdate() > 0;
            // Nếu đã xử lý xong -> gỡ cờ sâu bệnh nếu lô không còn ca chưa xử lý
            if (ok && "Đã xử lý".equals(s.getTrang_thai())) capNhatCoSauBenh(conn, s.getVuon_trong_id());
            return ok;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private void capNhatCoSauBenh(Connection conn, int vuonTrongId) throws SQLException {
        String sql = "UPDATE VuonTrong SET co_sau_benh = CASE WHEN EXISTS "
                + "(SELECT 1 FROM GhiNhanSauBenh WHERE vuon_trong_id=? AND trang_thai <> N'Đã xử lý') THEN 1 ELSE 0 END, "
                + "ngay_cap_nhat=GETDATE() WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vuonTrongId);
            ps.setInt(2, vuonTrongId);
            ps.executeUpdate();
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM GhiNhanSauBenh WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
