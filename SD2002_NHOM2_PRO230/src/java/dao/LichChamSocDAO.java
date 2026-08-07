package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import models.LichChamSoc;
import url.DBConnect;

/**
 * UC-4.3 - Lập lịch chăm sóc định kỳ theo lô, tự sinh nhắc việc theo chu kỳ.
 */
public class LichChamSocDAO {

    public List<LichChamSoc> getAllWithLo() {
        List<LichChamSoc> list = new ArrayList<>();
        // Gộp danh sách lô đất áp dụng vào 1 chuỗi
        String sql = "SELECT l.*, "
                + "STUFF((SELECT ', ' + d.ten_don_vi FROM LichChamSoc_LoDat ld JOIN DonViQuanLy d ON ld.lo_dat_id=d.id "
                + "       WHERE ld.lich_cham_soc_id=l.id FOR XML PATH('')),1,2,'') AS ds_lo "
                + "FROM LichChamSoc l ORDER BY l.id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LichChamSoc l = new LichChamSoc();
                l.setId(rs.getInt("id"));
                l.setLoai_cong_viec(rs.getNString("loai_cong_viec"));
                l.setNgay_bat_dau(rs.getDate("ngay_bat_dau"));
                int ck = rs.getInt("chu_ky_ngay");
                l.setChu_ky_ngay(rs.wasNull() ? null : ck);
                l.setNgay_ket_thuc(rs.getDate("ngay_ket_thuc"));
                l.setMo_ta(rs.getNString("mo_ta"));
                l.setTrang_thai(rs.getNString("trang_thai"));
                l.setNguoi_tao_id(rs.getInt("nguoi_tao_id"));
                l.setDanh_sach_lo_ten(rs.getNString("ds_lo"));
                list.add(l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tạo lịch, gán cho nhiều lô (alt 3b) và tự sinh nhắc việc theo chu kỳ.
     * danh_sach_lo_id: chuỗi id lô cách nhau dấu phẩy.
     */
    public boolean insert(LichChamSoc l) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            int lichId;
            String sql = "INSERT INTO LichChamSoc (loai_cong_viec, ngay_bat_dau, chu_ky_ngay, ngay_ket_thuc, mo_ta, trang_thai, nguoi_tao_id, ngay_tao) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setNString(1, l.getLoai_cong_viec());
                ps.setDate(2, new Date(l.getNgay_bat_dau().getTime()));
                if (l.getChu_ky_ngay() != null && l.getChu_ky_ngay() > 0) ps.setInt(3, l.getChu_ky_ngay());
                else ps.setNull(3, java.sql.Types.INTEGER);
                if (l.getNgay_ket_thuc() != null) ps.setDate(4, new Date(l.getNgay_ket_thuc().getTime()));
                else ps.setNull(4, java.sql.Types.DATE);
                ps.setNString(5, l.getMo_ta());
                ps.setNString(6, l.getTrang_thai() != null ? l.getTrang_thai() : "Đang áp dụng");
                ps.setInt(7, l.getNguoi_tao_id());
                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    gk.next();
                    lichId = gk.getInt(1);
                }
            }

            // Gán cho từng lô + sinh nhắc việc
            if (l.getDanh_sach_lo_id() != null) {
                for (String s : l.getDanh_sach_lo_id().split(",")) {
                    s = s.trim();
                    if (s.isEmpty()) continue;
                    int loId = Integer.parseInt(s);
                    int lichLoId;
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO LichChamSoc_LoDat (lich_cham_soc_id, lo_dat_id) VALUES (?, ?)",
                            Statement.RETURN_GENERATED_KEYS)) {
                        ps.setInt(1, lichId);
                        ps.setInt(2, loId);
                        ps.executeUpdate();
                        try (ResultSet gk = ps.getGeneratedKeys()) { gk.next(); lichLoId = gk.getInt(1); }
                    }
                    sinhNhacViec(conn, lichLoId, l);
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            return false;
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    /** Sinh các bản ghi nhắc việc theo chu kỳ từ ngày bắt đầu đến ngày kết thúc. */
    private void sinhNhacViec(Connection conn, int lichLoId, LichChamSoc l) throws SQLException {
        List<java.util.Date> cacNgay = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(l.getNgay_bat_dau());
        cacNgay.add(c.getTime());

        Integer chuKy = l.getChu_ky_ngay();
        if (chuKy != null && chuKy > 0 && l.getNgay_ket_thuc() != null) {
            int guard = 0;
            while (guard++ < 200) {
                c.add(Calendar.DAY_OF_MONTH, chuKy);
                if (c.getTime().after(l.getNgay_ket_thuc())) break;
                cacNgay.add(c.getTime());
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO NhacViec (lich_lo_dat_id, ngay_nhac, trang_thai) VALUES (?, ?, N'Chờ xử lý')")) {
            for (java.util.Date d : cacNgay) {
                ps.setInt(1, lichLoId);
                ps.setDate(2, new Date(d.getTime()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public boolean update(LichChamSoc l) {
        String sql = "UPDATE LichChamSoc SET loai_cong_viec=?, ngay_bat_dau=?, chu_ky_ngay=?, ngay_ket_thuc=?, mo_ta=?, trang_thai=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, l.getLoai_cong_viec());
            ps.setDate(2, new Date(l.getNgay_bat_dau().getTime()));
            if (l.getChu_ky_ngay() != null && l.getChu_ky_ngay() > 0) ps.setInt(3, l.getChu_ky_ngay());
            else ps.setNull(3, java.sql.Types.INTEGER);
            if (l.getNgay_ket_thuc() != null) ps.setDate(4, new Date(l.getNgay_ket_thuc().getTime()));
            else ps.setNull(4, java.sql.Types.DATE);
            ps.setNString(5, l.getMo_ta());
            ps.setNString(6, l.getTrang_thai());
            ps.setInt(7, l.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        // ON DELETE CASCADE trên LichChamSoc_LoDat -> NhacViec sẽ tự dọn
        String sql = "DELETE FROM LichChamSoc WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
