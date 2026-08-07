package service;

import java.util.List;

import dao.GhiNhanSauBenhDAO;
import dao.GhiNhanThuHoachDAO;
import dao.GiongSauRiengDAO;
import dao.LichChamSocDAO;
import dao.LichSuSinhTruongDAO;
import dao.MatDoThamChieuDAO;
import dao.NhatKyChamSocDAO;
import dao.VuonTrongDAO;

import models.GhiNhanSauBenh;
import models.GhiNhanThuHoach;
import models.GiongSauRieng;
import models.LichChamSoc;
import models.LichSuSinhTruong;
import models.MatDoThamChieu;
import models.NhatKyChamSoc;
import models.VuonTrong;

/**
 * Tầng nghiệp vụ tổng hợp cho module Quản lý canh tác (UC-4.1 → UC-4.7).
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

    // ---- UC-4.1 Giống ----
    public List<GiongSauRieng> getAllGiong() { return giongDAO.getAll(); }

    public String addGiong(GiongSauRieng g) {
        if (giongDAO.tenTonTai(g.getTen_giong(), 0)) return "Tên giống đã tồn tại";
        return giongDAO.insert(g) ? null : "Không thể thêm giống";
    }

    public String updateGiong(GiongSauRieng g) {
        if (giongDAO.tenTonTai(g.getTen_giong(), g.getId())) return "Tên giống đã tồn tại";
        return giongDAO.update(g) ? null : "Không thể cập nhật giống";
    }

    public String deleteGiong(int id) {
        int soLo = giongDAO.demLoDangDung(id);
        if (soLo > 0) return "Không thể xóa: giống đang được " + soLo + " lô đất sử dụng. Vui lòng gỡ khỏi các lô trước.";
        return giongDAO.delete(id) ? null : "Không thể xóa giống";
    }

    // ---- Tham chiếu mật độ ----
    public List<MatDoThamChieu> getBangMatDo() { return matDoDAO.getAll(); }

    // ---- UC-4.2 Vườn trồng ----
    public List<VuonTrong> getAllVuon() { return vuonDAO.getAllWithNames(); }
    public boolean addVuon(VuonTrong v) { return vuonDAO.insert(v); }
    public boolean updateVuon(VuonTrong v) { return vuonDAO.update(v); }
    public boolean deleteVuon(int id) { return vuonDAO.delete(id); }

    // ---- UC-4.3 Lịch chăm sóc ----
    public List<LichChamSoc> getAllLich() { return lichDAO.getAllWithLo(); }
    public boolean addLich(LichChamSoc l) { return lichDAO.insert(l); }
    public boolean updateLich(LichChamSoc l) { return lichDAO.update(l); }
    public boolean deleteLich(int id) { return lichDAO.delete(id); }

    // ---- UC-4.4 Nhật ký chăm sóc ----
    public List<NhatKyChamSoc> getAllNhatKy() { return nhatKyDAO.getAllWithLo(); }
    public String addNhatKy(NhatKyChamSoc n) { return nhatKyDAO.insertFull(n); }
    public boolean deleteNhatKy(int id) { return nhatKyDAO.delete(id); }

    // ---- UC-4.5 Sinh trưởng ----
    public List<LichSuSinhTruong> getAllSinhTruong() { return sinhTruongDAO.getAllWithVuon(); }
    public boolean addSinhTruong(LichSuSinhTruong s) { return sinhTruongDAO.insert(s); }

    // ---- UC-4.6 Sâu bệnh ----
    public List<GhiNhanSauBenh> getAllSauBenh() { return sauBenhDAO.getAllWithVuon(); }
    public String addSauBenh(GhiNhanSauBenh s) { return sauBenhDAO.insert(s); }
    public boolean updateSauBenh(GhiNhanSauBenh s) { return sauBenhDAO.update(s); }
    public boolean deleteSauBenh(int id) { return sauBenhDAO.delete(id); }

    // ---- UC-4.7 Thu hoạch ----
    public List<GhiNhanThuHoach> getAllThuHoach() { return thuHoachDAO.getAllWithVuon(); }
    public String addThuHoach(GhiNhanThuHoach t, boolean hoanTat) { return thuHoachDAO.insert(t, hoanTat); }
    public boolean deleteThuHoach(int id) { return thuHoachDAO.delete(id); }
}
