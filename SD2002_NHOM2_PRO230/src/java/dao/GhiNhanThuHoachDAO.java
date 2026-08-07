package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.GhiNhanThuHoach;
import url.DBConnect;

/**
 * UC-4.7 - Ghi nhận thu hoạch theo lô/vụ, phân loại chất lượng, cập nhật trạng thái lô.
 */
public class GhiNhanThuHoachDAO {

    public List<GhiNhanThuHoach> getAllWithVuon() {
        List<GhiNhanThuHoach> list = new ArrayList<>();
        String sql = "SELECT t.*, d.ten_don_vi AS ten_lo, g.ten_giong AS ten_giong FROM GhiNhanThuHoach t "
                + "JOIN VuonTrong v ON t.vuon_trong_id = v.id "
                + "JOIN DonViQuanLy d ON v.lo_dat_id = d.id "
                + "JOIN GiongSauRieng g ON v.giong_id = g.id ORDER BY t.id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GhiNhanThuHoach t = new GhiNhanThuHoach();
                t.setId(rs.getInt("id"));
                t.setVuon_trong_id(rs.getInt("vuon_trong_id"));
                t.setTen_vu_mua(rs.getNString("ten_vu_mua"));
                t.setNgay_thu_hoach(rs.getDate("ngay_thu_hoach"));
                t.setVi_tri_luu_tru_id(rs.getInt("vi_tri_luu_tru_id"));
                t.setTong_san_luong_kg(rs.getDouble("tong_san_luong_kg"));
                t.setTong_dien_tich_chiem_dung(rs.getDouble("tong_dien_tich_chiem_dung"));
                t.setTrang_thai_luu_kho(rs.getNString("trang_thai_luu_kho"));
                t.setNguoi_ghi_nhan_id(rs.getInt("nguoi_ghi_nhan_id"));
                t.setGhi_chu(rs.getNString("ghi_chu"));
                t.setTen_lo_dat(rs.getNString("ten_lo"));
                t.setTen_giong(rs.getNString("ten_giong"));
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Ghi nhận thu hoạch + chi tiết phân loại; cập nhật trạng thái lô.
     * completed=true -> "Đã thu hoạch"; ngược lại giữ "Đang thu hoạch" (alt 4b).
     * Trả về null nếu thành công, hoặc thông báo lỗi.
     */
    public String insert(GhiNhanThuHoach t, boolean hoanTat) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            int newId = layIdMoi(conn, "GhiNhanThuHoach");

            String sql = "INSERT INTO GhiNhanThuHoach (id, vuon_trong_id, ten_vu_mua, ngay_thu_hoach, vi_tri_luu_tru_id, "
                    + "tong_san_luong_kg, tong_dien_tich_chiem_dung, trang_thai_luu_kho, nguoi_ghi_nhan_id, ghi_chu, ngay_tao) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, newId);
                ps.setInt(2, t.getVuon_trong_id());
                ps.setNString(3, t.getTen_vu_mua());
                ps.setDate(4, new Date(t.getNgay_thu_hoach().getTime()));
                ps.setInt(5, t.getVi_tri_luu_tru_id());
                ps.setDouble(6, t.getTong_san_luong_kg());
                ps.setDouble(7, t.getTong_dien_tich_chiem_dung());
                ps.setNString(8, t.getTrang_thai_luu_kho() != null ? t.getTrang_thai_luu_kho() : "Đã nhập kho");
                ps.setInt(9, t.getNguoi_ghi_nhan_id());
                ps.setNString(10, t.getGhi_chu());
                ps.executeUpdate();
            }

            // Chi tiết phân loại chất lượng
            if (!t.getDongPhanLoai().isEmpty()) {
                int ctId = layIdMoi(conn, "ChiTietPhanLoaiThuHoach");
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ChiTietPhanLoaiThuHoach (id, ghi_nhan_thu_hoach_id, xep_loai, san_luong_kg, dien_tich_chiem_dung) VALUES (?, ?, ?, ?, ?)")) {
                    for (GhiNhanThuHoach.DongPhanLoai pl : t.getDongPhanLoai()) {
                        if (pl.xepLoai == null || pl.xepLoai.trim().isEmpty()) continue;
                        ps.setInt(1, ctId++);
                        ps.setInt(2, newId);
                        ps.setNString(3, pl.xepLoai);
                        ps.setDouble(4, pl.sanLuongKg);
                        ps.setDouble(5, pl.dienTichChiemDung);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // Cập nhật trạng thái lô (UC-4.5)
            String trangThaiLo = hoanTat ? "Đã thu hoạch" : "Đang thu hoạch";
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE VuonTrong SET trang_thai_sinh_truong=?, ngay_cap_nhat=GETDATE() WHERE id=?")) {
                ps.setNString(1, trangThaiLo);
                ps.setInt(2, t.getVuon_trong_id());
                ps.executeUpdate();
            }

            conn.commit();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            return e.getMessage();
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    public boolean delete(int id) {
        // ChiTietPhanLoaiThuHoach ON DELETE CASCADE
        String sql = "DELETE FROM GhiNhanThuHoach WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private int layIdMoi(Connection conn, String bang) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT ISNULL(MAX(id),0)+1 FROM " + bang);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 1;
    }
}
