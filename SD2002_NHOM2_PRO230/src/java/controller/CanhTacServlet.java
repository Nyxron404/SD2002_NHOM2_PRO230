package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.DonViQuanLyDAO;
import dao.DungCuDAO;
import dao.ThietBiDAO;
import dao.VatTuDAO;

import models.GhiNhanSauBenh;
import models.GhiNhanThuHoach;
import models.GiongSauRieng;
import models.LichChamSoc;
import models.LichSuSinhTruong;
import models.NhatKyChamSoc;
import models.TaiKhoan;
import models.VuonTrong;

import service.CanhTacService;

/**
 * UC-4 - Điều phối toàn bộ chức năng Quản lý canh tác sầu riêng.
 * doGet: nạp dữ liệu + forward JSP. doPost: xử lý hành động, trả JSON.
 */
@WebServlet(name = "CanhTacServlet", urlPatterns = {"/canhtac"})
public class CanhTacServlet extends HttpServlet {

    private final CanhTacService service = new CanhTacService();
    private final DonViQuanLyDAO donViDAO = new DonViQuanLyDAO();
    private final VatTuDAO vatTuDAO = new VatTuDAO();
    private final DungCuDAO dungCuDAO = new DungCuDAO();
    private final ThietBiDAO thietBiDAO = new ThietBiDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        request.setAttribute("listGiong", service.getAllGiong());
        request.setAttribute("listMatDo", service.getBangMatDo());
        request.setAttribute("listVuon", service.getAllVuon());
        request.setAttribute("listLich", service.getAllLich());
        request.setAttribute("listNhatKy", service.getAllNhatKy());
        request.setAttribute("listSinhTruong", service.getAllSinhTruong());
        request.setAttribute("listSauBenh", service.getAllSauBenh());
        request.setAttribute("listThuHoach", service.getAllThuHoach());

        // Danh mục dùng cho dropdown
        request.setAttribute("listLoDat", donViDAO.getLoDat());
        request.setAttribute("listVatTu", vatTuDAO.getAll());
        request.setAttribute("listDungCu", dungCuDAO.getAll());
        request.setAttribute("listThietBi", thietBiDAO.getAll());

