package service;

import java.util.List;

import dao.GhiNhanSauBenhDAO;
import dao.GhiNhanThuHoachDAO;
import dao.GiongSauRiengDAO;
import dao.LichChamSocDAO;
import dao.LichSuSinhTruongDAO;
import dao.MatDoThamChieuDAO;
import dao.NhatKyChamSocDAO;
import dao.ThongKeCanhTacDAO;
import dao.TonKhoCanhTacDAO;
import dao.VuonTrongDAO;

import models.ChiPhiLoDat;
import models.GhiNhanSauBenh;
import models.GhiNhanThuHoach;
import models.GiongSauRieng;
import models.LichChamSoc;
import models.LichSuSinhTruong;
import models.MatDoThamChieu;
import models.NhacViecItem;
import models.NhatKyChamSoc;
import models.VatTuTonKho;
import models.VuonTrong;

/**
 * Tầng nghiệp vụ cho module Quản lý canh tác (UC-4.1 → UC-4.7).
 *
 * Nguyên tắc: mọi số tiền và diện tích đều do backend tính, không nhận từ form.
 * Service chỉ kiểm tra hợp lệ nghiệp vụ rồi ủy quyền cho DAO (nơi mở transaction).
 */
public class CanhTacService {

    private final GiongSauRiengDAO giongDAO = new GiongSauRiengDAO();
    private final MatDoThamChieuDAO matDoDAO = new MatDoThamChieuDAO();
    private final VuonTrongDAO vuonDAO = new VuonTrongDAO();
    private final LichChamSocDAO lichDAO = new LichChamSocDAO();
    private final NhatKyChamSocDAO nhatKyDAO = new NhatKyChamSocDAO();
    private final LichSuSinhTruongDAO sinhTruongDAO = new LichSuSinhTruongDAO();
    private final GhiNhanSauBenhDAO sauBenhDAO = new GhiNhanSauBenhDAO();
    private final GhiNhanThuHoachDAO thuHoachDAO = new GhiNhanThuHoachDAO();
    private final ThongKeCanhTacDAO thongKeDAO = new ThongKeCanhTacDAO();
    private final TonKhoCanhTacDAO tonKhoDAO = new TonKhoCanhTacDAO();

    // ===================================================================
    // UC-4.1 GIỐNG
    // ===================================================================

    public List<GiongSauRieng> getAllGiong() { return giongDAO.getAll(); }
    public GiongSauRieng getGiong(int id) { return giongDAO.getById(id); }
    public String[] getDanhSachTrangThaiGiong() { return GiongSauRiengDAO.TRANG_THAI; }

    public String addGiong(GiongSauRieng g) {
        String loi = kiemTraGiong(g, 0);
        if (loi != null) return loi;
        return giongDAO.insert(g) ? null : "Không thể thêm giống.";
    }

    public String updateGiong(GiongSauRieng g) {
        if (g.getId() <= 0) return "Thiếu mã giống cần sửa.";
        String loi = kiemTraGiong(g, g.getId());
        if (loi != null) return loi;
        return giongDAO.update(g) ? null : "Không thể cập nhật giống.";
    }

    private String kiemTraGiong(GiongSauRieng g, int excludeId) {
        if (g.getTen_giong() == null || g.getTen_giong().isBlank()) return "Tên giống không được để trống.";
        if (giongDAO.tenTonTai(g.getTen_giong(), excludeId)) return "Tên giống đã tồn tại.";
        if (g.getThoi_gian_sinh_truong_thu_hoach() < 0) return "Thời gian sinh trưởng không được âm.";
        if (g.getNang_suat_tham_khao() < 0) return "Năng suất tham khảo không được âm.";
        if (!GiongSauRiengDAO.trangThaiHopLe(g.getTrang_thai()))
            return "Trạng thái giống không hợp lệ.";
        return null;
    }

    public String deleteGiong(int id) {
        int soLo = giongDAO.demLoDangDung(id);
        if (soLo > 0) return "Không thể xóa: giống đang được " + soLo
                + " lô đất sử dụng. Vui lòng gỡ khỏi các lô trước.";
        return giongDAO.delete(id) ? null : "Không thể xóa giống.";
    }

    // ===================================================================
    // UC-4.2 VƯỜN TRỒNG
    // ===================================================================

    public List<MatDoThamChieu> getBangMatDo() { return matDoDAO.getAll(); }
    public List<VuonTrong> getAllVuon() { return vuonDAO.getAllWithNames(); }
    public VuonTrong getVuon(int id) { return vuonDAO.getById(id); }

    public String addVuon(VuonTrong v) {
        String loi = kiemTraVuon(v, 0);
        if (loi != null) return loi;
        return vuonDAO.insert(v) ? null
                : "Không thể thiết lập vườn. Kiểm tra lô đất đã có diện tích hợp lệ chưa.";
    }

