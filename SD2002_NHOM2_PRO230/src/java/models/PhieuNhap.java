package models;

import java.util.Date;

public class PhieuNhap {
    private int id;
    private String ma_phieu_nhap;
    private String loai_phieu_nhap; // ĐÃ THÊM TRƯỜNG NÀY
    private int nha_cung_cap_id;
    private String so_hoa_don;
    private String mau_so;
    private String ky_hieu;
    private Date ngay_hoa_don;
    private String ma_so_thue_ncc;
    private double tong_tien_hang;
    private double tien_thue_gtgt;
    private double tong_thanh_toan;
    private String nguoi_mua_hang;
    private String nguoi_ban_hang;
    private String url_anh_hoa_don;
    private int nguoi_lap_phieu_id;
    private double tong_dien_tich_tieu_ton;
    private String trang_thai;
    private String ghi_chu;
    private Date ngay_tao;

    public PhieuNhap() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMa_phieu_nhap() { return ma_phieu_nhap; }
    public void setMa_phieu_nhap(String ma_phieu_nhap) { this.ma_phieu_nhap = ma_phieu_nhap; }
    public String getLoai_phieu_nhap() { return loai_phieu_nhap; }
    public void setLoai_phieu_nhap(String loai_phieu_nhap) { this.loai_phieu_nhap = loai_phieu_nhap; }
    public int getNha_cung_cap_id() { return nha_cung_cap_id; }
    public void setNha_cung_cap_id(int nha_cung_cap_id) { this.nha_cung_cap_id = nha_cung_cap_id; }
    public String getSo_hoa_don() { return so_hoa_don; }
    public void setSo_hoa_don(String so_hoa_don) { this.so_hoa_don = so_hoa_don; }
    public String getMau_so() { return mau_so; }
    public void setMau_so(String mau_so) { this.mau_so = mau_so; }
    public String getKy_hieu() { return ky_hieu; }
    public void setKy_hieu(String ky_hieu) { this.ky_hieu = ky_hieu; }
    public Date getNgay_hoa_don() { return ngay_hoa_don; }
    public void setNgay_hoa_don(Date ngay_hoa_don) { this.ngay_hoa_don = ngay_hoa_don; }
    public String getMa_so_thue_ncc() { return ma_so_thue_ncc; }
    public void setMa_so_thue_ncc(String ma_so_thue_ncc) { this.ma_so_thue_ncc = ma_so_thue_ncc; }
    public double getTong_tien_hang() { return tong_tien_hang; }
    public void setTong_tien_hang(double tong_tien_hang) { this.tong_tien_hang = tong_tien_hang; }
    public double getTien_thue_gtgt() { return tien_thue_gtgt; }
    public void setTien_thue_gtgt(double tien_thue_gtgt) { this.tien_thue_gtgt = tien_thue_gtgt; }
    public double getTong_thanh_toan() { return tong_thanh_toan; }
    public void setTong_thanh_toan(double tong_thanh_toan) { this.tong_thanh_toan = tong_thanh_toan; }
    public String getNguoi_mua_hang() { return nguoi_mua_hang; }
    public void setNguoi_mua_hang(String nguoi_mua_hang) { this.nguoi_mua_hang = nguoi_mua_hang; }
    public String getNguoi_ban_hang() { return nguoi_ban_hang; }
    public void setNguoi_ban_hang(String nguoi_ban_hang) { this.nguoi_ban_hang = nguoi_ban_hang; }
    public String getUrl_anh_hoa_don() { return url_anh_hoa_don; }
    public void setUrl_anh_hoa_don(String url_anh_hoa_don) { this.url_anh_hoa_don = url_anh_hoa_don; }
    public int getNguoi_lap_phieu_id() { return nguoi_lap_phieu_id; }
    public void setNguoi_lap_phieu_id(int nguoi_lap_phieu_id) { this.nguoi_lap_phieu_id = nguoi_lap_phieu_id; }
    public double getTong_dien_tich_tieu_ton() { return tong_dien_tich_tieu_ton; }
    public void setTong_dien_tich_tieu_ton(double tong_dien_tich_tieu_ton) { this.tong_dien_tich_tieu_ton = tong_dien_tich_tieu_ton; }
    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }
    public String getGhi_chu() { return ghi_chu; }
    public void setGhi_chu(String ghi_chu) { this.ghi_chu = ghi_chu; }
    public Date getNgay_tao() { return ngay_tao; }
    public void setNgay_tao(Date ngay_tao) { this.ngay_tao = ngay_tao; }
}