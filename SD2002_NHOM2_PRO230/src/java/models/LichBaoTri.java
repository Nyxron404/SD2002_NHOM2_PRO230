package models;

import java.util.Date;

public class LichBaoTri {
    private int id;
    private int ma_thiet_bi;
    private Date ngay_lap_lich;
    private Date ngay_bao_tri_du_kien;
    private Date ngay_bao_tri_thuc_te;
    private String loai_bao_tri;
    private String noi_dung_bao_tri;
    private int trang_thai; // 0: Chờ, 1: Đang làm, 2: Xong, 3: Hủy
    private String ghi_chu;

    public LichBaoTri() {
    }

    public LichBaoTri(int id, int ma_thiet_bi, Date ngay_lap_lich, Date ngay_bao_tri_du_kien, Date ngay_bao_tri_thuc_te, String loai_bao_tri, String noi_dung_bao_tri, int trang_thai, String ghi_chu) {
        this.id = id;
        this.ma_thiet_bi = ma_thiet_bi;
        this.ngay_lap_lich = ngay_lap_lich;
        this.ngay_bao_tri_du_kien = ngay_bao_tri_du_kien;
        this.ngay_bao_tri_thuc_te = ngay_bao_tri_thuc_te;
        this.loai_bao_tri = loai_bao_tri;
        this.noi_dung_bao_tri = noi_dung_bao_tri;
        this.trang_thai = trang_thai;
        this.ghi_chu = ghi_chu;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMa_thiet_bi() { return ma_thiet_bi; }
    public void setMa_thiet_bi(int ma_thiet_bi) { this.ma_thiet_bi = ma_thiet_bi; }
    public Date getNgay_lap_lich() { return ngay_lap_lich; }
    public void setNgay_lap_lich(Date ngay_lap_lich) { this.ngay_lap_lich = ngay_lap_lich; }
    public Date getNgay_bao_tri_du_kien() { return ngay_bao_tri_du_kien; }
    public void setNgay_bao_tri_du_kien(Date ngay_bao_tri_du_kien) { this.ngay_bao_tri_du_kien = ngay_bao_tri_du_kien; }
    public Date getNgay_bao_tri_thuc_te() { return ngay_bao_tri_thuc_te; }
    public void setNgay_bao_tri_thuc_te(Date ngay_bao_tri_thuc_te) { this.ngay_bao_tri_thuc_te = ngay_bao_tri_thuc_te; }
    public String getLoai_bao_tri() { return loai_bao_tri; }
    public void setLoai_bao_tri(String loai_bao_tri) { this.loai_bao_tri = loai_bao_tri; }
    public String getNoi_dung_bao_tri() { return noi_dung_bao_tri; }
    public void setNoi_dung_bao_tri(String noi_dung_bao_tri) { this.noi_dung_bao_tri = noi_dung_bao_tri; }
    public int getTrang_thai() { return trang_thai; }
    public void setTrang_thai(int trang_thai) { this.trang_thai = trang_thai; }
    public String getGhi_chu() { return ghi_chu; }
    public void setGhi_chu(String ghi_chu) { this.ghi_chu = ghi_chu; }
}