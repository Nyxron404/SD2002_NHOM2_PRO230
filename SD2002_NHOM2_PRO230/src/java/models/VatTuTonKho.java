package models;

/**
 * Dòng "tồn kho khả dụng" hiển thị trên form Nhật ký chăm sóc / Xử lý sâu bệnh.
 *
 * Không phải bảng trong CSDL. Tồn khả dụng của vật tư được tính từ
 * LoHangVatTu (chỉ lấy lô còn hàng và còn hạn sử dụng), KHÔNG lấy
 * VatTu.ton_kho_hien_tai vì cột đó có thể bao gồm cả lô đã hết hạn.
 */
public class VatTuTonKho {

    private int id;
    private String ten;
    private String don_vi_tinh;
    private double ton_kha_dung;      // số lượng thực sự xuất được
    private double don_gia_gan_nhat;  // đơn giá của lô sẽ xuất đầu tiên (FIFO/FEFO)
    private double dien_tich_don_vi;  // diện tích chiếm dụng cho 1 đơn vị
    private int loai_id;              // LoaiVatTu.id (vật tư) - không dùng cho dụng cụ/thiết bị
    private String ten_loai;          // tên loại vật tư / trạng thái thiết bị
    private String han_su_dung_gan_nhat;

    public VatTuTonKho() {}

    /** Ước tính chi phí nếu dùng hết số lượng nhập vào (chỉ để hiển thị). */
    public double getThanh_tien_uoc_tinh() {
        return Math.round(ton_kha_dung * don_gia_gan_nhat * 100.0) / 100.0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getDon_vi_tinh() { return don_vi_tinh; }
    public void setDon_vi_tinh(String v) { this.don_vi_tinh = v; }

    public double getTon_kha_dung() { return ton_kha_dung; }
    public void setTon_kha_dung(double v) { this.ton_kha_dung = v; }

    public double getDon_gia_gan_nhat() { return don_gia_gan_nhat; }
    public void setDon_gia_gan_nhat(double v) { this.don_gia_gan_nhat = v; }

    public double getDien_tich_don_vi() { return dien_tich_don_vi; }
    public void setDien_tich_don_vi(double v) { this.dien_tich_don_vi = v; }

    public int getLoai_id() { return loai_id; }
    public void setLoai_id(int v) { this.loai_id = v; }

    public String getTen_loai() { return ten_loai; }
    public void setTen_loai(String v) { this.ten_loai = v; }

    public String getHan_su_dung_gan_nhat() { return han_su_dung_gan_nhat; }
    public void setHan_su_dung_gan_nhat(String v) { this.han_su_dung_gan_nhat = v; }
}
