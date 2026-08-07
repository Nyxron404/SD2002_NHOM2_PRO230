package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.NguoiDung;
import models.TaiKhoan;
import service.TaiKhoanService;

/**
 * UC-1: Quản lý tài khoản (Thêm / Cập nhật / Khóa / Mở khóa).
 *   GET  /taikhoan  -> hiển thị danh sách tài khoản
 *   POST /taikhoan  action=insert|update|lock|unlock -> AJAX/JSON
 */
@WebServlet(name = "TaiKhoanServlet", urlPatterns = {"/taikhoan"})
public class TaiKhoanServlet extends HttpServlet {

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        List<TaiKhoan> listTaiKhoan = taiKhoanService.getAll();
        request.setAttribute("listTaiKhoan", listTaiKhoan);
        request.getRequestDispatcher("/views/taiKhoan/taikhoan.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        if (action == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Thiếu action.\"}");
            return;
        }

        try {
            TaiKhoanService.KetQua kq;
            switch (action) {
                case "insert": {
                    NguoiDung nd = docNguoiDung(request);
                    kq = taiKhoanService.themTaiKhoan(nd);
                    break;
                }
                case "update": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    NguoiDung nd = docNguoiDung(request);
                    kq = taiKhoanService.capNhatTaiKhoan(id, nd);
                    break;
                }
                case "lock": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    kq = taiKhoanService.khoaTaiKhoan(id);
                    break;
                }
                case "unlock": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    kq = taiKhoanService.moKhoaTaiKhoan(id);
                    break;
                }
                default:
                    out.print("{\"success\":false,\"message\":\"Action không hợp lệ.\"}");
                    return;
            }
            out.print("{\"success\":" + kq.thanhCong + ",\"message\":\"" + escape(kq.thongBao) + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\":false,\"message\":\"" + escape("Lỗi xử lý: " + e.getMessage()) + "\"}");
        }
    }

    private NguoiDung docNguoiDung(HttpServletRequest request) throws Exception {
        NguoiDung nd = new NguoiDung();
        nd.setHo_ten(trim(request.getParameter("ho_ten")));
        nd.setEmail(trim(request.getParameter("email")));
        nd.setSo_dien_thoai(trim(request.getParameter("so_dien_thoai")));

        String ngaySinh = request.getParameter("ngay_sinh");
        if (ngaySinh != null && !ngaySinh.isEmpty()) {
            nd.setNgay_sinh(new SimpleDateFormat("yyyy-MM-dd").parse(ngaySinh));
        }
        // gioi_tinh: "1" = Nam (true), "0" = Nữ (false)
        nd.setGioi_tinh("1".equals(request.getParameter("gioi_tinh")));
        nd.setDia_chi(trim(request.getParameter("dia_chi")));
        return nd;
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }

    @Override
    public String getServletInfo() {
        return "Quản lý tài khoản người dùng";
    }
}