    public String updateVuon(VuonTrong v) {
        if (v.getId() <= 0) return "Thiếu mã vườn cần sửa.";
        String loi = kiemTraVuon(v, v.getId());
        if (loi != null) return loi;
        return vuonDAO.update(v) ? null : "Không thể cập nhật vườn trồng.";
    }

    private String kiemTraVuon(VuonTrong v, int excludeId) {
        if (v.getLo_dat_id() <= 0) return "Vui lòng chọn lô đất.";
        if (v.getGiong_id() <= 0) return "Vui lòng chọn giống.";
        if (v.getSo_luong_cay() <= 0) return "Số lượng cây phải lớn hơn 0.";
        if (vuonDAO.loDaCoVuon(v.getLo_dat_id(), excludeId))
            return "Lô đất này đã được thiết lập vườn. Vui lòng chọn lô khác hoặc sửa vườn hiện có.";
        return null;
    }

    public String deleteVuon(int id) {
        int rangBuoc = vuonDAO.demRangBuoc(id);
        if (rangBuoc > 0) return "Không thể xóa: vườn đã có " + rangBuoc
                + " bản ghi sinh trưởng/sâu bệnh/thu hoạch liên quan.";
        return vuonDAO.delete(id) ? null : "Không thể xóa thiết lập vườn.";
    }

    // ===================================================================
    // UC-4.3 LỊCH CHĂM SÓC & NHẮC VIỆC
    // ===================================================================

    public List<LichChamSoc> getAllLich() { return lichDAO.getAllWithLo(); }
    public LichChamSoc getLich(int id) { return lichDAO.getById(id); }

    /** Sinh bù nhắc việc cho lịch vĩnh viễn và đánh dấu quá hạn. Gọi khi mở màn hình. */
    public void lamMoiNhacViec() { lichDAO.sinhBuNhacViecVinhVien(); }

    public List<NhacViecItem> getNhacViecChoXuLy() { return tonKhoDAO.getNhacViecChoXuLy(); }
    public List<NhacViecItem> getNhacViecGanDay() { return tonKhoDAO.getNhacViecGanDay(); }

    public String addLich(LichChamSoc l) {
        String loi = kiemTraLich(l);
        if (loi != null) return loi;
        return lichDAO.insert(l) ? null : "Không thể tạo lịch chăm sóc.";
    }

    public String updateLich(LichChamSoc l) {
        if (l.getId() <= 0) return "Thiếu mã lịch cần sửa.";
        String loi = kiemTraLich(l);
        if (loi != null) return loi;
        return lichDAO.update(l) ? null : "Không thể cập nhật lịch chăm sóc.";
    }

    private String kiemTraLich(LichChamSoc l) {
        if (l.getLoai_cong_viec() == null || l.getLoai_cong_viec().isBlank())
            return "Vui lòng nhập loại công việc.";
        if (l.getNgay_bat_dau() == null) return "Vui lòng chọn ngày bắt đầu.";
        if (l.getNgay_ket_thuc() != null && l.getNgay_ket_thuc().before(l.getNgay_bat_dau()))
            return "Ngày kết thúc phải sau ngày bắt đầu.";
        if (l.getDanh_sach_lo_id() == null || l.getDanh_sach_lo_id().isBlank())
            return "Vui lòng chọn ít nhất một lô đất áp dụng.";
        return null;
    }

    public String deleteLich(int id) {
        return lichDAO.delete(id) ? null
                : "Không thể xóa lịch. Lịch có thể đã phát sinh nhật ký chăm sóc.";
    }

    // ===================================================================
    // UC-4.4 NHẬT KÝ CHĂM SÓC
    // ===================================================================

    public List<NhatKyChamSoc> getAllNhatKy() { return nhatKyDAO.getAllWithLo(); }
    public List<NhatKyChamSoc> getNhatKyTheoLo(int loDatId) { return nhatKyDAO.getByLoDat(loDatId); }

    /** Tồn kho khả dụng để đổ lên form (đã loại lô hết hạn / hết hàng). */
    public List<VatTuTonKho> getVatTuKhaDung() { return tonKhoDAO.getVatTuKhaDung(); }
    public List<VatTuTonKho> getDungCuKhaDung() { return tonKhoDAO.getDungCuKhaDung(); }
    public List<VatTuTonKho> getThietBiKhaDung() { return tonKhoDAO.getThietBiKhaDung(); }
    public List<VatTuTonKho> getThuocBaoVeThucVat() { return tonKhoDAO.getThuocBaoVeThucVat(); }

