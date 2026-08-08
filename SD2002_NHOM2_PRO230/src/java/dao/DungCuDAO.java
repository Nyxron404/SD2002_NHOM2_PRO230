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
    
    public boolean updateHaoHut(int id, double soLuongMat, double chiPhiThietHai, String lyDo) {
        String sql = "UPDATE DungCu SET ton_kho_hien_tai = ton_kho_hien_tai - ?, ngay_cap_nhat = GETDATE() WHERE id = ?";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, soLuongMat);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Đã cập nhật hàm insert chuẩn
    public boolean insert(DungCu dc) {
        String sql = "INSERT INTO DungCu (ma_dung_cu, ten_dung_cu, don_vi_tinh, dien_tich_chiem_dung, vi_tri_luu_tru_id, gia_binh_quan, ton_kho_toi_thieu, ton_kho_hien_tai, trang_thai, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, 0, N'Sẵn sàng', GETDATE())";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dc.getMa_dung_cu());
            ps.setNString(2, dc.getTen_dung_cu());
            ps.setString(3, dc.getDon_vi_tinh());
            ps.setDouble(4, dc.getDien_tich_chiem_dung());
            ps.setInt(5, dc.getVi_tri_luu_tru_id());
            ps.setDouble(6, dc.getGia_binh_quan());
            ps.setDouble(7, dc.getTon_kho_toi_thieu());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean update(DungCu dc) {
        String sql = "UPDATE DungCu SET ten_dung_cu=?, don_vi_tinh=?, gia_binh_quan=?, ton_kho_toi_thieu=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, dc.getTen_dung_cu());
            ps.setString(2, dc.getDon_vi_tinh());
            ps.setDouble(3, dc.getGia_binh_quan());
            ps.setDouble(4, dc.getTon_kho_toi_thieu());
            ps.setInt(5, dc.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM DungCu WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}