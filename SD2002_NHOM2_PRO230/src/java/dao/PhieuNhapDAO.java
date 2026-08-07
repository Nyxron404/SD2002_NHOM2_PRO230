package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import models.PhieuNhap;
import models.ChiTietPhieuNhapKho;
import models.ChiTietPhieuNhapDungCu;
import url.DBConnect;

public class PhieuNhapDAO {

    // =========================================================================
    // 1. LƯU PHIẾU NHẬP VẬT TƯ (Bao gồm lô hàng)
    // =========================================================================
    public int insertPhieuNhap(PhieuNhap pnk, List<ChiTietPhieuNhapKho> listChiTiet) throws SQLException {
        String sqlPhieuNhap = "INSERT INTO PhieuNhap (ma_phieu_nhap, loai_phieu_nhap, nha_cung_cap_id, so_hoa_don, "
                + "mau_so, ky_hieu, ngay_hoa_don, ma_so_thue_ncc, tong_tien_hang, tien_thue_gtgt, "
                + "tong_thanh_toan, nguoi_mua_hang, nguoi_ban_hang, url_anh_hoa_don, nguoi_lap_phieu_id, "
                + "tong_dien_tich_tieu_ton, trang_thai, ghi_chu, ngay_tao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";

        String sqlChiTiet = "INSERT INTO ChiTietPhieuNhapKho (phieu_nhap_id, vat_tu_id, so_luong, "
                + "don_gia, thanh_tien, dien_tich_tieu_ton) VALUES (?, ?, ?, ?, ?, ?)";

        String sqlLoHang = "INSERT INTO LoHangVatTu (chi_tiet_phieu_nhap_id, so_lo, ngay_san_xuat, han_su_dung, "
                + "so_luong_nhap, so_luong_con_lai, vi_tri_luu_tru_id, trang_thai, ngay_tao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";

        String sqlCongTonKho = "UPDATE VatTu SET ton_kho_hien_tai = ton_kho_hien_tai + ?, ngay_cap_nhat = GETDATE() WHERE id = ?";

        if (listChiTiet == null || listChiTiet.isEmpty()) throw new SQLException("Phiếu nhập phải có ít nhất một dòng vật tư.");

        Connection conn = DBConnect.getConnection();
        if (conn == null) throw new SQLException("Không kết nối được DB.");

        try {
            conn.setAutoCommit(false);

            if (daTonTaiMaPhieu(conn, pnk.getMa_phieu_nhap())) throw new SQLException("Mã phiếu đã tồn tại.");
            int nguoiLapId = pnk.getNguoi_lap_phieu_id() > 0 ? pnk.getNguoi_lap_phieu_id() : layIdDauTien(conn, "TaiKhoan");
            int viTriMacDinh = layIdDauTien(conn, "DonViQuanLy");

            int phieuNhapId;
            try (PreparedStatement psPhieuNhap = conn.prepareStatement(sqlPhieuNhap, Statement.RETURN_GENERATED_KEYS)) {
                psPhieuNhap.setString(1, pnk.getMa_phieu_nhap());
                psPhieuNhap.setString(2, "Nhập Vật Tư");
                psPhieuNhap.setInt(3, pnk.getNha_cung_cap_id());
                psPhieuNhap.setString(4, pnk.getSo_hoa_don());
                psPhieuNhap.setString(5, pnk.getMau_so());
                psPhieuNhap.setString(6, pnk.getKy_hieu());
                psPhieuNhap.setDate(7, new java.sql.Date(pnk.getNgay_hoa_don().getTime()));
                psPhieuNhap.setString(8, pnk.getMa_so_thue_ncc());
                psPhieuNhap.setDouble(9, pnk.getTong_tien_hang());
                psPhieuNhap.setDouble(10, pnk.getTien_thue_gtgt());
                psPhieuNhap.setDouble(11, pnk.getTong_thanh_toan());
                psPhieuNhap.setString(12, pnk.getNguoi_mua_hang());
                psPhieuNhap.setString(13, pnk.getNguoi_ban_hang());
                psPhieuNhap.setString(14, pnk.getUrl_anh_hoa_don());
                psPhieuNhap.setInt(15, nguoiLapId);
                psPhieuNhap.setDouble(16, pnk.getTong_dien_tich_tieu_ton());
                psPhieuNhap.setString(17, "Đã nhập kho");
                psPhieuNhap.setNString(18, pnk.getGhi_chu());

                if (psPhieuNhap.executeUpdate() == 0) throw new SQLException("Lỗi thêm phiếu nhập.");
                try (ResultSet rsPK = psPhieuNhap.getGeneratedKeys()) {
                    if (!rsPK.next()) throw new SQLException("Lỗi lấy ID phiếu nhập.");
                    phieuNhapId = rsPK.getInt(1);
                }
            }

            try (PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psLoHang = conn.prepareStatement(sqlLoHang);
                 PreparedStatement psTonKho = conn.prepareStatement(sqlCongTonKho)) {

                for (int i = 0; i < listChiTiet.size(); i++) {
                    ChiTietPhieuNhapKho ct = listChiTiet.get(i);
                    psChiTiet.setInt(1, phieuNhapId);
                    psChiTiet.setInt(2, ct.getVat_tu_id());
                    psChiTiet.setDouble(3, ct.getSo_luong());
                    psChiTiet.setDouble(4, ct.getDon_gia());
                    psChiTiet.setDouble(5, ct.getThanh_tien());
                    psChiTiet.setDouble(6, ct.getDien_tich_tieu_ton());
                    psChiTiet.executeUpdate();

                    int chiTietId;
                    try (ResultSet rsChiTietKey = psChiTiet.getGeneratedKeys()) {
                        if (!rsChiTietKey.next()) throw new SQLException("Lỗi ID chi tiết.");
                        chiTietId = rsChiTietKey.getInt(1);
                    }

                    int viTri = ct.getVi_tri_luu_tru_id() > 0 ? ct.getVi_tri_luu_tru_id() : viTriMacDinh;
                    psLoHang.setInt(1, chiTietId);
                    psLoHang.setString(2, "LO" + System.currentTimeMillis() + "-" + (i + 1));
                    psLoHang.setDate(3, toSqlDate(ct.getNgay_san_xuat(), new java.sql.Date(System.currentTimeMillis())));
                    
                    java.sql.Date hsd = toSqlDate(ct.getHan_su_dung(), null);
                    if (hsd != null) psLoHang.setDate(4, hsd);
                    else psLoHang.setNull(4, java.sql.Types.DATE);

                    psLoHang.setDouble(5, ct.getSo_luong());
                    psLoHang.setDouble(6, ct.getSo_luong());
                    psLoHang.setInt(7, viTri);
                    psLoHang.setString(8, "Chưa xuất");
                    psLoHang.executeUpdate();

                    psTonKho.setDouble(1, ct.getSo_luong());
                    psTonKho.setInt(2, ct.getVat_tu_id());
                    psTonKho.executeUpdate();
                }
            }
            conn.commit();
            return phieuNhapId;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { }
            throw e;
        } finally {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { }
        }
    }

