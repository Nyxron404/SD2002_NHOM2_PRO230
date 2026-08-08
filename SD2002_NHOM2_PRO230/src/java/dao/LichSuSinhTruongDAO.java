package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.LichSuSinhTruong;
import url.DBConnect;

/**
 * UC-4.5 - Theo dõi trạng thái sinh trưởng theo lô.
 *
 * Thay đổi so với bản cũ:
 *   - MỖI VƯỜN CHỈ CÓ MỘT bản ghi theo dõi. Lần cập nhật sau sẽ SỬA bản ghi
 *     hiện có chứ không tạo bản ghi mới (phương thức {@link #luu(LichSuSinhTruong)}).
 *   - Tỷ lệ giai đoạn được ghi lại vào ChiTietTyLeGiaiDoan (xóa hết dòng cũ rồi
 *     ghi dòng mới) nên luôn hiển thị đúng sau khi lưu.
 *   - giai_doan_moi không phải cột của bảng LichSuSinhTruong; nó được ghi vào
 *     VuonTrong.trang_thai_sinh_truong và đọc ngược lại khi hiển thị.
 */
public class LichSuSinhTruongDAO {

    /** Danh sách giai đoạn hợp lệ, dùng chung cho form và kiểm tra. */
    public static final String[] GIAI_DOAN = {
        "Cây con", "Sinh trưởng", "Ra hoa", "Đậu trái", "Nuôi trái", "Sắp thu hoạch", "Đã thu hoạch"
    };

    // ===================================================================
    // ĐỌC
    // ===================================================================

