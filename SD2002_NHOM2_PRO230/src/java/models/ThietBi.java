package models;

import java.util.Date;

public class ThietBi {
    // --- CÁC TRƯỜNG DỮ LIỆU CỦA BẠN ---
    private int id;
    private String ma_thiet_bi;
    private String ten_thiet_bi;
    private double dien_tich_cat_tru;
    private int thoi_gian_khau_hao_nam;
    private String mo_ta;
    private int vi_tri_luu_tru_id;
    private String trang_thai;
    private Date ngay_tao;
    private Date ngay_cap_nhat;
    private Date ngay_bao_tri_du_kien;

    // --- TRƯỜNG DỮ LIỆU CỦA NHÓM TRƯỞNG THÊM VÀO ---
    private double nguyen_gia;

    // Constructor 0 tham số
    public ThietBi() {
    }

    // Constructor đầy đủ tham số của bạn
    public ThietBi(int id, String ma_thiet_bi, String ten_thiet_bi, double dien_tich_cat_tru, 
                   int thoi_gian_khau_hao_nam, String mo_ta, int vi_tri_luu_tru_id, 
                   String trang_thai, Date ngay_tao, Date ngay_cap_nhat, Date ngay_bao_tri_du_kien) {
        this.id = id;
        this.ma_thiet_bi = ma_thiet_bi;
        this.ten_thiet_bi = ten_thiet_bi;
        this.dien_tich_cat_tru = dien_tich_cat_tru;
        this.thoi_gian_khau_hao_nam = thoi_gian_khau_hao_nam;
        this.mo_ta = mo_ta;
        this.vi_tri_luu_tru_id = vi_tri_luu_tru_id;
        this.trang_thai = trang_thai;
        this.ngay_tao = ngay_tao;
        this.ngay_cap_nhat = ngay_cap_nhat;
        this.ngay_bao_tri_du_kien = ngay_bao_tri_du_kien;
    }

    // --- GETTERS & SETTERS CỦA BẠN ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMa_thiet_bi() { return ma_thiet_bi; }
    public void setMa_thiet_bi(String ma_thiet_bi) { this.ma_thiet_bi = ma_thiet_bi; }

    public String getTen_thiet_bi() { return ten_thiet_bi; }
    public void setTen_thiet_bi(String ten_thiet_bi) { this.ten_thiet_bi = ten_thiet_bi; }

    public double getDien_tich_cat_tru() { return dien_tich_cat_tru; }
    public void setDien_tich_cat_tru(double dien_tich_cat_tru) { this.dien_tich_cat_tru = dien_tich_cat_tru; }

    public int getThoi_gian_khau_hao_nam() { return thoi_gian_khau_hao_nam; }
    public void setThoi_gian_khau_hao_nam(int thoi_gian_khau_hao_nam) { this.thoi_gian_khau_hao_nam = thoi_gian_khau_hao_nam; }

    public String getMo_ta() { return mo_ta; }
    public void setMo_ta(String mo_ta) { this.mo_ta = mo_ta; }

    public int getVi_tri_luu_tru_id() { return vi_tri_luu_tru_id; }
    public void setVi_tri_luu_tru_id(int vi_tri_luu_tru_id) { this.vi_tri_luu_tru_id = vi_tri_luu_tru_id; }

    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }

    public Date getNgay_tao() { return ngay_tao; }
    public void setNgay_tao(Date ngay_tao) { this.ngay_tao = ngay_tao; }

    public Date getNgay_cap_nhat() { return ngay_cap_nhat; }
    public void setNgay_cap_nhat(Date ngay_cap_nhat) { this.ngay_cap_nhat = ngay_cap_nhat; }
    
    public Date getNgay_bao_tri_du_kien() { return ngay_bao_tri_du_kien; }
    public void setNgay_bao_tri_du_kien(Date ngay_bao_tri_du_kien) { this.ngay_bao_tri_du_kien = ngay_bao_tri_du_kien; }

    // --- GETTERS & SETTERS CỦA NHÓM TRƯỞNG ---
    public double getNguyen_gia() { return nguyen_gia; }
    public void setNguyen_gia(double nguyen_gia) { this.nguyen_gia = nguyen_gia; }

    public double getKhauHaoNgay() {
        return thoi_gian_khau_hao_nam > 0 ? nguyen_gia / (thoi_gian_khau_hao_nam * 365.0) : 0;
    }
}