package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import models.MatDoThamChieu;
import url.DBConnect;

/**
 * Bảng tham chiếu kỹ thuật mật độ trồng (cây/ha) cho UC-4.2.
 */
public class MatDoThamChieuDAO {

    public List<MatDoThamChieu> getAll() {
        List<MatDoThamChieu> list = new ArrayList<>();
        String sql = "SELECT * FROM MatDoThamChieu ORDER BY mat_do_tu ASC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MatDoThamChieu m = new MatDoThamChieu();
                m.setId(rs.getInt("id"));
                m.setPhan_loai(rs.getNString("phan_loai"));
                m.setMat_do_tu(rs.getDouble("mat_do_tu"));
                double den = rs.getDouble("mat_do_den");
                m.setMat_do_den(rs.wasNull() ? null : den);
                m.setDac_diem_rui_ro(rs.getNString("dac_diem_rui_ro"));
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đối chiếu mật độ (cây/ha) với bảng tham chiếu, trả về dòng phân loại phù hợp.
     * Trả null nếu không có dòng nào chứa mật độ này (mật độ bất thường).
     */
    public MatDoThamChieu phanLoai(double matDo) {
        for (MatDoThamChieu m : getAll()) {
            if (m.chuaMatDo(matDo)) return m;
        }
        return null;
    }
}
