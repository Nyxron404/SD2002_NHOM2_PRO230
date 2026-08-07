package models;

import java.util.Date;

public class DonViQuanLy {
    private int id;
    private int khu_vuc_id;
    private String loai_don_vi;
    private String ma_don_vi;
    private String ten_don_vi;
    private double dien_tich;
    // so_tang & suc_chua chỉ áp dụng cho đơn vị loại "Kệ" (khu kho). Với các loại khác giá trị này để null/0.
    private Integer so_tang;
    private double suc_chua;
    private String trang_thai;
    private Date ngay_tao;
    private Date ngay_cap_nhat;

    public DonViQuanLy() {
    }

    public DonViQuanLy(int id, int khu_vuc_id, String loai_don_vi, String ma_don_vi, String ten_don_vi, double dien_tich, Integer so_tang, double suc_chua, String trang_thai, Date ngay_tao, Date ngay_cap_nhat) {
        this.id = id;
        this.khu_vuc_id = khu_vuc_id;
        this.loai_don_vi = loai_don_vi;
        this.ma_don_vi = ma_don_vi;
        this.ten_don_vi = ten_don_vi;
        this.dien_tich = dien_tich;
        this.so_tang = so_tang;
        this.suc_chua = suc_chua;
        this.trang_thai = trang_thai;
        this.ngay_tao = ngay_tao;
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getKhu_vuc_id() { return khu_vuc_id; }
    public void setKhu_vuc_id(int khu_vuc_id) { this.khu_vuc_id = khu_vuc_id; }
    public String getLoai_don_vi() { return loai_don_vi; }
    public void setLoai_don_vi(String loai_don_vi) { this.loai_don_vi = loai_don_vi; }
    public String getMa_don_vi() { return ma_don_vi; }
    public void setMa_don_vi(String ma_don_vi) { this.ma_don_vi = ma_don_vi; }
    public String getTen_don_vi() { return ten_don_vi; }
    public void setTen_don_vi(String ten_don_vi) { this.ten_don_vi = ten_don_vi; }
    public double getDien_tich() { return dien_tich; }
    public void setDien_tich(double dien_tich) { this.dien_tich = dien_tich; }
    public Integer getSo_tang() { return so_tang; }
    public void setSo_tang(Integer so_tang) { this.so_tang = so_tang; }
    public double getSuc_chua() { return suc_chua; }
    public void setSuc_chua(double suc_chua) { this.suc_chua = suc_chua; }
    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }
    public Date getNgay_tao() { return ngay_tao; }
    public void setNgay_tao(Date ngay_tao) { this.ngay_tao = ngay_tao; }
    public Date getNgay_cap_nhat() { return ngay_cap_nhat; }
    public void setNgay_cap_nhat(Date ngay_cap_nhat) { this.ngay_cap_nhat = ngay_cap_nhat; }
}