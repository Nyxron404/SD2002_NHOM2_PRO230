package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.ThietBi;
import url.DBConnect;

/**
 * Truy vấn thiết bị + nguyên giá phục vụ tính hao mòn trong nhật ký chăm sóc (UC-4.4).
 */
public class ThietBiDAO {

    public List<ThietBi> getAll() {
        List<ThietBi> list = new ArrayList<>();
        String sql = "SELECT tb.id, tb.ma_thiet_bi, tb.ten_thiet_bi, tb.thoi_gian_khau_hao_nam, tb.trang_thai, "
                + "ISNULL(ct.don_gia, 0) AS nguyen_gia "
                + "FROM ThietBi tb LEFT JOIN ChiTietPhieuNhapThietBi ct ON tb.id = ct.thiet_bi_id "
                + "ORDER BY tb.id ASC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ThietBi tb = new ThietBi();
                tb.setId(rs.getInt("id"));
                tb.setMa_thiet_bi(rs.getString("ma_thiet_bi"));
                tb.setTen_thiet_bi(rs.getNString("ten_thiet_bi"));
                tb.setThoi_gian_khau_hao_nam(rs.getInt("thoi_gian_khau_hao_nam"));
                tb.setTrang_thai(rs.getNString("trang_thai"));
                tb.setNguyen_gia(rs.getDouble("nguyen_gia"));
                list.add(tb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