    /** Mỗi lô một dòng theo dõi, kèm trạng thái và số cây hiện tại. */
    public List<LichSuSinhTruong> getAllWithVuon() {
        List<LichSuSinhTruong> list = new ArrayList<>();
        String sql = "SELECT s.*, d.ten_don_vi AS ten_lo, g.ten_giong AS ten_giong, "
                   + "v.trang_thai_sinh_truong AS tt, v.so_luong_cay AS so_cay "
                   + "FROM LichSuSinhTruong s "
                   + "JOIN VuonTrong v ON s.vuon_trong_id = v.id "
                   + "JOIN DonViQuanLy d ON v.lo_dat_id = d.id "
                   + "JOIN GiongSauRieng g ON v.giong_id = g.id "
                   + "ORDER BY d.ten_don_vi";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LichSuSinhTruong s = new LichSuSinhTruong();
                s.setId(rs.getInt("id"));
                s.setVuon_trong_id(rs.getInt("vuon_trong_id"));
                s.setNgay_cap_nhat(rs.getDate("ngay_cap_nhat"));
                s.setLoai_cap_nhat(rs.getNString("loai_cap_nhat"));
                int giam = rs.getInt("so_luong_cay_giam");
                s.setSo_luong_cay_giam(rs.wasNull() ? null : giam);
                s.setGhi_chu(rs.getNString("ghi_chu"));
                s.setNguoi_cap_nhat_id(rs.getInt("nguoi_cap_nhat_id"));
                s.setTen_lo_dat(rs.getNString("ten_lo"));
                s.setTen_giong(rs.getNString("ten_giong"));
                s.setTrang_thai_hien_tai(rs.getString("tt"));
                // giai_doan_moi hiển thị chính là trạng thái đang áp dụng của lô
                s.setGiai_doan_moi(rs.getString("tt"));
                s.setSo_cay_con_lai(rs.getInt("so_cay"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Nạp tỷ lệ giai đoạn sau, dùng kết nối riêng cho từng dòng
        for (LichSuSinhTruong s : list) s.setTy_le_giai_doan(layTyLe(s.getId()));
        return list;
    }

    /** Chuỗi hiển thị dạng "Cây con: 30%, Sinh trưởng: 70%". */
    private String layTyLe(int lichSuId) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT giai_doan, ty_le_phan_tram FROM ChiTietTyLeGiaiDoan "
                   + "WHERE lich_su_sinh_truong_id = ? ORDER BY ty_le_phan_tram DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lichSuId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(rs.getNString("giai_doan")).append(": ")
                      .append(boSoKhongThua(rs.getDouble("ty_le_phan_tram"))).append('%');
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /** Bản ghi theo dõi của một vườn (null nếu chưa có). */
    public LichSuSinhTruong getByVuon(int vuonTrongId) {
        for (LichSuSinhTruong s : getAllWithVuon())
            if (s.getVuon_trong_id() == vuonTrongId) return s;
        return null;
    }

    // ===================================================================
    // LƯU (THÊM MỚI HOẶC CẬP NHẬT)
    // ===================================================================

    /**
     * Lưu theo dõi sinh trưởng của một lô.
     * Nếu lô đã có bản ghi thì cập nhật bản ghi đó, không tạo thêm dòng mới.
     *
     * @return null nếu thành công, ngược lại là thông báo lỗi.
     */
    public String luu(LichSuSinhTruong s) {
        if (s.getVuon_trong_id() <= 0) return "Vui lòng chọn vườn/lô.";

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return "Không kết nối được cơ sở dữ liệu.";
            conn.setAutoCommit(false);

            int soCayHienTai = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT so_luong_cay FROM VuonTrong WHERE id = ?")) {
                ps.setInt(1, s.getVuon_trong_id());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Vườn trồng không tồn tại.");
                    soCayHienTai = rs.getInt(1);
                }
            }

            int giam = s.getSo_luong_cay_giam() == null ? 0 : s.getSo_luong_cay_giam();
            if (giam > soCayHienTai)
                throw new SQLException("Số cây giảm (" + giam + ") lớn hơn số cây hiện có (" + soCayHienTai + ").");

            // Kiểm tra tổng tỷ lệ giai đoạn
            double tongTyLe = tinhTongTyLe(s.getTy_le_giai_doan());
            if (tongTyLe > 100.01)
                throw new SQLException("Tổng tỷ lệ các giai đoạn là " + boSoKhongThua(tongTyLe) + "%, không được vượt 100%.");

            int nguoiCapNhat = NhatKyChamSocDAO.chuanHoaTaiKhoan(conn, s.getNguoi_cap_nhat_id());

            // --- Tìm bản ghi hiện có của lô ---
            Integer lichSuId = null;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT TOP 1 id FROM LichSuSinhTruong WHERE vuon_trong_id = ? ORDER BY id")) {
                ps.setInt(1, s.getVuon_trong_id());
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) lichSuId = rs.getInt(1); }
            }

            if (lichSuId == null) {
                String sql = "INSERT INTO LichSuSinhTruong (vuon_trong_id, ngay_cap_nhat, loai_cap_nhat, "
                           + "so_luong_cay_giam, ghi_chu, nguoi_cap_nhat_id, ngay_tao) "
                           + "VALUES (?, CAST(GETDATE() AS DATE), ?, ?, ?, ?, GETDATE())";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, s.getVuon_trong_id());
                    ps.setNString(2, valueOr(s.getLoai_cap_nhat(), "Chuyển giai đoạn"));
                    if (giam > 0) ps.setInt(3, giam); else ps.setNull(3, java.sql.Types.INTEGER);
                    ps.setNString(4, valueOr(s.getGhi_chu(), ""));
                    ps.setInt(5, nguoiCapNhat);
                    ps.executeUpdate();
                    try (ResultSet gk = ps.getGeneratedKeys()) { gk.next(); lichSuId = gk.getInt(1); }
                }
            } else {
                String sql = "UPDATE LichSuSinhTruong SET ngay_cap_nhat = CAST(GETDATE() AS DATE), "
                           + "loai_cap_nhat = ?, so_luong_cay_giam = ?, ghi_chu = ?, nguoi_cap_nhat_id = ? "
                           + "WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setNString(1, valueOr(s.getLoai_cap_nhat(), "Chuyển giai đoạn"));
                    if (giam > 0) ps.setInt(2, giam); else ps.setNull(2, java.sql.Types.INTEGER);
                    ps.setNString(3, valueOr(s.getGhi_chu(), ""));
                    ps.setInt(4, nguoiCapNhat);
                    ps.setInt(5, lichSuId);
                    ps.executeUpdate();
                }
            }

            // --- Ghi lại tỷ lệ giai đoạn: xóa hết dòng cũ rồi ghi dòng mới ---
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM ChiTietTyLeGiaiDoan WHERE lich_su_sinh_truong_id = ?")) {
                ps.setInt(1, lichSuId);
                ps.executeUpdate();
            }
            List<Object[]> dong = phanTichTyLe(s.getTy_le_giai_doan());
            if (!dong.isEmpty()) {
                int ctId = NhatKyChamSocDAO.layIdMoi(conn, "ChiTietTyLeGiaiDoan");
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ChiTietTyLeGiaiDoan (id, lich_su_sinh_truong_id, giai_doan, ty_le_phan_tram) "
                      + "VALUES (?, ?, ?, ?)")) {
                    for (Object[] d : dong) {
                        ps.setInt(1, ctId++);
                        ps.setInt(2, lichSuId);
                        ps.setNString(3, (String) d[0]);
                        ps.setDouble(4, (Double) d[1]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // --- Cập nhật trạng thái của lô ---
            if (s.getGiai_doan_moi() != null && !s.getGiai_doan_moi().isBlank()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE VuonTrong SET trang_thai_sinh_truong = ?, ngay_cap_nhat = GETDATE() WHERE id = ?")) {
                    ps.setString(1, s.getGiai_doan_moi().trim());
                    ps.setInt(2, s.getVuon_trong_id());
                    ps.executeUpdate();
                }
            }

            // --- Giảm số cây và tính lại mật độ (cây/ha) ---
            if (giam > 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE VuonTrong SET so_luong_cay = so_luong_cay - ?, "
                      + "mat_do_trong = CASE WHEN dien_tich > 0 "
                      + "     THEN (so_luong_cay - ?) / (dien_tich / 10000.0) ELSE mat_do_trong END, "
                      + "ngay_cap_nhat = GETDATE() WHERE id = ?")) {
                    ps.setInt(1, giam);
                    ps.setInt(2, giam);
                    ps.setInt(3, s.getVuon_trong_id());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return e.getMessage() == null ? "Không thể lưu theo dõi sinh trưởng." : e.getMessage();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    /** Giữ lại tên cũ để code khác không vỡ. */
    public boolean insert(LichSuSinhTruong s) {
        return luu(s) == null;
    }

    // ===================================================================

    /** Chuỗi "Cây con:30,Sinh trưởng:70" -> danh sách [tên giai đoạn, %]. */
    private List<Object[]> phanTichTyLe(String chuoi) {
        List<Object[]> ds = new ArrayList<>();
        if (chuoi == null || chuoi.isBlank()) return ds;
        for (String phan : chuoi.split(",")) {
            String[] kv = phan.split(":");
            if (kv.length < 2) continue;
            String ten = kv[0].trim();
            if (ten.isEmpty()) continue;
            try {
                double tyLe = Double.parseDouble(kv[1].trim().replace("%", "").replace(",", "."));
                if (tyLe > 0) ds.add(new Object[]{ten, tyLe});
            } catch (NumberFormatException ignored) { }
        }
        return ds;
    }

    private double tinhTongTyLe(String chuoi) {
        double tong = 0;
        for (Object[] d : phanTichTyLe(chuoi)) tong += (Double) d[1];
        return tong;
    }

    private static String boSoKhongThua(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(v);
    }

    private static String valueOr(String s, String def) {
        return s == null || s.isBlank() ? def : s.trim();
    }
}