    public String addNhatKy(NhatKyChamSoc n) {
        boolean rong = n.getDongVatTu().isEmpty() && n.getDongDungCu().isEmpty()
                    && n.getDongThietBi().isEmpty();
        if (rong && (n.getMo_ta() == null || n.getMo_ta().isBlank()))
            return "Nhật ký phải có ít nhất một dòng vật tư/dụng cụ/thiết bị hoặc phần mô tả công việc.";
        return nhatKyDAO.insertFull(n);
    }

    public String deleteNhatKy(int id) {
        return nhatKyDAO.delete(id) ? null : "Không thể xóa nhật ký.";
    }

    // ===================================================================
    // UC-4.5 SINH TRƯỞNG
    // ===================================================================

    public List<LichSuSinhTruong> getAllSinhTruong() { return sinhTruongDAO.getAllWithVuon(); }
    public LichSuSinhTruong getSinhTruongTheoVuon(int vuonId) { return sinhTruongDAO.getByVuon(vuonId); }
    public String[] getDanhSachGiaiDoan() { return LichSuSinhTruongDAO.GIAI_DOAN; }

    /** Mỗi lô chỉ có một bản ghi theo dõi; gọi lại sẽ cập nhật bản ghi đó. */
    public String luuSinhTruong(LichSuSinhTruong s) {
        if (s.getVuon_trong_id() <= 0) return "Vui lòng chọn vườn/lô cần cập nhật.";
        boolean coGiaiDoan = s.getGiai_doan_moi() != null && !s.getGiai_doan_moi().isBlank();
        boolean coTyLe = s.getTy_le_giai_doan() != null && !s.getTy_le_giai_doan().isBlank();
        boolean coGiamCay = s.getSo_luong_cay_giam() != null && s.getSo_luong_cay_giam() > 0;
        if (!coGiaiDoan && !coTyLe && !coGiamCay)
            return "Cần chọn giai đoạn, nhập tỷ lệ giai đoạn hoặc số cây giảm.";
        return sinhTruongDAO.luu(s);
    }

    // ===================================================================
    // UC-4.6 SÂU BỆNH
    // ===================================================================

    public List<GhiNhanSauBenh> getAllSauBenh() { return sauBenhDAO.getAllWithVuon(); }
    public GhiNhanSauBenh getSauBenh(int id) { return sauBenhDAO.getById(id); }
    public String[] getDanhSachTrangThaiSauBenh() { return GhiNhanSauBenhDAO.TRANG_THAI; }
    public String[] getDanhSachMucDo() { return GhiNhanSauBenhDAO.MUC_DO; }
    public double getChiPhiXuLySauBenh(int id) { return sauBenhDAO.getChiPhiXuLy(id); }

    public String addSauBenh(GhiNhanSauBenh s) { return sauBenhDAO.insert(s); }

    /**
     * Cập nhật ghi nhận sâu bệnh. Nếu chuyển sang "Đã xử lý" kèm danh sách thuốc,
     * hệ thống trừ kho, tính tiền và diện tích thông qua một nhật ký chăm sóc.
     */
    public String capNhatSauBenh(GhiNhanSauBenh s, int nguoiThucHien) {
        if (s.getId() <= 0) return "Thiếu mã ghi nhận cần cập nhật.";
        boolean daXuLy = "Đã xử lý".equalsIgnoreCase(s.getTrang_thai());
        if (daXuLy && (s.getBien_phap_xu_ly() == null || s.getBien_phap_xu_ly().isBlank()))
            return "Vui lòng ghi rõ biện pháp đã áp dụng trước khi đóng ghi nhận.";
        return sauBenhDAO.capNhat(s, nguoiThucHien);
    }

    public String deleteSauBenh(int id) {
        return sauBenhDAO.delete(id) ? null : "Không thể xóa ghi nhận sâu bệnh.";
    }

    // ===================================================================
    // UC-4.7 THU HOẠCH
    // ===================================================================

    public List<GhiNhanThuHoach> getAllThuHoach() { return thuHoachDAO.getAllWithVuon(); }
    public String addThuHoach(GhiNhanThuHoach t, boolean hoanTat) { return thuHoachDAO.insert(t, hoanTat); }

    public String deleteThuHoach(int id) {
        return thuHoachDAO.delete(id) ? null : "Không thể xóa phiếu thu hoạch.";
    }

    // ===================================================================
    // THEO DÕI CHI PHÍ & TIÊU HAO
    // ===================================================================

    public List<ChiPhiLoDat> getTongHopChiPhi() { return thongKeDAO.getTongHopTheoLo(); }
    public double getTongChiPhiCanhTac() { return thongKeDAO.getTongChiPhiToanTrangTrai(); }
    public List<String[]> getChiTietVatTuNhatKy(int nhatKyId) { return thongKeDAO.getChiTietVatTu(nhatKyId); }
    public List<String[]> getChiTietThietBiNhatKy(int nhatKyId) { return thongKeDAO.getChiTietThietBi(nhatKyId); }
}
