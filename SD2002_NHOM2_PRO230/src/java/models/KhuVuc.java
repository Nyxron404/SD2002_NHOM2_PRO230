package models;

import java.util.Date;

public class KhuVuc {
    private int id;
    private int trang_trai_id;
    private String ten_khu_vuc;
    private String loai_khu_vuc;
    private double dien_tich_khai_thac;
    private double dien_tich_chiem_dung;
    private double dien_tich_khu_vuc;
    private String mo_ta;
    private Date ngay_tao;
    private Date ngay_cap_nhat;

    public KhuVuc() {
    }

    public KhuVuc(int id, int trang_trai_id, String ten_khu_vuc, String loai_khu_vuc, double dien_tich_khai_thac, double dien_tich_chiem_dung, double dien_tich_khu_vuc, String mo_ta, Date ngay_tao, Date ngay_cap_nhat) {
        this.id = id;
        this.trang_trai_id = trang_trai_id;
        this.ten_khu_vuc = ten_khu_vuc;
        this.loai_khu_vuc = loai_khu_vuc;
        this.dien_tich_khai_thac = dien_tich_khai_thac;
        this.dien_tich_chiem_dung = dien_tich_chiem_dung;
        this.dien_tich_khu_vuc = dien_tich_khu_vuc;
        this.mo_ta = mo_ta;
        this.ngay_tao = ngay_tao;
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTrang_trai_id() { return trang_trai_id; }
    public void setTrang_trai_id(int trang_trai_id) { this.trang_trai_id = trang_trai_id; }
    public String getTen_khu_vuc() { return ten_khu_vuc; }
    public void setTen_khu_vuc(String ten_khu_vuc) { this.ten_khu_vuc = ten_khu_vuc; }
    public String getLoai_khu_vuc() { return loai_khu_vuc; }
    public void setLoai_khu_vuc(String loai_khu_vuc) { this.loai_khu_vuc = loai_khu_vuc; }
    public double getDien_tich_khai_thac() { return dien_tich_khai_thac; }
    public void setDien_tich_khai_thac(double dien_tich_khai_thac) { this.dien_tich_khai_thac = dien_tich_khai_thac; }
    public double getDien_tich_chiem_dung() { return dien_tich_chiem_dung; }
    public void setDien_tich_chiem_dung(double dien_tich_chiem_dung) { this.dien_tich_chiem_dung = dien_tich_chiem_dung; }
    public double getDien_tich_khu_vuc() { return dien_tich_khu_vuc; }
    public void setDien_tich_khu_vuc(double dien_tich_khu_vuc) { this.dien_tich_khu_vuc = dien_tich_khu_vuc; }
    public String getMo_ta() { return mo_ta; }
    public void setMo_ta(String mo_ta) { this.mo_ta = mo_ta; }
    public Date getNgay_tao() { return ngay_tao; }
    public void setNgay_tao(Date ngay_tao) { this.ngay_tao = ngay_tao; }
    public Date getNgay_cap_nhat() { return ngay_cap_nhat; }
    public void setNgay_cap_nhat(Date ngay_cap_nhat) { this.ngay_cap_nhat = ngay_cap_nhat; }
}