package models;

import java.util.Date;

/**
 * UC-4.3 - Một bản ghi nhắc việc sinh ra từ lịch chăm sóc.
 * Bảng: NhacViec (nối qua LichChamSoc_LoDat -> LichChamSoc + DonViQuanLy).
 */
public class NhacViecItem {

    private int id;                 // NhacViec.id
    private int lich_lo_dat_id;
    private int lich_cham_soc_id;
    private int lo_dat_id;
    private String ten_lo_dat;
    private String loai_cong_viec;
    private Date ngay_nhac;
    private String trang_thai;      // Chờ xử lý / Đã thực hiện / Quá hạn
    private Integer nhat_ky_cham_soc_id;
    private boolean qua_han;

    public NhacViecItem() {}

    /** Nhãn hiển thị trong dropdown khi ghi nhật ký. */
    public String getNhan() {
        return ngay_nhac + " - " + loai_cong_viec + " (" + ten_lo_dat + ")";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLich_lo_dat_id() { return lich_lo_dat_id; }
    public void setLich_lo_dat_id(int v) { this.lich_lo_dat_id = v; }

    public int getLich_cham_soc_id() { return lich_cham_soc_id; }
    public void setLich_cham_soc_id(int v) { this.lich_cham_soc_id = v; }

    public int getLo_dat_id() { return lo_dat_id; }
    public void setLo_dat_id(int v) { this.lo_dat_id = v; }

    public String getTen_lo_dat() { return ten_lo_dat; }
    public void setTen_lo_dat(String v) { this.ten_lo_dat = v; }

    public String getLoai_cong_viec() { return loai_cong_viec; }
    public void setLoai_cong_viec(String v) { this.loai_cong_viec = v; }

    public Date getNgay_nhac() { return ngay_nhac; }
    public void setNgay_nhac(Date v) { this.ngay_nhac = v; }

    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String v) { this.trang_thai = v; }

    public Integer getNhat_ky_cham_soc_id() { return nhat_ky_cham_soc_id; }
    public void setNhat_ky_cham_soc_id(Integer v) { this.nhat_ky_cham_soc_id = v; }

    public boolean isQua_han() { return qua_han; }
    public void setQua_han(boolean v) { this.qua_han = v; }
}
