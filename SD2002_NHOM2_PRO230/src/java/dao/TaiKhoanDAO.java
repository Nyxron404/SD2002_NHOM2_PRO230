package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.NguoiDung;
import models.TaiKhoan;
import url.DBConnect;

public class TaiKhoanDAO {

    // Câu SELECT chuẩn có JOIN sang NguoiDung và VaiTro để lấy đủ thông tin hiển thị.
    private static final String SELECT_JOIN =
            "SELECT tk.*, nd.ho_ten, nd.email, nd.so_dien_thoai, nd.ngay_sinh, nd.gioi_tinh, nd.dia_chi, "
            + "nd.ngay_tao AS nd_ngay_tao, nd.ngay_cap_nhat AS nd_ngay_cap_nhat, vt.ten_vai_tro "
            + "FROM TaiKhoan tk "
            + "JOIN NguoiDung nd ON tk.nguoi_dung_id = nd.id "
            + "LEFT JOIN VaiTro vt ON tk.vai_tro_id = vt.id ";

    private TaiKhoan mapRow(ResultSet rs) throws SQLException {
        TaiKhoan tk = new TaiKhoan(
                rs.getInt("id"),
                rs.getInt("nguoi_dung_id"),
                rs.getInt("vai_tro_id"),
                rs.getString("ten_dang_nhap"),
                rs.getString("mat_khau"),
                rs.getBoolean("trang_thai"),
                rs.getBoolean("phai_doi_mat_khau"),
                rs.getTimestamp("lan_dang_nhap_cuoi"),
                rs.getTimestamp("ngay_tao"),
                rs.getTimestamp("ngay_cap_nhat")
        );
        NguoiDung nd = new NguoiDung(
                rs.getInt("nguoi_dung_id"),
                rs.getNString("ho_ten"),
                rs.getString("email"),
                rs.getString("so_dien_thoai"),
                rs.getDate("ngay_sinh"),
                rs.getBoolean("gioi_tinh"),
                rs.getNString("dia_chi"),
                rs.getTimestamp("nd_ngay_tao"),
                rs.getTimestamp("nd_ngay_cap_nhat")
        );
        tk.setNguoiDung(nd);
        tk.setTen_vai_tro(rs.getString("ten_vai_tro"));
        return tk;
    }

    /** Lấy toàn bộ tài khoản (kèm thông tin người dùng, vai trò) - UC-1. */
    public List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY tk.id DESC";
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

