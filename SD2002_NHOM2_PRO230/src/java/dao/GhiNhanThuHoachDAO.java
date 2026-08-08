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
 * UC-4.7 - Ghi nhận thu hoạch.
 *
 * Điểm sửa quan trọng so với bản cũ:
 *   1. Nông sản nhập kho phải CHIẾM diện tích: cộng vào KhuVuc.dien_tich_chiem_dung
 *      của khu chứa vị trí lưu trữ, và kiểm tra không vượt sức chứa của đơn vị.
 *   2. Nếu người dùng không nhập tổng diện tích chiếm dụng, hệ thống tự cộng từ
 *      các dòng phân loại chất lượng (ChiTietPhanLoaiThuHoach).
 *   3. Tổng sản lượng phải khớp với tổng các dòng phân loại (nếu có khai báo).
 *   4. nguoi_ghi_nhan_id là khóa ngoại tới TaiKhoan -> phải chuẩn hóa, không được để 0.
 *   5. Xóa phiếu thu hoạch sẽ trả lại diện tích kho đã chiếm.
 *
 * Lưu ý schema: GhiNhanThuHoach.id và ChiTietPhanLoaiThuHoach.id đều KHÔNG phải IDENTITY.
 */
public class GhiNhanThuHoachDAO {

    public List<GhiNhanThuHoach> getAllWithVuon() {
        List<GhiNhanThuHoach> list = new ArrayList<>();
        String sql = "SELECT t.*, d.ten_don_vi AS ten_lo, g.ten_giong AS ten_giong "
                   + "FROM GhiNhanThuHoach t "
                   + "JOIN VuonTrong v ON t.vuon_trong_id = v.id "
                   + "JOIN DonViQuanLy d ON v.lo_dat_id = d.id "
                   + "JOIN GiongSauRieng g ON v.giong_id = g.id "
                   + "ORDER BY t.ngay_thu_hoach DESC, t.id DESC";
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
     * Ghi nhận thu hoạch + phân loại + chiếm diện tích kho + cập nhật trạng thái lô.
     * @param hoanTat true -> lô chuyển "Đã thu hoạch"; false -> "Đang thu hoạch".
     * @return null nếu thành công, ngược lại là thông báo lỗi.
     */
    public String insert(GhiNhanThuHoach t, boolean hoanTat) {
        if (t == null) return "Dữ liệu thu hoạch rỗng.";
        if (t.getVuon_trong_id() <= 0) return "Vui lòng chọn vườn/lô thu hoạch.";
        if (t.getTen_vu_mua() == null || t.getTen_vu_mua().isBlank()) return "Vui lòng nhập tên vụ mùa.";
        if (t.getNgay_thu_hoach() == null) return "Vui lòng chọn ngày thu hoạch.";
        if (t.getVi_tri_luu_tru_id() <= 0) return "Vui lòng chọn vị trí lưu trữ nông sản.";
        if (t.getTong_san_luong_kg() <= 0) return "Tổng sản lượng phải lớn hơn 0.";

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return "Không kết nối được cơ sở dữ liệu.";
            conn.setAutoCommit(false);

            // --- Đối chiếu chi tiết phân loại với tổng ---
            double sanLuongChiTiet = 0, dienTichChiTiet = 0;
            for (GhiNhanThuHoach.DongPhanLoai pl : t.getDongPhanLoai()) {
                if (pl.xepLoai == null || pl.xepLoai.isBlank()) continue;
                sanLuongChiTiet  += pl.sanLuongKg;
                dienTichChiTiet  += pl.dienTichChiemDung;
            }
            if (sanLuongChiTiet > 0 && Math.abs(sanLuongChiTiet - t.getTong_san_luong_kg()) > 0.01) {
                throw new SQLException("Tổng sản lượng (" + t.getTong_san_luong_kg()
                        + " kg) không khớp tổng các dòng phân loại (" + sanLuongChiTiet + " kg).");
            }
            // Nếu không nhập tổng diện tích thì lấy từ chi tiết
            double dienTichChiem = t.getTong_dien_tich_chiem_dung() > 0
                    ? t.getTong_dien_tich_chiem_dung() : dienTichChiTiet;
            if (dienTichChiem <= 0) {
                throw new SQLException("Vui lòng nhập diện tích chiếm dụng của lô nông sản trong kho.");
            }

            // --- Kiểm tra sức chứa của vị trí lưu trữ ---
            kiemTraSucChuaKho(conn, t.getVi_tri_luu_tru_id(), dienTichChiem);

            int nguoiGhiNhan = NhatKyChamSocDAO.chuanHoaTaiKhoan(conn, t.getNguoi_ghi_nhan_id());
            if (nguoiGhiNhan <= 0) throw new SQLException("Không xác định được tài khoản người ghi nhận.");

            int newId = NhatKyChamSocDAO.layIdMoi(conn, "GhiNhanThuHoach");

            String sql = "INSERT INTO GhiNhanThuHoach (id, vuon_trong_id, ten_vu_mua, ngay_thu_hoach, vi_tri_luu_tru_id, "
                       + "tong_san_luong_kg, tong_dien_tich_chiem_dung, trang_thai_luu_kho, nguoi_ghi_nhan_id, ghi_chu, ngay_tao) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, newId);
                ps.setInt(2, t.getVuon_trong_id());
                ps.setNString(3, t.getTen_vu_mua().trim());
                ps.setDate(4, new Date(t.getNgay_thu_hoach().getTime()));
                ps.setInt(5, t.getVi_tri_luu_tru_id());
                ps.setDouble(6, t.getTong_san_luong_kg());
                ps.setDouble(7, dienTichChiem);
                ps.setNString(8, t.getTrang_thai_luu_kho() == null ? "Đã nhập kho" : t.getTrang_thai_luu_kho());
                ps.setInt(9, nguoiGhiNhan);
                ps.setNString(10, t.getGhi_chu());
                ps.executeUpdate();
            }

            // --- Chi tiết phân loại chất lượng ---
            if (!t.getDongPhanLoai().isEmpty()) {
                int ctId = NhatKyChamSocDAO.layIdMoi(conn, "ChiTietPhanLoaiThuHoach");
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ChiTietPhanLoaiThuHoach (id, ghi_nhan_thu_hoach_id, xep_loai, san_luong_kg, dien_tich_chiem_dung) "
                      + "VALUES (?, ?, ?, ?, ?)")) {
                    for (GhiNhanThuHoach.DongPhanLoai pl : t.getDongPhanLoai()) {
                        if (pl.xepLoai == null || pl.xepLoai.isBlank()) continue;
                        ps.setInt(1, ctId++);
                        ps.setInt(2, newId);
                        ps.setNString(3, pl.xepLoai.trim());
                        ps.setDouble(4, pl.sanLuongKg);
                        ps.setDouble(5, pl.dienTichChiemDung);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // --- Nông sản nhập kho -> CHIẾM diện tích kho ---
            NhatKyChamSocDAO.capNhatDienTichKhuVuc(conn, t.getVi_tri_luu_tru_id(), dienTichChiem);
            capNhatTrangThaiDonVi(conn, t.getVi_tri_luu_tru_id());

            // --- Cập nhật trạng thái sinh trưởng của lô ---
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE VuonTrong SET trang_thai_sinh_truong = ?, ngay_cap_nhat = GETDATE() WHERE id = ?")) {
                ps.setString(1, hoanTat ? "Đã thu hoạch" : "Đang thu hoạch");
                ps.setInt(2, t.getVuon_trong_id());
                ps.executeUpdate();
            }

            conn.commit();
            t.setId(newId);
            t.setTong_dien_tich_chiem_dung(dienTichChiem);
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return e.getMessage() == null ? "Không thể ghi nhận thu hoạch." : e.getMessage();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    /** Xóa phiếu thu hoạch và TRẢ LẠI diện tích kho đã chiếm. */
    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            int viTri = 0;
            double dienTich = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT vi_tri_luu_tru_id, tong_dien_tich_chiem_dung FROM GhiNhanThuHoach WHERE id=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        viTri = rs.getInt(1);
                        dienTich = rs.getDouble(2);
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM ChiTietPhanLoaiThuHoach WHERE ghi_nhan_thu_hoach_id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            int r;
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM GhiNhanThuHoach WHERE id=?")) {
                ps.setInt(1, id);
                r = ps.executeUpdate();
            }

