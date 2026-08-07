package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import models.NhatKyChamSoc.DongVatTu;

/**
 * UC-4.6 - Ghi nhận và xử lý sâu bệnh trên lô (có thể xuất kho thuốc - include UC-3.2).
 * Bảng: GhiNhanSauBenh
 */
public class GhiNhanSauBenh {
    private int id;
    private int vuon_trong_id;      // -> VuonTrong
    private String ten_sau_benh;
    private String muc_do_nghiem_trong;
    private Date ngay_phat_hien;
    private String bien_phap_xu_ly;
    private Integer nhat_ky_cham_soc_id;
    private String trang_thai;      // Chưa xử lý / Đang xử lý / Đã xử lý
    private int nguoi_ghi_nhan_id;

    private String ten_lo_dat; // hiển thị

    // Thuốc BVTV sử dụng khi xử lý (xuất kho FIFO)
    private List<DongVatTu> dongThuoc = new ArrayList<>();

    public GhiNhanSauBenh() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVuon_trong_id() { return vuon_trong_id; }
    public void setVuon_trong_id(int vuon_trong_id) { this.vuon_trong_id = vuon_trong_id; }

    public String getTen_sau_benh() { return ten_sau_benh; }
    public void setTen_sau_benh(String ten_sau_benh) { this.ten_sau_benh = ten_sau_benh; }

    public String getMuc_do_nghiem_trong() { return muc_do_nghiem_trong; }
    public void setMuc_do_nghiem_trong(String muc_do_nghiem_trong) { this.muc_do_nghiem_trong = muc_do_nghiem_trong; }

    public Date getNgay_phat_hien() { return ngay_phat_hien; }
    public void setNgay_phat_hien(Date ngay_phat_hien) { this.ngay_phat_hien = ngay_phat_hien; }

    public String getBien_phap_xu_ly() { return bien_phap_xu_ly; }
    public void setBien_phap_xu_ly(String bien_phap_xu_ly) { this.bien_phap_xu_ly = bien_phap_xu_ly; }

    public Integer getNhat_ky_cham_soc_id() { return nhat_ky_cham_soc_id; }
    public void setNhat_ky_cham_soc_id(Integer nhat_ky_cham_soc_id) { this.nhat_ky_cham_soc_id = nhat_ky_cham_soc_id; }

    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }

    public int getNguoi_ghi_nhan_id() { return nguoi_ghi_nhan_id; }
    public void setNguoi_ghi_nhan_id(int nguoi_ghi_nhan_id) { this.nguoi_ghi_nhan_id = nguoi_ghi_nhan_id; }

    public String getTen_lo_dat() { return ten_lo_dat; }
    public void setTen_lo_dat(String ten_lo_dat) { this.ten_lo_dat = ten_lo_dat; }

    public List<DongVatTu> getDongThuoc() { return dongThuoc; }
    public void setDongThuoc(List<DongVatTu> dongThuoc) { this.dongThuoc = dongThuoc; }
}