        request.getRequestDispatcher("/views/canhTac/canhTac.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        int taiKhoanId = layTaiKhoanId(request);

        try {
            String err = null;
            boolean ok = false;

            switch (action == null ? "" : action) {
                // ===== UC-4.1 Giống =====
                case "giong_insert": {
                    err = service.addGiong(docGiong(request));
                    ok = err == null; break;
                }
                case "giong_update": {
                    GiongSauRieng g = docGiong(request);
                    g.setId(pInt(request, "id", 0));
                    err = service.updateGiong(g);
                    ok = err == null; break;
                }
                case "giong_delete": {
                    err = service.deleteGiong(pInt(request, "id", 0));
                    ok = err == null; break;
                }

                // ===== UC-4.2 Vườn trồng =====
                case "vuon_insert": { ok = service.addVuon(docVuon(request)); break; }
                case "vuon_update": {
                    VuonTrong v = docVuon(request);
                    v.setId(pInt(request, "id", 0));
                    ok = service.updateVuon(v); break;
                }
                case "vuon_delete": { ok = service.deleteVuon(pInt(request, "id", 0)); break; }

                // ===== UC-4.3 Lịch chăm sóc =====
                case "lich_insert": {
                    LichChamSoc l = docLich(request);
                    l.setNguoi_tao_id(taiKhoanId);
                    ok = service.addLich(l); break;
                }
                case "lich_update": {
                    LichChamSoc l = docLich(request);
                    l.setId(pInt(request, "id", 0));
                    ok = service.updateLich(l); break;
                }
                case "lich_delete": { ok = service.deleteLich(pInt(request, "id", 0)); break; }

                // ===== UC-4.4 Nhật ký chăm sóc =====
                case "nhatky_insert": {
                    NhatKyChamSoc n = docNhatKy(request);
                    n.setNguoi_ghi_nhan_id(taiKhoanId);
                    err = service.addNhatKy(n);
                    ok = err == null; break;
                }
                case "nhatky_delete": { ok = service.deleteNhatKy(pInt(request, "id", 0)); break; }

                // ===== UC-4.5 Sinh trưởng =====
                case "sinhtruong_insert": {
                    LichSuSinhTruong s = docSinhTruong(request);
                    s.setNguoi_cap_nhat_id(taiKhoanId);
                    ok = service.addSinhTruong(s); break;
                }

                // ===== UC-4.6 Sâu bệnh =====
                case "saubenh_insert": {
                    GhiNhanSauBenh s = docSauBenh(request);
                    s.setNguoi_ghi_nhan_id(taiKhoanId);
                    err = service.addSauBenh(s);
                    ok = err == null; break;
                }
                case "saubenh_update": {
                    GhiNhanSauBenh s = docSauBenh(request);
                    s.setId(pInt(request, "id", 0));
                    ok = service.updateSauBenh(s); break;
                }
                case "saubenh_delete": { ok = service.deleteSauBenh(pInt(request, "id", 0)); break; }

                // ===== UC-4.7 Thu hoạch =====
                case "thuhoach_insert": {
                    GhiNhanThuHoach t = docThuHoach(request);
                    t.setNguoi_ghi_nhan_id(taiKhoanId);
                    boolean hoanTat = "true".equals(request.getParameter("hoan_tat")) || "1".equals(request.getParameter("hoan_tat"));
                    err = service.addThuHoach(t, hoanTat);
                    ok = err == null; break;
                }
                case "thuhoach_delete": { ok = service.deleteThuHoach(pInt(request, "id", 0)); break; }

                default:
                    err = "Hành động không hợp lệ";
            }

            out.print(jsonKetQua(ok, err));
        } catch (Exception e) {
            e.printStackTrace();
            out.print(jsonKetQua(false, e.getMessage()));
        }
    }

    // ===================== ĐỌC DỮ LIỆU TỪ REQUEST =====================

    private GiongSauRieng docGiong(HttpServletRequest r) {
        GiongSauRieng g = new GiongSauRieng();
        g.setTen_giong(r.getParameter("ten_giong"));
        g.setDac_diem(r.getParameter("dac_diem"));
        g.setThoi_gian_sinh_truong_thu_hoach(pInt(r, "thoi_gian_sinh_truong_thu_hoach", 0));
        g.setNang_suat_tham_khao(pDouble(r, "nang_suat_tham_khao", 0));
        String tt = r.getParameter("trang_thai");
        g.setTrang_thai(tt != null && !tt.isEmpty() ? tt : "Đang canh tác");
        return g;
    }

    private VuonTrong docVuon(HttpServletRequest r) {
        VuonTrong v = new VuonTrong();
        v.setLo_dat_id(pInt(r, "lo_dat_id", 0));
        v.setGiong_id(pInt(r, "giong_id", 0));
        v.setDien_tich(pDouble(r, "dien_tich", 0));
        v.setSo_luong_cay(pInt(r, "so_luong_cay", 0));
        v.setNgay_trong(parseDate(r.getParameter("ngay_trong")));
        v.setGhi_chu(r.getParameter("ghi_chu"));
        v.setTrang_thai_sinh_truong("Cây con");
        return v;
    }

    private LichChamSoc docLich(HttpServletRequest r) {
        LichChamSoc l = new LichChamSoc();
        l.setLoai_cong_viec(r.getParameter("loai_cong_viec"));
        l.setNgay_bat_dau(parseDate(r.getParameter("ngay_bat_dau")));
        int ck = pInt(r, "chu_ky_ngay", 0);
        l.setChu_ky_ngay(ck > 0 ? ck : null);
        l.setNgay_ket_thuc(parseDate(r.getParameter("ngay_ket_thuc")));
        l.setMo_ta(r.getParameter("mo_ta"));
        String tt = r.getParameter("trang_thai");
        l.setTrang_thai(tt != null && !tt.isEmpty() ? tt : "Đang áp dụng");
        l.setDanh_sach_lo_id(r.getParameter("lo_ids")); // "1,2,3"
        return l;
    }

