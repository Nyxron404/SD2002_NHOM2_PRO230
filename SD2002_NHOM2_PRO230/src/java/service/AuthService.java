package service;

import dao.MaXacThucMatKhauDAO;
import dao.TaiKhoanDAO;
import models.TaiKhoan;
import util.EmailUtil;
import util.PasswordUtil;

public class AuthService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final MaXacThucMatKhauDAO maDAO = new MaXacThucMatKhauDAO();

    private static final int MA_HIEU_LUC_PHUT = 10; 

    public static class KetQua {
        public boolean thanhCong;
        public String thongBao;
        public TaiKhoan taiKhoan;   
        public int taiKhoanId = -1; 
        public int maId = -1;       

        public KetQua(boolean thanhCong, String thongBao) {
            this.thanhCong = thanhCong;
            this.thongBao = thongBao;
        }
    }

    public KetQua dangNhap(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return new KetQua(false, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu."); 
        }

        TaiKhoan tk = taiKhoanDAO.findByUsername(username.trim());

        // ================================================================
        // ĐOẠN DEBUG: IN RA CONSOLE CỦA NETBEANS ĐỂ KIỂM TRA LỖI NẰM Ở ĐÂU
        // ================================================================
        System.out.println("====== KIỂM TRA ĐĂNG NHẬP ======");
        System.out.println("1. Bạn đang nhập Tài khoản: [" + username + "] - Mật khẩu: [" + password + "]");
        
        if (tk == null) {
            System.out.println("-> KẾT QUẢ: LỖI! Tên đăng nhập này KHÔNG TỒN TẠI trong Database!");
            return new KetQua(false, "Tên đăng nhập hoặc mật khẩu không chính xác."); 
        } else {
            System.out.println("-> Tồn tại user trong DB! Mật khẩu đang lưu trong DB là: [" + tk.getMat_khau() + "]");
            if (!PasswordUtil.matches(password, tk.getMat_khau())) {
                System.out.println("-> KẾT QUẢ: LỖI! Sai mật khẩu rồi bạn nhé!");
                return new KetQua(false, "Tên đăng nhập hoặc mật khẩu không chính xác."); 
            }
        }

        if (!tk.isTrang_thai()) {
            System.out.println("-> KẾT QUẢ: LỖI! Tài khoản đã bị khóa!");
            return new KetQua(false, "Tài khoản đã bị khóa hoặc không còn hiệu lực."); 
        }

        String vaiTro = tk.getTen_vai_tro();
        System.out.println("-> Vai trò của user này là: [" + vaiTro + "]");
        
        if (vaiTro == null || vaiTro.trim().isEmpty()) {
            return new KetQua(false, "Tài khoản chưa được gán vai trò. Vui lòng liên hệ quản trị viên.");
        }
        if (!"Admin".equalsIgnoreCase(vaiTro.trim()) && !"Quản trị viên".equalsIgnoreCase(vaiTro.trim())) {
            System.out.println("-> KẾT QUẢ: LỖI! Sai quyền truy cập (Không phải Admin).");
            return new KetQua(false, "Tài khoản không có quyền truy cập hệ thống.");
        }

        System.out.println("-> KẾT QUẢ: ĐĂNG NHẬP THÀNH CÔNG RỒI!");
        System.out.println("=================================");
        // ================================================================

        taiKhoanDAO.capNhatLanDangNhapCuoi(tk.getId());
        KetQua kq = new KetQua(true, "Đăng nhập thành công.");
        kq.taiKhoan = tk;
        return kq;
    }

    public KetQua guiMaXacThuc(String email, String username) {
        if (email == null || email.trim().isEmpty() || username == null || username.trim().isEmpty()) {
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

    public KetQua datLaiMatKhau(int taiKhoanId, int maId, String matKhauMoi, String nhapLai) {
        KetQua check = kiemTraMatKhauMoi(matKhauMoi, nhapLai);
        if (!check.thanhCong) {
            return check;
        }

        boolean ok = taiKhoanDAO.capNhatMatKhau(taiKhoanId, matKhauMoi);
        if (!ok) {
            return new KetQua(false, "Không cập nhật được mật khẩu, vui lòng thử lại.");
        }
        maDAO.danhDauDaSuDung(maId);
        return new KetQua(true, "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.");
    }

    public KetQua doiMatKhau(int taiKhoanId, String matKhauCu, String matKhauMoi, String nhapLai) {
        TaiKhoan tk = taiKhoanDAO.findById(taiKhoanId);
        if (tk == null) {
            return new KetQua(false, "Tài khoản không tồn tại.");
        }
        if (!PasswordUtil.matches(matKhauCu, tk.getMat_khau())) {
            return new KetQua(false, "Mật khẩu hiện tại không chính xác.");
        }
        KetQua check = kiemTraMatKhauMoi(matKhauMoi, nhapLai);
        if (!check.thanhCong) {
            return check;
        }
        if (PasswordUtil.matches(matKhauMoi, tk.getMat_khau())) {
            return new KetQua(false, "Mật khẩu mới phải khác mật khẩu hiện tại.");
        }

        boolean ok = taiKhoanDAO.capNhatMatKhau(taiKhoanId, matKhauMoi);
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