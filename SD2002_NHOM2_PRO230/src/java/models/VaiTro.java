package models;

public class VaiTro {
    private int id;
    private String ten_vai_tro;
    private String mo_ta;

    public VaiTro() {
    }

    public VaiTro(int id, String ten_vai_tro, String mo_ta) {
        this.id = id;
        this.ten_vai_tro = ten_vai_tro;
        this.mo_ta = mo_ta;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTen_vai_tro() { return ten_vai_tro; }
    public void setTen_vai_tro(String ten_vai_tro) { this.ten_vai_tro = ten_vai_tro; }
    public String getMo_ta() { return mo_ta; }
    public void setMo_ta(String mo_ta) { this.mo_ta = mo_ta; }
}
