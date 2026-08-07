package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * UC-4.4 - Nhật ký hoạt động chăm sóc (UC trung tâm module canh tác).
 * Bảng: NhatKyChamSoc (+ ChiTietVatTuTieuHao, ChiTietDungCuTieuHao)
 * Chi phí = vật tư (xuất kho FIFO) + dụng cụ + hao mòn thiết bị.
 */
public class NhatKyChamSoc {
    private int id;
    private int lo_dat_id;              // -> DonViQuanLy
    private String loai_cong_viec;
    private Date ngay_thuc_hien;
    private Integer nhac_viec_id;
    private boolean co_su_dung_vat_tu;
    private boolean co_su_dung_dung_cu;
    private boolean co_su_dung_thiet_bi;
    private double tong_chi_phi_vat_tu;
    private double tong_chi_phi_dung_cu;
    private double tong_chi_phi_thiet_bi;
    private double tong_chi_phi;
    private int nguoi_ghi_nhan_id;
    private String mo_ta;

    private String ten_lo_dat; // hiển thị

    // Chi tiết dòng vật tư / dụng cụ / thiết bị khi ghi nhận
    private List<DongVatTu> dongVatTu = new ArrayList<>();
    private List<DongDungCu> dongDungCu = new ArrayList<>();
    private List<DongThietBi> dongThietBi = new ArrayList<>();

    public NhatKyChamSoc() {}

    /** Một dòng vật tư sử dụng (sẽ xuất kho theo FIFO). */
    public static class DongVatTu {
        public int vatTuId;
        public double soLuong;
        public DongVatTu() {}
        public DongVatTu(int vatTuId, double soLuong) { this.vatTuId = vatTuId; this.soLuong = soLuong; }
    }

    /** Một dòng dụng cụ tiêu hao. */
    public static class DongDungCu {
        public int dungCuId;
        public double soLuong;
        public DongDungCu() {}
        public DongDungCu(int dungCuId, double soLuong) { this.dungCuId = dungCuId; this.soLuong = soLuong; }
    }

    /** Một dòng thiết bị sử dụng (tính khấu hao theo số ngày). */
    public static class DongThietBi {
        public int thietBiId;
        public int soNgaySuDung;
        public DongThietBi() {}
        public DongThietBi(int thietBiId, int soNgaySuDung) { this.thietBiId = thietBiId; this.soNgaySuDung = soNgaySuDung; }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLo_dat_id() { return lo_dat_id; }
    public void setLo_dat_id(int lo_dat_id) { this.lo_dat_id = lo_dat_id; }

    public String getLoai_cong_viec() { return loai_cong_viec; }
    public void setLoai_cong_viec(String loai_cong_viec) { this.loai_cong_viec = loai_cong_viec; }

    public Date getNgay_thuc_hien() { return ngay_thuc_hien; }
    public void setNgay_thuc_hien(Date ngay_thuc_hien) { this.ngay_thuc_hien = ngay_thuc_hien; }

    public Integer getNhac_viec_id() { return nhac_viec_id; }
    public void setNhac_viec_id(Integer nhac_viec_id) { this.nhac_viec_id = nhac_viec_id; }

    public boolean isCo_su_dung_vat_tu() { return co_su_dung_vat_tu; }
    public void setCo_su_dung_vat_tu(boolean v) { this.co_su_dung_vat_tu = v; }

    public boolean isCo_su_dung_dung_cu() { return co_su_dung_dung_cu; }
    public void setCo_su_dung_dung_cu(boolean v) { this.co_su_dung_dung_cu = v; }

    public boolean isCo_su_dung_thiet_bi() { return co_su_dung_thiet_bi; }
    public void setCo_su_dung_thiet_bi(boolean v) { this.co_su_dung_thiet_bi = v; }

    public double getTong_chi_phi_vat_tu() { return tong_chi_phi_vat_tu; }
    public void setTong_chi_phi_vat_tu(double v) { this.tong_chi_phi_vat_tu = v; }

    public double getTong_chi_phi_dung_cu() { return tong_chi_phi_dung_cu; }
    public void setTong_chi_phi_dung_cu(double v) { this.tong_chi_phi_dung_cu = v; }

    public double getTong_chi_phi_thiet_bi() { return tong_chi_phi_thiet_bi; }
    public void setTong_chi_phi_thiet_bi(double v) { this.tong_chi_phi_thiet_bi = v; }

    public double getTong_chi_phi() { return tong_chi_phi; }
    public void setTong_chi_phi(double tong_chi_phi) { this.tong_chi_phi = tong_chi_phi; }

    public int getNguoi_ghi_nhan_id() { return nguoi_ghi_nhan_id; }
    public void setNguoi_ghi_nhan_id(int nguoi_ghi_nhan_id) { this.nguoi_ghi_nhan_id = nguoi_ghi_nhan_id; }

    public String getMo_ta() { return mo_ta; }
    public void setMo_ta(String mo_ta) { this.mo_ta = mo_ta; }

    public String getTen_lo_dat() { return ten_lo_dat; }
    public void setTen_lo_dat(String ten_lo_dat) { this.ten_lo_dat = ten_lo_dat; }

    public List<DongVatTu> getDongVatTu() { return dongVatTu; }
    public void setDongVatTu(List<DongVatTu> dongVatTu) { this.dongVatTu = dongVatTu; }

    public List<DongDungCu> getDongDungCu() { return dongDungCu; }
    public void setDongDungCu(List<DongDungCu> dongDungCu) { this.dongDungCu = dongDungCu; }

    public List<DongThietBi> getDongThietBi() { return dongThietBi; }
    public void setDongThietBi(List<DongThietBi> dongThietBi) { this.dongThietBi = dongThietBi; }
}
