package models;

/**
 * UC-4.1 - Giống cây sầu riêng (danh mục dùng chung).
 * Bảng: GiongSauRieng
 */
public class GiongSauRieng {
    private int id;
    private String ten_giong;
    private String dac_diem;
    private int thoi_gian_sinh_truong_thu_hoach; // số tháng đến thu hoạch
    private double nang_suat_tham_khao;          // kg/cây
    private String trang_thai;

    // Số lô đất đang sử dụng giống này (chỉ dùng để hiển thị / kiểm tra xóa)
    private int so_lo_dang_dung;

    public GiongSauRieng() {}

    public GiongSauRieng(int id, String ten_giong, String dac_diem,
                         int thoi_gian_sinh_truong_thu_hoach, double nang_suat_tham_khao,
                         String trang_thai) {
        this.id = id;
        this.ten_giong = ten_giong;
        this.dac_diem = dac_diem;
        this.thoi_gian_sinh_truong_thu_hoach = thoi_gian_sinh_truong_thu_hoach;
        this.nang_suat_tham_khao = nang_suat_tham_khao;
        this.trang_thai = trang_thai;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTen_giong() { return ten_giong; }
    public void setTen_giong(String ten_giong) { this.ten_giong = ten_giong; }

    public String getDac_diem() { return dac_diem; }
    public void setDac_diem(String dac_diem) { this.dac_diem = dac_diem; }

    public int getThoi_gian_sinh_truong_thu_hoach() { return thoi_gian_sinh_truong_thu_hoach; }
    public void setThoi_gian_sinh_truong_thu_hoach(int v) { this.thoi_gian_sinh_truong_thu_hoach = v; }

    public double getNang_suat_tham_khao() { return nang_suat_tham_khao; }
    public void setNang_suat_tham_khao(double nang_suat_tham_khao) { this.nang_suat_tham_khao = nang_suat_tham_khao; }

    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }

    public int getSo_lo_dang_dung() { return so_lo_dang_dung; }
    public void setSo_lo_dang_dung(int so_lo_dang_dung) { this.so_lo_dang_dung = so_lo_dang_dung; }
}
