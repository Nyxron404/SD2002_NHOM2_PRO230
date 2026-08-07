package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.MatDoThamChieu;
import models.VuonTrong;
import url.DBConnect;

/**
 * UC-4.2 - Gán giống & thiết lập vườn trồng cho lô đất.
 * Chứa logic tính mật độ trồng (cây/ha) và phân loại thưa/vừa/dày.
 */
public class VuonTrongDAO {

    private final MatDoThamChieuDAO matDoDAO = new MatDoThamChieuDAO();

    // Ngưỡng phát hiện mật độ bất thường (quá thưa / quá dày) - alt flow 6a
    public static final double MAT_DO_MIN_HOP_LY = 60;
    public static final double MAT_DO_MAX_HOP_LY = 600;

    /** Tính mật độ cây/ha = số cây / diện tích khai thác (ha). */
    public static double tinhMatDo(int soCay, double dienTichHa) {
        if (dienTichHa <= 0) return 0;
        return soCay / dienTichHa;
    }

    public List<VuonTrong> getAllWithNames() {
        List<VuonTrong> list = new ArrayList<>();
        String sql = "SELECT v.*, d.ten_don_vi AS ten_lo, g.ten_giong AS ten_giong, "
                + "m.phan_loai AS phan_loai, m.dac_diem_rui_ro AS rui_ro "
                + "FROM VuonTrong v "
                + "JOIN DonViQuanLy d ON v.lo_dat_id = d.id "
                + "JOIN GiongSauRieng g ON v.giong_id = g.id "
                + "LEFT JOIN MatDoThamChieu m ON v.mat_do_tham_chieu_id = m.id "
                + "ORDER BY v.id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VuonTrong v = new VuonTrong();
                v.setId(rs.getInt("id"));
                v.setLo_dat_id(rs.getInt("lo_dat_id"));
                v.setGiong_id(rs.getInt("giong_id"));
                v.setDien_tich(rs.getDouble("dien_tich"));
                v.setSo_luong_cay(rs.getInt("so_luong_cay"));
                v.setMat_do_trong(rs.getDouble("mat_do_trong"));
                v.setMat_do_tham_chieu_id(rs.getInt("mat_do_tham_chieu_id"));
                v.setMat_do_bat_thuong(rs.getBoolean("mat_do_bat_thuong"));
                v.setNgay_trong(rs.getDate("ngay_trong"));
                v.setTrang_thai_sinh_truong(rs.getString("trang_thai_sinh_truong"));
                v.setCo_sau_benh(rs.getBoolean("co_sau_benh"));
                v.setGhi_chu(rs.getNString("ghi_chu"));
                v.setTen_lo_dat(rs.getNString("ten_lo"));
                v.setTen_giong(rs.getNString("ten_giong"));
                v.setPhan_loai_mat_do(rs.getNString("phan_loai"));
                v.setDac_diem_rui_ro(rs.getNString("rui_ro"));
                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thiết lập vườn: tự tính mật độ, phân loại và cờ bất thường trước khi lưu.
     * Trạng thái sinh trưởng khởi tạo "Cây con".
     */
    public boolean insert(VuonTrong v) {
        double matDo = tinhMatDo(v.getSo_luong_cay(), v.getDien_tich());
        MatDoThamChieu phanLoai = matDoDAO.phanLoai(matDo);
        boolean batThuong = matDo < MAT_DO_MIN_HOP_LY || matDo > MAT_DO_MAX_HOP_LY || phanLoai == null;

        // Nếu không khớp ngưỡng nào, tạm gán về dòng gần nhất để giữ ràng buộc khóa ngoại
        int matDoId = (phanLoai != null) ? phanLoai.getId() : timMatDoGanNhat(matDo);
        if (matDoId <= 0) return false; // chưa seed bảng tham chiếu

        String sql = "INSERT INTO VuonTrong (lo_dat_id, giong_id, dien_tich, so_luong_cay, mat_do_trong, "
                + "mat_do_tham_chieu_id, mat_do_bat_thuong, ngay_trong, trang_thai_sinh_truong, co_sau_benh, ghi_chu, ngay_tao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, GETDATE())";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getLo_dat_id());
            ps.setInt(2, v.getGiong_id());
            ps.setDouble(3, v.getDien_tich());
            ps.setInt(4, v.getSo_luong_cay());
            ps.setDouble(5, matDo);
            ps.setInt(6, matDoId);
            ps.setBoolean(7, batThuong);
            if (v.getNgay_trong() != null) ps.setDate(8, new Date(v.getNgay_trong().getTime()));
            else ps.setDate(8, new Date(System.currentTimeMillis()));
            ps.setNString(9, v.getTrang_thai_sinh_truong() != null ? v.getTrang_thai_sinh_truong() : "Cây con");
            ps.setNString(10, v.getGhi_chu());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean update(VuonTrong v) {
        double matDo = tinhMatDo(v.getSo_luong_cay(), v.getDien_tich());
        MatDoThamChieu phanLoai = matDoDAO.phanLoai(matDo);
        boolean batThuong = matDo < MAT_DO_MIN_HOP_LY || matDo > MAT_DO_MAX_HOP_LY || phanLoai == null;
        int matDoId = (phanLoai != null) ? phanLoai.getId() : timMatDoGanNhat(matDo);
        if (matDoId <= 0) return false;

        String sql = "UPDATE VuonTrong SET lo_dat_id=?, giong_id=?, dien_tich=?, so_luong_cay=?, mat_do_trong=?, "
                + "mat_do_tham_chieu_id=?, mat_do_bat_thuong=?, ghi_chu=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getLo_dat_id());
            ps.setInt(2, v.getGiong_id());
            ps.setDouble(3, v.getDien_tich());
            ps.setInt(4, v.getSo_luong_cay());
            ps.setDouble(5, matDo);
            ps.setInt(6, matDoId);
            ps.setBoolean(7, batThuong);
            ps.setNString(8, v.getGhi_chu());
            ps.setInt(9, v.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM VuonTrong WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /** Cập nhật trạng thái sinh trưởng của lô (dùng bởi UC-4.5 / 4.7). */
    public boolean capNhatTrangThai(int vuonTrongId, String trangThai) {
        String sql = "UPDATE VuonTrong SET trang_thai_sinh_truong=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setInt(2, vuonTrongId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /** Giảm số cây của lô (UC-4.5 alt 4a: cây chết/bị loại bỏ). */
    public boolean giamSoCay(int vuonTrongId, int soCayGiam) {
        String sql = "UPDATE VuonTrong SET so_luong_cay = CASE WHEN so_luong_cay - ? < 0 THEN 0 ELSE so_luong_cay - ? END, "
                + "mat_do_trong = CASE WHEN dien_tich > 0 THEN (CASE WHEN so_luong_cay - ? < 0 THEN 0 ELSE so_luong_cay - ? END)/dien_tich ELSE mat_do_trong END, "
                + "ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soCayGiam);
            ps.setInt(2, soCayGiam);
            ps.setInt(3, soCayGiam);
            ps.setInt(4, soCayGiam);
            ps.setInt(5, vuonTrongId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean capNhatSauBenh(int vuonTrongId, boolean coSauBenh) {
        String sql = "UPDATE VuonTrong SET co_sau_benh=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, coSauBenh);
            ps.setInt(2, vuonTrongId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /** Tìm dòng tham chiếu gần nhất để giữ ràng buộc khóa ngoại khi mật độ bất thường. */
    private int timMatDoGanNhat(double matDo) {
        String sql = "SELECT TOP 1 id FROM MatDoThamChieu ORDER BY ABS(mat_do_tu - ?) ASC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, matDo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}
