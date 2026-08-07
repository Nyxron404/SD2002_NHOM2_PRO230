package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.VaiTro;
import url.DBConnect;

public class VaiTroDAO {

    public List<VaiTro> getAll() {
        List<VaiTro> list = new ArrayList<>();
        String sql = "SELECT * FROM VaiTro ORDER BY id";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new VaiTro(rs.getInt("id"), rs.getString("ten_vai_tro"), rs.getNString("mo_ta")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Lấy id vai trò Admin (mặc định hệ thống hiện chỉ có 1 vai trò Admin). */
    public int getAdminId() {
        String sql = "SELECT TOP 1 id FROM VaiTro WHERE ten_vai_tro = 'Admin' ORDER BY id";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1; // fallback
    }
}
