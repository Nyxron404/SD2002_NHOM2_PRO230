package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import models.VatTu;
import models.LoHangVatTu;
import models.ChiPhiLoDat;
import models.TrangTrai;
import models.KhuVuc;
import models.VuonTrong;
import models.ThietBi;
import service.BaoCaoService;
import service.CanhTacService;

@WebServlet(name = "BaoCaoServlet", urlPatterns = {"/baocao"})
public class BaoCaoServlet extends HttpServlet {

    private BaoCaoService baoCaoService = new BaoCaoService();
    private CanhTacService canhTacService = new CanhTacService();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try {
            // 1. TỔNG QUAN TRANG TRẠI VÀ KHU VỰC
            TrangTrai trangTrai = baoCaoService.getThongTinTrangTrai();
            request.setAttribute("trangTrai", trangTrai);
            
            int soKhuVuc = 0;
            if(trangTrai != null) {
                List<KhuVuc> listKV = baoCaoService.getDanhSachKhuVuc(trangTrai.getId());
                if(listKV != null) soKhuVuc = listKV.size();
            }
            request.setAttribute("soKhuVuc", soKhuVuc);

            // 2. CANH TÁC
            List<VuonTrong> listVuon = baoCaoService.getAllVuonTrong();
            int tongSoCay = 0;
            int soVuon = 0;
            if(listVuon != null) {
                soVuon = listVuon.size();
                for (VuonTrong v : listVuon) {
                    tongSoCay += v.getSo_luong_cay();
                }
            }
            request.setAttribute("soVuon", soVuon);
            request.setAttribute("tongSoCay", tongSoCay);
            request.setAttribute("tongChiPhiTrangTrai", baoCaoService.getTongChiPhiToanTrangTrai());

            List<ChiPhiLoDat> listChiPhi = baoCaoService.getTongHopTheoLo();
            double tongSanLuong = 0;
            if(listChiPhi != null) {
                for (ChiPhiLoDat cp : listChiPhi) {
                    tongSanLuong += cp.getSan_luong_kg();
                }
            }
            request.setAttribute("tongSanLuong", tongSanLuong);
            request.setAttribute("uocTinhDoanhThu", tongSanLuong * 65000);

            // 3. KHO VẬT TƯ
            List<VatTu> listAllVatTu = baoCaoService.getAllVatTu();
            List<VatTu> vatTuDuoiNguong = baoCaoService.getVatTuDuoiNguong();
            List<LoHangVatTu> loHangSapHetHan = baoCaoService.getLoHangSapHetHan();
            List<LoHangVatTu> loHangDaHetHan = baoCaoService.getLoHangDaHetHan();
            
            request.setAttribute("listAllVatTu", listAllVatTu);
            request.setAttribute("tongVatTu", listAllVatTu != null ? listAllVatTu.size() : 0);
            request.setAttribute("vatTuDuoiNguong", vatTuDuoiNguong);
            request.setAttribute("loHangSapHetHan", loHangSapHetHan);
            request.setAttribute("loHangDaHetHan", loHangDaHetHan);

            // 4. THIẾT BỊ VÀ LỊCH BẢO TRÌ
            List<ThietBi> listThietBi = baoCaoService.getAllThietBi();
            int tbSanSang = 0, tbDangSuDung = 0, tbBaoTri = 0, tbSapBaoTri = 0, tbQuaHanBaoTri = 0;
            if(listThietBi != null) {
                long now = System.currentTimeMillis();
                long sevenDays = 7L * 24 * 3600 * 1000;
                for (ThietBi tb : listThietBi) {
                    String tt = tb.getTrang_thai() != null ? tb.getTrang_thai() : "";
                    if (tt.contains("Sẵn sàng") || tt.equals("1")) tbSanSang++;
                    else if (tt.contains("Đang sử dụng") || tt.contains("Đang dùng")) tbDangSuDung++;
                    else if (tt.contains("Bảo trì") || tt.contains("Hỏng")) tbBaoTri++;

                    if (tb.getNgay_bao_tri_du_kien() != null) {
                        long diff = tb.getNgay_bao_tri_du_kien().getTime() - now;
                        if (diff < 0) tbQuaHanBaoTri++;
                        else if (diff <= sevenDays) tbSapBaoTri++;
                    }
                }
            }
            request.setAttribute("tbSanSang", tbSanSang);
            request.setAttribute("tbDangSuDung", tbDangSuDung);
            request.setAttribute("tbBaoTri", tbBaoTri);
            request.setAttribute("tbSapBaoTri", tbSapBaoTri);
            request.setAttribute("tbQuaHanBaoTri", tbQuaHanBaoTri);

            // 5. NHẮC VIỆC
            try {
                request.setAttribute("nhacViecCho", canhTacService.getNhacViecChoXuLy().size());
            } catch(Exception ex) {
                request.setAttribute("nhacViecCho", 0);
            }

        } catch (Exception e) {
            System.out.println("Lỗi load dữ liệu Dashboard: " + e.getMessage());
        }

        // ĐIỀU HƯỚNG BÁO CÁO CỤ THỂ
        String reportType = request.getParameter("type");
        if (reportType == null || reportType.isEmpty()) {
            request.getRequestDispatcher("/views/baoCao/baoCao.jsp").forward(request, response);
            return;
        }

        switch (reportType) {
            case "chi-phi":
                handleChiPhiReport(request, response);
                break;
            case "ton-kho":
                request.getRequestDispatcher("/views/baoCao/baoCao.jsp").forward(request, response);
                break;
            case "nang-suat":
                List<ChiPhiLoDat> listChiPhiLo = baoCaoService.getTongHopTheoLo();
                handleNangSuatReport(request, response, listChiPhiLo);
                break;
            default:
                request.getRequestDispatcher("/views/baoCao/baoCao.jsp").forward(request, response);
        }
    }

    private void handleChiPhiReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        if (startDate == null || startDate.isEmpty()) startDate = LocalDate.now().withDayOfMonth(1).toString();
        if (endDate == null || endDate.isEmpty()) endDate = LocalDate.now().toString();

        Map<String, Double> chiPhiVatTu = baoCaoService.getChiPhiVatTuByDateRange(startDate, endDate);
        request.setAttribute("chiPhiDataMap", chiPhiVatTu); 
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.getRequestDispatcher("/views/baoCao/baoCao.jsp").forward(request, response);
    }

    private void handleNangSuatReport(HttpServletRequest request, HttpServletResponse response, List<ChiPhiLoDat> listChiPhiLo) throws ServletException, IOException {
        Map<String, Double> nangSuatMap = new HashMap<>();
        if(listChiPhiLo != null) {
            for(ChiPhiLoDat cp : listChiPhiLo) {
                if(cp.getSan_luong_kg() > 0) {
                    nangSuatMap.put(cp.getTen_lo_dat() + " (" + cp.getTen_giong() + ")", cp.getSan_luong_kg());
                }
            }
        }
        request.setAttribute("nangSuatDataMap", nangSuatMap);
        request.getRequestDispatcher("/views/baoCao/baoCao.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}