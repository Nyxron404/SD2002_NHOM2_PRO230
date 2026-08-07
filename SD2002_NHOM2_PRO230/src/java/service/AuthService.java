package service;

import dao.MaXacThucMatKhauDAO;
import dao.TaiKhoanDAO;
import models.TaiKhoan;
import util.EmailUtil;
import util.PasswordUtil;

/**
 * Xử lý nghiệp vụ đăng nhập / đăng xuất / quên mật khẩu / đổi mật khẩu
 * (UC-0.1, UC-0.2).
 */
public class AuthService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final MaXacThucMatKhauDAO maDAO = new MaXacThucMatKhauDAO();

    private static final int MA_HIEU_LUC_PHUT = 10; // Mã xác thực hiệu lực 10 phút

    /** Kết quả trả về cho tầng controller. */
    public static class KetQua {
        public boolean thanhCong;
        public String thongBao;
        public TaiKhoan taiKhoan;   // dùng khi đăng nhập
        public int taiKhoanId = -1; // dùng cho luồng quên mật khẩu
        public int maId = -1;       // id bản ghi mã xác thực đã xác thực

        public KetQua(boolean thanhCong, String thongBao) {
            this.thanhCong = thanhCong;
            this.thongBao = thongBao;
        }
    }

    /** UC-0.1: Đăng nhập. */
    public KetQua dangNhap(String username, String password) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return new KetQua(false, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu."); // 5a
        }

        TaiKhoan tk = taiKhoanDAO.findByUsername(username.trim());
        if (tk == null || !PasswordUtil.matches(password, tk.getMat_khau())) {
            return new KetQua(false, "Tên đăng nhập hoặc mật khẩu không chính xác."); // 5b
        }
        if (!tk.isTrang_thai()) {
            return new KetQua(false, "Tài khoản đã bị khóa hoặc không còn hiệu lực."); // 5c
        }

        taiKhoanDAO.capNhatLanDangNhapCuoi(tk.getId());
        KetQua kq = new KetQua(true, "Đăng nhập thành công.");
        kq.taiKhoan = tk;
        return kq;
    }

    /** UC-0.1 luồng 3a - bước 1: kiểm tra email + tên đăng nhập, gửi mã xác thực. */
    public KetQua guiMaXacThuc(String email, String username) {
        if (email == null || email.trim().isEmpty()
                || username == null || username.trim().isEmpty()) {
            return new KetQua(false, "Vui lòng nhập đầy đủ email và tên đăng nhập.");
        }
        TaiKhoan tk = taiKhoanDAO.findByEmailAndUsername(email.trim(), username.trim());
        if (tk == null) {
            return new KetQua(false, "Không tìm thấy tài khoản khớp với email và tên đăng nhập.");
        }
        if (!tk.isTrang_thai()) {
            return new KetQua(false, "Tài khoản đã bị khóa, không thể đặt lại mật khẩu.");
        }

        String ma = PasswordUtil.generateVerificationCode();
        boolean luu = maDAO.taoMa(tk.getId(), ma, MA_HIEU_LUC_PHUT);
        if (!luu) {
            return new KetQua(false, "Không tạo được mã xác thực, vui lòng thử lại.");
        }
        EmailUtil.guiMaXacThuc(tk.getNguoiDung().getEmail(), tk.getNguoiDung().getHo_ten(), ma, MA_HIEU_LUC_PHUT);

        KetQua kq = new KetQua(true, "Mã xác thực đã được gửi tới email của bạn.");
        kq.taiKhoanId = tk.getId();
        return kq;
    }

    /** UC-0.1 luồng 3a - bước 2: xác thực mã. */
    public KetQua xacThucMa(int taiKhoanId, String ma) {
        if (ma == null || ma.trim().isEmpty()) {
            return new KetQua(false, "Vui lòng nhập mã xác thực.");
        }
        int maId = maDAO.kiemTraMaHopLe(taiKhoanId, ma.trim());
        if (maId < 0) {
            return new KetQua(false, "Mã xác thực không đúng hoặc đã hết hạn.");
        }
        KetQua kq = new KetQua(true, "Xác thực thành công.");
        kq.taiKhoanId = taiKhoanId;
        kq.maId = maId;
        return kq;
    }

    /** UC-0.1 luồng 3a - bước 3: đặt lại mật khẩu mới sau khi đã xác thực mã. */
    public KetQua datLaiMatKhau(int taiKhoanId, int maId, String matKhauMoi, String nhapLai) {
        KetQua check = kiemTraMatKhauMoi(matKhauMoi, nhapLai);
        if (!check.thanhCong) return check;

        // Xác nhận lại mã còn hợp lệ và đúng bản ghi
        boolean ok = taiKhoanDAO.capNhatMatKhau(taiKhoanId, PasswordUtil.hash(matKhauMoi));
        if (!ok) {
            return new KetQua(false, "Không cập nhật được mật khẩu, vui lòng thử lại.");
        }
        maDAO.danhDauDaSuDung(maId);
        return new KetQua(true, "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.");
    }

    /** Đổi mật khẩu (lần đăng nhập đầu hoặc chủ động đổi). */
    public KetQua doiMatKhau(int taiKhoanId, String matKhauCu, String matKhauMoi, String nhapLai) {
        TaiKhoan tk = taiKhoanDAO.findById(taiKhoanId);
        if (tk == null) {
            return new KetQua(false, "Tài khoản không tồn tại.");
        }
        if (!PasswordUtil.matches(matKhauCu, tk.getMat_khau())) {
            return new KetQua(false, "Mật khẩu hiện tại không chính xác.");
        }
        KetQua check = kiemTraMatKhauMoi(matKhauMoi, nhapLai);
        if (!check.thanhCong) return check;
        if (PasswordUtil.matches(matKhauMoi, tk.getMat_khau())) {
            return new KetQua(false, "Mật khẩu mới phải khác mật khẩu hiện tại.");
        }

        boolean ok = taiKhoanDAO.capNhatMatKhau(taiKhoanId, PasswordUtil.hash(matKhauMoi));
        if (!ok) {
            return new KetQua(false, "Không cập nhật được mật khẩu, vui lòng thử lại.");
        }
        KetQua kq = new KetQua(true, "Đổi mật khẩu thành công.");
        kq.taiKhoan = taiKhoanDAO.findById(taiKhoanId);
        return kq;
    }

    private KetQua kiemTraMatKhauMoi(String matKhauMoi, String nhapLai) {
        if (matKhauMoi == null || matKhauMoi.length() < 6) {
            return new KetQua(false, "Mật khẩu mới phải có ít nhất 6 ký tự.");
        }
        if (!matKhauMoi.equals(nhapLai)) {
            return new KetQua(false, "Mật khẩu nhập lại không khớp.");
        }
        return new KetQua(true, "OK");
    }
}
