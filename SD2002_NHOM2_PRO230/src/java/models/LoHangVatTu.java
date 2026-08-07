package models;

import java.util.Date;

public class LoHangVatTu {
    private int id;
    private int chi_tiet_phieu_nhap_id;
    private String so_lo;
    private Date ngay_san_xuat;
    private Date han_su_dung;
    private double so_luong_nhap;
    private double so_luong_con_lai;
    private int vi_tri_luu_tru_id;
    private String trang_thai;
    private Date ngay_tao;

    public LoHangVatTu() {
    }

    public LoHangVatTu(int id, int chi_tiet_phieu_nhap_id, String so_lo, Date ngay_san_xuat, Date han_su_dung, double so_luong_nhap, double so_luong_con_lai, int vi_tri_luu_tru_id, String trang_thai, Date ngay_tao) {
        this.id = id;
        this.chi_tiet_phieu_nhap_id = chi_tiet_phieu_nhap_id;
        this.so_lo = so_lo;
        this.ngay_san_xuat = ngay_san_xuat;
        this.han_su_dung = han_su_dung;
        this.so_luong_nhap = so_luong_nhap;
        this.so_luong_con_lai = so_luong_con_lai;
        this.vi_tri_luu_tru_id = vi_tri_luu_tru_id;
        this.trang_thai = trang_thai;
        this.ngay_tao = ngay_tao;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getChi_tiet_phieu_nhap_id() { return chi_tiet_phieu_nhap_id; }
    public void setChi_tiet_phieu_nhap_id(int chi_tiet_phieu_nhap_id) { this.chi_tiet_phieu_nhap_id = chi_tiet_phieu_nhap_id; }
    public String getSo_lo() { return so_lo; }
    public void setSo_lo(String so_lo) { this.so_lo = so_lo; }
    public Date getNgay_san_xuat() { return ngay_san_xuat; }
    public void setNgay_san_xuat(Date ngay_san_xuat) { this.ngay_san_xuat = ngay_san_xuat; }
    public Date getHan_su_dung() { return han_su_dung; }
    public void setHan_su_dung(Date han_su_dung) { this.han_su_dung = han_su_dung; }
    public double getSo_luong_nhap() { return so_luong_nhap; }
    public void setSo_luong_nhap(double so_luong_nhap) { this.so_luong_nhap = so_luong_nhap; }
    public double getSo_luong_con_lai() { return so_luong_con_lai; }
    public void setSo_luong_con_lai(double so_luong_con_lai) { this.so_luong_con_lai = so_luong_con_lai; }
    public int getVi_tri_luu_tru_id() { return vi_tri_luu_tru_id; }
    public void setVi_tri_luu_tru_id(int vi_tri_luu_tru_id) { this.vi_tri_luu_tru_id = vi_tri_luu_tru_id; }
    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }
    public Date getNgay_tao() { return ngay_tao; }
    public void setNgay_tao(Date ngay_tao) { this.ngay_tao = ngay_tao; }
}