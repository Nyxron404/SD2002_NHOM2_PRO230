package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import models.MatDoThamChieu;
import models.VuonTrong;
import url.DBConnect;

/**
 * UC-4.2 - Thiết lập vườn trồng: gán giống cho lô đất, tính mật độ và phân loại.
 *
 * Sửa quan trọng: DonViQuanLy.dien_tich được lưu theo ĐƠN VỊ MÉT VUÔNG
 * (TrangTraiService kiểm tra diện tích khu vực/trang trại đều bằng m²),
 * trong khi MatDoThamChieu quy định mật độ theo CÂY/HA. Bản cũ chia thẳng
 * số cây cho m² nên mật độ nhỏ hơn thực tế 10.000 lần và lô nào cũng bị
 * gắn cờ "mật độ bất thường".
 *
 *      mật độ (cây/ha) = số cây / (diện tích m² / 10000)
 */
public class VuonTrongDAO {

    /** 1 hecta = 10.000 m². */
    public static final double M2_TREN_HA = 10000.0;

    /** Ngưỡng cảnh báo mật độ bất thường (cây/ha). */
    public static final double MAT_DO_MIN_HOP_LY = 50;
    public static final double MAT_DO_MAX_HOP_LY = 400;

    private final MatDoThamChieuDAO matDoDAO = new MatDoThamChieuDAO();

    // ===================================================================
    // TÍNH MẬT ĐỘ
    // ===================================================================

    /** Mật độ cây/ha từ số cây và diện tích tính bằng m². */
    public static double tinhMatDo(int soCay, double dienTichM2) {
        if (dienTichM2 <= 0) return 0;
        double ha = dienTichM2 / M2_TREN_HA;
        return Math.round(soCay / ha * 100.0) / 100.0;
    }

    public static boolean laBatThuong(double matDo) {
        return matDo < MAT_DO_MIN_HOP_LY || matDo > MAT_DO_MAX_HOP_LY;
    }

