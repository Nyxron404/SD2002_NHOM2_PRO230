package models;

/**
 * Dòng tổng hợp theo dõi chi phí & tiêu hao của một lô đất (UC-4.4 / UC-4.7).
 * Không phải bảng trong CSDL - đây là kết quả tổng hợp (aggregate) từ:
 *   NhatKyChamSoc + ChiTietVatTuTieuHao + ChiTietDungCuTieuHao + PhieuDieuDongThietBi
 *   + GhiNhanThuHoach.
 */
public class ChiPhiLoDat {

    private int lo_dat_id;
    private String ten_lo_dat;
    private String ten_giong;
    private double dien_tich_lo;          // m2, lấy từ DonViQuanLy.dien_tich

    private int so_lan_cham_soc;
    private double chi_phi_vat_tu;
    private double chi_phi_dung_cu;
    private double chi_phi_thiet_bi;
    private double tong_chi_phi;

    private double dien_tich_vat_tu_giai_phong; // m2 diện tích kho được giải phóng do dùng vật tư
    private double dien_tich_dung_cu_giai_phong;// m2 diện tích kho được giải phóng do dùng dụng cụ
    private int so_ngay_thiet_bi;               // tổng số ngày thiết bị phục vụ lô
    private int so_luot_thiet_bi;               // số lượt điều động thiết bị

    private double san_luong_kg;                // tổng sản lượng đã thu
    private double dien_tich_luu_kho;           // m2 diện tích kho bị chiếm bởi nông sản

    public ChiPhiLoDat() {}

    /** Chi phí trên mỗi m2 diện tích lô (0 nếu chưa có diện tích). */
    public double getChi_phi_tren_m2() {
        return dien_tich_lo > 0 ? Math.round(tong_chi_phi / dien_tich_lo * 100.0) / 100.0 : 0;
    }

    /** Giá thành trên mỗi kg nông sản đã thu hoạch (0 nếu chưa thu hoạch). */
    public double getGia_thanh_tren_kg() {
        return san_luong_kg > 0 ? Math.round(tong_chi_phi / san_luong_kg * 100.0) / 100.0 : 0;
    }

    /** Tổng diện tích kho được giải phóng nhờ tiêu hao vật tư + dụng cụ. */
    public double getTong_dien_tich_giai_phong() {
        return Math.round((dien_tich_vat_tu_giai_phong + dien_tich_dung_cu_giai_phong) * 100.0) / 100.0;
    }

    public int getLo_dat_id() { return lo_dat_id; }
    public void setLo_dat_id(int v) { this.lo_dat_id = v; }

    public String getTen_lo_dat() { return ten_lo_dat; }
    public void setTen_lo_dat(String v) { this.ten_lo_dat = v; }

    public String getTen_giong() { return ten_giong; }
    public void setTen_giong(String v) { this.ten_giong = v; }

    public double getDien_tich_lo() { return dien_tich_lo; }
    public void setDien_tich_lo(double v) { this.dien_tich_lo = v; }

    public int getSo_lan_cham_soc() { return so_lan_cham_soc; }
    public void setSo_lan_cham_soc(int v) { this.so_lan_cham_soc = v; }

    public double getChi_phi_vat_tu() { return chi_phi_vat_tu; }
    public void setChi_phi_vat_tu(double v) { this.chi_phi_vat_tu = v; }

    public double getChi_phi_dung_cu() { return chi_phi_dung_cu; }
    public void setChi_phi_dung_cu(double v) { this.chi_phi_dung_cu = v; }

    public double getChi_phi_thiet_bi() { return chi_phi_thiet_bi; }
    public void setChi_phi_thiet_bi(double v) { this.chi_phi_thiet_bi = v; }

    public double getTong_chi_phi() { return tong_chi_phi; }
    public void setTong_chi_phi(double v) { this.tong_chi_phi = v; }

    public double getDien_tich_vat_tu_giai_phong() { return dien_tich_vat_tu_giai_phong; }
    public void setDien_tich_vat_tu_giai_phong(double v) { this.dien_tich_vat_tu_giai_phong = v; }

    public double getDien_tich_dung_cu_giai_phong() { return dien_tich_dung_cu_giai_phong; }
    public void setDien_tich_dung_cu_giai_phong(double v) { this.dien_tich_dung_cu_giai_phong = v; }

    public int getSo_ngay_thiet_bi() { return so_ngay_thiet_bi; }
    public void setSo_ngay_thiet_bi(int v) { this.so_ngay_thiet_bi = v; }

    public int getSo_luot_thiet_bi() { return so_luot_thiet_bi; }
    public void setSo_luot_thiet_bi(int v) { this.so_luot_thiet_bi = v; }

    public double getSan_luong_kg() { return san_luong_kg; }
    public void setSan_luong_kg(double v) { this.san_luong_kg = v; }

    public double getDien_tich_luu_kho() { return dien_tich_luu_kho; }
    public void setDien_tich_luu_kho(double v) { this.dien_tich_luu_kho = v; }
}
