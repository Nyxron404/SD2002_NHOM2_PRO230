package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.LichSuSinhTruong;
import url.DBConnect;

/**
 * UC-4.5 - Theo dõi trạng thái sinh trưởng theo lô/đợt trồng.
 * Cập nhật giai đoạn, ghi lịch sử, cập nhật % giai đoạn khi lô không đồng đều,
 * và giảm số cây khi cây chết/bị loại bỏ (alt 4a).
 */
public class LichSuSinhTruongDAO {

    /** Lịch sử sinh trưởng của tất cả lô (kèm trạng thái & số cây hiện tại). */
    public List<LichSuSinhTruong> getAllWithVuon() {
        List<LichSuSinhTruong> list = new ArrayList<>();
        String sql = "SELECT s.*, d.ten_don_vi AS ten_lo, g.ten_giong AS ten_giong, "
                + "v.trang_thai_sinh_truong AS tt, v.so_luong_cay AS so_cay "
                + "FROM LichSuSinhTruong s "
                + "JOIN VuonTrong v ON s.vuon_trong_id = v.id "
                + "JOIN DonViQuanLy d ON v.lo_dat_id = d.id "
                + "JOIN GiongSauRieng g ON v.giong_id = g.id "
                + "ORDER BY s.id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LichSuSinhTruong s = new LichSuSinhTruong();
                s.setId(rs.getInt("id"));
                s.setVuon_trong_id(rs.getInt("vuon_trong_id"));
                s.setNgay_cap_nhat(rs.getDate("ngay_cap_nhat"));
                s.setLoai_cap_nhat(rs.getNString("loai_cap_nhat"));
                int giam = rs.getInt("so_luong_cay_giam");
                s.setSo_luong_cay_giam(rs.wasNull() ? null : giam);
                s.setGhi_chu(rs.getNString("ghi_chu"));
                s.setNguoi_cap_nhat_id(rs.getInt("nguoi_cap_nhat_id"));
                s.setTen_lo_dat(rs.getNString("ten_lo"));
                s.setTen_giong(rs.getNString("ten_giong"));
                s.setTrang_thai_hien_tai(rs.getString("tt"));
                s.setSo_cay_con_lai(rs.getInt("so_cay"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Lấy % giai đoạn (nếu có) - dùng kết nối riêng, tránh mở nhiều ResultSet trên 1 connection
        for (LichSuSinhTruong s : list) {
            s.setTy_le_giai_doan(layTyLe(s.getId()));
        }
        return list;
    }

    private String layTyLe(int lichSuId) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT giai_doan, ty_le_phan_tram FROM ChiTietTyLeGiaiDoan WHERE lich_su_sinh_truong_id=?")) {
            ps.setInt(1, lichSuId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(rs.getNString("giai_doan")).append(": ").append(rs.getDouble("ty_le_phan_tram")).append("%");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return sb.toString();
    }

    /**
     * Ghi nhận cập nhật sinh trưởng.
     * - Nếu có giai_doan_moi: cập nhật trạng thái lô.
     * - Nếu có ty_le_giai_doan ("Giai đoạn:%,..."): ghi các dòng ChiTietTyLeGiaiDoan.
     * - Nếu có so_luong_cay_giam > 0: giảm tổng số cây của lô.
     */
    public boolean insert(LichSuSinhTruong s) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            int lichSuId;
            String sql = "INSERT INTO LichSuSinhTruong (vuon_trong_id, ngay_cap_nhat, loai_cap_nhat, so_luong_cay_giam, ghi_chu, nguoi_cap_nhat_id, ngay_tao) "
                    + "VALUES (?, GETDATE(), ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, s.getVuon_trong_id());
                ps.setNString(2, s.getLoai_cap_nhat() != null ? s.getLoai_cap_nhat() : "Chuyển giai đoạn");
                if (s.getSo_luong_cay_giam() != null && s.getSo_luong_cay_giam() > 0) ps.setInt(3, s.getSo_luong_cay_giam());
                else ps.setNull(3, java.sql.Types.INTEGER);
                ps.setNString(4, s.getGhi_chu() != null ? s.getGhi_chu() : "");
                ps.setInt(5, s.getNguoi_cap_nhat_id());
                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) { gk.next(); lichSuId = gk.getInt(1); }
            }

            // Chi tiết % giai đoạn (lô không đồng đều)
            if (s.getTy_le_giai_doan() != null && !s.getTy_le_giai_doan().trim().isEmpty()) {
                int ctId = layIdTyLe(conn);
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ChiTietTyLeGiaiDoan (id, lich_su_sinh_truong_id, giai_doan, ty_le_phan_tram) VALUES (?, ?, ?, ?)")) {
                    for (String phan : s.getTy_le_giai_doan().split(",")) {
                        String[] kv = phan.split(":");
                        if (kv.length < 2) continue;
                        String giaiDoan = kv[0].trim();
                        double tyLe = Double.parseDouble(kv[1].trim().replace("%", ""));
                        ps.setInt(1, ctId++);
                        ps.setInt(2, lichSuId);
                        ps.setNString(3, giaiDoan);
                        ps.setDouble(4, tyLe);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // Cập nhật trạng thái lô
            if (s.getGiai_doan_moi() != null && !s.getGiai_doan_moi().trim().isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE VuonTrong SET trang_thai_sinh_truong=?, ngay_cap_nhat=GETDATE() WHERE id=?")) {
                    ps.setNString(1, s.getGiai_doan_moi());
                    ps.setInt(2, s.getVuon_trong_id());
                    ps.executeUpdate();
                }
            }

            // Giảm số cây (alt 4a)
            if (s.getSo_luong_cay_giam() != null && s.getSo_luong_cay_giam() > 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE VuonTrong SET so_luong_cay = CASE WHEN so_luong_cay - ? < 0 THEN 0 ELSE so_luong_cay - ? END, "
                        + "mat_do_trong = CASE WHEN dien_tich > 0 THEN (CASE WHEN so_luong_cay - ? < 0 THEN 0 ELSE so_luong_cay - ? END)/dien_tich ELSE mat_do_trong END, "
                        + "ngay_cap_nhat=GETDATE() WHERE id=?")) {
                    int g = s.getSo_luong_cay_giam();
                    ps.setInt(1, g); ps.setInt(2, g); ps.setInt(3, g); ps.setInt(4, g);
                    ps.setInt(5, s.getVuon_trong_id());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            return false;
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    private int layIdTyLe(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT ISNULL(MAX(id),0)+1 FROM ChiTietTyLeGiaiDoan");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 1;
    }
}