    // =========================================================================
    // 2. LƯU PHIẾU NHẬP DỤNG CỤ (Không cần Lô hàng)
    // =========================================================================
    public int insertPhieuNhapDungCu(PhieuNhap pnk, List<ChiTietPhieuNhapDungCu> listChiTiet) throws SQLException {
        String sqlPhieuNhap = "INSERT INTO PhieuNhap (ma_phieu_nhap, loai_phieu_nhap, nha_cung_cap_id, so_hoa_don, "
                + "mau_so, ky_hieu, ngay_hoa_don, ma_so_thue_ncc, tong_tien_hang, tien_thue_gtgt, "
                + "tong_thanh_toan, nguoi_mua_hang, nguoi_ban_hang, url_anh_hoa_don, nguoi_lap_phieu_id, "
                + "tong_dien_tich_tieu_ton, trang_thai, ghi_chu, ngay_tao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";

        String sqlChiTiet = "INSERT INTO ChiTietPhieuNhapDungCu (phieu_nhap_id, dung_cu_id, so_luong, "
                + "don_gia, thanh_tien, canh_bao_vuot_dien_tich) VALUES (?, ?, ?, ?, ?, ?)";

        String sqlCongTonKho = "UPDATE DungCu SET ton_kho_hien_tai = ton_kho_hien_tai + ?, ngay_cap_nhat = GETDATE() WHERE id = ?";

        if (listChiTiet == null || listChiTiet.isEmpty()) throw new SQLException("Phiếu nhập phải có dòng chi tiết.");

        Connection conn = DBConnect.getConnection();
        if (conn == null) throw new SQLException("Không kết nối được DB.");

        try {
            conn.setAutoCommit(false);

            if (daTonTaiMaPhieu(conn, pnk.getMa_phieu_nhap())) throw new SQLException("Mã phiếu đã tồn tại.");
            int nguoiLapId = pnk.getNguoi_lap_phieu_id() > 0 ? pnk.getNguoi_lap_phieu_id() : layIdDauTien(conn, "TaiKhoan");

            int phieuNhapId;
            try (PreparedStatement psPhieuNhap = conn.prepareStatement(sqlPhieuNhap, Statement.RETURN_GENERATED_KEYS)) {
                psPhieuNhap.setString(1, pnk.getMa_phieu_nhap());
                psPhieuNhap.setString(2, "Nhập Dụng Cụ");
                psPhieuNhap.setInt(3, pnk.getNha_cung_cap_id());
                psPhieuNhap.setString(4, pnk.getSo_hoa_don());
                psPhieuNhap.setString(5, pnk.getMau_so());
                psPhieuNhap.setString(6, pnk.getKy_hieu());
                psPhieuNhap.setDate(7, new java.sql.Date(pnk.getNgay_hoa_don().getTime()));
                psPhieuNhap.setString(8, pnk.getMa_so_thue_ncc());
                psPhieuNhap.setDouble(9, pnk.getTong_tien_hang());
                psPhieuNhap.setDouble(10, pnk.getTien_thue_gtgt());
                psPhieuNhap.setDouble(11, pnk.getTong_thanh_toan());
                psPhieuNhap.setString(12, pnk.getNguoi_mua_hang());
                psPhieuNhap.setString(13, pnk.getNguoi_ban_hang());
                psPhieuNhap.setString(14, pnk.getUrl_anh_hoa_don());
                psPhieuNhap.setInt(15, nguoiLapId);
                psPhieuNhap.setDouble(16, pnk.getTong_dien_tich_tieu_ton());
                psPhieuNhap.setString(17, "Đã nhập khu");
                psPhieuNhap.setNString(18, pnk.getGhi_chu());

                if (psPhieuNhap.executeUpdate() == 0) throw new SQLException("Lỗi thêm phiếu nhập.");
                try (ResultSet rsPK = psPhieuNhap.getGeneratedKeys()) {
                    if (!rsPK.next()) throw new SQLException("Lỗi lấy ID.");
                    phieuNhapId = rsPK.getInt(1);
                }
            }

            try (PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet);
                 PreparedStatement psTonKho = conn.prepareStatement(sqlCongTonKho)) {

                for (ChiTietPhieuNhapDungCu ct : listChiTiet) {
                    psChiTiet.setInt(1, phieuNhapId);
                    psChiTiet.setInt(2, ct.getDung_cu_id());
                    psChiTiet.setDouble(3, ct.getSo_luong());
                    psChiTiet.setDouble(4, ct.getDon_gia());
                    psChiTiet.setDouble(5, ct.getThanh_tien());
                    psChiTiet.setBoolean(6, ct.isCanh_bao_vuot_dien_tich());
                    psChiTiet.executeUpdate();

                    // Cộng tồn kho dụng cụ
                    psTonKho.setDouble(1, ct.getSo_luong());
                    psTonKho.setInt(2, ct.getDung_cu_id());
                    psTonKho.executeUpdate();
                }
            }
            conn.commit();
            return phieuNhapId;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { }
            throw e;
        } finally {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { }
        }
    }

    // ======================= CÁC HÀM TIỆN ÍCH =======================
    private java.sql.Date toSqlDate(String value, java.sql.Date macDinh) {
        if (value == null || value.trim().isEmpty()) return macDinh;
        try { return java.sql.Date.valueOf(value.trim()); } 
        catch (IllegalArgumentException e) { return macDinh; }
    }

    private boolean daTonTaiMaPhieu(Connection conn, String maPhieu) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM PhieuNhap WHERE ma_phieu_nhap = ?")) {
            ps.setString(1, maPhieu);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private int layIdDauTien(Connection conn, String tenBang) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT TOP 1 id FROM " + tenBang + " ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : -1;
        }
    }
}