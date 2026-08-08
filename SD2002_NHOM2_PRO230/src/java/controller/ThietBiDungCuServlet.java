package controller;

import dao.DonViQuanLyDAO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
import service.ThietBiService;
import dao.ThietBiDAO;
import dao.DungCuDAO;
import dao.KhuVucDAO;
import dao.LichBaoTriDAO;
import models.KhuVuc;
import models.ThietBi;
import models.LichBaoTri;
import url.DBConnect;

@WebServlet(name = "ThietBiDungCuServlet", urlPatterns = {"/tbdc"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class ThietBiDungCuServlet extends HttpServlet {

    private DungCuService dungCuService = new DungCuService();
    private NhaCungCapDAO nccDAO = new NhaCungCapDAO();
    private PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO();
    private ThietBiService thietBiService = new ThietBiService();
    private DungCuDAO dungCuDAO = new DungCuDAO();
    private KhuVucDAO khuVucDAO = new KhuVucDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        List<DungCu> listDungCu = dungCuService.getAllDungCu();
        request.setAttribute("listDungCu", listDungCu);

        List<NhaCungCap> listNCC = nccDAO.getAll();
        request.setAttribute("listNCC", listNCC);

        request.setAttribute("listThietBi", thietBiService.getAllThietBi());

        DonViQuanLyDAO donViQuanLyDAO = new DonViQuanLyDAO();
        request.setAttribute("listKhuVuc", donViQuanLyDAO.getAll());

        request.getRequestDispatcher("./views/thietBivaDungCu/thietBivaDungCu.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        try {
            if ("insertPhieuNhapDungCu".equals(action)) {
                String urlAnhHoaDon = luuFileHoaDon(request);

                PhieuNhap pnk = new PhieuNhap();
                pnk.setMa_phieu_nhap(request.getParameter("ma_phieu_nhap"));
                pnk.setNha_cung_cap_id(Integer.parseInt(request.getParameter("nha_cung_cap_id")));
                pnk.setSo_hoa_don(request.getParameter("so_hoa_don"));
                pnk.setMau_so(request.getParameter("mau_so"));
                pnk.setKy_hieu(request.getParameter("ky_hieu"));
                pnk.setNgay_hoa_don(new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("ngay_hoa_don")));
                pnk.setMa_so_thue_ncc(request.getParameter("ma_so_thue_ncc"));
                pnk.setTong_tien_hang(Double.parseDouble(request.getParameter("tong_tien_hang")));
                pnk.setTien_thue_gtgt(Double.parseDouble(request.getParameter("tien_thue_gtgt")));
                pnk.setTong_thanh_toan(Double.parseDouble(request.getParameter("tong_thanh_toan")));
                pnk.setNguoi_mua_hang(request.getParameter("nguoi_mua_hang"));
                pnk.setNguoi_ban_hang(request.getParameter("nguoi_ban_hang"));
                pnk.setGhi_chu(request.getParameter("ghi_chu"));
                pnk.setUrl_anh_hoa_don(urlAnhHoaDon);
                pnk.setTong_dien_tich_tieu_ton(0);

                String[] dungCuIds = request.getParameterValues("dung_cu_id[]");
                String[] soLuongs = request.getParameterValues("so_luong[]");
                String[] donGias = request.getParameterValues("don_gia[]");

                List<ChiTietPhieuNhapDungCu> listCT = new ArrayList<>();
                if (dungCuIds != null) {
                    for (int i = 0; i < dungCuIds.length; i++) {
                        ChiTietPhieuNhapDungCu ct = new ChiTietPhieuNhapDungCu();
                        ct.setDung_cu_id(Integer.parseInt(dungCuIds[i]));
                        ct.setSo_luong(Double.parseDouble(soLuongs[i]));
                        ct.setDon_gia(Double.parseDouble(donGias[i]));
                        ct.setThanh_tien(ct.getSo_luong() * ct.getDon_gia());
                        ct.setCanh_bao_vuot_dien_tich(false);
                        listCT.add(ct);
                    }
                }

                int phieuNhapId = phieuNhapDAO.insertPhieuNhapDungCu(pnk, listCT);
                out.print("{\"success\":true,\"id\":" + phieuNhapId + "}");

            } else if ("updateDungCu".equals(action)) {
                DungCu dc = new DungCu();
                dc.setId(Integer.parseInt(request.getParameter("id")));
                dc.setTen_dung_cu(request.getParameter("ten_dung_cu"));
                dc.setDon_vi_tinh(request.getParameter("don_vi_tinh"));
                dc.setGia_binh_quan(Double.parseDouble(request.getParameter("gia_binh_quan")));
                dc.setTon_kho_toi_thieu(Double.parseDouble(request.getParameter("ton_kho_toi_thieu")));

                boolean success = dungCuDAO.update(dc);
                out.print("{\"success\":" + success + "}");

            } else if ("insertPhieuNhapThietBi".equals(action)) {
                String urlAnhHoaDon = luuFileHoaDon(request);

                String maPhieu = request.getParameter("ma_phieu_nhap");
                int nccId = Integer.parseInt(request.getParameter("nha_cung_cap_id"));
                String soHoaDon = request.getParameter("so_hoa_don");
                String mauSo = request.getParameter("mau_so");
                String kyHieu = request.getParameter("ky_hieu");
                Date ngayHoaDon = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("ngay_hoa_don"));
                String mst = request.getParameter("ma_so_thue_ncc");
                double tongTien = Double.parseDouble(request.getParameter("tong_tien_hang"));
                double thue = Double.parseDouble(request.getParameter("tien_thue_gtgt"));
                double tongThanhToan = Double.parseDouble(request.getParameter("tong_thanh_toan"));
                String nguoiMua = request.getParameter("nguoi_mua_hang");
                String nguoiBan = request.getParameter("nguoi_ban_hang");
                String ghiChu = request.getParameter("ghi_chu");

                int tbId = Integer.parseInt(request.getParameter("thiet_bi_id"));
                double donGia = Double.parseDouble(request.getParameter("don_gia"));
                int khuTbId = Integer.parseInt(request.getParameter("khu_thiet_bi_id"));

                try (Connection conn = DBConnect.getConnection()) {
                    conn.setAutoCommit(false);

                    String sqlPN = "INSERT INTO PhieuNhap (ma_phieu_nhap, loai_phieu_nhap, nha_cung_cap_id, so_hoa_don, mau_so, ky_hieu, ngay_hoa_don, tong_tien_hang, tien_thue_gtgt, tong_thanh_toan, nguoi_mua_hang, nguoi_ban_hang, ma_so_thue_ncc, url_anh_hoa_don, nguoi_lap_phieu_id, tong_dien_tich_tieu_ton, trang_thai, ghi_chu, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
                    try (PreparedStatement ps = conn.prepareStatement(sqlPN, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, maPhieu);
                        ps.setString(2, "Nhập Thiết Bị");
                        ps.setInt(3, nccId);
                        ps.setString(4, soHoaDon);
                        ps.setString(5, mauSo);
                        ps.setString(6, kyHieu);
                        ps.setDate(7, new java.sql.Date(ngayHoaDon.getTime()));
                        ps.setDouble(8, tongTien);
                        ps.setDouble(9, thue);
                        ps.setDouble(10, tongThanhToan);
                        ps.setString(11, nguoiMua);
                        ps.setString(12, nguoiBan);
                        ps.setString(13, mst);
                        ps.setString(14, urlAnhHoaDon);
                        ps.setInt(15, 1);
                        ps.setDouble(16, 0);
                        ps.setString(17, "Đã nhập bãi");
                        ps.setString(18, ghiChu);
                        ps.executeUpdate();

                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) {
                                int pnId = rs.getInt(1);
                                String sqlCT = "INSERT INTO ChiTietPhieuNhapThietBi (phieu_nhap_id, thiet_bi_id, don_gia, khu_thiet_bi_id) VALUES (?, ?, ?, ?)";
                                try (PreparedStatement psCT = conn.prepareStatement(sqlCT)) {
                                    psCT.setInt(1, pnId);
                                    psCT.setInt(2, tbId);
                                    psCT.setDouble(3, donGia);
                                    psCT.setInt(4, 4);
                                    psCT.executeUpdate();
                                }
                            }
                        }
                    }
                    conn.commit();
                }
                out.print("{\"success\":true}");

            } else if ("haoHutDungCu".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                double qty = Double.parseDouble(request.getParameter("qty"));
                double chiphi = Double.parseDouble(request.getParameter("chiphi"));
                String reason = request.getParameter("reason");

                boolean success = dungCuService.ghiNhanHaoHut(id, qty, chiphi, reason);
                out.print("{\"success\":" + success + "}");

            } else if ("insertDungCu".equals(action)) {
                DungCu dc = new DungCu();
                dc.setMa_dung_cu(request.getParameter("ma_dung_cu"));
                dc.setTen_dung_cu(request.getParameter("ten_dung_cu"));
                dc.setDon_vi_tinh(request.getParameter("don_vi_tinh"));
                dc.setDien_tich_chiem_dung(Double.parseDouble(request.getParameter("dien_tich_chiem_dung")));
                dc.setVi_tri_luu_tru_id(Integer.parseInt(request.getParameter("vi_tri_luu_tru_id")));
                dc.setGia_binh_quan(Double.parseDouble(request.getParameter("gia_binh_quan")));
                dc.setTon_kho_toi_thieu(Double.parseDouble(request.getParameter("ton_kho_toi_thieu")));

                boolean success = dungCuDAO.insert(dc);
                out.print("{\"success\":" + success + "}");

            } else if ("insertThietBi".equals(action)) {
                ThietBi tb = new ThietBi();
                tb.setMa_thiet_bi(request.getParameter("ma_thiet_bi"));
                tb.setTen_thiet_bi(request.getParameter("ten_thiet_bi"));
                tb.setDien_tich_cat_tru(Double.parseDouble(request.getParameter("dien_tich_cat_tru")));
                tb.setThoi_gian_khau_hao_nam(Integer.parseInt(request.getParameter("khau_hao")));
                tb.setTrang_thai(request.getParameter("trang_thai"));
                tb.setMo_ta(request.getParameter("mo_ta"));
                tb.setVi_tri_luu_tru_id(Integer.parseInt(request.getParameter("vi_tri")));

                // Gọi qua Service thay vì DAO để kiểm tra check trùng lặp
                String result = thietBiService.insertThietBi(tb);
                
                if ("SUCCESS".equals(result)) {
                    out.print("{\"success\":true}");
                } else {
                    // Truyền thẳng câu báo lỗi từ Java ra ngoài JS
                    out.print("{\"success\":false, \"error\":\"" + result.replace("\"", "\\\"").replace("\n", " ") + "\"}");
                }

            } else if ("updateThietBi".equals(action)) {
                ThietBi tb = new ThietBi();
                if (request.getParameter("id") != null && !request.getParameter("id").isEmpty()) {
                    tb.setId(Integer.parseInt(request.getParameter("id")));
                }
                tb.setMa_thiet_bi(request.getParameter("ma_thiet_bi"));
                tb.setTen_thiet_bi(request.getParameter("ten_thiet_bi"));
                tb.setDien_tich_cat_tru(Double.parseDouble(request.getParameter("dien_tich_cat_tru")));
                tb.setThoi_gian_khau_hao_nam(Integer.parseInt(request.getParameter("khau_hao")));
                tb.setMo_ta(request.getParameter("mo_ta"));
                tb.setVi_tri_luu_tru_id(Integer.parseInt(request.getParameter("vi_tri")));
                tb.setTrang_thai(request.getParameter("trang_thai"));

                ThietBiDAO dao = new ThietBiDAO();
                boolean success = dao.update(tb);
                out.print("{\"success\":" + success + "}");

            } else if ("deleteThietBi".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                out.print("{\"success\":" + new ThietBiDAO().delete(id) + "}");

            } else if ("deleteDungCu".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                out.print("{\"success\":" + new DungCuDAO().delete(id) + "}");
                
            } else if ("lenLichBaoTri".equals(action)) {
                LichBaoTri lbt = new LichBaoTri();
                int tbId = Integer.parseInt(request.getParameter("ma_thiet_bi"));
                lbt.setMa_thiet_bi(tbId);
                lbt.setNgay_bao_tri_du_kien(new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("ngay_du_kien")));
                
                // Lấy giá trị loại bảo trì
                String loaiBaoTri = request.getParameter("loai_bao_tri");
                lbt.setLoai_bao_tri(loaiBaoTri);
                
                lbt.setNoi_dung_bao_tri(request.getParameter("noi_dung"));

                boolean ok = new LichBaoTriDAO().insert(lbt);
                if (ok) {
                    // CHỈ CHUYỂN TRẠNG THÁI NẾU LÀ SỬA CHỮA ĐỘT XUẤT
                    if ("Đột xuất".equals(loaiBaoTri)) {
                        new ThietBiDAO().updateTrangThai(tbId, "Bảo trì");
                    }
                    out.print("{\"success\":true}");
                } else {
                    out.print("{\"success\":false,\"error\":\"Lỗi DAO: Không thể lưu lịch bảo trì vào CSDL.\"}");
                }
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            out.print("{\"success\":false,\"error\":\"" + errorMsg.replace("\"", "\\\"").replace("\n", " ") + "\"}");
        }
    }

    private String luuFileHoaDon(HttpServletRequest request) throws Exception {
        Part filePart = request.getPart("anh_hoa_don");
        if (filePart == null || filePart.getSize() <= 0) {
            return "";
        }
        String fileName = System.currentTimeMillis() + "_" + Paths.get(filePart.getSubmittedFileName()).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        filePart.write(uploadPath + File.separator + fileName);
        return "uploads/" + fileName;
    }
}