package models;

import java.util.Date;

public class MaXacThucMatKhau {
    private int id;
    private int tai_khoan_id;
    private String ma_xac_thuc;
    private Date thoi_gian_tao;
    private Date thoi_gian_het_han;
    private boolean da_su_dung;

    public MaXacThucMatKhau() {
    }

    public MaXacThucMatKhau(int id, int tai_khoan_id, String ma_xac_thuc, Date thoi_gian_tao,
                            Date thoi_gian_het_han, boolean da_su_dung) {
        this.id = id;
        this.tai_khoan_id = tai_khoan_id;
        this.ma_xac_thuc = ma_xac_thuc;
        this.thoi_gian_tao = thoi_gian_tao;
        this.thoi_gian_het_han = thoi_gian_het_han;
        this.da_su_dung = da_su_dung;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTai_khoan_id() { return tai_khoan_id; }
    public void setTai_khoan_id(int tai_khoan_id) { this.tai_khoan_id = tai_khoan_id; }
    public String getMa_xac_thuc() { return ma_xac_thuc; }
    public void setMa_xac_thuc(String ma_xac_thuc) { this.ma_xac_thuc = ma_xac_thuc; }
    public Date getThoi_gian_tao() { return thoi_gian_tao; }
    public void setThoi_gian_tao(Date thoi_gian_tao) { this.thoi_gian_tao = thoi_gian_tao; }
    public Date getThoi_gian_het_han() { return thoi_gian_het_han; }
    public void setThoi_gian_het_han(Date thoi_gian_het_han) { this.thoi_gian_het_han = thoi_gian_het_han; }
    public boolean isDa_su_dung() { return da_su_dung; }
    public void setDa_su_dung(boolean da_su_dung) { this.da_su_dung = da_su_dung; }
}
