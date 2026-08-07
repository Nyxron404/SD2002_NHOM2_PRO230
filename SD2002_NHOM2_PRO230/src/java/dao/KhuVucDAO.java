package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.KhuVuc;
import url.DBConnect;

public class KhuVucDAO {

    private KhuVuc mapRow(ResultSet rs) throws Exception {
        return new KhuVuc(
                rs.getInt("id"),
                rs.getInt("trang_trai_id"),
                rs.getNString("ten_khu_vuc"),
                rs.getNString("loai_khu_vuc"),
                rs.getDouble("dien_tich_khai_thac"),
                rs.getDouble("dien_tich_chiem_dung"),
                rs.getDouble("dien_tich_khu_vuc"),
                rs.getNString("mo_ta"),
                rs.getTimestamp("ngay_tao"),
                rs.getTimestamp("ngay_cap_nhat")
        );
    }

    public List<KhuVuc> getAllByTrangTrai(int trangTraiId) {
        List<KhuVuc> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuVuc WHERE trang_trai_id = ? ORDER BY id ASC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trangTraiId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public KhuVuc getById(int id) {
        String sql = "SELECT * FROM KhuVuc WHERE id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tổng diện tích các khu vực đã phân chia của 1 trang trại (dùng để kiểm tra không vượt diện tích trang trại)
    public double getTongDienTichDaPhanChia(int trangTraiId, int excludeKhuVucId) {
        String sql = "SELECT ISNULL(SUM(dien_tich_khu_vuc),0) AS tong FROM KhuVuc WHERE trang_trai_id = ? AND id <> ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trangTraiId);
            ps.setInt(2, excludeKhuVucId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("tong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int insert(KhuVuc kv) {
        String sql = "INSERT INTO KhuVuc (trang_trai_id, ten_khu_vuc, loai_khu_vuc, dien_tich_khai_thac, dien_tich_chiem_dung, dien_tich_khu_vuc, mo_ta, ngay_tao, ngay_cap_nhat) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, kv.getTrang_trai_id());
            ps.setNString(2, kv.getTen_khu_vuc());
            ps.setNString(3, kv.getLoai_khu_vuc());
            ps.setDouble(4, kv.getDien_tich_khai_thac());
            ps.setDouble(5, kv.getDien_tich_chiem_dung());
            ps.setDouble(6, kv.getDien_tich_khu_vuc());
            ps.setNString(7, kv.getMo_ta());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(KhuVuc kv) {
        String sql = "UPDATE KhuVuc SET ten_khu_vuc = ?, loai_khu_vuc = ?, dien_tich_khai_thac = ?, dien_tich_chiem_dung = ?, dien_tich_khu_vuc = ?, mo_ta = ?, ngay_cap_nhat = GETDATE() WHERE id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, kv.getTen_khu_vuc());
            ps.setNString(2, kv.getLoai_khu_vuc());
            ps.setDouble(3, kv.getDien_tich_khai_thac());
            ps.setDouble(4, kv.getDien_tich_chiem_dung());
            ps.setDouble(5, kv.getDien_tich_khu_vuc());
            ps.setNString(6, kv.getMo_ta());
            ps.setInt(7, kv.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM KhuVuc WHERE id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
