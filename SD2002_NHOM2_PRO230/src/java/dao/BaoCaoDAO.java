package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import models.VatTu;
import models.LoHangVatTu;
import url.DBConnect;

public class BaoCaoDAO {

    public Map<String, Double> getChiPhiVatTuByDateRange(String startDate, String endDate) {
        Map<String, Double> chiPhiVatTu = new HashMap<>();
        String sql = "SELECT lvt.ten_loai, SUM(ctvt.thanh_tien) as tong_chi_phi " +
                     "FROM ChiTietVatTuTieuHao ctvt " +
                     "JOIN NhatKyChamSoc nkcs ON ctvt.nguon_tieu_hao_id = nkcs.id " +
                     "JOIN VatTu vt ON ctvt.vat_tu_id = vt.id " +
                     "JOIN LoaiVatTu lvt ON vt.loai_vat_tu_id = lvt.id " +
                     "WHERE nkcs.ngay_thuc_hien BETWEEN ? AND ? " +
                     "GROUP BY lvt.ten_loai";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                chiPhiVatTu.put(rs.getString("ten_loai"), rs.getDouble("tong_chi_phi"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return chiPhiVatTu;
    }
    
    public List<VatTu> getVatTuDuoiNguong() {
        List<VatTu> list = new ArrayList<>();
        String sql = "SELECT * FROM VatTu WHERE ton_kho_hien_tai <= ton_kho_toi_thieu";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                VatTu vt = new VatTu();
                vt.setId(rs.getInt("id"));
                vt.setTen_vat_tu(rs.getString("ten_vat_tu"));
                vt.setDon_vi_tinh(rs.getString("don_vi_tinh"));
                vt.setTon_kho_hien_tai(rs.getDouble("ton_kho_hien_tai"));
                vt.setTon_kho_toi_thieu(rs.getDouble("ton_kho_toi_thieu"));
                list.add(vt);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<LoHangVatTu> getLoHangSapHetHan() {
        List<LoHangVatTu> list = new ArrayList<>();
        String sql = "SELECT lhvt.*, vt.ten_vat_tu FROM LoHangVatTu lhvt " +
                     "JOIN ChiTietPhieuNhapKho ctpnk ON lhvt.chi_tiet_phieu_nhap_id = ctpnk.id " +
                     "JOIN VatTu vt ON ctpnk.vat_tu_id = vt.id " +
                     "WHERE lhvt.han_su_dung BETWEEN GETDATE() AND DATEADD(day, 30, GETDATE()) " +
                     "AND lhvt.so_luong_con_lai > 0";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                LoHangVatTu lo = new LoHangVatTu();
                lo.setId(rs.getInt("id"));
                lo.setSo_lo(rs.getString("so_lo"));
                lo.setHan_su_dung(rs.getDate("han_su_dung"));
                lo.setSo_luong_con_lai(rs.getDouble("so_luong_con_lai"));
                list.add(lo);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // HÀM MỚI: Lấy danh sách lô hàng ĐÃ hết hạn
    public List<LoHangVatTu> getLoHangDaHetHan() {
        List<LoHangVatTu> list = new ArrayList<>();
        String sql = "SELECT lhvt.*, vt.ten_vat_tu FROM LoHangVatTu lhvt " +
                     "JOIN ChiTietPhieuNhapKho ctpnk ON lhvt.chi_tiet_phieu_nhap_id = ctpnk.id " +
                     "JOIN VatTu vt ON ctpnk.vat_tu_id = vt.id " +
                     "WHERE lhvt.han_su_dung < CAST(GETDATE() AS DATE) " +
                     "AND lhvt.so_luong_con_lai > 0";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) {
                LoHangVatTu lo = new LoHangVatTu();
                lo.setId(rs.getInt("id"));
                lo.setSo_lo(rs.getString("so_lo"));
                lo.setHan_su_dung(rs.getDate("han_su_dung"));
                lo.setSo_luong_con_lai(rs.getDouble("so_luong_con_lai"));
                list.add(lo);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}