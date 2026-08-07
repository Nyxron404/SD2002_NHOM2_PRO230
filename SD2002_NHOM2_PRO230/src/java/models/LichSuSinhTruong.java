package models;

import java.util.Date;

/**
 * UC-4.5 - Theo dõi trạng thái sinh trưởng theo lô/đợt trồng.
 * Bảng: LichSuSinhTruong (+ ChiTietTyLeGiaiDoan khi lô không đồng đều)
 */
public class LichSuSinhTruong {
    private int id;
    private int vuon_trong_id;      // -> VuonTrong
    private Date ngay_cap_nhat;
    private String loai_cap_nhat;   // "Chuyển giai đoạn" / "Giảm số cây" / "Tỷ lệ giai đoạn"
    private Integer so_luong_cay_giam;
    private String ghi_chu;
    private int nguoi_cap_nhat_id;

    // Trạng thái mới muốn gán cho lô (khi chọn 1 giai đoạn duy nhất)
    private String giai_doan_moi;
    // Tỷ lệ % nhiều giai đoạn: "Cây con:30,Sinh trưởng:70"
    private String ty_le_giai_doan;

    // Hiển thị
    private String ten_lo_dat;
    private String ten_giong;
    private String trang_thai_hien_tai;
    private int so_cay_con_lai;

    public LichSuSinhTruong() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVuon_trong_id() { return vuon_trong_id; }
    public void setVuon_trong_id(int vuon_trong_id) { this.vuon_trong_id = vuon_trong_id; }

    public Date getNgay_cap_nhat() { return ngay_cap_nhat; }
    public void setNgay_cap_nhat(Date ngay_cap_nhat) { this.ngay_cap_nhat = ngay_cap_nhat; }

    public String getLoai_cap_nhat() { return loai_cap_nhat; }
    public void setLoai_cap_nhat(String loai_cap_nhat) { this.loai_cap_nhat = loai_cap_nhat; }

    public Integer getSo_luong_cay_giam() { return so_luong_cay_giam; }
    public void setSo_luong_cay_giam(Integer so_luong_cay_giam) { this.so_luong_cay_giam = so_luong_cay_giam; }

    public String getGhi_chu() { return ghi_chu; }
    public void setGhi_chu(String ghi_chu) { this.ghi_chu = ghi_chu; }

    public int getNguoi_cap_nhat_id() { return nguoi_cap_nhat_id; }
    public void setNguoi_cap_nhat_id(int nguoi_cap_nhat_id) { this.nguoi_cap_nhat_id = nguoi_cap_nhat_id; }

    public String getGiai_doan_moi() { return giai_doan_moi; }
    public void setGiai_doan_moi(String giai_doan_moi) { this.giai_doan_moi = giai_doan_moi; }

    public String getTy_le_giai_doan() { return ty_le_giai_doan; }
    public void setTy_le_giai_doan(String ty_le_giai_doan) { this.ty_le_giai_doan = ty_le_giai_doan; }

    public String getTen_lo_dat() { return ten_lo_dat; }
    public void setTen_lo_dat(String ten_lo_dat) { this.ten_lo_dat = ten_lo_dat; }

    public String getTen_giong() { return ten_giong; }
    public void setTen_giong(String ten_giong) { this.ten_giong = ten_giong; }

    public String getTrang_thai_hien_tai() { return trang_thai_hien_tai; }
    public void setTrang_thai_hien_tai(String trang_thai_hien_tai) { this.trang_thai_hien_tai = trang_thai_hien_tai; }

    public int getSo_cay_con_lai() { return so_cay_con_lai; }
    public void setSo_cay_con_lai(int so_cay_con_lai) { this.so_cay_con_lai = so_cay_con_lai; }
}
