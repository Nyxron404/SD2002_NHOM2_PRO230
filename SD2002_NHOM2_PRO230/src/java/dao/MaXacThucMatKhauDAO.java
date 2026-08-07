package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.MaXacThucMatKhau;
import url.DBConnect;

public class MaXacThucMatKhauDAO {

    /**
     * Lưu mã xác thực mới cho tài khoản. Bảng MaXacThucMatKhau không dùng IDENTITY
     * nên phải tự sinh id = MAX(id)+1.
     */
    public boolean taoMa(int taiKhoanId, String maXacThuc, int soPhutHieuLuc) {
        String sqlNextId = "SELECT ISNULL(MAX(id), 0) + 1 FROM MaXacThucMatKhau";
        String sqlInsert = "INSERT INTO MaXacThucMatKhau (id, tai_khoan_id, ma_xac_thuc, thoi_gian_tao, thoi_gian_het_han, da_su_dung) "
                + "VALUES (?, ?, ?, GETDATE(), DATEADD(MINUTE, ?, GETDATE()), 0)";
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            int nextId;
            try (PreparedStatement ps = conn.prepareStatement(sqlNextId);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                nextId = rs.getInt(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, nextId);
                ps.setInt(2, taiKhoanId);
                ps.setString(3, maXacThuc);
                ps.setInt(4, soPhutHieuLuc);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /**
     * Kiểm tra mã xác thực còn hiệu lực: đúng tài khoản, đúng mã, chưa dùng, chưa hết hạn.
     * Trả về id bản ghi mã nếu hợp lệ, ngược lại -1.
     */
    public int kiemTraMaHopLe(int taiKhoanId, String maXacThuc) {
        String sql = "SELECT TOP 1 id FROM MaXacThucMatKhau "
                + "WHERE tai_khoan_id = ? AND ma_xac_thuc = ? AND da_su_dung = 0 AND thoi_gian_het_han >= GETDATE() "
                + "ORDER BY id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taiKhoanId);
            ps.setString(2, maXacThuc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Đánh dấu mã đã sử dụng. */
    public boolean danhDauDaSuDung(int maId) {
        String sql = "UPDATE MaXacThucMatKhau SET da_su_dung = 1 WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
