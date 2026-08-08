package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.GhiNhanSauBenh;
import models.NhatKyChamSoc;
import url.DBConnect;

/**
 * UC-4.6 - Theo dõi và xử lý sâu bệnh.
 *
 * Luồng nghiệp vụ sau khi sửa:
 *   1. "Chưa xử lý"  : chỉ ghi nhận phát hiện, chưa động vào kho.
 *   2. "Đang xử lý"  : chọn thuốc bảo vệ thực vật và số lượng dự kiến -> lưu vào
 *                      biện pháp xử lý dưới dạng dự trù, CHƯA trừ kho.
 *   3. "Đã xử lý"    : trừ kho thuốc, tính tiền và diện tích kho được giải phóng.
 *
 * Điểm quan trọng: khi chuyển sang "Đã xử lý", hệ thống KHÔNG ghi thẳng vào
 * ChiTietVatTuTieuHao mà tạo một bản ghi NhatKyChamSoc loại "Phòng trừ sâu bệnh"
 * cho lô tương ứng rồi gán vào cột GhiNhanSauBenh.nhat_ky_cham_soc_id (cột này
 * đã có sẵn khóa ngoại trong CSDL).
 *
 * Lý do: cột ChiTietVatTuTieuHao.nguon_tieu_hao_id không có khóa ngoại, nếu vừa
 * chứa id nhật ký vừa chứa id sâu bệnh thì hai dải id sẽ đè lên nhau và mọi báo
 * cáo chi phí theo lô đều sai. Đi qua nhật ký thì chi phí thuốc tự động vào đúng
 * lô đất và hiển thị luôn ở bảng Chi phí & tiêu hao.
 */
public class GhiNhanSauBenhDAO {

    public static final String[] TRANG_THAI = {"Chưa xử lý", "Đang xử lý", "Đã xử lý"};
    public static final String[] MUC_DO = {"Nhẹ", "Trung bình", "Nặng"};

    private final NhatKyChamSocDAO nhatKyDAO = new NhatKyChamSocDAO();

    // ===================================================================
    // ĐỌC
    // ===================================================================

