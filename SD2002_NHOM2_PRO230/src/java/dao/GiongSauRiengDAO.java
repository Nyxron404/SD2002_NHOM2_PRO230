package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.GiongSauRieng;
import url.DBConnect;

/**
 * UC-4.1 - Quản lý danh mục giống sầu riêng.
 */
public class GiongSauRiengDAO {

    public List<GiongSauRieng> getAll() {
        List<GiongSauRieng> list = new ArrayList<>();
        // Kèm số lô đất đang sử dụng giống để phục vụ kiểm tra xóa
        String sql = "SELECT g.*, (SELECT COUNT(*) FROM VuonTrong v WHERE v.giong_id = g.id) AS so_lo "
                + "FROM GiongSauRieng g ORDER BY g.id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GiongSauRieng g = new GiongSauRieng();
                g.setId(rs.getInt("id"));
                g.setTen_giong(rs.getNString("ten_giong"));
                g.setDac_diem(rs.getNString("dac_diem"));
                g.setThoi_gian_sinh_truong_thu_hoach(rs.getInt("thoi_gian_sinh_truong_thu_hoach"));
                g.setNang_suat_tham_khao(rs.getDouble("nang_suat_tham_khao"));
                g.setTrang_thai(rs.getNString("trang_thai"));
                g.setSo_lo_dang_dung(rs.getInt("so_lo"));
                list.add(g);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Kiểm tra trùng tên giống (bỏ qua chính bản ghi đang sửa). */
    public boolean tenTonTai(String tenGiong, int excludeId) {
        String sql = "SELECT COUNT(*) FROM GiongSauRieng WHERE ten_giong = ? AND id <> ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, tenGiong);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Số lô đất đang gán giống này (không cho xóa nếu > 0). */
    public int demLoDangDung(int giongId) {
        String sql = "SELECT COUNT(*) FROM VuonTrong WHERE giong_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, giongId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public boolean insert(GiongSauRieng g) {
        String sql = "INSERT INTO GiongSauRieng (ten_giong, dac_diem, thoi_gian_sinh_truong_thu_hoach, nang_suat_tham_khao, trang_thai, ngay_tao) "
                + "VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, g.getTen_giong());
            ps.setNString(2, g.getDac_diem());
            ps.setInt(3, g.getThoi_gian_sinh_truong_thu_hoach());
            ps.setDouble(4, g.getNang_suat_tham_khao());
            ps.setNString(5, g.getTrang_thai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean update(GiongSauRieng g) {
        String sql = "UPDATE GiongSauRieng SET ten_giong=?, dac_diem=?, thoi_gian_sinh_truong_thu_hoach=?, nang_suat_tham_khao=?, trang_thai=? WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, g.getTen_giong());
            ps.setNString(2, g.getDac_diem());
            ps.setInt(3, g.getThoi_gian_sinh_truong_thu_hoach());
            ps.setDouble(4, g.getNang_suat_tham_khao());
            ps.setNString(5, g.getTrang_thai());
            ps.setInt(6, g.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM GiongSauRieng WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
