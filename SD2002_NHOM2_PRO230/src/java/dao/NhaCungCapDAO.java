package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.NhaCungCap;
import url.DBConnect; // Import class DBConnect của bạn

public class NhaCungCapDAO {
    
    public List<NhaCungCap> getAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT id, ten_ncc, ma_so_thue FROM NhaCungCap";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                    rs.getInt("id"),
                    rs.getString("ten_ncc"),
                    rs.getString("ma_so_thue")
                );
                list.add(ncc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}