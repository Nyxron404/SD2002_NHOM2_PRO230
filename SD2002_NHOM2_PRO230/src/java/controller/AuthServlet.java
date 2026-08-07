package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.TaiKhoan;
import service.AuthService;

/**
 * Xử lý Đăng nhập / Đăng xuất / Đổi mật khẩu lần đầu / Quên mật khẩu.
 * URL:
 *   GET  /auth                    -> hiển thị màn hình đăng nhập
 *   GET  /auth?view=doimatkhau    -> màn hình bắt buộc đổi mật khẩu (sau đăng nhập lần đầu)
 *   POST /auth  action=login      -> đăng nhập (form submit)
 *   POST /auth  action=changepassword -> đổi mật khẩu lần đầu (form submit)
 *   POST /auth  action=forgot|verify|reset -> luồng quên mật khẩu (AJAX/JSON)
 *   GET  /logout                  -> đăng xuất
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth", "/logout"})
public class AuthServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        // ĐĂNG XUẤT (UC-0.2)
        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        // /auth
        HttpSession session = request.getSession();
        String view = request.getParameter("view");

        if ("doimatkhau".equals(view)) {
            if (session.getAttribute("pendingTaiKhoanId") == null) {
                response.sendRedirect(request.getContextPath() + "/auth");
                return;
            }
            request.getRequestDispatcher("/views/auth/doimatkhau.jsp").forward(request, response);
            return;
        }

        // Đã đăng nhập rồi thì vào thẳng trang quản lý
        if (session.getAttribute("taiKhoan") != null) {
            response.sendRedirect(request.getContextPath() + "/taikhoan");
            return;
        }
        request.getRequestDispatcher("/views/auth/dangnhap.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) action = "login";

        switch (action) {
            case "login":
                xuLyDangNhap(request, response);
                break;
            case "changepassword":
                xuLyDoiMatKhau(request, response);
                break;
            case "forgot":
                xuLyQuenMatKhau(request, response);
                break;
            case "verify":
                xuLyXacThucMa(request, response);
                break;
            case "reset":
                xuLyDatLaiMatKhau(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // ================= UC-0.1 ĐĂNG NHẬP =================
    private void xuLyDangNhap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        AuthService.KetQua kq = authService.dangNhap(username, password);
        if (!kq.thanhCong) {
            request.setAttribute("error", kq.thongBao);
            request.setAttribute("username", username);
            request.getRequestDispatcher("/views/auth/dangnhap.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        TaiKhoan tk = kq.taiKhoan;

        // Lần đăng nhập đầu -> bắt buộc đổi mật khẩu (UC-1.1 lưu ý)
        if (tk.isPhai_doi_mat_khau()) {
            session.setAttribute("pendingTaiKhoanId", tk.getId());
            session.setAttribute("pendingHoTen", tk.getNguoiDung().getHo_ten());
            session.setAttribute("pendingUsername", tk.getTen_dang_nhap());
            response.sendRedirect(request.getContextPath() + "/auth?view=doimatkhau");
            return;
        }

        thietLapPhienDangNhap(session, tk);
        response.sendRedirect(request.getContextPath() + "/taikhoan");
    }

    // ============ ĐỔI MẬT KHẨU LẦN ĐẦU ============
    private void xuLyDoiMatKhau(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object pending = session.getAttribute("pendingTaiKhoanId");
        if (pending == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }
        int taiKhoanId = (int) pending;
        String matKhauCu = request.getParameter("matKhauCu");
        String matKhauMoi = request.getParameter("matKhauMoi");
        String nhapLai = request.getParameter("nhapLai");

        AuthService.KetQua kq = authService.doiMatKhau(taiKhoanId, matKhauCu, matKhauMoi, nhapLai);
        if (!kq.thanhCong) {
            request.setAttribute("error", kq.thongBao);
            request.getRequestDispatcher("/views/auth/doimatkhau.jsp").forward(request, response);
            return;
        }

        // Đổi thành công -> thiết lập phiên đăng nhập đầy đủ, vào trang quản lý
        session.removeAttribute("pendingTaiKhoanId");
        session.removeAttribute("pendingHoTen");
        session.removeAttribute("pendingUsername");
        thietLapPhienDangNhap(session, kq.taiKhoan);
        response.sendRedirect(request.getContextPath() + "/taikhoan");
    }

    // ============ UC-0.1 (3a) QUÊN MẬT KHẨU - GỬI MÃ ============
    private void xuLyQuenMatKhau(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String username = request.getParameter("username");

        AuthService.KetQua kq = authService.guiMaXacThuc(email, username);
        if (kq.thanhCong) {
            HttpSession session = request.getSession();
            session.setAttribute("fp_taiKhoanId", kq.taiKhoanId);
            session.removeAttribute("fp_verified");
            session.removeAttribute("fp_maId");
        }
        writeJson(response, kq.thanhCong, kq.thongBao);
    }

    // ============ XÁC THỰC MÃ ============
    private void xuLyXacThucMa(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Object tkId = session.getAttribute("fp_taiKhoanId");
        if (tkId == null) {
            writeJson(response, false, "Phiên làm việc đã hết hạn, vui lòng thực hiện lại.");
            return;
        }
        String ma = request.getParameter("ma");
        AuthService.KetQua kq = authService.xacThucMa((int) tkId, ma);
        if (kq.thanhCong) {
            session.setAttribute("fp_verified", Boolean.TRUE);
            session.setAttribute("fp_maId", kq.maId);
        }
        writeJson(response, kq.thanhCong, kq.thongBao);
    }

    // ============ ĐẶT LẠI MẬT KHẨU ============
    private void xuLyDatLaiMatKhau(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Object tkId = session.getAttribute("fp_taiKhoanId");
        Object maId = session.getAttribute("fp_maId");
        Boolean verified = (Boolean) session.getAttribute("fp_verified");
        if (tkId == null || maId == null || verified == null || !verified) {
            writeJson(response, false, "Bạn chưa xác thực mã, vui lòng thực hiện lại.");
            return;
        }
        String matKhauMoi = request.getParameter("matKhauMoi");
        String nhapLai = request.getParameter("nhapLai");

        AuthService.KetQua kq = authService.datLaiMatKhau((int) tkId, (int) maId, matKhauMoi, nhapLai);
        if (kq.thanhCong) {
            session.removeAttribute("fp_taiKhoanId");
            session.removeAttribute("fp_maId");
            session.removeAttribute("fp_verified");
        }
        writeJson(response, kq.thanhCong, kq.thongBao);
    }

    // ================= Tiện ích =================
    private void thietLapPhienDangNhap(HttpSession session, TaiKhoan tk) {
        // Không lưu mật khẩu vào session
        tk.setMat_khau(null);
        session.setAttribute("taiKhoan", tk);
        session.setAttribute("hoTen", tk.getNguoiDung().getHo_ten());
        session.setAttribute("vaiTro", tk.getTen_vai_tro());
    }

    private void writeJson(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String safe = message == null ? "" : message.replace("\\", "\\\\").replace("\"", "\\\"");
            out.print("{\"success\":" + success + ",\"message\":\"" + safe + "\"}");
        }
    }

    @Override
    public String getServletInfo() {
        return "Xử lý xác thực người dùng (đăng nhập, đăng xuất, quên mật khẩu)";
    }
}
