package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.LoHangVatTu;
import url.DBConnect;

public class LoHangVatTuDAO {
    
    public List<LoHangVatTu> getAllWithVatTuId() {
        List<LoHangVatTu> list = new ArrayList<>();
        // Kết hợp với ChiTietPhieuNhapKho để xác định Lô hàng này thuộc Vật tư nào
        String sql = "SELECT l.id, c.vat_tu_id, l.so_lo, l.ngay_san_xuat, l.han_su_dung, l.so_luong_con_lai " +
                     "FROM LoHangVatTu l " +
                     "JOIN ChiTietPhieuNhapKho c ON l.chi_tiet_phieu_nhap_id = c.id " +
                     "WHERE l.so_luong_con_lai > 0 " +
                     "ORDER BY l.han_su_dung ASC";
                     
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                LoHangVatTu lo = new LoHangVatTu();
                lo.setId(rs.getInt("id"));
                lo.setVat_tu_id(rs.getInt("vat_tu_id"));
                lo.setSo_lo(rs.getString("so_lo"));
                lo.setNgay_san_xuat(rs.getDate("ngay_san_xuat"));
                lo.setHan_su_dung(rs.getDate("han_su_dung"));
                lo.setSo_luong_con_lai(rs.getDouble("so_luong_con_lai"));
                list.add(lo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}