    /** Diện tích của lô đất, lấy từ DonViQuanLy. Không bao giờ nhận từ form. */
    private Double getDienTichLo(Connection conn, int loDatId) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT dien_tich FROM DonViQuanLy WHERE id = ? AND loai_don_vi = N'Lô đất'")) {
            ps.setInt(1, loDatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double area = rs.getDouble("dien_tich");
                    return rs.wasNull() ? null : area;
                }
            }
        }
        return null;
    }

    /** Dòng tham chiếu mật độ phù hợp; nếu không có dòng nào thì lấy dòng gần nhất. */
    private int timMatDoThamChieu(Connection conn, double matDo) throws Exception {
        MatDoThamChieu m = matDoDAO.phanLoai(matDo);
        if (m != null) return m.getId();

        String sql = "SELECT TOP 1 id FROM MatDoThamChieu ORDER BY ABS(mat_do_tu - ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, matDo);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        return 0;
    }

    // ===================================================================
    // ĐỌC
    // ===================================================================

    public List<VuonTrong> getAllWithNames() {
        List<VuonTrong> list = new ArrayList<>();
        String sql = """
            SELECT v.*, d.ten_don_vi AS ten_lo,
                   g.ten_giong AS ten_giong,
                   m.phan_loai AS phan_loai,
                   m.dac_diem_rui_ro AS rui_ro
            FROM VuonTrong v
            JOIN DonViQuanLy d ON v.lo_dat_id = d.id
            JOIN GiongSauRieng g ON v.giong_id = g.id
            LEFT JOIN MatDoThamChieu m ON v.mat_do_tham_chieu_id = m.id
            ORDER BY d.ten_don_vi
            """;
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VuonTrong v = new VuonTrong();
                v.setId(rs.getInt("id"));
                v.setLo_dat_id(rs.getInt("lo_dat_id"));
                v.setGiong_id(rs.getInt("giong_id"));
                v.setDien_tich(rs.getDouble("dien_tich"));
                v.setSo_luong_cay(rs.getInt("so_luong_cay"));
                v.setMat_do_trong(rs.getDouble("mat_do_trong"));
                v.setMat_do_tham_chieu_id(rs.getInt("mat_do_tham_chieu_id"));
                v.setMat_do_bat_thuong(rs.getBoolean("mat_do_bat_thuong"));
                v.setNgay_trong(rs.getDate("ngay_trong"));
                v.setTrang_thai_sinh_truong(rs.getString("trang_thai_sinh_truong"));
                v.setCo_sau_benh(rs.getBoolean("co_sau_benh"));
                v.setGhi_chu(rs.getString("ghi_chu"));
                v.setTen_lo_dat(rs.getString("ten_lo"));
                v.setTen_giong(rs.getString("ten_giong"));
                v.setPhan_loai_mat_do(rs.getString("phan_loai"));
                v.setDac_diem_rui_ro(rs.getString("rui_ro"));
                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public VuonTrong getById(int id) {
        for (VuonTrong v : getAllWithNames()) if (v.getId() == id) return v;
        return null;
    }

    /** Một lô đất chỉ được thiết lập một vườn. */
    public boolean loDaCoVuon(int loDatId, int excludeId) {
        String sql = "SELECT COUNT(*) FROM VuonTrong WHERE lo_dat_id = ? AND id <> ?";
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, loDatId);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===================================================================
    // THÊM / SỬA / XÓA
    // ===================================================================

    public boolean insert(VuonTrong v) {
        try (Connection c = DBConnect.getConnection()) {
            Double area = getDienTichLo(c, v.getLo_dat_id());
            if (area == null || area <= 0) return false;

            v.setDien_tich(area);
            double matDo = tinhMatDo(v.getSo_luong_cay(), area);
            int matDoId = timMatDoThamChieu(c, matDo);
            if (matDoId <= 0) return false;

            String sql = """
                INSERT INTO VuonTrong
                (lo_dat_id, giong_id, dien_tich, so_luong_cay, mat_do_trong,
                 mat_do_tham_chieu_id, mat_do_bat_thuong, ngay_trong,
                 trang_thai_sinh_truong, co_sau_benh, ghi_chu, ngay_tao)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, GETDATE())
                """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, v.getLo_dat_id());
                ps.setInt(2, v.getGiong_id());
                ps.setDouble(3, area);
                ps.setInt(4, v.getSo_luong_cay());
                ps.setDouble(5, matDo);
                ps.setInt(6, matDoId);
                ps.setBoolean(7, laBatThuong(matDo));
                ps.setDate(8, v.getNgay_trong() != null
                        ? new Date(v.getNgay_trong().getTime())
                        : new Date(System.currentTimeMillis()));
                ps.setString(9, v.getTrang_thai_sinh_truong() != null
                        ? v.getTrang_thai_sinh_truong() : "Cây con");
                ps.setString(10, v.getGhi_chu());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật vườn. Diện tích và mật độ luôn được tính lại từ lô đất,
     * kể cả khi người dùng đổi sang lô khác.
     */
    public boolean update(VuonTrong v) {
        try (Connection c = DBConnect.getConnection()) {
            Double area = getDienTichLo(c, v.getLo_dat_id());
            if (area == null || area <= 0) return false;

            v.setDien_tich(area);
            double matDo = tinhMatDo(v.getSo_luong_cay(), area);
            int matDoId = timMatDoThamChieu(c, matDo);
            if (matDoId <= 0) return false;

            String sql = """
                UPDATE VuonTrong
                SET lo_dat_id=?, giong_id=?, dien_tich=?, so_luong_cay=?,
                    mat_do_trong=?, mat_do_tham_chieu_id=?, mat_do_bat_thuong=?,
                    ngay_trong=?, trang_thai_sinh_truong=?, ghi_chu=?, ngay_cap_nhat=GETDATE()
                WHERE id=?
                """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, v.getLo_dat_id());
                ps.setInt(2, v.getGiong_id());
                ps.setDouble(3, area);
                ps.setInt(4, v.getSo_luong_cay());
                ps.setDouble(5, matDo);
                ps.setInt(6, matDoId);
                ps.setBoolean(7, laBatThuong(matDo));
                ps.setDate(8, v.getNgay_trong() != null
                        ? new Date(v.getNgay_trong().getTime())
                        : new Date(System.currentTimeMillis()));
                ps.setString(9, v.getTrang_thai_sinh_truong() != null
                        ? v.getTrang_thai_sinh_truong() : "Cây con");
                ps.setString(10, v.getGhi_chu());
                ps.setInt(11, v.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM VuonTrong WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Số bản ghi đang tham chiếu tới vườn này (chặn xóa nhầm). */
    public int demRangBuoc(int vuonId) {
        String sql = "SELECT (SELECT COUNT(*) FROM LichSuSinhTruong WHERE vuon_trong_id=?) "
                   + "+ (SELECT COUNT(*) FROM GhiNhanSauBenh WHERE vuon_trong_id=?) "
                   + "+ (SELECT COUNT(*) FROM GhiNhanThuHoach WHERE vuon_trong_id=?)";
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vuonId);
            ps.setInt(2, vuonId);
            ps.setInt(3, vuonId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean capNhatTrangThai(int id, String trangThai) {
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(
                "UPDATE VuonTrong SET trang_thai_sinh_truong=?, ngay_cap_nhat=GETDATE() WHERE id=?")) {
            ps.setString(1, trangThai);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
