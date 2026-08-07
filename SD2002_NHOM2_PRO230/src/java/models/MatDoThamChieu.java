package models;

/**
 * Bảng tham chiếu kỹ thuật phân loại mật độ trồng (cây/ha).
 * Bảng: MatDoThamChieu - phục vụ UC-4.2.
 */
public class MatDoThamChieu {
    private int id;
    private String phan_loai;      // Thưa / Vừa / Dày / Rất dày
    private double mat_do_tu;      // ngưỡng dưới (>=)
    private Double mat_do_den;     // ngưỡng trên (<), null = không giới hạn
    private String dac_diem_rui_ro;

    public MatDoThamChieu() {}

    public MatDoThamChieu(int id, String phan_loai, double mat_do_tu, Double mat_do_den, String dac_diem_rui_ro) {
        this.id = id;
        this.phan_loai = phan_loai;
        this.mat_do_tu = mat_do_tu;
        this.mat_do_den = mat_do_den;
        this.dac_diem_rui_ro = dac_diem_rui_ro;
    }

    /** Kiểm tra một mật độ (cây/ha) có rơi vào ngưỡng của dòng này không. */
    public boolean chuaMatDo(double matDo) {
        if (matDo < mat_do_tu) return false;
        return mat_do_den == null || matDo < mat_do_den;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPhan_loai() { return phan_loai; }
    public void setPhan_loai(String phan_loai) { this.phan_loai = phan_loai; }

    public double getMat_do_tu() { return mat_do_tu; }
    public void setMat_do_tu(double mat_do_tu) { this.mat_do_tu = mat_do_tu; }

    public Double getMat_do_den() { return mat_do_den; }
    public void setMat_do_den(Double mat_do_den) { this.mat_do_den = mat_do_den; }

    public String getDac_diem_rui_ro() { return dac_diem_rui_ro; }
    public void setDac_diem_rui_ro(String dac_diem_rui_ro) { this.dac_diem_rui_ro = dac_diem_rui_ro; }
}
