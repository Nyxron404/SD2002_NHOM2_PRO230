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

public class VuonTrongDAO {
    private final MatDoThamChieuDAO matDoDAO = new MatDoThamChieuDAO();

    public static final double MAT_DO_MIN_HOP_LY = 60.0;
    public static final double MAT_DO_MAX_HOP_LY = 600.0;

    public static double tinhMatDo(int soCay, double dienTichHa) {
        if (dienTichHa <= 0) return 0;
        return soCay / dienTichHa;
    }

    /**
     * Diện tích được lấy từ module Phân chia khu vực.
     * Không nhận diện tích từ form/request.
     */
    private Double getDienTichLo(Connection conn, int loDatId) throws Exception {
        String sql = "SELECT dien_tich FROM DonViQuanLy WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
            ORDER BY v.id DESC
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

    public boolean insert(VuonTrong v) {
        try (Connection c = DBConnect.getConnection()) {
            Double area = getDienTichLo(c, v.getLo_dat_id());

            if (area == null || area <= 0) {
                return false;
            }

            // Tuyệt đối không lấy diện tích từ request.
            v.setDien_tich(area);

            double matDo = tinhMatDo(v.getSo_luong_cay(), area);
            MatDoThamChieu m = matDoDAO.phanLoai(matDo);
            int matDoId = m != null ? m.getId() : timMatDoGanNhat(c, matDo);

            if (matDoId <= 0) {
                return false;
            }

            boolean batThuong = matDo < MAT_DO_MIN_HOP_LY || matDo > MAT_DO_MAX_HOP_LY;

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
                ps.setBoolean(7, batThuong);

                if (v.getNgay_trong() != null) {
                    ps.setDate(8, new Date(v.getNgay_trong().getTime()));
                } else {
                    ps.setDate(8, new Date(System.currentTimeMillis()));
                }

                ps.setString(9, "Cây con");
                ps.setString(10, v.getGhi_chu());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(VuonTrong v) {
        try (Connection c = DBConnect.getConnection()) {
            Double area = getDienTichLo(c, v.getLo_dat_id());

            if (area == null || area <= 0) {
                return false;
            }

            v.setDien_tich(area);

            double matDo = tinhMatDo(v.getSo_luong_cay(), area);
            MatDoThamChieu m = matDoDAO.phanLoai(matDo);
            int matDoId = m != null ? m.getId() : timMatDoGanNhat(c, matDo);

            if (matDoId <= 0) {
                return false;
            }

            boolean batThuong = matDo < MAT_DO_MIN_HOP_LY || matDo > MAT_DO_MAX_HOP_LY;

            String sql = """
                UPDATE VuonTrong
                SET lo_dat_id=?, giong_id=?, dien_tich=?, so_luong_cay=?,
                    mat_do_trong=?, mat_do_tham_chieu_id=?,
                    mat_do_bat_thuong=?, ghi_chu=?, ngay_cap_nhat=GETDATE()
                WHERE id=?
                """;

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, v.getLo_dat_id());
                ps.setInt(2, v.getGiong_id());
                ps.setDouble(3, area);
                ps.setInt(4, v.getSo_luong_cay());
                ps.setDouble(5, matDo);
                ps.setInt(6, matDoId);
                ps.setBoolean(7, batThuong);
                ps.setString(8, v.getGhi_chu());
                ps.setInt(9, v.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM VuonTrong WHERE id=?";
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatTrangThai(int id, String trangThai) {
        String sql = "UPDATE VuonTrong SET trang_thai_sinh_truong=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean giamSoCay(int id, int giam) {
        String sql = """
            UPDATE VuonTrong
            SET so_luong_cay =
                    CASE WHEN so_luong_cay-? < 0 THEN 0
                         ELSE so_luong_cay-? END,
                mat_do_trong =
                    CASE WHEN dien_tich > 0
                         THEN (CASE WHEN so_luong_cay-? < 0 THEN 0
                                    ELSE so_luong_cay-? END)/dien_tich
                         ELSE mat_do_trong END,
                ngay_cap_nhat=GETDATE()
            WHERE id=?
            """;
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, giam);
            ps.setInt(2, giam);
            ps.setInt(3, giam);
            ps.setInt(4, giam);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatSauBenh(int id, boolean coSauBenh) {
        String sql = "UPDATE VuonTrong SET co_sau_benh=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection c = DBConnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, coSauBenh);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int timMatDoGanNhat(Connection c, double matDo) {
        String sql = "SELECT TOP 1 id FROM MatDoThamChieu ORDER BY ABS(mat_do_tu-?) ASC";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, matDo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