    /** Tìm tài khoản theo tên đăng nhập (phục vụ đăng nhập - UC-0.1). */
    public TaiKhoan findByUsername(String username) {
        String sql = SELECT_JOIN + "WHERE tk.ten_dang_nhap = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public TaiKhoan findById(int id) {
        String sql = SELECT_JOIN + "WHERE tk.id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Tìm tài khoản theo email + tên đăng nhập (phục vụ quên mật khẩu - UC-0.1 luồng 3a). */
    public TaiKhoan findByEmailAndUsername(String email, String username) {
        String sql = SELECT_JOIN + "WHERE nd.email = ? AND tk.ten_dang_nhap = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean tenDangNhapTonTai(String username) {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE ten_dang_nhap = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sinh tên đăng nhập duy nhất từ tên gốc: nếu đã tồn tại thì thêm số thứ tự phía sau
     * (UC-1.1 bước 6,7). Có thể loại trừ 1 tài khoản (khi cập nhật chính nó).
     */
    public String taoTenDangNhapDuyNhat(String base, int excludeTaiKhoanId) {
        String candidate = base;
        int i = 1;
        while (tenDangNhapTonTaiKhac(candidate, excludeTaiKhoanId)) {
            candidate = base + i;
            i++;
        }
        return candidate;
    }

    private boolean tenDangNhapTonTaiKhac(String username, int excludeId) {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE ten_dang_nhap = ? AND id <> ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tạo mới: thêm NguoiDung rồi thêm TaiKhoan trong cùng 1 giao dịch (UC-1.1).
     * mat_khau truyền vào đã được băm sẵn.
     */
    public boolean taoTaiKhoan(NguoiDung nd, TaiKhoan tk) {
        String sqlNguoiDung = "INSERT INTO NguoiDung (ho_ten, email, so_dien_thoai, ngay_sinh, gioi_tinh, dia_chi, ngay_tao) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        String sqlTaiKhoan = "INSERT INTO TaiKhoan (nguoi_dung_id, vai_tro_id, ten_dang_nhap, mat_khau, trang_thai, phai_doi_mat_khau, ngay_tao) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            int nguoiDungId;
            try (PreparedStatement ps = conn.prepareStatement(sqlNguoiDung, Statement.RETURN_GENERATED_KEYS)) {
                ps.setNString(1, nd.getHo_ten());
                ps.setString(2, nd.getEmail());
                ps.setString(3, nd.getSo_dien_thoai());
                ps.setDate(4, nd.getNgay_sinh() == null ? null : new java.sql.Date(nd.getNgay_sinh().getTime()));
                ps.setBoolean(5, nd.isGioi_tinh());
                ps.setNString(6, nd.getDia_chi());
                if (ps.executeUpdate() == 0) throw new SQLException("Không thêm được người dùng.");
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Không lấy được id người dùng.");
                    nguoiDungId = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlTaiKhoan)) {
                ps.setInt(1, nguoiDungId);
                ps.setInt(2, tk.getVai_tro_id());
                ps.setString(3, tk.getTen_dang_nhap());
                ps.setString(4, tk.getMat_khau());
                ps.setBoolean(5, tk.isTrang_thai());
                ps.setBoolean(6, tk.isPhai_doi_mat_khau());
                if (ps.executeUpdate() == 0) throw new SQLException("Không thêm được tài khoản.");
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /**
     * Cập nhật thông tin người dùng + tên đăng nhập mới (nếu đổi) trong 1 giao dịch (UC-1.2).
     */
    public boolean capNhatTaiKhoan(NguoiDung nd, TaiKhoan tk) {
        String sqlNguoiDung = "UPDATE NguoiDung SET ho_ten=?, email=?, so_dien_thoai=?, ngay_sinh=?, gioi_tinh=?, dia_chi=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        String sqlTaiKhoan = "UPDATE TaiKhoan SET ten_dang_nhap=?, vai_tro_id=?, ngay_cap_nhat=GETDATE() WHERE id=?";

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlNguoiDung)) {
                ps.setNString(1, nd.getHo_ten());
                ps.setString(2, nd.getEmail());
                ps.setString(3, nd.getSo_dien_thoai());
                ps.setDate(4, nd.getNgay_sinh() == null ? null : new java.sql.Date(nd.getNgay_sinh().getTime()));
                ps.setBoolean(5, nd.isGioi_tinh());
                ps.setNString(6, nd.getDia_chi());
                ps.setInt(7, nd.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlTaiKhoan)) {
                ps.setString(1, tk.getTen_dang_nhap());
                ps.setInt(2, tk.getVai_tro_id());
                ps.setInt(3, tk.getId());
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /** Khóa / mở khóa tài khoản (UC-1.3, UC-1.4). trang_thai: true=hoạt động, false=khóa. */
    public boolean capNhatTrangThai(int taiKhoanId, boolean trangThai) {
        String sql = "UPDATE TaiKhoan SET trang_thai=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, trangThai);
            ps.setInt(2, taiKhoanId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Cập nhật mật khẩu (đã băm) và tắt cờ phải-đổi-mật-khẩu. */
    public boolean capNhatMatKhau(int taiKhoanId, String matKhauDaBam) {
        String sql = "UPDATE TaiKhoan SET mat_khau=?, phai_doi_mat_khau=0, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matKhauDaBam);
            ps.setInt(2, taiKhoanId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Ghi nhận thời điểm đăng nhập gần nhất. */
    public void capNhatLanDangNhapCuoi(int taiKhoanId) {
        String sql = "UPDATE TaiKhoan SET lan_dang_nhap_cuoi=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taiKhoanId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