            if (r > 0 && viTri > 0) {
                NhatKyChamSocDAO.capNhatDienTichKhuVuc(conn, viTri, -dienTich);
                capNhatTrangThaiDonVi(conn, viTri);
            }

            conn.commit();
            return r > 0;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // ===================================================================

    /**
     * Kiểm tra đơn vị lưu trữ còn đủ chỗ cho lượng nông sản sắp nhập.
     * Sức chứa khả dụng = DonViQuanLy.dien_tich (m2) trừ đi phần đã bị nông sản chiếm.
     */
    private void kiemTraSucChuaKho(Connection conn, int viTriId, double dienTichCanThem) throws SQLException {
        double dienTichDonVi = 0;
        String tenDonVi = "#" + viTriId;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ten_don_vi, dien_tich, trang_thai FROM DonViQuanLy WHERE id=?")) {
            ps.setInt(1, viTriId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Vị trí lưu trữ không tồn tại.");
                tenDonVi      = rs.getNString("ten_don_vi");
                dienTichDonVi = rs.getDouble("dien_tich");
                String tt     = rs.getNString("trang_thai");
                if ("Ngừng sử dụng".equalsIgnoreCase(tt) || "Bảo trì".equalsIgnoreCase(tt))
                    throw new SQLException("Vị trí lưu trữ \"" + tenDonVi + "\" đang " + tt + ", không thể nhập kho.");
            }
        }

