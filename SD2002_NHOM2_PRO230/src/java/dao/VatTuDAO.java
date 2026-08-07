package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.VatTu;
import url.DBConnect;

public class VatTuDAO {
    
    public List<VatTu> getAll() {
        List<VatTu> list = new ArrayList<>();
        String sql = "SELECT * FROM VatTu";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String tenVatTu = rs.getString("ten_vat_tu");
                int loaiVatTuId = rs.getInt("loai_vat_tu_id");
                String hoatChat = rs.getString("hoat_chat") != null ? rs.getString("hoat_chat") : "";
                String doiTuongPhongTru = rs.getString("doi_tuong_phong_tru") != null ? rs.getString("doi_tuong_phong_tru") : "";
                int thoiGianCachLy = rs.getInt("thoi_gian_cach_ly");
                String donViTinh = rs.getString("don_vi_tinh");
                String quyCachDongGoi = rs.getString("quy_cach_dong_goi");
                double dienTichChiemDung = rs.getDouble("dien_tich_chiem_dung");
                int viTriLuuTruMacDinhId = rs.getInt("vi_tri_luu_tru_mac_dinh_id");
                double tonKhoHienTai = rs.getDouble("ton_kho_hien_tai");
                double tonKhoToiThieu = rs.getDouble("ton_kho_toi_thieu");
                double tonKhoToiDa = rs.getDouble("ton_kho_toi_da");
                String trangThai = rs.getString("trang_thai");

                VatTu vt = new VatTu(id, tenVatTu, loaiVatTuId, hoatChat, doiTuongPhongTru, 
                                     thoiGianCachLy, donViTinh, quyCachDongGoi, dienTichChiemDung, 
                                     viTriLuuTruMacDinhId, tonKhoHienTai, tonKhoToiThieu, tonKhoToiDa, trangThai);
                list.add(vt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(VatTu vt) {
        String sql = "INSERT INTO VatTu (ten_vat_tu, loai_vat_tu_id, hoat_chat, doi_tuong_phong_tru, thoi_gian_cach_ly, don_vi_tinh, quy_cach_dong_goi, dien_tich_chiem_dung, vi_tri_luu_tru_mac_dinh_id, ton_kho_hien_tai, ton_kho_toi_thieu, ton_kho_toi_da, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, vt.getTen_vat_tu());
            ps.setInt(2, vt.getLoai_vat_tu_id());
            ps.setNString(3, vt.getHoat_chat());
            ps.setNString(4, vt.getDoi_tuong_phong_tru());
            ps.setInt(5, vt.getThoi_gian_cach_ly());
            ps.setNString(6, vt.getDon_vi_tinh());
            ps.setNString(7, vt.getQuy_cach_dong_goi());
            ps.setDouble(8, vt.getDien_tich_chiem_dung());
            if(vt.getVi_tri_luu_tru_mac_dinh_id() > 0) ps.setInt(9, vt.getVi_tri_luu_tru_mac_dinh_id()); else ps.setNull(9, java.sql.Types.INTEGER);
            ps.setDouble(10, vt.getTon_kho_hien_tai());
            ps.setDouble(11, vt.getTon_kho_toi_thieu());
            ps.setDouble(12, vt.getTon_kho_toi_da());
            ps.setNString(13, vt.getTrang_thai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean update(VatTu vt) {
        String sql = "UPDATE VatTu SET ten_vat_tu=?, loai_vat_tu_id=?, hoat_chat=?, doi_tuong_phong_tru=?, thoi_gian_cach_ly=?, don_vi_tinh=?, quy_cach_dong_goi=?, dien_tich_chiem_dung=?, vi_tri_luu_tru_mac_dinh_id=?, ton_kho_toi_thieu=?, ton_kho_toi_da=?, trang_thai=?, ngay_cap_nhat=GETDATE() WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, vt.getTen_vat_tu());
            ps.setInt(2, vt.getLoai_vat_tu_id());
            ps.setNString(3, vt.getHoat_chat());
            ps.setNString(4, vt.getDoi_tuong_phong_tru());
            ps.setInt(5, vt.getThoi_gian_cach_ly());
            ps.setNString(6, vt.getDon_vi_tinh());
            ps.setNString(7, vt.getQuy_cach_dong_goi());
            ps.setDouble(8, vt.getDien_tich_chiem_dung());
            if(vt.getVi_tri_luu_tru_mac_dinh_id() > 0) ps.setInt(9, vt.getVi_tri_luu_tru_mac_dinh_id()); else ps.setNull(9, java.sql.Types.INTEGER);
            ps.setDouble(10, vt.getTon_kho_toi_thieu());
            ps.setDouble(11, vt.getTon_kho_toi_da());
            ps.setNString(12, vt.getTrang_thai());
            ps.setInt(13, vt.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM VatTu WHERE id=?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}