package models;

/**
 * Thiết bị (UC-5) - dùng trong module canh tác để tính hao mòn (khấu hao).
 * Bảng: ThietBi (+ ChiTietPhieuNhapThietBi cung cấp nguyên giá)
 */
public class ThietBi {
    private int id;
    private String ma_thiet_bi;
    private String ten_thiet_bi;
    private int thoi_gian_khau_hao_nam;
    private double nguyen_gia;          // từ ChiTietPhieuNhapThietBi.don_gia
    private String trang_thai;

    public ThietBi() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMa_thiet_bi() { return ma_thiet_bi; }
    public void setMa_thiet_bi(String ma_thiet_bi) { this.ma_thiet_bi = ma_thiet_bi; }

    public String getTen_thiet_bi() { return ten_thiet_bi; }
    public void setTen_thiet_bi(String ten_thiet_bi) { this.ten_thiet_bi = ten_thiet_bi; }

    public int getThoi_gian_khau_hao_nam() { return thoi_gian_khau_hao_nam; }
    public void setThoi_gian_khau_hao_nam(int v) { this.thoi_gian_khau_hao_nam = v; }

    public double getNguyen_gia() { return nguyen_gia; }
    public void setNguyen_gia(double nguyen_gia) { this.nguyen_gia = nguyen_gia; }

    /** Khấu hao mỗi ngày = nguyên giá / (số năm * 365). */
    public double getKhauHaoNgay() {
        return thoi_gian_khau_hao_nam > 0 ? nguyen_gia / (thoi_gian_khau_hao_nam * 365.0) : 0;
    }

    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }
}
