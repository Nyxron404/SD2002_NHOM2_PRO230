package models;

public class VatTu {
    private int id;
    private String ten_vat_tu;
    private int loai_vat_tu_id;
    private String hoat_chat;
    private String doi_tuong_phong_tru;
    private int thoi_gian_cach_ly;
    private String don_vi_tinh;
    private String quy_cach_dong_goi;
    private double dien_tich_chiem_dung;
    private int vi_tri_luu_tru_mac_dinh_id;
    private double ton_kho_hien_tai;
    private double ton_kho_toi_thieu;
    private double ton_kho_toi_da;
    private String trang_thai;

    // Constructor không tham số
    public VatTu() {}

    public VatTu(int id, String ten_vat_tu, int loai_vat_tu_id, String hoat_chat, String doi_tuong_phong_tru, int thoi_gian_cach_ly, String don_vi_tinh, String quy_cach_dong_goi, double dien_tich_chiem_dung, int vi_tri_luu_tru_mac_dinh_id, double ton_kho_hien_tai, double ton_kho_toi_thieu, double ton_kho_toi_da, String trang_thai) {
        this.id = id;
        this.ten_vat_tu = ten_vat_tu;
        this.loai_vat_tu_id = loai_vat_tu_id;
        this.hoat_chat = hoat_chat;
        this.doi_tuong_phong_tru = doi_tuong_phong_tru;
        this.thoi_gian_cach_ly = thoi_gian_cach_ly;
        this.don_vi_tinh = don_vi_tinh;
        this.quy_cach_dong_goi = quy_cach_dong_goi;
        this.dien_tich_chiem_dung = dien_tich_chiem_dung;
        this.vi_tri_luu_tru_mac_dinh_id = vi_tri_luu_tru_mac_dinh_id;
        this.ton_kho_hien_tai = ton_kho_hien_tai;
        this.ton_kho_toi_thieu = ton_kho_toi_thieu;
        this.ton_kho_toi_da = ton_kho_toi_da;
        this.trang_thai = trang_thai;
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTen_vat_tu() { return ten_vat_tu; }
    public void setTen_vat_tu(String ten_vat_tu) { this.ten_vat_tu = ten_vat_tu; }

    public int getLoai_vat_tu_id() { return loai_vat_tu_id; }
    public void setLoai_vat_tu_id(int loai_vat_tu_id) { this.loai_vat_tu_id = loai_vat_tu_id; }

    public String getHoat_chat() { return hoat_chat; }
    public void setHoat_chat(String hoat_chat) { this.hoat_chat = hoat_chat; }

    public String getDoi_tuong_phong_tru() { return doi_tuong_phong_tru; }
    public void setDoi_tuong_phong_tru(String doi_tuong_phong_tru) { this.doi_tuong_phong_tru = doi_tuong_phong_tru; }

    public int getThoi_gian_cach_ly() { return thoi_gian_cach_ly; }
    public void setThoi_gian_cach_ly(int thoi_gian_cach_ly) { this.thoi_gian_cach_ly = thoi_gian_cach_ly; }

    public String getDon_vi_tinh() { return don_vi_tinh; }
    public void setDon_vi_tinh(String don_vi_tinh) { this.don_vi_tinh = don_vi_tinh; }

    public String getQuy_cach_dong_goi() { return quy_cach_dong_goi; }
    public void setQuy_cach_dong_goi(String quy_cach_dong_goi) { this.quy_cach_dong_goi = quy_cach_dong_goi; }

    public double getDien_tich_chiem_dung() { return dien_tich_chiem_dung; }
    public void setDien_tich_chiem_dung(double dien_tich_chiem_dung) { this.dien_tich_chiem_dung = dien_tich_chiem_dung; }

    public int getVi_tri_luu_tru_mac_dinh_id() { return vi_tri_luu_tru_mac_dinh_id; }
    public void setVi_tri_luu_tru_mac_dinh_id(int vi_tri_luu_tru_mac_dinh_id) { this.vi_tri_luu_tru_mac_dinh_id = vi_tri_luu_tru_mac_dinh_id; }

    public double getTon_kho_hien_tai() { return ton_kho_hien_tai; }
    public void setTon_kho_hien_tai(double ton_kho_hien_tai) { this.ton_kho_hien_tai = ton_kho_hien_tai; }

    public double getTon_kho_toi_thieu() { return ton_kho_toi_thieu; }
    public void setTon_kho_toi_thieu(double ton_kho_toi_thieu) { this.ton_kho_toi_thieu = ton_kho_toi_thieu; }

    public double getTon_kho_toi_da() { return ton_kho_toi_da; }
    public void setTon_kho_toi_da(double ton_kho_toi_da) { this.ton_kho_toi_da = ton_kho_toi_da; }

    public String getTrang_thai() { return trang_thai; }
    public void setTrang_thai(String trang_thai) { this.trang_thai = trang_thai; }
}