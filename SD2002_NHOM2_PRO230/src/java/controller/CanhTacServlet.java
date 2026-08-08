package controller;

import java.io.IOException;
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
import models.VuonTrong;
import service.CanhTacService;

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

        request.setAttribute("listGiong", service.getAllGiong());
        request.setAttribute("listMatDo", service.getBangMatDo());
        request.setAttribute("listVuon", service.getAllVuon());
        request.setAttribute("listLich", service.getAllLich());
        request.setAttribute("listNhatKy", service.getAllNhatKy());
        request.setAttribute("listSinhTruong", service.getAllSinhTruong());
        request.setAttribute("listSauBenh", service.getAllSauBenh());
        request.setAttribute("listThuHoach", service.getAllThuHoach());

        // Lấy các lô đã được phân chia từ module Quản lý khu vực.
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

        String action = request.getParameter("action");
        String tab = request.getParameter("tab");
        HttpSession session = request.getSession();

        try {
            String message;

            switch (action == null ? "" : action) {
                case "giong_insert":
                    message = service.addGiong(docGiong(request));
                    session.setAttribute("canhTacMessage",
                            message == null ? "Thêm giống thành công." : message);
                    break;

                case "giong_delete":
                    session.setAttribute("canhTacMessage",
                            service.deleteGiong(pInt(request, "id", 0))
                                    ? "Xóa giống thành công." : "Không thể xóa giống.");
                    break;

                case "vuon_insert":
                    // KHÔNG đọc dien_tich từ request.
                    // VuonTrongDAO tự lấy DonViQuanLy.dien_tich.
                    session.setAttribute("canhTacMessage",
                            service.addVuon(docVuon(request))
                                    ? "Thiết lập vườn thành công. Diện tích và mật độ đã được tính từ lô đất."
                                    : "Không thể thiết lập vườn. Kiểm tra lô đất có diện tích hợp lệ.");
                    break;

                case "vuon_delete":
                    session.setAttribute("canhTacMessage",
                            service.deleteVuon(pInt(request, "id", 0))
                                    ? "Xóa thiết lập vườn thành công." : "Không thể xóa thiết lập vườn.");
                    break;

                case "lich_insert":
                    LichChamSoc l = docLich(request);
                    l.setNguoi_tao_id(0);
                    session.setAttribute("canhTacMessage",
                            service.addLich(l) ? "Tạo lịch chăm sóc thành công." : "Không thể tạo lịch.");
                    break;

                case "lich_delete":
                    session.setAttribute("canhTacMessage",
                            service.deleteLich(pInt(request, "id", 0))
                                    ? "Xóa lịch thành công." : "Không thể xóa lịch.");
                    break;

                case "nhatky_insert":
                    NhatKyChamSoc n = docNhatKy(request);
                    n.setNguoi_ghi_nhan_id(0);
                    String errNhatKy = service.addNhatKy(n);
                    session.setAttribute("canhTacMessage",
                            errNhatKy == null ? "Ghi nhật ký thành công. Chi phí vật tư/dụng cụ/thiết bị được tính ở backend."
                                              : errNhatKy);
                    break;

                case "nhatky_delete":
                    session.setAttribute("canhTacMessage",
                            service.deleteNhatKy(pInt(request, "id", 0))
                                    ? "Xóa nhật ký thành công." : "Không thể xóa nhật ký.");
                    break;

                case "sinhtruong_insert":
                    LichSuSinhTruong st = docSinhTruong(request);
                    st.setNguoi_cap_nhat_id(0);
                    session.setAttribute("canhTacMessage",
                            service.addSinhTruong(st) ? "Cập nhật sinh trưởng thành công."
                                                      : "Không thể cập nhật sinh trưởng.");
                    break;

                case "saubenh_insert":
                    GhiNhanSauBenh sb = docSauBenh(request);
                    sb.setNguoi_ghi_nhan_id(0);
                    String errSauBenh = service.addSauBenh(sb);
                    session.setAttribute("canhTacMessage",
                            errSauBenh == null ? "Ghi nhận sâu bệnh thành công." : errSauBenh);
                    break;

                case "thuhoach_insert":
                    GhiNhanThuHoach th = docThuHoach(request);
                    th.setNguoi_ghi_nhan_id(0);
                    boolean hoanTat = "true".equalsIgnoreCase(request.getParameter("hoan_tat"));
                    String errThu = service.addThuHoach(th, hoanTat);
                    session.setAttribute("canhTacMessage",
                            errThu == null ? "Ghi nhận thu hoạch thành công." : errThu);
                    break;

                default:
                    session.setAttribute("canhTacMessage", "Hành động không hợp lệ.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("canhTacMessage",
                    "Lỗi xử lý: " + (e.getMessage() == null ? "Không xác định" : e.getMessage()));
        }

        String redirect = request.getContextPath() + "/canhtac";
        if (tab != null && !tab.isBlank()) {
            redirect += "?tab=" + java.net.URLEncoder.encode(tab, java.nio.charset.StandardCharsets.UTF_8);
        }
        response.sendRedirect(redirect);
    }

    private GiongSauRieng docGiong(HttpServletRequest r) {
        GiongSauRieng g = new GiongSauRieng();
        g.setTen_giong(r.getParameter("ten_giong"));
        g.setDac_diem(r.getParameter("dac_diem"));
        g.setThoi_gian_sinh_truong_thu_hoach(pInt(r, "thoi_gian_sinh_truong_thu_hoach", 0));
        g.setNang_suat_tham_khao(pDouble(r, "nang_suat_tham_khao", 0));
        g.setTrang_thai(valueOr(r.getParameter("trang_thai"), "Đang canh tác"));
        return g;
    }

    private VuonTrong docVuon(HttpServletRequest r) {
        VuonTrong v = new VuonTrong();
        v.setLo_dat_id(pInt(r, "lo_dat_id", 0));
        v.setGiong_id(pInt(r, "giong_id", 0));
        // Cố ý KHÔNG có v.setDien_tich(...).
        // Diện tích phải được lấy bởi VuonTrongDAO từ DonViQuanLy.
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
        l.setTrang_thai(valueOr(r.getParameter("trang_thai"), "Đang áp dụng"));
        l.setDanh_sach_lo_id(r.getParameter("lo_ids"));
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
                double qty = toDouble(safe(vtQty, i), 0);
                if (id > 0 && qty > 0)
                    n.getDongVatTu().add(new NhatKyChamSoc.DongVatTu(id, qty));
            }
        }

        String[] dcId = r.getParameterValues("dc_id");
        String[] dcQty = r.getParameterValues("dc_qty");
        if (dcId != null) {
            for (int i = 0; i < dcId.length; i++) {
                int id = toInt(dcId[i], 0);
                double qty = toDouble(safe(dcQty, i), 0);
                if (id > 0 && qty > 0)
                    n.getDongDungCu().add(new NhatKyChamSoc.DongDungCu(id, qty));
            }
        }

        String[] tbId = r.getParameterValues("tb_id");
        String[] tbNgay = r.getParameterValues("tb_ngay");
        if (tbId != null) {
            for (int i = 0; i < tbId.length; i++) {
                int id = toInt(tbId[i], 0);
                int ngay = toInt(safe(tbNgay, i), 1);
                if (id > 0)
                    n.getDongThietBi().add(new NhatKyChamSoc.DongThietBi(id, Math.max(1, ngay)));
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
        s.setLoai_cap_nhat(valueOr(r.getParameter("loai_cap_nhat"), "Chuyển giai đoạn"));
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
        s.setTrang_thai(valueOr(r.getParameter("trang_thai"), "Chưa xử lý"));
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
        t.setTrang_thai_luu_kho(valueOr(r.getParameter("trang_thai_luu_kho"), "Đã nhập kho"));
        t.setGhi_chu(r.getParameter("ghi_chu"));
        return t;
    }

    private Date parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            if (s.length() > 10) return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(s);
            return new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private int pInt(HttpServletRequest r, String name, int def) {
        return toInt(r.getParameter(name), def);
    }

    private double pDouble(HttpServletRequest r, String name, double def) {
        return toDouble(r.getParameter(name), def);
    }

    private int toInt(String s, int def) {
        try { return s == null || s.isBlank() ? def : Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }

    private double toDouble(String s, double def) {
        try { return s == null || s.isBlank() ? def : Double.parseDouble(s); }
        catch (Exception e) { return def; }
    }

    private String safe(String[] a, int i) {
        return a != null && i < a.length ? a[i] : null;
    }

    private String valueOr(String s, String def) {
        return s == null || s.isBlank() ? def : s;
    }
}
