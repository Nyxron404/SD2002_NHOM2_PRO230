package service;

import dao.ThietBiDAO;
import java.util.List;
import models.ThietBi;

public class ThietBiService {
    private ThietBiDAO thietBiDAO = new ThietBiDAO();

    public List<ThietBi> getAllThietBi() {
        return thietBiDAO.getAll();
    }
    
    public String insertThietBi(ThietBi tb) {
        if (tb.getMa_thiet_bi() == null || tb.getMa_thiet_bi().trim().isEmpty()) {
            return "Mã thiết bị không được để trống!";
        }
        if (tb.getTen_thiet_bi() == null || tb.getTen_thiet_bi().trim().isEmpty()) {
            return "Tên thiết bị không được để trống!";
        }
        
        if (thietBiDAO.isMaThietBiExist(tb.getMa_thiet_bi())) {
            return "Mã thiết bị [" + tb.getMa_thiet_bi() + "] đã tồn tại trong hệ thống!";
        }
        
        boolean isSuccess = thietBiDAO.insert(tb);
        return isSuccess ? "SUCCESS" : "Lỗi hệ thống: Không thể lưu vào cơ sở dữ liệu!";
    }
}