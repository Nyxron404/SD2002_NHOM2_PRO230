package service;

import java.util.List;
import java.util.Map;

import dao.BaoCaoDAO;
import dao.ThongKeCanhTacDAO;
import dao.TrangTraiDAO;
import dao.KhuVucDAO;
import dao.VuonTrongDAO;
import dao.ThietBiDAO;
import dao.VatTuDAO;

import models.ChiPhiLoDat;
import models.LoHangVatTu;
import models.TrangTrai;
import models.VatTu;
import models.KhuVuc;
import models.VuonTrong;
import models.ThietBi;

public class BaoCaoService {
    
    private BaoCaoDAO baoCaoDAO = new BaoCaoDAO();
    private ThongKeCanhTacDAO thongKeDAO = new ThongKeCanhTacDAO();
    private TrangTraiDAO trangTraiDAO = new TrangTraiDAO();
    private KhuVucDAO khuVucDAO = new KhuVucDAO();
    private VuonTrongDAO vuonTrongDAO = new VuonTrongDAO();
    private ThietBiDAO thietBiDAO = new ThietBiDAO();
    private VatTuDAO vatTuDAO = new VatTuDAO();

    // --- BÁO CÁO CHI PHÍ ---
    public Map<String, Double> getChiPhiVatTuByDateRange(String startDate, String endDate) {
        return baoCaoDAO.getChiPhiVatTuByDateRange(startDate, endDate);
    }
    public double getTongChiPhiToanTrangTrai() {
        return thongKeDAO.getTongChiPhiToanTrangTrai();
    }

    // --- BÁO CÁO TỒN KHO ---
    public List<VatTu> getAllVatTu() { return vatTuDAO.getAll(); } // HÀM MỚI
    public List<VatTu> getVatTuDuoiNguong() { return baoCaoDAO.getVatTuDuoiNguong(); }
    public List<LoHangVatTu> getLoHangSapHetHan() { return baoCaoDAO.getLoHangSapHetHan(); }
    public List<LoHangVatTu> getLoHangDaHetHan() { return baoCaoDAO.getLoHangDaHetHan(); }

    // --- NĂNG SUẤT & CÂY TRỒNG ---
    public List<ChiPhiLoDat> getTongHopTheoLo() { return thongKeDAO.getTongHopTheoLo(); }
    public List<VuonTrong> getAllVuonTrong() { return vuonTrongDAO.getAllWithNames(); }

    // --- TỔNG QUAN ---
    public TrangTrai getThongTinTrangTrai() { return trangTraiDAO.getTrangTrai(); }
    public List<KhuVuc> getDanhSachKhuVuc(int trangTraiId) { return khuVucDAO.getAllByTrangTrai(trangTraiId); }
    public List<ThietBi> getAllThietBi() { return thietBiDAO.getAll(); }
}