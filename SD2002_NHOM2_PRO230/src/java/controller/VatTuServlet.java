package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.VatTu;
import models.DonViQuanLy;
import service.VatTuService;
import dao.DonViQuanLyDAO;
import dao.NhaCungCapDAO;
import models.NhaCungCap;

@WebServlet(name = "VatTuServlet", urlPatterns = {"/vattu"})
public class VatTuServlet extends HttpServlet {
    
    private VatTuService vatTuService = new VatTuService();
    private DonViQuanLyDAO donViDAO = new DonViQuanLyDAO(); // Khởi tạo DAO Đơn vị
    private NhaCungCapDAO nccDAO = new NhaCungCapDAO(); // Khởi tạo DAO Nhà cung cấp

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // 1. Lấy danh sách Vật Tư
        List<VatTu> listVatTu = vatTuService.getAllVatTu();
        request.setAttribute("listVatTu", listVatTu);
        
        // 2. Lấy danh sách Đơn Vị Quản Lý (Kho) đẩy sang JSP
        List<DonViQuanLy> listDonVi = donViDAO.getAll();
        request.setAttribute("listDonVi", listDonVi);
        
        // 3. Lấy danh sách Nhà cung cấp đẩy sang JSP
        List<NhaCungCap> listNCC = nccDAO.getAll();
        request.setAttribute("listNCC", listNCC);
        
        request.getRequestDispatcher("./views/vatTu/vatTu.jsp").forward(request, response);
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
            return;
        }

        try {
            if (action.equals("delete")) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean success = vatTuService.deleteVatTu(id);
                out.print("{\"success\":" + success + "}");
            } 
            else {
                int id = 0;
                String idStr = request.getParameter("id");
                if (idStr != null && !idStr.isEmpty()) {
                    id = Integer.parseInt(idStr);
                }
                
                String tenVatTu = request.getParameter("ten_vat_tu");
                int loaiVatTuId = Integer.parseInt(request.getParameter("loai_vat_tu_id"));
                String hoatChat = request.getParameter("hoat_chat");
                String doiTuongPhongTru = request.getParameter("doi_tuong_phong_tru");
                
                String tgCachLyStr = request.getParameter("thoi_gian_cach_ly");
                int thoiGianCachLy = (tgCachLyStr != null && !tgCachLyStr.isEmpty()) ? Integer.parseInt(tgCachLyStr) : 0;
                
                String donViTinh = request.getParameter("don_vi_tinh");
                String quyCachDongGoi = request.getParameter("quy_cach_dong_goi");
                double dienTichChiemDung = Double.parseDouble(request.getParameter("dien_tich_chiem_dung"));
                
                String viTriStr = request.getParameter("vi_tri_luu_tru_mac_dinh_id");
                int viTriLuuTruMacDinhId = (viTriStr != null && !viTriStr.isEmpty()) ? Integer.parseInt(viTriStr) : 0;
                
                double tonKhoHienTai = Double.parseDouble(request.getParameter("ton_kho_hien_tai"));
                double tonKhoToiThieu = Double.parseDouble(request.getParameter("ton_kho_toi_thieu"));
                double tonKhoToiDa = Double.parseDouble(request.getParameter("ton_kho_toi_da"));
                String trangThai = request.getParameter("trang_thai");

                VatTu vt = new VatTu(id, tenVatTu, loaiVatTuId, hoatChat, doiTuongPhongTru, 
                                     thoiGianCachLy, donViTinh, quyCachDongGoi, dienTichChiemDung, 
                                     viTriLuuTruMacDinhId, tonKhoHienTai, tonKhoToiThieu, tonKhoToiDa, trangThai);
                
                boolean success = false;
                if (action.equals("insert")) {
                    success = vatTuService.addVatTu(vt);
                } else if (action.equals("update")) {
                    success = vatTuService.updateVatTu(vt);
                }
                out.print("{\"success\":" + success + "}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\":false, \"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    public String getServletInfo() { return "Short description"; }
}