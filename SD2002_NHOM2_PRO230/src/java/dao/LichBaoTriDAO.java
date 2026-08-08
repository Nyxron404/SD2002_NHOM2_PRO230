package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import models.LichBaoTri;
import url.DBConnect;

public class LichBaoTriDAO {

    public boolean insert(LichBaoTri lbt) {
        String sql = "INSERT INTO LichBaoTri (ma_thiet_bi, ngay_bao_tri_du_kien, loai_bao_tri, noi_dung_bao_tri, trang_thai) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lbt.getMa_thiet_bi());
            ps.setDate(2, new java.sql.Date(lbt.getNgay_bao_tri_du_kien().getTime()));
            ps.setNString(3, lbt.getLoai_bao_tri());
            ps.setNString(4, lbt.getNoi_dung_bao_tri());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
