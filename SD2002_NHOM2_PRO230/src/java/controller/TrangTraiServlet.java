package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import models.DonViQuanLy;
import models.KhuVuc;
import models.TrangTrai;
import service.TrangTraiService;

/**
 * UC-2.1 Thông tin trang trại | UC-2.2 Phân chia khu vực | UC-2.3 Quản lý khu vực chi tiết
 */
@WebServlet(name = "TrangTraiServlet", urlPatterns = {"/trangtrai"})
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,      // 5MB / ảnh
        maxRequestSize = 12 * 1024 * 1024   // tổng request (2 ảnh + text)
)
public class TrangTraiServlet extends HttpServlet {

    // Thư mục lưu ảnh, tính từ gốc ứng dụng web (webapp root)
    private static final String THU_MUC_UPLOAD = "/uploads/trangtrai/";

    private final TrangTraiService service = new TrangTraiService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        napDuLieuVaHienThi(request, response, null);
    }

    private void napDuLieuVaHienThi(HttpServletRequest request, HttpServletResponse response, String thongBaoLoiTrangTrai)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        TrangTrai trangTrai = service.getTrangTrai();
        request.setAttribute("trangTrai", trangTrai);
        request.setAttribute("loiTrangTrai", thongBaoLoiTrangTrai);

        List<KhuVuc> danhSachKhuVuc;
        if (trangTrai != null) {
            danhSachKhuVuc = service.getDanhSachKhuVuc(trangTrai.getId());
        } else {
            danhSachKhuVuc = new java.util.ArrayList<>();
        }
        request.setAttribute("danhSachKhuVuc", danhSachKhuVuc);

        // Nạp trước toàn bộ đơn vị quản lý của mọi khu vực (dùng để mở nhanh modal chi tiết khu vực - UC-2.3)
        List<DonViQuanLy> danhSachDonVi = new java.util.ArrayList<>();
        for (KhuVuc kv : danhSachKhuVuc) {
            danhSachDonVi.addAll(service.getDonViTheoKhuVuc(kv.getId()));
        }
        request.setAttribute("danhSachDonVi", danhSachDonVi);

        request.getRequestDispatcher("./views/trangTrai/quanLyTrangTrai.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        // ---------- UC-2.1: form thường (không AJAX) vì có upload ảnh ----------
        if ("capNhatTrangTrai".equals(action)) {
            xuLyCapNhatTrangTrai(request, response);
            return;
        }

        // ---------- UC-2.2 / UC-2.3: giữ nguyên cơ chế AJAX trả JSON ----------
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            switch (action == null ? "" : action) {
                case "themKhuVuc":
                case "suaKhuVuc": {
                    boolean isUpdate = "suaKhuVuc".equals(action);
                    KhuVuc kv = new KhuVuc();
                    if (isUpdate) kv.setId(Integer.parseInt(request.getParameter("id")));
                    kv.setTen_khu_vuc(request.getParameter("ten_khu_vuc"));
                    kv.setLoai_khu_vuc(request.getParameter("loai_khu_vuc"));
                    kv.setDien_tich_khai_thac(Double.parseDouble(request.getParameter("dien_tich_khai_thac")));
                    kv.setDien_tich_chiem_dung(Double.parseDouble(request.getParameter("dien_tich_chiem_dung")));
                    kv.setMo_ta(request.getParameter("mo_ta"));
                    String loi = service.luuKhuVuc(kv, isUpdate);
                    writeResult(out, loi);
                    break;
                }
                case "xoaKhuVuc": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String loi = service.xoaKhuVuc(id);
                    writeResult(out, loi);
                    break;
                }
                case "themDonVi":
                case "suaDonVi": {
                    boolean isUpdate = "suaDonVi".equals(action);
                    DonViQuanLy dv = new DonViQuanLy();
                    if (isUpdate) dv.setId(Integer.parseInt(request.getParameter("id")));
                    dv.setKhu_vuc_id(Integer.parseInt(request.getParameter("khu_vuc_id")));
                    dv.setLoai_don_vi(request.getParameter("loai_don_vi"));
                    dv.setMa_don_vi(request.getParameter("ma_don_vi"));
                    dv.setTen_don_vi(request.getParameter("ten_don_vi"));
                    dv.setDien_tich(Double.parseDouble(request.getParameter("dien_tich")));
                    String sucChua = request.getParameter("suc_chua");
                    dv.setSuc_chua(sucChua == null || sucChua.isEmpty() ? 0 : Double.parseDouble(sucChua));
                    dv.setTrang_thai(request.getParameter("trang_thai") != null ? request.getParameter("trang_thai") : "Đang hoạt động");
                    String loi = service.luuDonVi(dv, isUpdate);
                    writeResult(out, loi);
                    break;
                }
                case "xoaDonVi": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String loi = service.xoaDonVi(id);
                    writeResult(out, loi);
                    break;
                }
                default:
                    out.print("{\"success\":false,\"error\":\"Hành động không hợp lệ.\"}");
            }
        } catch (Exception e) {
            out.print("{\"success\":false,\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }

    private void xuLyCapNhatTrangTrai(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            TrangTrai tt = new TrangTrai();
            tt.setTen_trang_trai(trim(request.getParameter("ten_trang_trai")));
            tt.setMa_so_thue(trim(request.getParameter("ma_so_thue")));
            tt.setMa_so_vung_trong(trim(request.getParameter("ma_so_vung_trong")));
            tt.setNguoi_dai_dien(trim(request.getParameter("nguoi_dai_dien")));
            tt.setChuc_vu_dai_dien(trim(request.getParameter("chuc_vu_dai_dien")));
            tt.setDia_chi(trim(request.getParameter("dia_chi")));
            tt.setEmail(trim(request.getParameter("email")));
            tt.setSo_dien_thoai(trim(request.getParameter("so_dien_thoai")));
            tt.setToa_do_gps(trim(request.getParameter("toa_do_gps")));
            tt.setNong_san_chu_luc(trim(request.getParameter("nong_san_chu_luc")));
            tt.setLinh_vuc_hoat_dong(trim(request.getParameter("linh_vuc_hoat_dong")));
            tt.setMo_ta(trim(request.getParameter("mo_ta")));
            tt.setChung_nhan(trim(request.getParameter("chung_nhan")));
            tt.setTrang_thai(trim(request.getParameter("trang_thai")));

            String dienTich = request.getParameter("dien_tich_tong");
            tt.setDien_tich_tong(dienTich == null || dienTich.isEmpty() ? 0 : Double.parseDouble(dienTich));

            String namThanhLap = request.getParameter("nam_thanh_lap");
            tt.setNam_thanh_lap(namThanhLap == null || namThanhLap.trim().isEmpty() ? null : Integer.parseInt(namThanhLap.trim()));

            // Ảnh: nếu người dùng không chọn file mới thì để null -> Service sẽ tự giữ ảnh cũ
            String duongDanLogo = luuAnhUpload(request, "logo");
            if (duongDanLogo != null) tt.setLogo(duongDanLogo);
            String duongDanBanDo = luuAnhUpload(request, "anh_ban_do");
            if (duongDanBanDo != null) tt.setAnh_ban_do(duongDanBanDo);

            String loi = service.capNhatTrangTrai(tt);
            napDuLieuVaHienThi(request, response, loi);
        } catch (NumberFormatException e) {
            napDuLieuVaHienThi(request, response, "Dữ liệu số không hợp lệ. Vui lòng kiểm tra lại diện tích / năm thành lập.");
        } catch (Exception e) {
            napDuLieuVaHienThi(request, response, "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    /**
     * Lưu file ảnh upload (nếu có) vào thư mục /uploads/trangtrai/ của ứng dụng web.
     * Trả về đường dẫn tương đối (VD: "uploads/trangtrai/xxx.jpg") để lưu vào DB,
     * hoặc null nếu người dùng không chọn ảnh mới (giữ nguyên ảnh cũ).
     */
    private String luuAnhUpload(HttpServletRequest request, String tenTruong) throws IOException, ServletException {
        Part part = request.getPart(tenTruong);
        if (part == null || part.getSize() <= 0) {
            return null;
        }
        String contentType = part.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Tệp \"" + tenTruong + "\" phải là hình ảnh (jpg, png, gif, webp...).");
        }

        String tenGoc = part.getSubmittedFileName();
        String duoiFile = "";
        if (tenGoc != null && tenGoc.contains(".")) {
            duoiFile = tenGoc.substring(tenGoc.lastIndexOf('.'));
        }
        String tenFileMoi = tenTruong + "_" + UUID.randomUUID().toString().replace("-", "") + duoiFile;

        String duongDanThucTe = getServletContext().getRealPath(THU_MUC_UPLOAD);
        Path thuMuc = Path.of(duongDanThucTe);
        if (!Files.exists(thuMuc)) {
            Files.createDirectories(thuMuc);
        }
        Path fileDich = thuMuc.resolve(tenFileMoi);
        try (var input = part.getInputStream()) {
            Files.copy(input, fileDich);
        }

        return "uploads/trangtrai/" + tenFileMoi;
    }

    private void writeResult(PrintWriter out, String loi) {
        if (loi == null) {
            out.print("{\"success\":true}");
        } else {
            out.print("{\"success\":false,\"error\":\"" + loi.replace("\"", "\\\"") + "\"}");
        }
    }
}