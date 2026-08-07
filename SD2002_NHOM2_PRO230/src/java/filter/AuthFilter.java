package filter;

import java.io.IOException;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Bộ lọc xác thực: chặn mọi truy cập khi chưa đăng nhập, chuyển hướng về màn hình đăng nhập.
 *
 * Hiện tại hệ thống chỉ có 1 vai trò Admin và Admin quản lý toàn bộ web, nên chỉ cần
 * kiểm tra "đã đăng nhập" (có tài khoản trong session) là được phép truy cập tất cả chức năng.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"},
        dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class AuthFilter implements Filter {

    private FilterConfig filterConfig = null;

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String context = req.getContextPath();
        String uri = req.getRequestURI();
        // Đường dẫn tính từ gốc ứng dụng, vd: /auth, /taikhoan, /views/...
        String path = uri.substring(context.length());

        if (laDuongDanCongKhai(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean daDangNhap = session != null && session.getAttribute("taiKhoan") != null;

        if (daDangNhap) {
            chain.doFilter(request, response);
        } else {
            // Chưa đăng nhập -> về màn hình đăng nhập
            res.sendRedirect(context + "/auth");
        }
    }

    /** Các đường dẫn cho phép truy cập khi CHƯA đăng nhập. */
    private boolean laDuongDanCongKhai(String path) {
        if (path == null || path.equals("/") || path.isEmpty()) {
            return true;
        }
        // Trang & endpoint xác thực
        if (path.equals("/auth") || path.equals("/logout")) {
            return true;
        }
        if (path.startsWith("/views/auth/")) { // dangnhap.jsp, quenmatkhau.jsp, doimatkhau.jsp
            return true;
        }
        if (path.equals("/index.html") || path.equals("/index.jsp")) {
            return true;
        }
        // Tài nguyên tĩnh (ảnh, css, js, font...) & thư mục upload
        if (path.startsWith("/uploads/")) {
            return true;
        }
        String lower = path.toLowerCase();
        return lower.endsWith(".css") || lower.endsWith(".js") || lower.endsWith(".png")
                || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif")
                || lower.endsWith(".svg") || lower.endsWith(".ico") || lower.endsWith(".webp")
                || lower.endsWith(".woff") || lower.endsWith(".woff2") || lower.endsWith(".ttf");
    }

    @Override
    public void destroy() {
    }
}
