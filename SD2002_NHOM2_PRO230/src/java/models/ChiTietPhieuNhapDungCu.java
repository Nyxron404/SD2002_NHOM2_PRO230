package models;

public class ChiTietPhieuNhapDungCu {
    private int id;
    private int phieu_nhap_id;
    private int dung_cu_id;
    private double so_luong;
    private double don_gia;
    private double thanh_tien;
    private boolean canh_bao_vuot_dien_tich;

    // Constructor không tham số
    public ChiTietPhieuNhapDungCu() {
    }

    // Constructor có đầy đủ tham số
    public ChiTietPhieuNhapDungCu(int id, int phieu_nhap_id, int dung_cu_id, double so_luong, double don_gia, double thanh_tien, boolean canh_bao_vuot_dien_tich) {
        this.id = id;
        this.phieu_nhap_id = phieu_nhap_id;
        this.dung_cu_id = dung_cu_id;
        this.so_luong = so_luong;
        this.don_gia = don_gia;
        this.thanh_tien = thanh_tien;
        this.canh_bao_vuot_dien_tich = canh_bao_vuot_dien_tich;
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPhieu_nhap_id() { return phieu_nhap_id; }
    public void setPhieu_nhap_id(int phieu_nhap_id) { this.phieu_nhap_id = phieu_nhap_id; }

    public int getDung_cu_id() { return dung_cu_id; }
    public void setDung_cu_id(int dung_cu_id) { this.dung_cu_id = dung_cu_id; }

    public double getSo_luong() { return so_luong; }
    public void setSo_luong(double so_luong) { this.so_luong = so_luong; }

    public double getDon_gia() { return don_gia; }
    public void setDon_gia(double don_gia) { this.don_gia = don_gia; }

    public double getThanh_tien() { return thanh_tien; }
    public void setThanh_tien(double thanh_tien) { this.thanh_tien = thanh_tien; }

    public boolean isCanh_bao_vuot_dien_tich() { return canh_bao_vuot_dien_tich; }
    public void setCanh_bao_vuot_dien_tich(boolean canh_bao_vuot_dien_tich) { this.canh_bao_vuot_dien_tich = canh_bao_vuot_dien_tich; }
}