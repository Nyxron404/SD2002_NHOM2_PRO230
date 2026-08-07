package service;

import java.util.Calendar;
import java.util.regex.Pattern;

import dao.DonViQuanLyDAO;
import dao.KhuVucDAO;
import dao.TrangTraiDAO;
import models.DonViQuanLy;
import models.KhuVuc;
import models.TrangTrai;

public class TrangTraiService {

    private final TrangTraiDAO trangTraiDAO = new TrangTraiDAO();
    private final KhuVucDAO khuVucDAO = new KhuVucDAO();
    private final DonViQuanLyDAO donViDAO = new DonViQuanLyDAO();

    public static final String[] LOAI_KHU_VUC_HOP_LE = {"Khu trồng", "Khu kho", "Khu thiết bị", "Khu thu hoạch", "Khác"};
    public static final String[] TRANG_THAI_TRANG_TRAI_HOP_LE = {"Đang hoạt động", "Tạm ngừng hoạt động"};
    // Trạng thái đơn vị quản lý phản ánh mức độ sử dụng để dễ theo dõi (còn trống -> đầy) và trạng thái vận hành.
    public static final String[] TRANG_THAI_DON_VI_HOP_LE = {"Còn trống", "Đang sử dụng", "Gần đầy", "Đầy", "Bảo trì", "Ngừng sử dụng"};

    // Loại đơn vị quản lý được suy ra tự động theo loại khu vực (không cho người dùng chọn lại):
    //   Khu trồng   -> Lô đất
    //   Khu kho     -> Kệ
    //   Khu thiết bị-> Bãi
    //   Khu thu hoạch-> Ô chứa
    //   Khác        -> Khác
    public static String loaiDonViTheoKhuVuc(String loaiKhuVuc) {
        if (loaiKhuVuc == null) return "Khác";
        switch (loaiKhuVuc) {
            case "Khu trồng":    return "Lô đất";
            case "Khu kho":      return "Kệ";
            case "Khu thiết bị": return "Bãi";
            case "Khu thu hoạch":return "Ô chứa";
            default:             return "Khác";
        }
    }

