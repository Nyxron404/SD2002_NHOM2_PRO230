package models;

import java.util.Date;

public class NguoiDung {
    private int id;
    private String ho_ten;
    private String email;
    private String so_dien_thoai;
    private Date ngay_sinh;
    private boolean gioi_tinh; // true = Nam, false = Nữ
    private String dia_chi;
    private Date ngay_tao;
    private Date ngay_cap_nhat;

    public NguoiDung() {
    }

    public NguoiDung(int id, String ho_ten, String email, String so_dien_thoai, Date ngay_sinh,
                     boolean gioi_tinh, String dia_chi, Date ngay_tao, Date ngay_cap_nhat) {
        this.id = id;
        this.ho_ten = ho_ten;
        this.email = email;
        this.so_dien_thoai = so_dien_thoai;
        this.ngay_sinh = ngay_sinh;
        this.gioi_tinh = gioi_tinh;
        this.dia_chi = dia_chi;
        this.ngay_tao = ngay_tao;
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getHo_ten() { return ho_ten; }
    public void setHo_ten(String ho_ten) { this.ho_ten = ho_ten; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSo_dien_thoai() { return so_dien_thoai; }
    public void setSo_dien_thoai(String so_dien_thoai) { this.so_dien_thoai = so_dien_thoai; }
    public Date getNgay_sinh() { return ngay_sinh; }
    public void setNgay_sinh(Date ngay_sinh) { this.ngay_sinh = ngay_sinh; }
    public boolean isGioi_tinh() { return gioi_tinh; }
    public void setGioi_tinh(boolean gioi_tinh) { this.gioi_tinh = gioi_tinh; }
    public String getDia_chi() { return dia_chi; }
    public void setDia_chi(String dia_chi) { this.dia_chi = dia_chi; }
    public Date getNgay_tao() { return ngay_tao; }
    public void setNgay_tao(Date ngay_tao) { this.ngay_tao = ngay_tao; }
    public Date getNgay_cap_nhat() { return ngay_cap_nhat; }
    public void setNgay_cap_nhat(Date ngay_cap_nhat) { this.ngay_cap_nhat = ngay_cap_nhat; }
}
