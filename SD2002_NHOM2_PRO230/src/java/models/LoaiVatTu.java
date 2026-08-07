package models;

public class LoaiVatTu {
    private int id;
    private String ten_loai;
    private boolean yeu_cau_han_su_dung;
    private boolean yeu_cau_cach_ly;

    public LoaiVatTu() {
    }

    public LoaiVatTu(int id, String ten_loai, boolean yeu_cau_han_su_dung, boolean yeu_cau_cach_ly) {
        this.id = id;
        this.ten_loai = ten_loai;
        this.yeu_cau_han_su_dung = yeu_cau_han_su_dung;
        this.yeu_cau_cach_ly = yeu_cau_cach_ly;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTen_loai() { return ten_loai; }
    public void setTen_loai(String ten_loai) { this.ten_loai = ten_loai; }
    public boolean isYeu_cau_han_su_dung() { return yeu_cau_han_su_dung; }
    public void setYeu_cau_han_su_dung(boolean yeu_cau_han_su_dung) { this.yeu_cau_han_su_dung = yeu_cau_han_su_dung; }
    public boolean isYeu_cau_cach_ly() { return yeu_cau_cach_ly; }
    public void setYeu_cau_cach_ly(boolean yeu_cau_cach_ly) { this.yeu_cau_cach_ly = yeu_cau_cach_ly; }
}