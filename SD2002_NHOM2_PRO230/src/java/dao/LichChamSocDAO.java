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
 * UC-4.3 - Lập lịch chăm sóc định kỳ theo lô, tự sinh nhắc việc (bảng NhacViec).
 *
 * Quy ước nhập liệu:
 *   - chu_ky_ngay = 0 hoặc để trống  -> lặp HẰNG NGÀY (lưu xuống DB là 1).
 *   - ngay_ket_thuc để trống         -> lịch VĨNH VIỄN. Hệ thống chỉ sinh trước
 *     {@link #SO_NGAY_SINH_TRUOC} ngày để không làm phình bảng NhacViec; mỗi lần
 *     mở màn hình canh tác sẽ tự sinh bù cho khoảng thời gian tiếp theo.
 *
 * Sửa lịch sẽ sinh lại toàn bộ nhắc việc CHƯA thực hiện; các nhắc việc đã gắn
 * nhật ký chăm sóc được giữ nguyên để không mất dấu vết chi phí.
 */
public class LichChamSocDAO {

    /** Số ngày sinh nhắc việc trước cho lịch không có ngày kết thúc. */
    public static final int SO_NGAY_SINH_TRUOC = 90;

    /** Chặn trên số nhắc việc sinh ra trong một lần, tránh vòng lặp vô hạn. */
    private static final int GIOI_HAN_NHAC_VIEC = 500;

    // ===================================================================
    // ĐỌC
    // ===================================================================

    public List<LichChamSoc> getAllWithLo() {
        List<LichChamSoc> list = new ArrayList<>();
        String sql = "SELECT l.*, "
                + "STUFF((SELECT ', ' + d.ten_don_vi FROM LichChamSoc_LoDat ld "
                + "       JOIN DonViQuanLy d ON ld.lo_dat_id = d.id "
                + "       WHERE ld.lich_cham_soc_id = l.id FOR XML PATH('')),1,2,'') AS ds_lo, "
                + "STUFF((SELECT ',' + CAST(ld.lo_dat_id AS varchar(10)) FROM LichChamSoc_LoDat ld "
                + "       WHERE ld.lich_cham_soc_id = l.id FOR XML PATH('')),1,1,'') AS ds_lo_id, "
                + "(SELECT COUNT(*) FROM NhacViec nv JOIN LichChamSoc_LoDat ld2 ON nv.lich_lo_dat_id = ld2.id "
                + " WHERE ld2.lich_cham_soc_id = l.id) AS so_nhac_viec, "
                + "(SELECT COUNT(*) FROM NhacViec nv JOIN LichChamSoc_LoDat ld3 ON nv.lich_lo_dat_id = ld3.id "
                + " WHERE ld3.lich_cham_soc_id = l.id AND nv.nhat_ky_cham_soc_id IS NOT NULL) AS so_da_lam "
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
                l.setDanh_sach_lo_id(rs.getString("ds_lo_id"));
                list.add(l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public LichChamSoc getById(int id) {
        for (LichChamSoc l : getAllWithLo()) if (l.getId() == id) return l;
        return null;
    }

    // ===================================================================
    // THÊM / SỬA / XÓA
    // ===================================================================

    public boolean insert(LichChamSoc l) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            int lichId;
            String sql = "INSERT INTO LichChamSoc (loai_cong_viec, ngay_bat_dau, chu_ky_ngay, ngay_ket_thuc, "
                       + "mo_ta, trang_thai, nguoi_tao_id, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ganThamSo(ps, l);
                ps.setInt(7, NhatKyChamSocDAO.chuanHoaTaiKhoan(conn, l.getNguoi_tao_id()));
                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) { gk.next(); lichId = gk.getInt(1); }
            }

            ganLoVaSinhNhacViec(conn, lichId, l);

            conn.commit();
            l.setId(lichId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            rollback(conn);
            return false;
        } finally {
            dong(conn);
        }
    }

    /**
     * Cập nhật lịch: sửa thông tin chung, gán lại danh sách lô và sinh lại nhắc việc.
     * Các nhắc việc ĐÃ gắn nhật ký được giữ nguyên.
     */
    public boolean update(LichChamSoc l) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            String sql = "UPDATE LichChamSoc SET loai_cong_viec=?, ngay_bat_dau=?, chu_ky_ngay=?, "
                       + "ngay_ket_thuc=?, mo_ta=?, trang_thai=?, ngay_cap_nhat=GETDATE() WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ganThamSo(ps, l);
                ps.setInt(7, l.getId());
                if (ps.executeUpdate() == 0) throw new SQLException("Không tìm thấy lịch cần sửa.");
            }

            // Xóa nhắc việc chưa thực hiện của lịch này
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM NhacViec WHERE nhat_ky_cham_soc_id IS NULL AND lich_lo_dat_id IN "
                  + "(SELECT id FROM LichChamSoc_LoDat WHERE lich_cham_soc_id = ?)")) {
                ps.setInt(1, l.getId());
                ps.executeUpdate();
            }
            // Gỡ các lô không còn được chọn (chỉ gỡ được lô chưa phát sinh nhật ký)
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM LichChamSoc_LoDat WHERE lich_cham_soc_id = ? "
                  + "AND id NOT IN (SELECT lich_lo_dat_id FROM NhacViec)")) {
                ps.setInt(1, l.getId());
                ps.executeUpdate();
            }

            ganLoVaSinhNhacViec(conn, l.getId(), l);

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            rollback(conn);
            return false;
        } finally {
            dong(conn);
        }
    }

    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            // Không cho xóa nếu đã có nhật ký gắn vào nhắc việc của lịch này
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM NhacViec nv JOIN LichChamSoc_LoDat ld ON nv.lich_lo_dat_id = ld.id "
                  + "WHERE ld.lich_cham_soc_id = ? AND nv.nhat_ky_cham_soc_id IS NOT NULL")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0)
                        throw new SQLException("Lịch đã phát sinh nhật ký chăm sóc, không thể xóa.");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM NhacViec WHERE lich_lo_dat_id IN "
                  + "(SELECT id FROM LichChamSoc_LoDat WHERE lich_cham_soc_id = ?)")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM LichChamSoc_LoDat WHERE lich_cham_soc_id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            int r;
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM LichChamSoc WHERE id = ?")) {
                ps.setInt(1, id);
                r = ps.executeUpdate();
            }

            conn.commit();
            return r > 0;
        } catch (Exception e) {
            e.printStackTrace();
            rollback(conn);
            return false;
        } finally {
            dong(conn);
        }
    }

    // ===================================================================
    // SINH NHẮC VIỆC
    // ===================================================================

    /** Gán lịch cho các lô được chọn và sinh nhắc việc cho từng lô. */
    private void ganLoVaSinhNhacViec(Connection conn, int lichId, LichChamSoc l) throws SQLException {
        if (l.getDanh_sach_lo_id() == null || l.getDanh_sach_lo_id().isBlank()) return;

        for (String s : l.getDanh_sach_lo_id().split(",")) {
            s = s.trim();
            if (s.isEmpty()) continue;
            int loId;
            try { loId = Integer.parseInt(s); } catch (NumberFormatException e) { continue; }

            // Tái sử dụng bản ghi nối nếu đã tồn tại (trường hợp sửa lịch)
            Integer lichLoId = timLichLoDat(conn, lichId, loId);
            if (lichLoId == null) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO LichChamSoc_LoDat (lich_cham_soc_id, lo_dat_id) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, lichId);
                    ps.setInt(2, loId);
                    ps.executeUpdate();
                    try (ResultSet gk = ps.getGeneratedKeys()) { gk.next(); lichLoId = gk.getInt(1); }
                }
            }
            sinhNhacViec(conn, lichLoId, l);
        }
    }

    private Integer timLichLoDat(Connection conn, int lichId, int loId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM LichChamSoc_LoDat WHERE lich_cham_soc_id = ? AND lo_dat_id = ?")) {
            ps.setInt(1, lichId);
            ps.setInt(2, loId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : null; }
        }
    }

    /**
     * Sinh các mốc nhắc việc từ ngày bắt đầu theo chu kỳ.
     *   - chu kỳ null/0 -> hằng ngày (1 ngày).
     *   - không có ngày kết thúc -> sinh tới ngày hiện tại + SO_NGAY_SINH_TRUOC.
     * Bỏ qua ngày đã tồn tại nhắc việc để không tạo trùng khi sửa lịch.
     */
    private void sinhNhacViec(Connection conn, int lichLoId, LichChamSoc l) throws SQLException {
        int chuKy = (l.getChu_ky_ngay() == null || l.getChu_ky_ngay() <= 0) ? 1 : l.getChu_ky_ngay();

        Calendar moc = Calendar.getInstance();
        moc.setTime(l.getNgay_bat_dau());
        xoaGio(moc);

        Calendar gioiHan = Calendar.getInstance();
        xoaGio(gioiHan);
        if (l.getNgay_ket_thuc() != null) {
            gioiHan.setTime(l.getNgay_ket_thuc());
            xoaGio(gioiHan);
        } else {
            gioiHan.add(Calendar.DAY_OF_MONTH, SO_NGAY_SINH_TRUOC);
        }

        List<java.util.Date> cacNgay = new ArrayList<>();
        int dem = 0;
        while (!moc.getTime().after(gioiHan.getTime()) && dem < GIOI_HAN_NHAC_VIEC) {
            cacNgay.add(moc.getTime());
            moc.add(Calendar.DAY_OF_MONTH, chuKy);
            dem++;
        }
        if (cacNgay.isEmpty()) return;

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO NhacViec (lich_lo_dat_id, ngay_nhac, trang_thai) "
              + "SELECT ?, ?, N'Chờ xử lý' WHERE NOT EXISTS "
              + "(SELECT 1 FROM NhacViec WHERE lich_lo_dat_id = ? AND ngay_nhac = ?)")) {
            for (java.util.Date d : cacNgay) {
                Date sqlDate = new Date(d.getTime());
                ps.setInt(1, lichLoId);
                ps.setDate(2, sqlDate);
                ps.setInt(3, lichLoId);
                ps.setDate(4, sqlDate);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Sinh bù nhắc việc cho các lịch vĩnh viễn (không có ngày kết thúc).
     * Gọi mỗi lần mở màn hình canh tác để danh sách nhắc việc luôn có sẵn
     * cho SO_NGAY_SINH_TRUOC ngày tiếp theo.
     */
    public void sinhBuNhacViecVinhVien() {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return;
            conn.setAutoCommit(false);

            List<LichChamSoc> canSinh = new ArrayList<>();
            String sql = "SELECT * FROM LichChamSoc WHERE ngay_ket_thuc IS NULL AND trang_thai = N'Đang áp dụng'";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LichChamSoc l = new LichChamSoc();
                    l.setId(rs.getInt("id"));
                    l.setNgay_bat_dau(rs.getDate("ngay_bat_dau"));
                    int ck = rs.getInt("chu_ky_ngay");
                    l.setChu_ky_ngay(rs.wasNull() ? null : ck);
                    canSinh.add(l);
                }
            }

            for (LichChamSoc l : canSinh) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id FROM LichChamSoc_LoDat WHERE lich_cham_soc_id = ?")) {
                    ps.setInt(1, l.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) sinhNhacViec(conn, rs.getInt(1), l);
                    }
                }
            }

            // Đánh dấu quá hạn cho các nhắc việc chưa làm
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE NhacViec SET trang_thai = N'Quá hạn' "
                  + "WHERE nhat_ky_cham_soc_id IS NULL AND ngay_nhac < CAST(GETDATE() AS DATE) "
                  + "AND trang_thai = N'Chờ xử lý'")) {
                ps.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            rollback(conn);
        } finally {
            dong(conn);
        }
    }

    // ===================================================================

    private void ganThamSo(PreparedStatement ps, LichChamSoc l) throws SQLException {
        ps.setNString(1, l.getLoai_cong_viec());
        ps.setDate(2, new Date(l.getNgay_bat_dau().getTime()));
        int chuKy = (l.getChu_ky_ngay() == null || l.getChu_ky_ngay() <= 0) ? 1 : l.getChu_ky_ngay();
        ps.setInt(3, chuKy);
        if (l.getNgay_ket_thuc() != null) ps.setDate(4, new Date(l.getNgay_ket_thuc().getTime()));
        else ps.setNull(4, java.sql.Types.DATE);
        ps.setNString(5, l.getMo_ta());
        ps.setNString(6, l.getTrang_thai() != null ? l.getTrang_thai() : "Đang áp dụng");
    }

    private void xoaGio(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private void rollback(Connection conn) {
        if (conn != null) try { conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
    }

    private void dong(Connection conn) {
        if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
