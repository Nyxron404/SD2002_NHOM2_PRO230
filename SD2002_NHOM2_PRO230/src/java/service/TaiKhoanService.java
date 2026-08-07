package service;

import java.util.List;
import dao.NguoiDungDAO;
import dao.TaiKhoanDAO;
import dao.VaiTroDAO;
import models.NguoiDung;
import models.TaiKhoan;
import util.EmailUtil;
import util.PasswordUtil;

/**
 * Nghiệp vụ quản lý tài khoản (UC-1.1 Thêm, UC-1.2 Cập nhật, UC-1.3 Khóa, UC-1.4 Mở khóa).
 */
public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();
    private final VaiTroDAO vaiTroDAO = new VaiTroDAO();

    public static class KetQua {
        public boolean thanhCong;
        public String thongBao;
        public KetQua(boolean thanhCong, String thongBao) {
            this.thanhCong = thanhCong;
            this.thongBao = thongBao;
        }
    }

    public List<TaiKhoan> getAll() {
        return taiKhoanDAO.getAll();
    }

    /** UC-1.1: Thêm tài khoản. Tự sinh tên đăng nhập + mật khẩu mặc định + gửi email. */
    public KetQua themTaiKhoan(NguoiDung nd) {
        KetQua v = kiemTraNguoiDung(nd, 0);
        if (!v.thanhCong) return v;

        if (nguoiDungDAO.emailTonTai(nd.getEmail())) {
            return new KetQua(false, "Email đã được sử dụng cho tài khoản khác.");
        }

        // Sinh tên đăng nhập duy nhất từ họ tên (bước 6,7)
        String base = PasswordUtil.generateBaseUsername(nd.getHo_ten());
        String username = taiKhoanDAO.taoTenDangNhapDuyNhat(base, 0);

        // Sinh mật khẩu mặc định 8 chữ số (bước 8)
        String matKhauTho = PasswordUtil.generateDefaultPassword();

        TaiKhoan tk = new TaiKhoan();
        tk.setVai_tro_id(vaiTroDAO.getAdminId()); // hiện tại chỉ có vai trò Admin
        tk.setTen_dang_nhap(username);
        tk.setMat_khau(PasswordUtil.hash(matKhauTho));
        tk.setTrang_thai(true);        // hoạt động
        tk.setPhai_doi_mat_khau(true); // buộc đổi mật khẩu lần đầu

        boolean ok = taiKhoanDAO.taoTaiKhoan(nd, tk);
        if (!ok) {
            return new KetQua(false, "Không tạo được tài khoản. Vui lòng thử lại.");
        }

        // Gửi email thông tin đăng nhập (bước 10)
        EmailUtil.guiThongTinDangNhap(nd.getEmail(), nd.getHo_ten(), username, matKhauTho);

        return new KetQua(true, "Tạo tài khoản thành công! Tên đăng nhập \"" + username
                + "\" và mật khẩu mặc định đã được gửi tới email " + nd.getEmail() + ".");
    }

    /**
     * UC-1.2: Cập nhật tài khoản. Nếu họ tên thay đổi thì sinh lại tên đăng nhập và
     * gửi lại tên đăng nhập mới vào email.
     */
    public KetQua capNhatTaiKhoan(int taiKhoanId, NguoiDung nd) {
        TaiKhoan tkCu = taiKhoanDAO.findById(taiKhoanId);
        if (tkCu == null) {
            return new KetQua(false, "Không tìm thấy tài khoản cần cập nhật.");
        }
        nd.setId(tkCu.getNguoi_dung_id());

        KetQua v = kiemTraNguoiDung(nd, tkCu.getNguoi_dung_id());
        if (!v.thanhCong) return v;

        if (nguoiDungDAO.emailTonTaiKhac(nd.getEmail(), tkCu.getNguoi_dung_id())) {
            return new KetQua(false, "Email đã được sử dụng cho tài khoản khác.");
        }

        // Nếu họ tên thay đổi -> sinh lại tên đăng nhập theo quy tắc, đảm bảo không trùng
        boolean doiTen = !normalize(tkCu.getNguoiDung().getHo_ten()).equals(normalize(nd.getHo_ten()));
        String usernameMoi = tkCu.getTen_dang_nhap();
        if (doiTen) {
            String base = PasswordUtil.generateBaseUsername(nd.getHo_ten());
            usernameMoi = taiKhoanDAO.taoTenDangNhapDuyNhat(base, taiKhoanId);
        }

        TaiKhoan tk = new TaiKhoan();
        tk.setId(taiKhoanId);
        tk.setTen_dang_nhap(usernameMoi);
        tk.setVai_tro_id(tkCu.getVai_tro_id());

        boolean ok = taiKhoanDAO.capNhatTaiKhoan(nd, tk);
        if (!ok) {
            return new KetQua(false, "Không cập nhật được tài khoản. Vui lòng thử lại.");
        }

        if (doiTen && !usernameMoi.equals(tkCu.getTen_dang_nhap())) {
            EmailUtil.guiTenDangNhapMoi(nd.getEmail(), nd.getHo_ten(), usernameMoi);
            return new KetQua(true, "Cập nhật thành công! Tên đăng nhập mới \"" + usernameMoi
                    + "\" đã được gửi tới email người dùng.");
        }
        return new KetQua(true, "Cập nhật thông tin tài khoản thành công.");
    }

    /** UC-1.3: Khóa tài khoản. */
    public KetQua khoaTaiKhoan(int taiKhoanId) {
        boolean ok = taiKhoanDAO.capNhatTrangThai(taiKhoanId, false);
        return ok ? new KetQua(true, "Đã khóa tài khoản.")
                  : new KetQua(false, "Không khóa được tài khoản.");
    }

    /** UC-1.4: Mở khóa tài khoản. */
    public KetQua moKhoaTaiKhoan(int taiKhoanId) {
        boolean ok = taiKhoanDAO.capNhatTrangThai(taiKhoanId, true);
        return ok ? new KetQua(true, "Đã mở khóa tài khoản.")
                  : new KetQua(false, "Không mở khóa được tài khoản.");
    }

    private KetQua kiemTraNguoiDung(NguoiDung nd, int idHienTai) {
        if (nd.getHo_ten() == null || nd.getHo_ten().trim().isEmpty()) {
            return new KetQua(false, "Vui lòng nhập họ tên.");
        }
        if (nd.getEmail() == null || !nd.getEmail().matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$")) {
            return new KetQua(false, "Email không hợp lệ.");
        }
        if (nd.getSo_dien_thoai() == null || !nd.getSo_dien_thoai().matches("^0\\d{9}$")) {
            return new KetQua(false, "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.");
        }
        if (nd.getNgay_sinh() == null) {
            return new KetQua(false, "Vui lòng chọn ngày sinh.");
        }
        if (nd.getDia_chi() == null || nd.getDia_chi().trim().isEmpty()) {
            return new KetQua(false, "Vui lòng nhập địa chỉ.");
        }
        return new KetQua(true, "OK");
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
