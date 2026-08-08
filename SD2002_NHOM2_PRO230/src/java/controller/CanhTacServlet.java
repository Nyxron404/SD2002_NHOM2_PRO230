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
 * UC-4 - Quản lý canh tác sầu riêng.
 *
 * Toàn bộ action:
 *   giong_insert     giong_update     giong_delete
 *   vuon_insert      vuon_update      vuon_delete
 *   lich_insert      lich_update      lich_delete
 *   nhatky_insert    nhatky_delete
 *   sinhtruong_luu
 *   saubenh_insert   saubenh_update   saubenh_delete
 *   thuhoach_insert  thuhoach_delete
 */
@WebServlet(name = "CanhTacServlet", urlPatterns = {"/canhtac"})
public class CanhTacServlet extends HttpServlet {

    private final CanhTacService service = new CanhTacService();
    private final DonViQuanLyDAO donViDAO = new DonViQuanLyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Sinh bù nhắc việc cho lịch vĩnh viễn + đánh dấu quá hạn
        service.lamMoiNhacViec();

        request.setAttribute("listGiong", service.getAllGiong());
        request.setAttribute("trangThaiGiong", service.getDanhSachTrangThaiGiong());

        request.setAttribute("listMatDo", service.getBangMatDo());
        request.setAttribute("listVuon", service.getAllVuon());

        request.setAttribute("listLich", service.getAllLich());
        request.setAttribute("listNhacViec", service.getNhacViecGanDay());
        request.setAttribute("nhacViecChoXuLy", service.getNhacViecChoXuLy());

        request.setAttribute("listNhatKy", service.getAllNhatKy());
        request.setAttribute("listSinhTruong", service.getAllSinhTruong());
        request.setAttribute("giaiDoan", service.getDanhSachGiaiDoan());

        request.setAttribute("listSauBenh", service.getAllSauBenh());
        request.setAttribute("trangThaiSauBenh", service.getDanhSachTrangThaiSauBenh());
        request.setAttribute("mucDoSauBenh", service.getDanhSachMucDo());
        request.setAttribute("listThuoc", service.getThuocBaoVeThucVat());

        request.setAttribute("listThuHoach", service.getAllThuHoach());

        // Lô đất và ô chứa lấy từ module Quản lý khu vực
        request.setAttribute("listLoDat", donViDAO.getLoDat());
        request.setAttribute("listOChua", donViDAO.getOChua());

        // Tồn kho KHẢ DỤNG - đã loại lô hết hạn, hết hàng, thiết bị đang bảo trì
        request.setAttribute("listVatTu", service.getVatTuKhaDung());
        request.setAttribute("listDungCu", service.getDungCuKhaDung());
        request.setAttribute("listThietBi", service.getThietBiKhaDung());

        request.setAttribute("listChiPhi", service.getTongHopChiPhi());
        request.setAttribute("tongChiPhiCanhTac", service.getTongChiPhiCanhTac());

