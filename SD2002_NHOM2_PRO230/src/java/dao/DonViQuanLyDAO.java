package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.DonViQuanLy;
import url.DBConnect;

public class DonViQuanLyDAO {
    
    public List<DonViQuanLy> getAll() {
        List<DonViQuanLy> list = new ArrayList<>();
        String sql = "SELECT * FROM DonViQuanLy WHERE trang_thai = N'Đang hoạt động' OR trang_thai = 'Active'"; // Tùy trạng thái DB của bạn
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                DonViQuanLy dv = new DonViQuanLy(
                    rs.getInt("id"),
                    rs.getInt("khu_vuc_id"),
                    rs.getString("loai_don_vi"),
                    rs.getString("ma_don_vi"),
                    rs.getNString("ten_don_vi"), // Dùng getNString cho tiếng Việt
                    rs.getDouble("dien_tich"),
                    rs.getDouble("suc_chua"),
                    rs.getNString("trang_thai"),
                    rs.getDate("ngay_tao"),
                    rs.getDate("ngay_cap_nhat")
                );
                list.add(dv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}