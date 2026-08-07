package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.DungCu;
import url.DBConnect;

public class DungCuDAO {
    public List<DungCu> getAll() {
        List<DungCu> list = new ArrayList<>();
        String sql = "SELECT * FROM DungCu";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DungCu dc = new DungCu();
                dc.setId(rs.getInt("id"));
                dc.setMa_dung_cu(rs.getString("ma_dung_cu"));
                dc.setTen_dung_cu(rs.getString("ten_dung_cu"));
                dc.setDon_vi_tinh(rs.getString("don_vi_tinh"));
                dc.setDien_tich_chiem_dung(rs.getDouble("dien_tich_chiem_dung"));
                dc.setVi_tri_luu_tru_id(rs.getInt("vi_tri_luu_tru_id"));
                dc.setGia_binh_quan(rs.getDouble("gia_binh_quan"));
                dc.setTon_kho_hien_tai(rs.getDouble("ton_kho_hien_tai"));
                dc.setTon_kho_toi_thieu(rs.getDouble("ton_kho_toi_thieu"));
                dc.setTrang_thai(rs.getString("trang_thai"));
                list.add(dc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}