package models;

import java.util.Date;

public class DungCu {
    private int id;
    private String ma_dung_cu;
    private String ten_dung_cu;
    private String don_vi_tinh;
    private double dien_tich_chiem_dung;
    private int vi_tri_luu_tru_id;
    private double gia_binh_quan;
    private double ton_kho_hien_tai;
    private double ton_kho_toi_thieu;
    private String trang_thai;
    private Date ngay_tao;
    private Date ngay_cap_nhat;

    // Constructor không tham số
    public DungCu() {
    }

    // Constructor có đầy đủ tham số
    public DungCu(int id, String ma_dung_cu, String ten_dung_cu, String don_vi_tinh, double dien_tich_chiem_dung, int vi_tri_luu_tru_id, double gia_binh_quan, double ton_kho_hien_tai, double ton_kho_toi_thieu, String trang_thai, Date ngay_tao, Date ngay_cap_nhat) {
        this.id = id;
        this.ma_dung_cu = ma_dung_cu;
        this.ten_dung_cu = ten_dung_cu;
        this.don_vi_tinh = don_vi_tinh;
        this.dien_tich_chiem_dung = dien_tich_chiem_dung;
        this.vi_tri_luu_tru_id = vi_tri_luu_tru_id;
        this.gia_binh_quan = gia_binh_quan;
        this.ton_kho_hien_tai = ton_kho_hien_tai;
        this.ton_kho_toi_thieu = ton_kho_toi_thieu;
        this.trang_thai = trang_thai;
        this.ngay_tao = ngay_tao;
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMa_dung_cu() { return ma_dung_cu; }
    public void setMa_dung_cu(String ma_dung_cu) { this.ma_dung_cu = ma_dung_cu; }

    public String getTen_dung_cu() { return ten_dung_cu; }
    public void setTen_dung_cu(String ten_dung_cu) { this.ten_dung_cu = ten_dung_cu; }

    public String getDon_vi_tinh() { return don_vi_tinh; }
    public void setDon_vi_tinh(String don_vi_tinh) { this.don_vi_tinh = don_vi_tinh; }

    public double getDien_tich_chiem_dung() { return dien_tich_chiem_dung; }
    public void setDien_tich_chiem_dung(double dien_tich_chiem_dung) { this.dien_tich_chiem_dung = dien_tich_chiem_dung; }

    public int getVi_tri_luu_tru_id() { return vi_tri_luu_tru_id; }
    public void setVi_tri_luu_tru_id(int vi_tri_luu_tru_id) { this.vi_tri_luu_tru_id = vi_tri_luu_tru_id; }

    public double getGia_binh_quan() { return gia_binh_quan; }
    public void setGia_binh_quan(double gia_binh_quan) { this.gia_binh_quan = gia_binh_quan; }

    public double getTon_kho_hien_tai() { return ton_kho_hien_tai; }
    public void setTon_kho_hien_tai(double ton_kho_hien_tai) { this.ton_kho_hien_tai = ton_kho_hien_tai; }

    public double getTon_kho_toi_thieu() { return ton_kho_toi_thieu; }
    public void setTon_kho_toi_thieu(double ton_kho_toi_thieu) { this.ton_kho_toi_thieu = ton_kho_toi_thieu; }

    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }

    public Date getNgay_tao() { return ngay_tao; }
    public void setNgay_tao(Date ngay_tao) { this.ngay_tao = ngay_tao; }

    public Date getNgay_cap_nhat() { return ngay_cap_nhat; }
    public void setNgay_cap_nhat(Date ngay_cap_nhat) { this.ngay_cap_nhat = ngay_cap_nhat; }
}