        double daChiem = 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ISNULL(SUM(tong_dien_tich_chiem_dung),0) FROM GhiNhanThuHoach WHERE vi_tri_luu_tru_id=?")) {
            ps.setInt(1, viTriId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) daChiem = rs.getDouble(1); }
        }

        double conLai = dienTichDonVi - daChiem;
        if (dienTichCanThem > conLai) {
            throw new SQLException("Vị trí lưu trữ \"" + tenDonVi + "\" không đủ chỗ: cần "
                    + dienTichCanThem + " m², còn trống " + Math.max(0, conLai) + " m².");
        }
    }

    /** Cập nhật trạng thái đơn vị lưu trữ theo mức lấp đầy (Còn trống / Đang sử dụng / Gần đầy / Đầy). */
    private void capNhatTrangThaiDonVi(Connection conn, int viTriId) throws SQLException {
        double dienTich = 0, daChiem = 0;
        try (PreparedStatement ps = conn.prepareStatement("SELECT dien_tich FROM DonViQuanLy WHERE id=?")) {
            ps.setInt(1, viTriId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) dienTich = rs.getDouble(1); }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ISNULL(SUM(tong_dien_tich_chiem_dung),0) FROM GhiNhanThuHoach WHERE vi_tri_luu_tru_id=?")) {
            ps.setInt(1, viTriId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) daChiem = rs.getDouble(1); }
        }
        if (dienTich <= 0) return;

        double tyLe = daChiem / dienTich;
        String trangThai;
        if (tyLe <= 0)        trangThai = "Còn trống";
        else if (tyLe < 0.8)  trangThai = "Đang sử dụng";
        else if (tyLe < 1.0)  trangThai = "Gần đầy";
        else                  trangThai = "Đầy";

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE DonViQuanLy SET trang_thai = ?, ngay_cap_nhat = GETDATE() WHERE id = ?")) {
            ps.setNString(1, trangThai);
            ps.setInt(2, viTriId);
            ps.executeUpdate();
        }
    }
}
