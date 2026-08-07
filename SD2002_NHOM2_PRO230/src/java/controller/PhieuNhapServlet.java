package controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import dao.PhieuNhapDAO;
import dao.VatTuDAO;
import models.ChiTietPhieuNhapKho;
import models.PhieuNhap;
import models.VatTu;

@WebServlet(name = "PhieuNhapServlet", urlPatterns = {"/phieunhap"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class PhieuNhapServlet extends HttpServlet {

    private PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();
    private VatTuDAO vatTuDAO = new VatTuDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");

        if (!"insertPhieuNhap".equals(action)) {
            // Luôn trả về JSON hợp lệ, tránh phía JSP gọi res.json() trên body rỗng
            out.print("{\"success\":false,\"error\":\"" + escapeJson("Hành động không hợp lệ: " + action) + "\"}");
            return;
        }

        try {
            // 1. Tự động chuyển đổi file ảnh hóa đơn thành link URL lưu vào database
            String urlAnhHoaDon = luuFileHoaDon(request);

            // 2. Lấy thông tin chứng từ từ form gửi lên
            String maPhieuNhap = batBuoc(request, "ma_phieu_nhap", "Mã phiếu nhập");
            String loaiPhieuNhap = request.getParameter("loai_phieu_nhap"); // Mặc định "Nhập Vật Tư"
            String soHoaDon = batBuoc(request, "so_hoa_don", "Số hóa đơn");
            String mauSo = request.getParameter("mau_so");
            String kyHieu = request.getParameter("ky_hieu");

            String ngayHoaDonStr = batBuoc(request, "ngay_hoa_don", "Ngày hóa đơn");
            Date ngayHoaDon;
            try {
                ngayHoaDon = new SimpleDateFormat("yyyy-MM-dd").parse(ngayHoaDonStr);
            } catch (Exception e) {
                throw new IllegalArgumentException("Ngày hóa đơn không đúng định dạng yyyy-MM-dd.");
            }

            int nhaCungCapId = (int) doiSo(batBuoc(request, "nha_cung_cap_id", "ID Nhà cung cấp"), "ID Nhà cung cấp");
            String maSoThueNcc = request.getParameter("ma_so_thue_ncc");
            String nguoiMuaHang = batBuoc(request, "nguoi_mua_hang", "Người mua hàng");
            String nguoiBanHang = request.getParameter("nguoi_ban_hang");
            String ghiChu = request.getParameter("ghi_chu");

            double tongTienHang = doiSo(request.getParameter("tong_tien_hang"), "Tổng tiền hàng");
            double tienThueGtgt = doiSo(request.getParameter("tien_thue_gtgt"), "Tiền thuế GTGT");
            double tongThanhToan = doiSo(request.getParameter("tong_thanh_toan"), "Tổng thanh toán");

            // 3. Xử lý danh sách chi tiết vật tư truyền qua mảng tham số (không cần Gson)
            String[] vatTuIds = request.getParameterValues("vat_tu_id[]");
            String[] soLuongs = request.getParameterValues("so_luong[]");
            String[] donGias = request.getParameterValues("don_gia[]");
            String[] ngaySanXuats = request.getParameterValues("ngay_san_xuat[]");
            String[] hanSuDungs = request.getParameterValues("han_su_dung[]");

            if (vatTuIds == null || vatTuIds.length == 0) {
                throw new IllegalArgumentException("Chưa có dòng vật tư nào trong phiếu nhập.");
            }
            if (soLuongs == null || soLuongs.length != vatTuIds.length
                    || donGias == null || donGias.length != vatTuIds.length) {
                throw new IllegalArgumentException("Dữ liệu chi tiết vật tư gửi lên không khớp (thiếu số lượng hoặc đơn giá).");
            }

            List<ChiTietPhieuNhapKho> listChiTiet = new ArrayList<>();
            double tongDienTichTieuTon = 0;
            List<VatTu> allVatTu = vatTuDAO.getAll();

            for (int i = 0; i < vatTuIds.length; i++) {
                int dong = i + 1;
                int vtId = (int) doiSo(vatTuIds[i], "Vật tư ở dòng " + dong);
                double sl = doiSo(soLuongs[i], "Số lượng ở dòng " + dong);
                double dg = doiSo(donGias[i], "Đơn giá ở dòng " + dong);
                String nsx = (ngaySanXuats != null && i < ngaySanXuats.length) ? ngaySanXuats[i] : null;
                String hsd = (hanSuDungs != null && i < hanSuDungs.length) ? hanSuDungs[i] : null;

                if (sl <= 0) {
                    throw new IllegalArgumentException("Số lượng ở dòng " + dong + " phải lớn hơn 0.");
                }

                VatTu vatTu = null;
                for (VatTu vt : allVatTu) {
                    if (vt.getId() == vtId) {
                        vatTu = vt;
                        break;
                    }
                }
                if (vatTu == null) {
                    throw new IllegalArgumentException("Không tìm thấy vật tư có ID = " + vtId + " (dòng " + dong + ").");
                }

                ChiTietPhieuNhapKho ct = new ChiTietPhieuNhapKho();
                ct.setVat_tu_id(vtId);
                ct.setSo_luong(sl);
                ct.setDon_gia(dg);
                ct.setThanh_tien(sl * dg);
                ct.setNgay_san_xuat(nsx);
                ct.setHan_su_dung(hsd);
                ct.setVi_tri_luu_tru_id(vatTu.getVi_tri_luu_tru_mac_dinh_id());

                // Tự động tính toán diện tích tiêu tốn dựa vào diện tích chiếm dụng của từng vật tư
                double dienTichDong = sl * vatTu.getDien_tich_chiem_dung();
                ct.setDien_tich_tieu_ton(dienTichDong);
                tongDienTichTieuTon += dienTichDong;

                listChiTiet.add(ct);
            }

            // 4. Khởi tạo đối tượng PhieuNhap đầy đủ
            PhieuNhap pnk = new PhieuNhap();
            pnk.setMa_phieu_nhap(maPhieuNhap);
            pnk.setLoai_phieu_nhap(loaiPhieuNhap != null ? loaiPhieuNhap : "Nhập Vật Tư");
            pnk.setNha_cung_cap_id(nhaCungCapId);
            pnk.setSo_hoa_don(soHoaDon);
            pnk.setMau_so(mauSo != null ? mauSo : "");
            pnk.setKy_hieu(kyHieu != null ? kyHieu : "");
            pnk.setNgay_hoa_don(ngayHoaDon);
            pnk.setMa_so_thue_ncc(maSoThueNcc != null ? maSoThueNcc : "");
            pnk.setTong_tien_hang(tongTienHang);
            pnk.setTien_thue_gtgt(tienThueGtgt);
            pnk.setTong_thanh_toan(tongThanhToan);
            pnk.setNguoi_mua_hang(nguoiMuaHang);
            pnk.setNguoi_ban_hang(nguoiBanHang != null ? nguoiBanHang : "");
            pnk.setUrl_anh_hoa_don(urlAnhHoaDon); // Link URL ảnh hoàn chỉnh
            pnk.setNguoi_lap_phieu_id(layIdNguoiLap(request)); // Lấy từ session, nếu chưa đăng nhập thì DAO tự chọn
            pnk.setTong_dien_tich_tieu_ton(tongDienTichTieuTon); // Tổng diện tích tự động tính toán
            pnk.setTrang_thai("Đã nhập kho");
            pnk.setGhi_chu(ghiChu != null ? ghiChu : "");

            // 5. Lưu vào Database qua DAO
            int phieuNhapId = phieuNhapDAO.insertPhieuNhap(pnk, listChiTiet);
            out.print("{\"success\":true,\"id\":" + phieuNhapId + "}");

        } catch (Exception e) {
            e.printStackTrace();
            String message = (e.getMessage() != null && !e.getMessage().trim().isEmpty())
                    ? e.getMessage()
                    : e.getClass().getSimpleName();
            out.print("{\"success\":false,\"error\":\"" + escapeJson(message) + "\"}");
        }
    }

    /** Lưu file hóa đơn vào thư mục uploads, trả về đường dẫn tương đối để ghi vào database. */
    private String luuFileHoaDon(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("anh_hoa_don");
        if (filePart == null || filePart.getSize() <= 0 || filePart.getSubmittedFileName() == null) {
            return "";
        }

        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        // Thêm mốc thời gian để 2 hóa đơn trùng tên file không ghi đè lên nhau
        fileName = System.currentTimeMillis() + "_" + fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        filePart.write(uploadPath + File.separator + fileName);

        return "uploads/" + fileName;
    }

    /** Lấy id người lập phiếu từ session (nếu đã có chức năng đăng nhập), 0 nghĩa là để DAO tự xác định. */
    private int layIdNguoiLap(HttpServletRequest request) {
        Object id = request.getSession().getAttribute("taiKhoanId");
        if (id instanceof Integer) {
            return (Integer) id;
        }
        if (id != null) {
            try {
                return Integer.parseInt(id.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    private String batBuoc(HttpServletRequest request, String param, String tenHienThi) {
        String value = request.getParameter(param);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập " + tenHienThi + ".");
        }
        return value.trim();
    }

    private double doiSo(String value, String tenHienThi) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(tenHienThi + " phải là số hợp lệ (đang nhận: “" + value + "”).");
        }
    }

    /** Escape chuỗi để JSON trả về luôn hợp lệ (thông báo lỗi của SQL Server hay có dấu nháy, xuống dòng). */
    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}