    public List<GhiNhanSauBenh> getAllWithVuon() {
        List<GhiNhanSauBenh> list = new ArrayList<>();
        String sql = "SELECT s.*, d.ten_don_vi AS ten_lo, g.ten_giong AS ten_giong, "
                   + "ISNULL(n.tong_chi_phi, 0) AS chi_phi_xu_ly "
                   + "FROM GhiNhanSauBenh s "
                   + "JOIN VuonTrong v ON s.vuon_trong_id = v.id "
                   + "JOIN DonViQuanLy d ON v.lo_dat_id = d.id "
                   + "JOIN GiongSauRieng g ON v.giong_id = g.id "
                   + "LEFT JOIN NhatKyChamSoc n ON s.nhat_ky_cham_soc_id = n.id "
                   + "ORDER BY s.ngay_phat_hien DESC, s.id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GhiNhanSauBenh s = new GhiNhanSauBenh();
                s.setId(rs.getInt("id"));
                s.setVuon_trong_id(rs.getInt("vuon_trong_id"));
                s.setTen_sau_benh(rs.getNString("ten_sau_benh"));
                s.setMuc_do_nghiem_trong(rs.getNString("muc_do_nghiem_trong"));
                s.setNgay_phat_hien(rs.getDate("ngay_phat_hien"));
                s.setBien_phap_xu_ly(rs.getNString("bien_phap_xu_ly"));
                int nk = rs.getInt("nhat_ky_cham_soc_id");
                s.setNhat_ky_cham_soc_id(rs.wasNull() ? null : nk);
                s.setTrang_thai(rs.getNString("trang_thai"));
                s.setNguoi_ghi_nhan_id(rs.getInt("nguoi_ghi_nhan_id"));
                s.setTen_lo_dat(rs.getNString("ten_lo"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public GhiNhanSauBenh getById(int id) {
        for (GhiNhanSauBenh s : getAllWithVuon()) if (s.getId() == id) return s;
        return null;
    }

    /** Chi phí thuốc đã dùng để xử lý một ghi nhận sâu bệnh (0 nếu chưa xử lý). */
    public double getChiPhiXuLy(int sauBenhId) {
        String sql = "SELECT ISNULL(n.tong_chi_phi, 0) FROM GhiNhanSauBenh s "
                   + "JOIN NhatKyChamSoc n ON s.nhat_ky_cham_soc_id = n.id WHERE s.id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sauBenhId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getDouble(1); }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===================================================================
    // GHI NHẬN MỚI
    // ===================================================================

    /**
     * Ghi nhận phát hiện sâu bệnh. Bước này KHÔNG trừ kho - việc trừ kho chỉ
     * xảy ra khi chuyển trạng thái sang "Đã xử lý" ở màn hình cập nhật.
     *
     * @return null nếu thành công, ngược lại là thông báo lỗi.
     */
    public String insert(GhiNhanSauBenh s) {
        if (s.getVuon_trong_id() <= 0) return "Vui lòng chọn lô có sâu bệnh.";
        if (s.getTen_sau_benh() == null || s.getTen_sau_benh().isBlank())
            return "Vui lòng nhập tên sâu bệnh.";

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return "Không kết nối được cơ sở dữ liệu.";
            conn.setAutoCommit(false);

            int nguoiGhiNhan = NhatKyChamSocDAO.chuanHoaTaiKhoan(conn, s.getNguoi_ghi_nhan_id());
            int newId;

            String sql = "INSERT INTO GhiNhanSauBenh (vuon_trong_id, ten_sau_benh, muc_do_nghiem_trong, "
                       + "ngay_phat_hien, bien_phap_xu_ly, trang_thai, nguoi_ghi_nhan_id, ngay_tao) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, s.getVuon_trong_id());
                ps.setNString(2, s.getTen_sau_benh().trim());
                ps.setNString(3, valueOr(s.getMuc_do_nghiem_trong(), "Nhẹ"));
                ps.setDate(4, s.getNgay_phat_hien() != null
                        ? new Date(s.getNgay_phat_hien().getTime())
                        : new Date(System.currentTimeMillis()));
                ps.setNString(5, s.getBien_phap_xu_ly());
                ps.setNString(6, valueOr(s.getTrang_thai(), "Chưa xử lý"));
                ps.setInt(7, nguoiGhiNhan);
                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) { gk.next(); newId = gk.getInt(1); }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE VuonTrong SET co_sau_benh = 1, ngay_cap_nhat = GETDATE() WHERE id = ?")) {
                ps.setInt(1, s.getVuon_trong_id());
                ps.executeUpdate();
            }

            conn.commit();
            s.setId(newId);
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            rollback(conn);
            return e.getMessage() == null ? "Không thể ghi nhận sâu bệnh." : e.getMessage();
        } finally {
            dong(conn);
        }
    }

    // ===================================================================
    // CẬP NHẬT / XỬ LÝ
    // ===================================================================

    /**
     * Cập nhật ghi nhận sâu bệnh.
     *
     * Nếu trạng thái mới là "Đã xử lý" và có khai báo thuốc, hệ thống sẽ tạo một
     * nhật ký chăm sóc "Phòng trừ sâu bệnh" để trừ kho thuốc theo FIFO/FEFO,
     * tính tiền và giải phóng diện tích kho, rồi gắn nhật ký đó vào bản ghi này.
     *
     * @param nguoiThucHien id tài khoản đang thao tác.
     * @return null nếu thành công, ngược lại là thông báo lỗi.
     */
    public String capNhat(GhiNhanSauBenh s, int nguoiThucHien) {
        if (s.getId() <= 0) return "Thiếu mã ghi nhận cần cập nhật.";

        String trangThaiMoi = valueOr(s.getTrang_thai(), "Chưa xử lý");
        boolean daXuLy = "Đã xử lý".equalsIgnoreCase(trangThaiMoi);

        // ---- Đọc bản ghi hiện tại ----
        GhiNhanSauBenh hienTai = getById(s.getId());
        if (hienTai == null) return "Không tìm thấy ghi nhận sâu bệnh.";
        if (hienTai.getNhat_ky_cham_soc_id() != null && !s.getDongThuoc().isEmpty())
            return "Ghi nhận này đã trừ kho thuốc trước đó, không thể khai báo thuốc lần nữa.";

        Integer nhatKyId = hienTai.getNhat_ky_cham_soc_id();

        // ---- Trừ kho thuốc khi chuyển sang Đã xử lý ----
        if (daXuLy && nhatKyId == null && !s.getDongThuoc().isEmpty()) {
            int loDatId = layLoDatCuaVuon(hienTai.getVuon_trong_id());
            if (loDatId <= 0) return "Không xác định được lô đất của vườn này.";

            NhatKyChamSoc n = new NhatKyChamSoc();
            n.setLo_dat_id(loDatId);
            n.setLoai_cong_viec("Phòng trừ sâu bệnh");
            n.setNgay_thuc_hien(new java.util.Date());
            n.setNguoi_ghi_nhan_id(nguoiThucHien);
            n.setMo_ta("Xử lý sâu bệnh: " + hienTai.getTen_sau_benh()
                    + (s.getBien_phap_xu_ly() == null || s.getBien_phap_xu_ly().isBlank()
                        ? "" : ". Biện pháp: " + s.getBien_phap_xu_ly().trim()));
            n.getDongVatTu().addAll(s.getDongThuoc());

            String loi = nhatKyDAO.insertFull(n);
            if (loi != null) return "Không thể trừ kho thuốc: " + loi;
            nhatKyId = n.getId();
        }

        // ---- Cập nhật bản ghi sâu bệnh ----
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return "Không kết nối được cơ sở dữ liệu.";
            conn.setAutoCommit(false);

            String sql = "UPDATE GhiNhanSauBenh SET ten_sau_benh = ?, muc_do_nghiem_trong = ?, "
                       + "ngay_phat_hien = ?, bien_phap_xu_ly = ?, trang_thai = ?, nhat_ky_cham_soc_id = ? "
                       + "WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setNString(1, valueOr(s.getTen_sau_benh(), hienTai.getTen_sau_benh()));
                ps.setNString(2, valueOr(s.getMuc_do_nghiem_trong(), hienTai.getMuc_do_nghiem_trong()));
                ps.setDate(3, s.getNgay_phat_hien() != null
                        ? new Date(s.getNgay_phat_hien().getTime())
                        : new Date(hienTai.getNgay_phat_hien().getTime()));
                ps.setNString(4, s.getBien_phap_xu_ly());
                ps.setNString(5, trangThaiMoi);
                if (nhatKyId != null) ps.setInt(6, nhatKyId); else ps.setNull(6, java.sql.Types.INTEGER);
                ps.setInt(7, s.getId());
                ps.executeUpdate();
            }

            // ---- Lô chỉ hết cờ sâu bệnh khi KHÔNG còn ghi nhận nào chưa xong ----
            capNhatCoSauBenh(conn, hienTai.getVuon_trong_id());

            conn.commit();
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            rollback(conn);
            return e.getMessage() == null ? "Không thể cập nhật ghi nhận sâu bệnh." : e.getMessage();
        } finally {
            dong(conn);
        }
    }

    /** Giữ lại chữ ký cũ để không vỡ code khác. */
    public boolean update(GhiNhanSauBenh s) {
        return capNhat(s, s.getNguoi_ghi_nhan_id()) == null;
    }

    public boolean delete(int id) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            GhiNhanSauBenh s = getById(id);
            if (s == null) return false;
            if (s.getNhat_ky_cham_soc_id() != null) {
                // Đã trừ kho -> xóa luôn nhật ký để hoàn tác tồn kho và diện tích
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE GhiNhanSauBenh SET nhat_ky_cham_soc_id = NULL WHERE id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                conn.commit();
                nhatKyDAO.delete(s.getNhat_ky_cham_soc_id());
                conn.setAutoCommit(false);
            }

            int r;
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM GhiNhanSauBenh WHERE id = ?")) {
                ps.setInt(1, id);
                r = ps.executeUpdate();
            }
            capNhatCoSauBenh(conn, s.getVuon_trong_id());

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

    private int layLoDatCuaVuon(int vuonTrongId) {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT lo_dat_id FROM VuonTrong WHERE id = ?")) {
            ps.setInt(1, vuonTrongId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Lô còn cờ sâu bệnh nếu vẫn còn ghi nhận ở trạng thái chưa/đang xử lý. */
    private void capNhatCoSauBenh(Connection conn, int vuonTrongId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE VuonTrong SET co_sau_benh = CASE WHEN EXISTS ("
              + "   SELECT 1 FROM GhiNhanSauBenh WHERE vuon_trong_id = ? AND trang_thai <> N'Đã xử lý'"
              + ") THEN 1 ELSE 0 END, ngay_cap_nhat = GETDATE() WHERE id = ?")) {
            ps.setInt(1, vuonTrongId);
            ps.setInt(2, vuonTrongId);
            ps.executeUpdate();
        }
    }

    private static String valueOr(String s, String def) {
        return s == null || s.isBlank() ? def : s.trim();
    }

    private void rollback(Connection conn) {
        if (conn != null) try { conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
    }

    private void dong(Connection conn) {
        if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
