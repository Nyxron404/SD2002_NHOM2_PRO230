package models;

public class ChiTietPhieuNhapKho {
    private int id;
    private int phieu_nhap_id;
    private int vat_tu_id;
    private double so_luong;
    private double don_gia;
    private double thanh_tien;
    private double dien_tich_tieu_ton;

    // CÁC TRƯỜNG ẢO ĐỂ HỨNG DỮ LIỆU TỪ JSP RỒI CHUYỂN VÀO LÔ HÀNG
    private String ngay_san_xuat;
    private String han_su_dung;
    private int vi_tri_luu_tru_id;

    public ChiTietPhieuNhapKho() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPhieu_nhap_id() { return phieu_nhap_id; }
    public void setPhieu_nhap_id(int phieu_nhap_id) { this.phieu_nhap_id = phieu_nhap_id; }
    public int getVat_tu_id() { return vat_tu_id; }
    public void setVat_tu_id(int vat_tu_id) { this.vat_tu_id = vat_tu_id; }
    public double getSo_luong() { return so_luong; }
    public void setSo_luong(double so_luong) { this.so_luong = so_luong; }
    public double getDon_gia() { return don_gia; }
    public void setDon_gia(double don_gia) { this.don_gia = don_gia; }
    public double getThanh_tien() { return thanh_tien; }
    public void setThanh_tien(double thanh_tien) { this.thanh_tien = thanh_tien; }
    public double getDien_tich_tieu_ton() { return dien_tich_tieu_ton; }
    public void setDien_tich_tieu_ton(double dien_tich_tieu_ton) { this.dien_tich_tieu_ton = dien_tich_tieu_ton; }
    public String getNgay_san_xuat() { return ngay_san_xuat; }
    public void setNgay_san_xuat(String ngay_san_xuat) { this.ngay_san_xuat = ngay_san_xuat; }
    public String getHan_su_dung() { return han_su_dung; }
    public void setHan_su_dung(String han_su_dung) { this.han_su_dung = han_su_dung; }
    public int getVi_tri_luu_tru_id() { return vi_tri_luu_tru_id; }
    public void setVi_tri_luu_tru_id(int vi_tri_luu_tru_id) { this.vi_tri_luu_tru_id = vi_tri_luu_tru_id; }
}
