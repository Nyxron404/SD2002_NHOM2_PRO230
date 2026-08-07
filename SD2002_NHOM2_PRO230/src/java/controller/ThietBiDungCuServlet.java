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

import models.DungCu;
import models.NhaCungCap;
import models.PhieuNhap;
import models.ChiTietPhieuNhapDungCu;
import service.DungCuService;
import dao.NhaCungCapDAO;
import dao.PhieuNhapDAO;

@WebServlet(name = "ThietBiDungCuServlet", urlPatterns = {"/tbdc"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class ThietBiDungCuServlet extends HttpServlet {

    private DungCuService dungCuService = new DungCuService();
    private NhaCungCapDAO nccDAO = new NhaCungCapDAO();
    private PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        List<DungCu> listDungCu = dungCuService.getAllDungCu();
        request.setAttribute("listDungCu", listDungCu);
        
        List<NhaCungCap> listNCC = nccDAO.getAll();
        request.setAttribute("listNCC", listNCC);

        request.getRequestDispatcher("./views/thietBivaDungCu/thietBivaDungCu.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        if ("insertPhieuNhapDungCu".equals(action)) {
            try {
                String urlAnhHoaDon = luuFileHoaDon(request);
                
                PhieuNhap pnk = new PhieuNhap();
                pnk.setMa_phieu_nhap(request.getParameter("ma_phieu_nhap"));
                pnk.setNha_cung_cap_id(Integer.parseInt(request.getParameter("nha_cung_cap_id")));
                pnk.setSo_hoa_don(request.getParameter("so_hoa_don"));
                pnk.setMau_so(request.getParameter("mau_so"));
                pnk.setKy_hieu(request.getParameter("ky_hieu"));
                pnk.setNgay_hoa_don(new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("ngay_hoa_don")));
                pnk.setMa_so_thue_ncc(request.getParameter("ma_so_thue_ncc"));
                pnk.setTong_tien_hang(Double.parseDouble(request.getParameter("tong_tien_hang").replace(",", "")));
                pnk.setTien_thue_gtgt(Double.parseDouble(request.getParameter("tien_thue_gtgt").replace(",", "")));
                pnk.setTong_thanh_toan(Double.parseDouble(request.getParameter("tong_thanh_toan").replace(",", "")));
                pnk.setNguoi_mua_hang(request.getParameter("nguoi_mua_hang"));
                pnk.setNguoi_ban_hang(request.getParameter("nguoi_ban_hang"));
                pnk.setGhi_chu(request.getParameter("ghi_chu"));
                pnk.setUrl_anh_hoa_don(urlAnhHoaDon);
                pnk.setTong_dien_tich_tieu_ton(0); // Dụng cụ tính sau nếu cần

                String[] dungCuIds = request.getParameterValues("dung_cu_id[]");
                String[] soLuongs = request.getParameterValues("so_luong[]");
                String[] donGias = request.getParameterValues("don_gia[]");

                List<ChiTietPhieuNhapDungCu> listCT = new ArrayList<>();
                if (dungCuIds != null) {
                    for (int i = 0; i < dungCuIds.length; i++) {
                        ChiTietPhieuNhapDungCu ct = new ChiTietPhieuNhapDungCu();
                        ct.setDung_cu_id(Integer.parseInt(dungCuIds[i]));
                        double sl = Double.parseDouble(soLuongs[i]);
                        double dg = Double.parseDouble(donGias[i]);
                        ct.setSo_luong(sl);
                        ct.setDon_gia(dg);
                        ct.setThanh_tien(sl * dg);
                        ct.setCanh_bao_vuot_dien_tich(false);
                        listCT.add(ct);
                    }
                }

                int phieuNhapId = phieuNhapDAO.insertPhieuNhapDungCu(pnk, listCT);
                out.print("{\"success\":true,\"id\":" + phieuNhapId + "}");

            } catch (Exception e) {
                out.print("{\"success\":false,\"error\":\"" + e.getMessage().replace("\"", "\\\"").replace("\n", " ") + "\"}");
            }
        } else if ("haoHutDungCu".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                double qty = Double.parseDouble(request.getParameter("qty"));
                double chiphi = Double.parseDouble(request.getParameter("chiphi"));
                String reason = request.getParameter("reason");
                
                boolean success = dungCuService.ghiNhanHaoHut(id, qty, chiphi, reason);
                out.print("{\"success\":" + success + "}");
            } catch (Exception e) {
                out.print("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }

    private String luuFileHoaDon(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("anh_hoa_don");
        if (filePart == null || filePart.getSize() <= 0) return "";
        String fileName = System.currentTimeMillis() + "_" + Paths.get(filePart.getSubmittedFileName()).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();
        filePart.write(uploadPath + File.separator + fileName);
        return "uploads/" + fileName;
    }
}