    private NhatKyChamSoc docNhatKy(HttpServletRequest r) {
        NhatKyChamSoc n = new NhatKyChamSoc();
        n.setLo_dat_id(pInt(r, "lo_dat_id", 0));
        n.setLoai_cong_viec(r.getParameter("loai_cong_viec"));
        n.setNgay_thuc_hien(parseDate(r.getParameter("ngay_thuc_hien")));
        n.setMo_ta(r.getParameter("mo_ta"));

        String[] vtId = r.getParameterValues("vt_id");
        String[] vtQty = r.getParameterValues("vt_qty");
        if (vtId != null) {
            for (int i = 0; i < vtId.length; i++) {
                int id = toInt(vtId[i], 0);
                double q = toDouble(safe(vtQty, i), 0);
                if (id > 0 && q > 0) n.getDongVatTu().add(new NhatKyChamSoc.DongVatTu(id, q));
            }
        }
        String[] dcId = r.getParameterValues("dc_id");
        String[] dcQty = r.getParameterValues("dc_qty");
        if (dcId != null) {
            for (int i = 0; i < dcId.length; i++) {
                int id = toInt(dcId[i], 0);
                double q = toDouble(safe(dcQty, i), 0);
                if (id > 0 && q > 0) n.getDongDungCu().add(new NhatKyChamSoc.DongDungCu(id, q));
            }
        }
        String[] tbId = r.getParameterValues("tb_id");
        String[] tbNgay = r.getParameterValues("tb_ngay");
        if (tbId != null) {
            for (int i = 0; i < tbId.length; i++) {
                int id = toInt(tbId[i], 0);
                int ngay = toInt(safe(tbNgay, i), 1);
                if (id > 0) n.getDongThietBi().add(new NhatKyChamSoc.DongThietBi(id, ngay));
            }
        }
        return n;
    }

    private LichSuSinhTruong docSinhTruong(HttpServletRequest r) {
        LichSuSinhTruong s = new LichSuSinhTruong();
        s.setVuon_trong_id(pInt(r, "vuon_trong_id", 0));
        s.setGiai_doan_moi(r.getParameter("giai_doan_moi"));
        s.setTy_le_giai_doan(r.getParameter("ty_le_giai_doan"));
        int giam = pInt(r, "so_luong_cay_giam", 0);
        s.setSo_luong_cay_giam(giam > 0 ? giam : null);
        String loai = r.getParameter("loai_cap_nhat");
        if (loai == null || loai.isEmpty()) loai = giam > 0 ? "Giảm số cây" : "Chuyển giai đoạn";
        s.setLoai_cap_nhat(loai);
        s.setGhi_chu(r.getParameter("ghi_chu"));
        return s;
    }

    private GhiNhanSauBenh docSauBenh(HttpServletRequest r) {
        GhiNhanSauBenh s = new GhiNhanSauBenh();
        s.setVuon_trong_id(pInt(r, "vuon_trong_id", 0));
        s.setTen_sau_benh(r.getParameter("ten_sau_benh"));
        s.setMuc_do_nghiem_trong(r.getParameter("muc_do_nghiem_trong"));
        s.setNgay_phat_hien(parseDate(r.getParameter("ngay_phat_hien")));
        s.setBien_phap_xu_ly(r.getParameter("bien_phap_xu_ly"));
        String tt = r.getParameter("trang_thai");
        s.setTrang_thai(tt != null && !tt.isEmpty() ? tt : "Chưa xử lý");

        String[] thId = r.getParameterValues("thuoc_id");
        String[] thQty = r.getParameterValues("thuoc_qty");
        if (thId != null) {
            for (int i = 0; i < thId.length; i++) {
                int id = toInt(thId[i], 0);
                double q = toDouble(safe(thQty, i), 0);
                if (id > 0 && q > 0) s.getDongThuoc().add(new NhatKyChamSoc.DongVatTu(id, q));
            }
        }
        return s;
    }