        request.getRequestDispatcher("/views/canhTac/canhTac.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String tab = request.getParameter("tab");
        HttpSession session = request.getSession();
        int nguoiThucHien = layTaiKhoanId(session);

        try {
            switch (action == null ? "" : action) {

                // ---------- UC-4.1 GIỐNG ----------
                case "giong_insert":
                    bao(session, service.addGiong(docGiong(request, false)), "Thêm giống thành công.");
                    break;

                case "giong_update":
                    bao(session, service.updateGiong(docGiong(request, true)), "Cập nhật giống thành công.");
                    break;

                case "giong_delete":
                    bao(session, service.deleteGiong(pInt(request, "id", 0)), "Xóa giống thành công.");
                    break;

                // ---------- UC-4.2 VƯỜN ----------
                case "vuon_insert":
                    bao(session, service.addVuon(docVuon(request, false)),
                        "Thiết lập vườn thành công. Diện tích và mật độ được tính từ lô đất.");
                    break;

                case "vuon_update":
                    bao(session, service.updateVuon(docVuon(request, true)),
                        "Cập nhật vườn thành công. Mật độ đã được tính lại.");
                    break;

                case "vuon_delete":
                    bao(session, service.deleteVuon(pInt(request, "id", 0)), "Xóa thiết lập vườn thành công.");
                    break;

                // ---------- UC-4.3 LỊCH ----------
                case "lich_insert": {
                    LichChamSoc l = docLich(request, false);
                    l.setNguoi_tao_id(nguoiThucHien);
                    bao(session, service.addLich(l), "Tạo lịch chăm sóc và sinh nhắc việc thành công.");
                    break;
                }

                case "lich_update": {
                    LichChamSoc l = docLich(request, true);
                    l.setNguoi_tao_id(nguoiThucHien);
                    bao(session, service.updateLich(l), "Cập nhật lịch và sinh lại nhắc việc thành công.");
                    break;
                }

                case "lich_delete":
                    bao(session, service.deleteLich(pInt(request, "id", 0)), "Xóa lịch thành công.");
                    break;

                // ---------- UC-4.4 NHẬT KÝ ----------
                case "nhatky_insert": {
                    NhatKyChamSoc n = docNhatKy(request);
                    n.setNguoi_ghi_nhan_id(nguoiThucHien);
                    bao(session, service.addNhatKy(n),
                        "Ghi nhật ký thành công. Chi phí và diện tích kho đã được cập nhật.");
                    break;
                }

                case "nhatky_delete":
                    bao(session, service.deleteNhatKy(pInt(request, "id", 0)),
                        "Đã xóa nhật ký và hoàn tác tồn kho, diện tích, thiết bị.");
                    break;

                // ---------- UC-4.5 SINH TRƯỞNG ----------
                case "sinhtruong_luu":
                case "sinhtruong_insert": {
                    LichSuSinhTruong st = docSinhTruong(request);
                    st.setNguoi_cap_nhat_id(nguoiThucHien);
                    bao(session, service.luuSinhTruong(st), "Đã lưu theo dõi sinh trưởng của lô.");
                    break;
                }

                // ---------- UC-4.6 SÂU BỆNH ----------
                case "saubenh_insert": {
                    GhiNhanSauBenh sb = docSauBenh(request, false);
                    sb.setNguoi_ghi_nhan_id(nguoiThucHien);
                    bao(session, service.addSauBenh(sb), "Ghi nhận sâu bệnh thành công.");
                    break;
                }

                case "saubenh_update": {
                    GhiNhanSauBenh sb = docSauBenh(request, true);
                    sb.setNguoi_ghi_nhan_id(nguoiThucHien);
                    String tt = sb.getTrang_thai();
                    String thanhCong = "Đã xử lý".equalsIgnoreCase(tt) && !sb.getDongThuoc().isEmpty()
                            ? "Đã đóng ghi nhận sâu bệnh. Thuốc đã được trừ kho, chi phí và diện tích đã tính vào lô."
                            : "Cập nhật ghi nhận sâu bệnh thành công.";
                    bao(session, service.capNhatSauBenh(sb, nguoiThucHien), thanhCong);
                    break;
                }

                case "saubenh_delete":
                    bao(session, service.deleteSauBenh(pInt(request, "id", 0)),
                        "Đã xóa ghi nhận sâu bệnh và hoàn tác thuốc đã dùng (nếu có).");
                    break;

                // ---------- UC-4.7 THU HOẠCH ----------
                case "thuhoach_insert": {
                    GhiNhanThuHoach th = docThuHoach(request);
                    th.setNguoi_ghi_nhan_id(nguoiThucHien);
                    boolean hoanTat = "true".equalsIgnoreCase(request.getParameter("hoan_tat"));
                    bao(session, service.addThuHoach(th, hoanTat),
                        "Ghi nhận thu hoạch thành công. Diện tích kho lưu trữ đã được cập nhật.");
                    break;
                }

                case "thuhoach_delete":
                    bao(session, service.deleteThuHoach(pInt(request, "id", 0)),
                        "Đã xóa phiếu thu hoạch và trả lại diện tích kho.");
                    break;

                default:
                    session.setAttribute("canhTacMessage", "Hành động không hợp lệ.");
                    session.setAttribute("canhTacLoi", true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("canhTacMessage",
                    "Lỗi xử lý: " + (e.getMessage() == null ? "Không xác định" : e.getMessage()));
            session.setAttribute("canhTacLoi", true);
        }

        String redirect = request.getContextPath() + "/canhtac";
        if (tab != null && !tab.isBlank()) {
            redirect += "?tab=" + java.net.URLEncoder.encode(tab, java.nio.charset.StandardCharsets.UTF_8);
        }
        response.sendRedirect(redirect);
    }

    // ===================================================================
    // ĐỌC DỮ LIỆU TỪ FORM
    // ===================================================================

    private GiongSauRieng docGiong(HttpServletRequest r, boolean laSua) {
        GiongSauRieng g = new GiongSauRieng();
        if (laSua) g.setId(pInt(r, "id", 0));
        g.setTen_giong(trim(r.getParameter("ten_giong")));
        g.setDac_diem(valueOr(r.getParameter("dac_diem"), ""));
        g.setThoi_gian_sinh_truong_thu_hoach(pInt(r, "thoi_gian_sinh_truong_thu_hoach", 0));
        g.setNang_suat_tham_khao(pDouble(r, "nang_suat_tham_khao", 0));
        g.setTrang_thai(valueOr(r.getParameter("trang_thai"), "Đang canh tác"));
        return g;
    }

    private VuonTrong docVuon(HttpServletRequest r, boolean laSua) {
        VuonTrong v = new VuonTrong();
        if (laSua) v.setId(pInt(r, "id", 0));
        v.setLo_dat_id(pInt(r, "lo_dat_id", 0));
        v.setGiong_id(pInt(r, "giong_id", 0));
        // Cố ý KHÔNG đọc dien_tich và mat_do_trong từ form: hai giá trị này do
        // VuonTrongDAO tính lại từ DonViQuanLy.dien_tich. Các ô trên giao diện
        // chỉ là hiển thị xem trước bằng JavaScript.
        v.setSo_luong_cay(pInt(r, "so_luong_cay", 0));
        Date ngayTrong = parseDate(r.getParameter("ngay_trong"));
        v.setNgay_trong(ngayTrong != null ? ngayTrong : new Date());
        v.setGhi_chu(r.getParameter("ghi_chu"));
        v.setTrang_thai_sinh_truong(valueOr(r.getParameter("trang_thai_sinh_truong"), "Cây con"));
        return v;
    }

    private LichChamSoc docLich(HttpServletRequest r, boolean laSua) {
        LichChamSoc l = new LichChamSoc();
        if (laSua) l.setId(pInt(r, "id", 0));
        l.setLoai_cong_viec(trim(r.getParameter("loai_cong_viec")));
        l.setNgay_bat_dau(parseDate(r.getParameter("ngay_bat_dau")));
        // 0 hoặc để trống = lặp hằng ngày -> DAO quy đổi thành 1
        l.setChu_ky_ngay(pInt(r, "chu_ky_ngay", 0));
        // Để trống ngày kết thúc = lịch vĩnh viễn
        l.setNgay_ket_thuc(parseDate(r.getParameter("ngay_ket_thuc")));
        l.setMo_ta(r.getParameter("mo_ta"));
        l.setTrang_thai(valueOr(r.getParameter("trang_thai"), "Đang áp dụng"));
        l.setDanh_sach_lo_id(gop(r.getParameterValues("lo_ids")));
        return l;
    }

    /**
     * Đọc nhật ký chăm sóc.
     * Checkbox gửi id dòng được chọn (vt_id), ô số lượng đặt tên theo id
     * (vt_qty_&lt;id&gt;) nên số lượng luôn khớp đúng vật tư kể cả khi người dùng
     * chỉ tích vài dòng giữa bảng.
     */
    private NhatKyChamSoc docNhatKy(HttpServletRequest r) {
        NhatKyChamSoc n = new NhatKyChamSoc();
        n.setLo_dat_id(pInt(r, "lo_dat_id", 0));
        n.setLoai_cong_viec(trim(r.getParameter("loai_cong_viec")));
        n.setNgay_thuc_hien(parseDate(r.getParameter("ngay_thuc_hien")));
        n.setMo_ta(r.getParameter("mo_ta"));
        int nhacViec = pInt(r, "nhac_viec_id", 0);
        n.setNhac_viec_id(nhacViec > 0 ? nhacViec : null);

        docDongVatTu(r, n, "vt_id", "vt_qty_");
        docDongDungCu(r, n);
        docDongThietBi(r, n);
        return n;
    }

    private void docDongVatTu(HttpServletRequest r, NhatKyChamSoc n, String tenCheckbox, String tienTo) {
        String[] ids = r.getParameterValues(tenCheckbox);
        if (ids == null) return;
        for (String s : ids) {
            int id = toInt(s, 0);
            if (id <= 0) continue;
            double qty = pDouble(r, tienTo + id, 0);
            if (qty > 0) n.getDongVatTu().add(new NhatKyChamSoc.DongVatTu(id, qty));
        }
    }

    private void docDongDungCu(HttpServletRequest r, NhatKyChamSoc n) {
        String[] ids = r.getParameterValues("dc_id");
        if (ids == null) return;
        for (String s : ids) {
            int id = toInt(s, 0);
            if (id <= 0) continue;
            double qty = pDouble(r, "dc_qty_" + id, 0);
            if (qty > 0) n.getDongDungCu().add(new NhatKyChamSoc.DongDungCu(id, qty));
        }
    }

    private void docDongThietBi(HttpServletRequest r, NhatKyChamSoc n) {
        String[] ids = r.getParameterValues("tb_id");
        if (ids == null) return;
        for (String s : ids) {
            int id = toInt(s, 0);
            if (id <= 0) continue;
            int soNgay = pInt(r, "tb_ngay_" + id, 1);
            n.getDongThietBi().add(new NhatKyChamSoc.DongThietBi(id, Math.max(1, soNgay)));
        }
    }

    /**
     * Đọc theo dõi sinh trưởng. Tỷ lệ giai đoạn nhập theo dòng
     * (gd_ten[] / gd_ty_le[]) rồi gộp thành chuỗi "Giai đoạn:%,Giai đoạn:%".
     */
    private LichSuSinhTruong docSinhTruong(HttpServletRequest r) {
        LichSuSinhTruong s = new LichSuSinhTruong();
        s.setVuon_trong_id(pInt(r, "vuon_trong_id", 0));
        s.setGiai_doan_moi(trim(r.getParameter("giai_doan_moi")));
        int giam = pInt(r, "so_luong_cay_giam", 0);
        s.setSo_luong_cay_giam(giam > 0 ? giam : null);
        s.setLoai_cap_nhat(valueOr(r.getParameter("loai_cap_nhat"), "Chuyển giai đoạn"));
        s.setGhi_chu(valueOr(r.getParameter("ghi_chu"), ""));

        String[] ten = r.getParameterValues("gd_ten");
        String[] tyLe = r.getParameterValues("gd_ty_le");
        StringBuilder sb = new StringBuilder();
        if (ten != null) {
            for (int i = 0; i < ten.length; i++) {
                if (ten[i] == null || ten[i].isBlank()) continue;
                double pt = toDouble(safe(tyLe, i), 0);
                if (pt <= 0) continue;
                if (sb.length() > 0) sb.append(',');
                sb.append(ten[i].trim()).append(':').append(pt);
            }
        }
        // Cho phép nhập tự do bằng ô text nếu bảng dòng để trống
        s.setTy_le_giai_doan(sb.length() > 0 ? sb.toString() : trim(r.getParameter("ty_le_giai_doan")));
        return s;
    }

    private GhiNhanSauBenh docSauBenh(HttpServletRequest r, boolean laSua) {
        GhiNhanSauBenh s = new GhiNhanSauBenh();
        if (laSua) s.setId(pInt(r, "id", 0));
        s.setVuon_trong_id(pInt(r, "vuon_trong_id", 0));
        s.setTen_sau_benh(trim(r.getParameter("ten_sau_benh")));
        s.setMuc_do_nghiem_trong(valueOr(r.getParameter("muc_do_nghiem_trong"), "Nhẹ"));
        Date ngay = parseDate(r.getParameter("ngay_phat_hien"));
        s.setNgay_phat_hien(ngay != null ? ngay : new Date());
        s.setBien_phap_xu_ly(r.getParameter("bien_phap_xu_ly"));
        s.setTrang_thai(valueOr(r.getParameter("trang_thai"), "Chưa xử lý"));

        // Thuốc BVTV chỉ đọc khi đóng ghi nhận (trạng thái Đã xử lý)
        String[] ids = r.getParameterValues("thuoc_id");
        if (ids != null) {
            for (String v : ids) {
                int id = toInt(v, 0);
                if (id <= 0) continue;
                double qty = pDouble(r, "thuoc_qty_" + id, 0);
                if (qty > 0) s.getDongThuoc().add(new NhatKyChamSoc.DongVatTu(id, qty));
            }
        }
        return s;
    }

    private GhiNhanThuHoach docThuHoach(HttpServletRequest r) {
        GhiNhanThuHoach t = new GhiNhanThuHoach();
        t.setVuon_trong_id(pInt(r, "vuon_trong_id", 0));
        t.setTen_vu_mua(trim(r.getParameter("ten_vu_mua")));
        t.setNgay_thu_hoach(parseDate(r.getParameter("ngay_thu_hoach")));
        t.setVi_tri_luu_tru_id(pInt(r, "vi_tri_luu_tru_id", 0));
        t.setTong_san_luong_kg(pDouble(r, "tong_san_luong_kg", 0));
        t.setTong_dien_tich_chiem_dung(pDouble(r, "tong_dien_tich_chiem_dung", 0));
        t.setTrang_thai_luu_kho(valueOr(r.getParameter("trang_thai_luu_kho"), "Đã nhập kho"));
        t.setGhi_chu(r.getParameter("ghi_chu"));

        String[] loai = r.getParameterValues("pl_loai");
        String[] kg = r.getParameterValues("pl_kg");
        String[] dt = r.getParameterValues("pl_dt");
        if (loai != null) {
            for (int i = 0; i < loai.length; i++) {
                if (loai[i] == null || loai[i].isBlank()) continue;
                t.getDongPhanLoai().add(new GhiNhanThuHoach.DongPhanLoai(
                        loai[i].trim(), toDouble(safe(kg, i), 0), toDouble(safe(dt, i), 0)));
            }
        }
        return t;
    }

    // ===================================================================
    // TIỆN ÍCH
    // ===================================================================

    private int layTaiKhoanId(HttpSession session) {
        Object tk = session == null ? null : session.getAttribute("taiKhoan");
        return (tk instanceof TaiKhoan) ? ((TaiKhoan) tk).getId() : 0;
    }

    /** loi == null nghĩa là thành công. */
    private void bao(HttpSession session, String loi, String thanhCong) {
        session.setAttribute("canhTacMessage", loi == null ? thanhCong : loi);
        session.setAttribute("canhTacLoi", loi != null);
    }

    private String gop(String[] values) {
        if (values == null || values.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (String v : values) {
            if (v == null || v.isBlank()) continue;
            if (sb.length() > 0) sb.append(',');
            sb.append(v.trim());
        }
        return sb.length() == 0 ? null : sb.toString();
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

    private int pInt(HttpServletRequest r, String name, int def) { return toInt(r.getParameter(name), def); }
    private double pDouble(HttpServletRequest r, String name, double def) { return toDouble(r.getParameter(name), def); }

    private int toInt(String s, int def) {
        try { return s == null || s.isBlank() ? def : Integer.parseInt(s.trim()); }
        catch (Exception e) { return def; }
    }

    private double toDouble(String s, double def) {
        try { return s == null || s.isBlank() ? def : Double.parseDouble(s.trim().replace(",", ".")); }
        catch (Exception e) { return def; }
    }

    private String safe(String[] a, int i) { return a != null && i < a.length ? a[i] : null; }
    private String trim(String s) { return s == null ? null : s.trim(); }
    private String valueOr(String s, String def) { return s == null || s.isBlank() ? def : s.trim(); }
}
