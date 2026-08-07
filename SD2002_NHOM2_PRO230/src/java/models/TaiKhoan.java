package models;

import java.util.Date;

public class TaiKhoan {
    private int id;
    private int nguoi_dung_id;
    private int vai_tro_id;
    private String ten_dang_nhap;
    private String mat_khau;
    private boolean trang_thai;        // true = Hoạt động, false = Bị khóa
    private boolean phai_doi_mat_khau; // true = phải đổi mật khẩu ở lần đăng nhập đầu
    private Date lan_dang_nhap_cuoi;
    private Date ngay_tao;
    private Date ngay_cap_nhat;

    // Thông tin tham chiếu (join) - phục vụ hiển thị, không thuộc bảng TaiKhoan
    private NguoiDung nguoiDung;
    private String ten_vai_tro;

    public TaiKhoan() {
    }

    public TaiKhoan(int id, int nguoi_dung_id, int vai_tro_id, String ten_dang_nhap, String mat_khau,
                    boolean trang_thai, boolean phai_doi_mat_khau, Date lan_dang_nhap_cuoi,
                    Date ngay_tao, Date ngay_cap_nhat) {
        this.id = id;
        this.nguoi_dung_id = nguoi_dung_id;
        this.vai_tro_id = vai_tro_id;
        this.ten_dang_nhap = ten_dang_nhap;
        this.mat_khau = mat_khau;
        this.trang_thai = trang_thai;
        this.phai_doi_mat_khau = phai_doi_mat_khau;
        this.lan_dang_nhap_cuoi = lan_dang_nhap_cuoi;
        this.ngay_tao = ngay_tao;
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNguoi_dung_id() { return nguoi_dung_id; }
    public void setNguoi_dung_id(int nguoi_dung_id) { this.nguoi_dung_id = nguoi_dung_id; }
    public int getVai_tro_id() { return vai_tro_id; }
    public void setVai_tro_id(int vai_tro_id) { this.vai_tro_id = vai_tro_id; }
    public String getTen_dang_nhap() { return ten_dang_nhap; }
    public void setTen_dang_nhap(String ten_dang_nhap) { this.ten_dang_nhap = ten_dang_nhap; }
    public String getMat_khau() { return mat_khau; }
    public void setMat_khau(String mat_khau) { this.mat_khau = mat_khau; }
    public boolean isTrang_thai() { return trang_thai; }
    public void setTrang_thai(boolean trang_thai) { this.trang_thai = trang_thai; }
    public boolean isPhai_doi_mat_khau() { return phai_doi_mat_khau; }
    public void setPhai_doi_mat_khau(boolean phai_doi_mat_khau) { this.phai_doi_mat_khau = phai_doi_mat_khau; }
    public Date getLan_dang_nhap_cuoi() { return lan_dang_nhap_cuoi; }
    public void setLan_dang_nhap_cuoi(Date lan_dang_nhap_cuoi) { this.lan_dang_nhap_cuoi = lan_dang_nhap_cuoi; }
    public Date getNgay_tao() { return ngay_tao; }
    public void setNgay_tao(Date ngay_tao) { this.ngay_tao = ngay_tao; }
    public Date getNgay_cap_nhat() { return ngay_cap_nhat; }
    public void setNgay_cap_nhat(Date ngay_cap_nhat) { this.ngay_cap_nhat = ngay_cap_nhat; }

    public NguoiDung getNguoiDung() { return nguoiDung; }
    public void setNguoiDung(NguoiDung nguoiDung) { this.nguoiDung = nguoiDung; }
    public String getTen_vai_tro() { return ten_vai_tro; }
    public void setTen_vai_tro(String ten_vai_tro) { this.ten_vai_tro = ten_vai_tro; }
}