    private boolean trangThaiDonViHopLe(String trangThai) {
        for (String t : TRANG_THAI_DON_VI_HOP_LE) if (t.equals(trangThai)) return true;
        return false;
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");
    // Toạ độ chọn qua bản đồ được lưu ở dạng "vĩ_độ,kinh_độ", ví dụ: "12.6689,108.0378"
    private static final Pattern GPS_PATTERN = Pattern.compile("^-?\\d{1,3}(\\.\\d+)?,\\s*-?\\d{1,3}(\\.\\d+)?$");

    // ===================== UC-2.1: THÔNG TIN TRANG TRẠI =====================
    public TrangTrai getTrangTrai() {
        return trangTraiDAO.getTrangTrai();
    }

    /**
     * Thêm mới (nếu hệ thống chưa có bản ghi trang trại nào) hoặc cập nhật thông tin & diện tích trang trại.
     * 5a. Dữ liệu không hợp lệ -> lỗi
     * 5b. Diện tích mới nhỏ hơn tổng diện tích các khu vực đã phân chia -> lỗi
     */
    public String capNhatTrangTrai(TrangTrai tt) {
        String loi = kiemTraDuLieu(tt);
        if (loi != null) return loi;

        TrangTrai hienTai = trangTraiDAO.getTrangTrai();
        boolean isUpdate = hienTai != null;

        if (isUpdate) {
            double tongDaPhanChia = khuVucDAO.getTongDienTichDaPhanChia(hienTai.getId(), -1);
            if (tt.getDien_tich_tong() < tongDaPhanChia) {
                return "Diện tích mới (" + tt.getDien_tich_tong() + " m²) nhỏ hơn tổng diện tích các khu vực đã phân chia (" + tongDaPhanChia + " m²).";
            }
            // Giữ lại ảnh cũ nếu lần này không upload ảnh mới
            if (tt.getLogo() == null) tt.setLogo(hienTai.getLogo());
            if (tt.getAnh_ban_do() == null) tt.setAnh_ban_do(hienTai.getAnh_ban_do());
            tt.setId(hienTai.getId());
            boolean ok = trangTraiDAO.update(tt);
            return ok ? null : "Không thể cập nhật thông tin trang trại. Vui lòng thử lại.";
        } else {
            boolean ok = trangTraiDAO.insert(tt);
            return ok ? null : "Không thể lưu thông tin trang trại. Vui lòng thử lại.";
        }
    }

    private String kiemTraDuLieu(TrangTrai tt) {
        if (isBlank(tt.getTen_trang_trai())) return "Tên trang trại không được để trống.";
        if (isBlank(tt.getMa_so_thue())) return "Mã số thuế không được để trống.";
        if (isBlank(tt.getMa_so_vung_trong())) return "Mã số vùng trồng không được để trống.";
        if (isBlank(tt.getDia_chi())) return "Địa chỉ không được để trống.";
        if (isBlank(tt.getEmail()) || !EMAIL_PATTERN.matcher(tt.getEmail()).matches()) return "Email không hợp lệ.";
        if (isBlank(tt.getSo_dien_thoai()) || !PHONE_PATTERN.matcher(tt.getSo_dien_thoai()).matches())
            return "Số điện thoại không hợp lệ (phải gồm 10 chữ số, bắt đầu bằng số 0).";
        if (isBlank(tt.getToa_do_gps()) || !GPS_PATTERN.matcher(tt.getToa_do_gps()).matches())
            return "Vui lòng chọn toạ độ GPS trên bản đồ.";
        if (isBlank(tt.getNong_san_chu_luc())) return "Nông sản chủ lực không được để trống.";
        if (isBlank(tt.getLinh_vuc_hoat_dong())) return "Lĩnh vực hoạt động không được để trống.";
        if (tt.getDien_tich_tong() <= 0) return "Diện tích trang trại phải lớn hơn 0.";
        if (tt.getNam_thanh_lap() != null) {
            int namHienTai = Calendar.getInstance().get(Calendar.YEAR);
            if (tt.getNam_thanh_lap() < 1900 || tt.getNam_thanh_lap() > namHienTai)
                return "Năm thành lập không hợp lệ.";
        }
        if (isBlank(tt.getTrang_thai())) tt.setTrang_thai("Đang hoạt động");
        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // ===================== UC-2.2: PHÂN CHIA KHU VỰC =====================
    public java.util.List<KhuVuc> getDanhSachKhuVuc(int trangTraiId) {
        return khuVucDAO.getAllByTrangTrai(trangTraiId);
    }

    public KhuVuc getKhuVuc(int id) {
        return khuVucDAO.getById(id);
    }

    private boolean loaiKhuVucHopLe(String loai) {
        for (String l : LOAI_KHU_VUC_HOP_LE) if (l.equals(loai)) return true;
        return false;
    }

    /**
     * Thêm mới hoặc cập nhật 1 khu vực.
     * 5a. Dữ liệu không hợp lệ -> lỗi
     * 5b. Diện tích khu vực vượt diện tích còn lại của trang trại -> lỗi
     */
    public String luuKhuVuc(KhuVuc kv, boolean isUpdate) {
        if (kv.getTen_khu_vuc() == null || kv.getTen_khu_vuc().trim().isEmpty()) {
            return "Tên khu vực không được để trống.";
        }
        if (!loaiKhuVucHopLe(kv.getLoai_khu_vuc())) {
            return "Loại khu vực không hợp lệ.";
        }
        if (kv.getDien_tich_khai_thac() < 0 || kv.getDien_tich_chiem_dung() < 0) {
            return "Diện tích không được âm.";
        }
        double dienTichKhuVuc = kv.getDien_tich_khai_thac() + kv.getDien_tich_chiem_dung();
        kv.setDien_tich_khu_vuc(dienTichKhuVuc);
        if (dienTichKhuVuc <= 0) {
            return "Diện tích khu vực phải lớn hơn 0.";
        }

        TrangTrai tt = trangTraiDAO.getTrangTrai();
        if (tt == null) {
            return "Chưa có thông tin trang trại. Vui lòng khai báo trang trại trước.";
        }
        int excludeId = isUpdate ? kv.getId() : -1;
        double tongDaPhanChia = khuVucDAO.getTongDienTichDaPhanChia(tt.getId(), excludeId);
        double conLai = tt.getDien_tich_tong() - tongDaPhanChia;
        if (dienTichKhuVuc > conLai) {
            return "Diện tích khu vực (" + dienTichKhuVuc + " m²) vượt quá diện tích còn lại của trang trại (" + conLai + " m²).";
        }

        if (isUpdate) {
            // Nếu khu vực đã có đơn vị con, diện tích khai thác mới không được nhỏ hơn tổng diện tích các đơn vị đã phân chia
            double tongDonVi = donViDAO.getTongDienTichByKhuVuc(kv.getId(), -1);
            if (kv.getDien_tich_khai_thac() < tongDonVi) {
                return "Diện tích khai thác mới (" + kv.getDien_tich_khai_thac() + " m²) nhỏ hơn tổng diện tích các đơn vị đã phân chia trong khu vực (" + tongDonVi + " m²).";
            }
            boolean ok = khuVucDAO.update(kv);
            return ok ? null : "Không thể cập nhật khu vực. Vui lòng thử lại.";
        } else {
            kv.setTrang_trai_id(tt.getId());
            int newId = khuVucDAO.insert(kv);
            return newId > 0 ? null : "Không thể lưu khu vực mới. Vui lòng thử lại.";
        }
    }

    public String xoaKhuVuc(int id) {
        java.util.List<DonViQuanLy> donViList = donViDAO.getByKhuVuc(id);
        if (!donViList.isEmpty()) {
            return "Không thể xoá khu vực vì vẫn còn " + donViList.size() + " đơn vị quản lý bên trong.";
        }
        boolean ok = khuVucDAO.delete(id);
        return ok ? null : "Không thể xoá khu vực. Vui lòng thử lại.";
    }

    // ===================== UC-2.3: QUẢN LÝ KHU VỰC CHI TIẾT =====================
    public java.util.List<DonViQuanLy> getDonViTheoKhuVuc(int khuVucId) {
        return donViDAO.getByKhuVuc(khuVucId);
    }

    /**
     * Thêm mới hoặc cập nhật 1 đơn vị quản lý (lô đất / kệ / vị trí lưu trữ) trong khu vực.
     * 4a. Dữ liệu không hợp lệ -> lỗi
     * 5b. Diện tích phân chia vượt diện tích khai thác khu vực -> lỗi
     */
    public String luuDonVi(DonViQuanLy dv, boolean isUpdate) {
        if (dv.getTen_don_vi() == null || dv.getTen_don_vi().trim().isEmpty()) {
            return "Tên đơn vị không được để trống.";
        }
        if (dv.getDien_tich() <= 0) {
            return "Diện tích đơn vị phải lớn hơn 0.";
        }
        KhuVuc kv = khuVucDAO.getById(dv.getKhu_vuc_id());
        if (kv == null) {
            return "Khu vực không tồn tại.";
        }

        // Loại đơn vị được xác định tự động theo loại khu vực -> không phụ thuộc vào giá trị người dùng gửi lên.
        String loaiDonVi = loaiDonViTheoKhuVuc(kv.getLoai_khu_vuc());
        dv.setLoai_don_vi(loaiDonVi);
        boolean laKe = "Kệ".equals(loaiDonVi);

        // Sức chứa & số tầng chỉ áp dụng cho Kệ (khu kho); các loại đơn vị khác không lưu 2 thông tin này.
        if (laKe) {
            if (dv.getSuc_chua() < 0) {
                return "Sức chứa không được âm.";
            }
            if (dv.getSo_tang() != null && dv.getSo_tang() < 1) {
                return "Số tầng phải lớn hơn hoặc bằng 1.";
            }
        } else {
            dv.setSuc_chua(0);
            dv.setSo_tang(null);
        }

        // Trạng thái phải nằm trong danh sách hợp lệ; mặc định "Còn trống".
        if (dv.getTrang_thai() == null || dv.getTrang_thai().trim().isEmpty()) {
            dv.setTrang_thai("Còn trống");
        } else if (!trangThaiDonViHopLe(dv.getTrang_thai())) {
            return "Trạng thái đơn vị không hợp lệ.";
        }

        int excludeId = isUpdate ? dv.getId() : -1;
        double tongDaPhanChia = donViDAO.getTongDienTichByKhuVuc(dv.getKhu_vuc_id(), excludeId);
        double conLai = kv.getDien_tich_khai_thac() - tongDaPhanChia;
        if (dv.getDien_tich() > conLai) {
            return "Diện tích đơn vị (" + dv.getDien_tich() + " m²) vượt quá diện tích khai thác còn lại của khu vực (" + conLai + " m²).";
        }

        if (isUpdate) {
            boolean ok = donViDAO.update(dv);
            return ok ? null : "Không thể cập nhật đơn vị. Vui lòng thử lại.";
        } else {
            int newId = donViDAO.insert(dv);
            return newId > 0 ? null : "Không thể lưu đơn vị mới. Vui lòng thử lại.";
        }
    }

    public String xoaDonVi(int id) {
        boolean ok = donViDAO.delete(id);
        return ok ? null : "Không thể xoá đơn vị. Vui lòng thử lại.";
    }
}