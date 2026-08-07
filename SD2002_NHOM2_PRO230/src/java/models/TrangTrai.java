package models;

import java.util.Date;

public class TrangTrai {
    private int id;
    private String ten_trang_trai;
    private String ma_so_thue;
    private String ma_so_vung_trong;
    private String logo;               // đường dẫn ảnh logo thương hiệu
    private String nguoi_dai_dien;
    private String chuc_vu_dai_dien;
    private String dia_chi;
    private String email;
    private String so_dien_thoai;
    private String toa_do_gps;         // định dạng "lat,lng" - chọn qua bản đồ
    private String nong_san_chu_luc;
    private String linh_vuc_hoat_dong;
    private double dien_tich_tong;
    private String mo_ta;
    private Integer nam_thanh_lap;
    private String chung_nhan;
    private String trang_thai;
    private String anh_ban_do;         // đường dẫn ảnh bản đồ / sơ đồ bố trí trang trại
    private Date ngay_tao;
    private Date ngay_cap_nhat;

    public TrangTrai() {
    }

    public TrangTrai(int id, String ten_trang_trai, String ma_so_thue, String ma_so_vung_trong,
                      String logo, String nguoi_dai_dien, String chuc_vu_dai_dien, String dia_chi,
                      String email, String so_dien_thoai, String toa_do_gps, String nong_san_chu_luc,
                      String linh_vuc_hoat_dong, double dien_tich_tong, String mo_ta, Integer nam_thanh_lap,
                      String chung_nhan, String trang_thai, String anh_ban_do, Date ngay_tao, Date ngay_cap_nhat) {
        this.id = id;
        this.ten_trang_trai = ten_trang_trai;
        this.ma_so_thue = ma_so_thue;
        this.ma_so_vung_trong = ma_so_vung_trong;
        this.logo = logo;
        this.nguoi_dai_dien = nguoi_dai_dien;
        this.chuc_vu_dai_dien = chuc_vu_dai_dien;
        this.dia_chi = dia_chi;
        this.email = email;
        this.so_dien_thoai = so_dien_thoai;
        this.toa_do_gps = toa_do_gps;
        this.nong_san_chu_luc = nong_san_chu_luc;
        this.linh_vuc_hoat_dong = linh_vuc_hoat_dong;
        this.dien_tich_tong = dien_tich_tong;
        this.mo_ta = mo_ta;
        this.nam_thanh_lap = nam_thanh_lap;
        this.chung_nhan = chung_nhan;
        this.trang_thai = trang_thai;
        this.anh_ban_do = anh_ban_do;
        this.ngay_tao = ngay_tao;
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTen_trang_trai() { return ten_trang_trai; }
    public void setTen_trang_trai(String ten_trang_trai) { this.ten_trang_trai = ten_trang_trai; }
    public String getMa_so_thue() { return ma_so_thue; }
    public void setMa_so_thue(String ma_so_thue) { this.ma_so_thue = ma_so_thue; }
    public String getMa_so_vung_trong() { return ma_so_vung_trong; }
    public void setMa_so_vung_trong(String ma_so_vung_trong) { this.ma_so_vung_trong = ma_so_vung_trong; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public String getNguoi_dai_dien() { return nguoi_dai_dien; }
    public void setNguoi_dai_dien(String nguoi_dai_dien) { this.nguoi_dai_dien = nguoi_dai_dien; }
    public String getChuc_vu_dai_dien() { return chuc_vu_dai_dien; }
    public void setChuc_vu_dai_dien(String chuc_vu_dai_dien) { this.chuc_vu_dai_dien = chuc_vu_dai_dien; }
    public String getDia_chi() { return dia_chi; }
    public void setDia_chi(String dia_chi) { this.dia_chi = dia_chi; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSo_dien_thoai() { return so_dien_thoai; }
    public void setSo_dien_thoai(String so_dien_thoai) { this.so_dien_thoai = so_dien_thoai; }
    public String getToa_do_gps() { return toa_do_gps; }
    public void setToa_do_gps(String toa_do_gps) { this.toa_do_gps = toa_do_gps; }
    public String getNong_san_chu_luc() { return nong_san_chu_luc; }
    public void setNong_san_chu_luc(String nong_san_chu_luc) { this.nong_san_chu_luc = nong_san_chu_luc; }
    public String getLinh_vuc_hoat_dong() { return linh_vuc_hoat_dong; }
    public void setLinh_vuc_hoat_dong(String linh_vuc_hoat_dong) { this.linh_vuc_hoat_dong = linh_vuc_hoat_dong; }
    public double getDien_tich_tong() { return dien_tich_tong; }
    public void setDien_tich_tong(double dien_tich_tong) { this.dien_tich_tong = dien_tich_tong; }
    public String getMo_ta() { return mo_ta; }
    public void setMo_ta(String mo_ta) { this.mo_ta = mo_ta; }
    public Integer getNam_thanh_lap() { return nam_thanh_lap; }
    public void setNam_thanh_lap(Integer nam_thanh_lap) { this.nam_thanh_lap = nam_thanh_lap; }
    public String getChung_nhan() { return chung_nhan; }
    public void setChung_nhan(String chung_nhan) { this.chung_nhan = chung_nhan; }
    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }
    public String getAnh_ban_do() { return anh_ban_do; }
    public void setAnh_ban_do(String anh_ban_do) { this.anh_ban_do = anh_ban_do; }
    public Date getNgay_tao() { return ngay_tao; }
    public void setNgay_tao(Date ngay_tao) { this.ngay_tao = ngay_tao; }
    public Date getNgay_cap_nhat() { return ngay_cap_nhat; }
    public void setNgay_cap_nhat(Date ngay_cap_nhat) { this.ngay_cap_nhat = ngay_cap_nhat; }

    /** Tách vĩ độ từ toa_do_gps dạng "lat,lng". Trả về null nếu không hợp lệ. */
    public Double getLat() {
        if (toa_do_gps == null || !toa_do_gps.contains(",")) return null;
        try { return Double.parseDouble(toa_do_gps.split(",")[0].trim()); }
        catch (Exception e) { return null; }
    }

    /** Tách kinh độ từ toa_do_gps dạng "lat,lng". Trả về null nếu không hợp lệ. */
    public Double getLng() {
        if (toa_do_gps == null || !toa_do_gps.contains(",")) return null;
        try { return Double.parseDouble(toa_do_gps.split(",")[1].trim()); }
        catch (Exception e) { return null; }
    }
}