    private GhiNhanThuHoach docThuHoach(HttpServletRequest r) {
        GhiNhanThuHoach t = new GhiNhanThuHoach();
        t.setVuon_trong_id(pInt(r, "vuon_trong_id", 0));
        t.setTen_vu_mua(r.getParameter("ten_vu_mua"));
        t.setNgay_thu_hoach(parseDate(r.getParameter("ngay_thu_hoach")));
        t.setVi_tri_luu_tru_id(pInt(r, "vi_tri_luu_tru_id", 0));
        t.setTong_san_luong_kg(pDouble(r, "tong_san_luong_kg", 0));
        t.setTong_dien_tich_chiem_dung(pDouble(r, "tong_dien_tich_chiem_dung", 0));
        String tt = r.getParameter("trang_thai_luu_kho");
        t.setTrang_thai_luu_kho(tt != null && !tt.isEmpty() ? tt : "Đã nhập kho");
        t.setGhi_chu(r.getParameter("ghi_chu"));

        String[] loai = r.getParameterValues("pl_loai");
        String[] sl = r.getParameterValues("pl_sl");
        String[] dt = r.getParameterValues("pl_dt");
        if (loai != null) {
            for (int i = 0; i < loai.length; i++) {
                if (loai[i] == null || loai[i].trim().isEmpty()) continue;
                t.getDongPhanLoai().add(new GhiNhanThuHoach.DongPhanLoai(
                        loai[i].trim(), toDouble(safe(sl, i), 0), toDouble(safe(dt, i), 0)));
            }
        }
        return t;
    }

    // ===================== TIỆN ÍCH =====================

    private int layTaiKhoanId(HttpServletRequest r) {
        HttpSession s = r.getSession(false);
        if (s != null) {
            Object tk = s.getAttribute("taiKhoan");
            if (tk instanceof TaiKhoan) return ((TaiKhoan) tk).getId();
        }
        int fromForm = pInt(r, "nguoi_ghi_nhan_id", 0);
        return fromForm > 0 ? fromForm : 1;
    }

    private static String safe(String[] arr, int i) { return (arr != null && i < arr.length) ? arr[i] : null; }

    private static int pInt(HttpServletRequest r, String name, int def) { return toInt(r.getParameter(name), def); }
    private static double pDouble(HttpServletRequest r, String name, double def) { return toDouble(r.getParameter(name), def); }

    private static int toInt(String s, int def) {
        try { return (s == null || s.trim().isEmpty()) ? def : Integer.parseInt(s.trim()); }
        catch (Exception e) { return def; }
    }
    private static double toDouble(String s, double def) {
        try { return (s == null || s.trim().isEmpty()) ? def : Double.parseDouble(s.trim()); }
        catch (Exception e) { return def; }
    }

    /** Chấp nhận cả yyyy-MM-dd và yyyy-MM-dd'T'HH:mm (datetime-local). */
    private static Date parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        s = s.trim();
        String[] patterns = {"yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"};
        for (String p : patterns) {
            try { return new SimpleDateFormat(p).parse(s); } catch (Exception ignored) {}
        }
        return null;
    }

    private static String jsonKetQua(boolean ok, String err) {
        String safe = err == null ? "" : err.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
        return "{\"success\":" + ok + ",\"message\":\"" + safe + "\"}";
    }

    @Override
    public String getServletInfo() { return "UC-4 Quản lý canh tác sầu riêng"; }
}
