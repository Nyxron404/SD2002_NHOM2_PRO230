package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.ThietBi;
import url.DBConnect;

public class ThietBiDAO {

    public List<ThietBi> getAll() {
        List<ThietBi> list = new ArrayList<>();
        // Câu truy vấn gộp cả logic của bạn (LichBaoTri) và logic của nhóm trưởng (ChiTietPhieuNhapThietBi)
        String sql = "SELECT t.*, "
                   + "ISNULL(ct.don_gia, 0) AS nguyen_gia, "
                   + "(SELECT TOP 1 ngay_bao_tri_du_kien FROM LichBaoTri l WHERE l.ma_thiet_bi = t.id AND l.trang_thai IN (0, 1) ORDER BY id DESC) as ngay_du_kien "
                   + "FROM ThietBi t "
                   + "LEFT JOIN ChiTietPhieuNhapThietBi ct ON t.id = ct.thiet_bi_id "
                   + "ORDER BY t.id DESC";
        
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ThietBi tb = new ThietBi();
                // Data của bạn
                tb.setId(rs.getInt("id"));
                tb.setMa_thiet_bi(rs.getString("ma_thiet_bi"));
                tb.setTen_thiet_bi(rs.getNString("ten_thiet_bi"));
                tb.setDien_tich_cat_tru(rs.getDouble("dien_tich_cat_tru"));
                tb.setThoi_gian_khau_hao_nam(rs.getInt("thoi_gian_khau_hao_nam"));
                tb.setMo_ta(rs.getNString("mo_ta"));
                tb.setVi_tri_luu_tru_id(rs.getInt("vi_tri_luu_tru_id"));
                tb.setTrang_thai(rs.getString("trang_thai"));
                tb.setNgay_tao(rs.getTimestamp("ngay_tao"));
                tb.setNgay_bao_tri_du_kien(rs.getDate("ngay_du_kien")); 
                
                // Data của nhóm trưởng
                tb.setNguyen_gia(rs.getDouble("nguyen_gia"));
                
                list.add(tb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ThietBi tb) {
        String sql = "INSERT INTO ThietBi (ma_thiet_bi, ten_thiet_bi, dien_tich_cat_tru, thoi_gian_khau_hao_nam, mo_ta, vi_tri_luu_tru_id, trang_thai, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tb.getMa_thiet_bi());
            ps.setNString(2, tb.getTen_thiet_bi());
            ps.setDouble(3, tb.getDien_tich_cat_tru());
            ps.setInt(4, tb.getThoi_gian_khau_hao_nam());
            ps.setNString(5, tb.getMo_ta());

            if (tb.getVi_tri_luu_tru_id() > 0) {
                ps.setInt(6, tb.getVi_tri_luu_tru_id());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            ps.setString(7, tb.getTrang_thai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(ThietBi tb) {
        String sql = "UPDATE ThietBi SET ten_thiet_bi=?, dien_tich_cat_tru=?, thoi_gian_khau_hao_nam=?, mo_ta=?, vi_tri_luu_tru_id=?, trang_thai=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, tb.getTen_thiet_bi());
            ps.setDouble(2, tb.getDien_tich_cat_tru());
            ps.setInt(3, tb.getThoi_gian_khau_hao_nam());
            ps.setNString(4, tb.getMo_ta());

            if (tb.getVi_tri_luu_tru_id() > 0) {
                ps.setInt(5, tb.getVi_tri_luu_tru_id());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.setString(6, tb.getTrang_thai());
            ps.setInt(7, tb.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM ThietBi WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThai(int id, String trangThai) {
        String sql = "UPDATE ThietBi SET trang_thai=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isMaThietBiExist(String maThietBi) {
        String sql = "SELECT 1 FROM ThietBi WHERE ma_thiet_bi = ?";
        try (Connection conn = DBConnect.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maThietBi);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}