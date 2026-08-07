package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import url.DBConnect;

public class NguoiDungDAO {

    /** Kiểm tra email đã tồn tại chưa (email phải là duy nhất - UC-1.1). */
    public boolean emailTonTai(String email) {
        String sql = "SELECT COUNT(*) FROM NguoiDung WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Kiểm tra email đã tồn tại ở người dùng KHÁC (phục vụ cập nhật - UC-1.2). */
    public boolean emailTonTaiKhac(String email, int nguoiDungId) {
        String sql = "SELECT COUNT(*) FROM NguoiDung WHERE email = ? AND id <> ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, nguoiDungId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
