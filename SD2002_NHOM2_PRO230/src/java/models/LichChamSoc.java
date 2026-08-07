package models;

import java.util.Date;

/**
 * UC-4.3 - Lịch chăm sóc định kỳ theo lô đất.
 * Bảng: LichChamSoc (+ bảng nối LichChamSoc_LoDat, NhacViec)
 */
public class LichChamSoc {
    private int id;
    private String loai_cong_viec;
    private Date ngay_bat_dau;
    private Integer chu_ky_ngay;   // null / 0 = không lặp
    private Date ngay_ket_thuc;
    private String mo_ta;
    private String trang_thai;
    private int nguoi_tao_id;

    // Danh sách lô đất áp dụng (id, cách nhau bởi dấu phẩy) + tên hiển thị
    private String danh_sach_lo_id;
    private String danh_sach_lo_ten;

    public LichChamSoc() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLoai_cong_viec() { return loai_cong_viec; }
    public void setLoai_cong_viec(String loai_cong_viec) { this.loai_cong_viec = loai_cong_viec; }

    public Date getNgay_bat_dau() { return ngay_bat_dau; }
    public void setNgay_bat_dau(Date ngay_bat_dau) { this.ngay_bat_dau = ngay_bat_dau; }

    public Integer getChu_ky_ngay() { return chu_ky_ngay; }
    public void setChu_ky_ngay(Integer chu_ky_ngay) { this.chu_ky_ngay = chu_ky_ngay; }

    public Date getNgay_ket_thuc() { return ngay_ket_thuc; }
    public void setNgay_ket_thuc(Date ngay_ket_thuc) { this.ngay_ket_thuc = ngay_ket_thuc; }

    public String getMo_ta() { return mo_ta; }
    public void setMo_ta(String mo_ta) { this.mo_ta = mo_ta; }

    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }

    public int getNguoi_tao_id() { return nguoi_tao_id; }
    public void setNguoi_tao_id(int nguoi_tao_id) { this.nguoi_tao_id = nguoi_tao_id; }

    public String getDanh_sach_lo_id() { return danh_sach_lo_id; }
    public void setDanh_sach_lo_id(String danh_sach_lo_id) { this.danh_sach_lo_id = danh_sach_lo_id; }

    public String getDanh_sach_lo_ten() { return danh_sach_lo_ten; }
    public void setDanh_sach_lo_ten(String danh_sach_lo_ten) { this.danh_sach_lo_ten = danh_sach_lo_ten; }
}
