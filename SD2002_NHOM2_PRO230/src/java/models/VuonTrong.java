package models;

import java.util.Date;

/**
 * UC-4.2 - Vườn trồng: gán giống cho lô đất, tính mật độ và phân loại.
 * Bảng: VuonTrong
 */
public class VuonTrong {
    private int id;
    private int lo_dat_id;                 // -> DonViQuanLy (loại "Lô đất")
    private int giong_id;                  // -> GiongSauRieng
    private double dien_tich;              // ha (diện tích khai thác của dòng giống)
    private int so_luong_cay;
    private double mat_do_trong;           // cây/ha = so_luong_cay / dien_tich
    private int mat_do_tham_chieu_id;      // -> MatDoThamChieu
    private boolean mat_do_bat_thuong;
    private Date ngay_trong;
    private String trang_thai_sinh_truong; // Cây con / Sinh trưởng / Ra hoa / ...
    private boolean co_sau_benh;
    private String ghi_chu;

    // Thông tin kết hợp phục vụ hiển thị
    private String ten_lo_dat;
    private String ten_giong;
    private String phan_loai_mat_do;
    private String dac_diem_rui_ro;

    public VuonTrong() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLo_dat_id() { return lo_dat_id; }
    public void setLo_dat_id(int lo_dat_id) { this.lo_dat_id = lo_dat_id; }

    public int getGiong_id() { return giong_id; }
    public void setGiong_id(int giong_id) { this.giong_id = giong_id; }

    public double getDien_tich() { return dien_tich; }
    public void setDien_tich(double dien_tich) { this.dien_tich = dien_tich; }

    public int getSo_luong_cay() { return so_luong_cay; }
    public void setSo_luong_cay(int so_luong_cay) { this.so_luong_cay = so_luong_cay; }

    public double getMat_do_trong() { return mat_do_trong; }
    public void setMat_do_trong(double mat_do_trong) { this.mat_do_trong = mat_do_trong; }

    public int getMat_do_tham_chieu_id() { return mat_do_tham_chieu_id; }
    public void setMat_do_tham_chieu_id(int mat_do_tham_chieu_id) { this.mat_do_tham_chieu_id = mat_do_tham_chieu_id; }

    public boolean isMat_do_bat_thuong() { return mat_do_bat_thuong; }
    public void setMat_do_bat_thuong(boolean mat_do_bat_thuong) { this.mat_do_bat_thuong = mat_do_bat_thuong; }

    public Date getNgay_trong() { return ngay_trong; }
    public void setNgay_trong(Date ngay_trong) { this.ngay_trong = ngay_trong; }

    public String getTrang_thai_sinh_truong() { return trang_thai_sinh_truong; }
    public void setTrang_thai_sinh_truong(String v) { this.trang_thai_sinh_truong = v; }

    public boolean isCo_sau_benh() { return co_sau_benh; }
    public void setCo_sau_benh(boolean co_sau_benh) { this.co_sau_benh = co_sau_benh; }

    public String getGhi_chu() { return ghi_chu; }
    public void setGhi_chu(String ghi_chu) { this.ghi_chu = ghi_chu; }

    public String getTen_lo_dat() { return ten_lo_dat; }
    public void setTen_lo_dat(String ten_lo_dat) { this.ten_lo_dat = ten_lo_dat; }

    public String getTen_giong() { return ten_giong; }
    public void setTen_giong(String ten_giong) { this.ten_giong = ten_giong; }

    public String getPhan_loai_mat_do() { return phan_loai_mat_do; }
    public void setPhan_loai_mat_do(String phan_loai_mat_do) { this.phan_loai_mat_do = phan_loai_mat_do; }

    public String getDac_diem_rui_ro() { return dac_diem_rui_ro; }
    public void setDac_diem_rui_ro(String dac_diem_rui_ro) { this.dac_diem_rui_ro = dac_diem_rui_ro; }
}
