package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.DonViQuanLy;
import url.DBConnect;

public class DonViQuanLyDAO {

    private DonViQuanLy mapRow(ResultSet rs) throws Exception {
        return new DonViQuanLy(
                rs.getInt("id"),
                rs.getInt("khu_vuc_id"),
                rs.getNString("loai_don_vi"),
                rs.getString("ma_don_vi"),
                rs.getNString("ten_don_vi"),
                rs.getDouble("dien_tich"),
                rs.getDouble("suc_chua"),
                rs.getNString("trang_thai"),
                rs.getTimestamp("ngay_tao"),
                rs.getTimestamp("ngay_cap_nhat")
        );
    }

    public List<DonViQuanLy> getAll() {
        List<DonViQuanLy> list = new ArrayList<>();
        String sql = "SELECT * FROM DonViQuanLy WHERE trang_thai = N'Đang hoạt động' OR trang_thai = 'Active'";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Toàn bộ đơn vị (mọi trạng thái) thuộc 1 khu vực - dùng cho màn hình quản lý chi tiết khu vực
    public List<DonViQuanLy> getByKhuVuc(int khuVucId) {
        List<DonViQuanLy> list = new ArrayList<>();
        String sql = "SELECT * FROM DonViQuanLy WHERE khu_vuc_id = ? ORDER BY id ASC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, khuVucId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public DonViQuanLy getById(int id) {
        String sql = "SELECT * FROM DonViQuanLy WHERE id = ?";
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

    // Tổng diện tích các đơn vị đã phân chia trong 1 khu vực (dùng để kiểm tra không vượt diện tích khai thác)
    public double getTongDienTichByKhuVuc(int khuVucId, int excludeId) {
        String sql = "SELECT ISNULL(SUM(dien_tich),0) AS tong FROM DonViQuanLy WHERE khu_vuc_id = ? AND id <> ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, khuVucId);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("tong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int insert(DonViQuanLy dv) {
        String sql = "INSERT INTO DonViQuanLy (khu_vuc_id, loai_don_vi, ma_don_vi, ten_don_vi, dien_tich, suc_chua, trang_thai, ngay_tao, ngay_cap_nhat) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, dv.getKhu_vuc_id());
            ps.setNString(2, dv.getLoai_don_vi());
            ps.setString(3, dv.getMa_don_vi());
            ps.setNString(4, dv.getTen_don_vi());
            ps.setDouble(5, dv.getDien_tich());
            ps.setDouble(6, dv.getSuc_chua());
            ps.setNString(7, dv.getTrang_thai());
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

    public boolean update(DonViQuanLy dv) {
        String sql = "UPDATE DonViQuanLy SET loai_don_vi = ?, ma_don_vi = ?, ten_don_vi = ?, dien_tich = ?, suc_chua = ?, trang_thai = ?, ngay_cap_nhat = GETDATE() WHERE id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, dv.getLoai_don_vi());
            ps.setString(2, dv.getMa_don_vi());
            ps.setNString(3, dv.getTen_don_vi());
            ps.setDouble(4, dv.getDien_tich());
            ps.setDouble(5, dv.getSuc_chua());
            ps.setNString(6, dv.getTrang_thai());
            ps.setInt(7, dv.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM DonViQuanLy WHERE id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
