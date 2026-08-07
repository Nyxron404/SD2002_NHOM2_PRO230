package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * UC-4.7 - Ghi nhận thu hoạch theo lô/vụ, phân loại chất lượng trái.
 * Bảng: GhiNhanThuHoach (+ ChiTietPhanLoaiThuHoach)
 */
public class GhiNhanThuHoach {
    private int id;
    private int vuon_trong_id;             // -> VuonTrong
    private String ten_vu_mua;
    private Date ngay_thu_hoach;
    private int vi_tri_luu_tru_id;         // -> DonViQuanLy (kho chứa)
    private double tong_san_luong_kg;
    private double tong_dien_tich_chiem_dung;
    private String trang_thai_luu_kho;
    private int nguoi_ghi_nhan_id;
    private String ghi_chu;

    // Hiển thị
    private String ten_lo_dat;
    private String ten_giong;

    // Chi tiết phân loại chất lượng
    private List<DongPhanLoai> dongPhanLoai = new ArrayList<>();

    public GhiNhanThuHoach() {}

    /** Một dòng phân loại chất lượng trái. */
    public static class DongPhanLoai {
        public String xepLoai;
        public double sanLuongKg;
        public double dienTichChiemDung;
        public DongPhanLoai() {}
        public DongPhanLoai(String xepLoai, double sanLuongKg, double dienTichChiemDung) {
            this.xepLoai = xepLoai; this.sanLuongKg = sanLuongKg; this.dienTichChiemDung = dienTichChiemDung;
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVuon_trong_id() { return vuon_trong_id; }
    public void setVuon_trong_id(int vuon_trong_id) { this.vuon_trong_id = vuon_trong_id; }

    public String getTen_vu_mua() { return ten_vu_mua; }
    public void setTen_vu_mua(String ten_vu_mua) { this.ten_vu_mua = ten_vu_mua; }

    public Date getNgay_thu_hoach() { return ngay_thu_hoach; }
    public void setNgay_thu_hoach(Date ngay_thu_hoach) { this.ngay_thu_hoach = ngay_thu_hoach; }

    public int getVi_tri_luu_tru_id() { return vi_tri_luu_tru_id; }
    public void setVi_tri_luu_tru_id(int vi_tri_luu_tru_id) { this.vi_tri_luu_tru_id = vi_tri_luu_tru_id; }

    public double getTong_san_luong_kg() { return tong_san_luong_kg; }
    public void setTong_san_luong_kg(double tong_san_luong_kg) { this.tong_san_luong_kg = tong_san_luong_kg; }

    public double getTong_dien_tich_chiem_dung() { return tong_dien_tich_chiem_dung; }
    public void setTong_dien_tich_chiem_dung(double v) { this.tong_dien_tich_chiem_dung = v; }

    public String getTrang_thai_luu_kho() { return trang_thai_luu_kho; }
    public void setTrang_thai_luu_kho(String trang_thai_luu_kho) { this.trang_thai_luu_kho = trang_thai_luu_kho; }

    public int getNguoi_ghi_nhan_id() { return nguoi_ghi_nhan_id; }
    public void setNguoi_ghi_nhan_id(int nguoi_ghi_nhan_id) { this.nguoi_ghi_nhan_id = nguoi_ghi_nhan_id; }

    public String getGhi_chu() { return ghi_chu; }
    public void setGhi_chu(String ghi_chu) { this.ghi_chu = ghi_chu; }

    public String getTen_lo_dat() { return ten_lo_dat; }
    public void setTen_lo_dat(String ten_lo_dat) { this.ten_lo_dat = ten_lo_dat; }

    public String getTen_giong() { return ten_giong; }
    public void setTen_giong(String ten_giong) { this.ten_giong = ten_giong; }

    public List<DongPhanLoai> getDongPhanLoai() { return dongPhanLoai; }
    public void setDongPhanLoai(List<DongPhanLoai> dongPhanLoai) { this.dongPhanLoai = dongPhanLoai; }
}
