package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import models.TrangTrai;
import url.DBConnect;

public class TrangTraiDAO {

    private TrangTrai mapRow(ResultSet rs) throws Exception {
        int namThanhLapRaw = rs.getInt("nam_thanh_lap");
        Integer namThanhLap = rs.wasNull() ? null : namThanhLapRaw;
        return new TrangTrai(
                rs.getInt("id"),
                rs.getNString("ten_trang_trai"),
                rs.getString("ma_so_thue"),
                rs.getString("ma_so_vung_trong"),
                rs.getString("logo"),
                rs.getNString("nguoi_dai_dien"),
                rs.getNString("chuc_vu_dai_dien"),
                rs.getNString("dia_chi"),
                rs.getString("email"),
                rs.getString("so_dien_thoai"),
                rs.getString("toa_do_gps"),
                rs.getNString("nong_san_chu_luc"),
                rs.getNString("linh_vuc_hoat_dong"),
                rs.getDouble("dien_tich_tong"),
                rs.getNString("mo_ta"),
                namThanhLap,
                rs.getNString("chung_nhan"),
                rs.getNString("trang_thai"),
                rs.getString("anh_ban_do"),
                rs.getTimestamp("ngay_tao"),
                rs.getTimestamp("ngay_cap_nhat")
        );
    }

    // Hệ thống chỉ quản lý MỘT trang trại duy nhất -> luôn lấy bản ghi đầu tiên
    public TrangTrai getTrangTrai() {
        String sql = "SELECT TOP 1 * FROM TrangTrai ORDER BY id ASC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(TrangTrai tt) {
        String sql = "UPDATE TrangTrai SET ten_trang_trai = ?, ma_so_thue = ?, ma_so_vung_trong = ?, "
                + "logo = ?, nguoi_dai_dien = ?, chuc_vu_dai_dien = ?, dia_chi = ?, email = ?, "
                + "so_dien_thoai = ?, toa_do_gps = ?, nong_san_chu_luc = ?, linh_vuc_hoat_dong = ?, "
                + "dien_tich_tong = ?, mo_ta = ?, nam_thanh_lap = ?, chung_nhan = ?, trang_thai = ?, "
                + "anh_ban_do = ?, ngay_cap_nhat = GETDATE() WHERE id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, tt);
            ps.setInt(19, tt.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Dùng khi bảng TrangTrai chưa có bản ghi nào (khởi tạo lần đầu)
    public boolean insert(TrangTrai tt) {
        String sql = "INSERT INTO TrangTrai (ten_trang_trai, ma_so_thue, ma_so_vung_trong, logo, "
                + "nguoi_dai_dien, chuc_vu_dai_dien, dia_chi, email, so_dien_thoai, toa_do_gps, "
                + "nong_san_chu_luc, linh_vuc_hoat_dong, dien_tich_tong, mo_ta, nam_thanh_lap, "
                + "chung_nhan, trang_thai, anh_ban_do, ngay_tao, ngay_cap_nhat) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, tt);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void bindParams(PreparedStatement ps, TrangTrai tt) throws Exception {
        ps.setNString(1, tt.getTen_trang_trai());
        ps.setString(2, tt.getMa_so_thue());
        ps.setString(3, tt.getMa_so_vung_trong());
        ps.setString(4, tt.getLogo());
        ps.setNString(5, tt.getNguoi_dai_dien());
        ps.setNString(6, tt.getChuc_vu_dai_dien());
        ps.setNString(7, tt.getDia_chi());
        ps.setString(8, tt.getEmail());
        ps.setString(9, tt.getSo_dien_thoai());
        ps.setString(10, tt.getToa_do_gps());
        ps.setNString(11, tt.getNong_san_chu_luc());
        ps.setNString(12, tt.getLinh_vuc_hoat_dong());
        ps.setDouble(13, tt.getDien_tich_tong());
        ps.setNString(14, tt.getMo_ta());
        if (tt.getNam_thanh_lap() == null) ps.setNull(15, java.sql.Types.INTEGER);
        else ps.setInt(15, tt.getNam_thanh_lap());
        ps.setNString(16, tt.getChung_nhan());
        ps.setNString(17, tt.getTrang_thai());
        ps.setString(18, tt.getAnh_ban_do());
    }
}