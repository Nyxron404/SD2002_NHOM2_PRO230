package models;

public class NhaCungCap {
    private int id;
    private String ten_ncc;
    private String ma_so_thue;
    
    // Thêm các trường khác nếu cần thiết (dia_chi, so_dien_thoai, v.v.)
    
    public NhaCungCap() {}

    public NhaCungCap(int id, String ten_ncc, String ma_so_thue) {
        this.id = id;
        this.ten_ncc = ten_ncc;
        this.ma_so_thue = ma_so_thue;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTen_ncc() { return ten_ncc; }
    public void setTen_ncc(String ten_ncc) { this.ten_ncc = ten_ncc; }

    public String getMa_so_thue() { return ma_so_thue; }
    public void setMa_so_thue(String ma_so_thue) { this.ma_so_thue = ma_so_thue; }
}