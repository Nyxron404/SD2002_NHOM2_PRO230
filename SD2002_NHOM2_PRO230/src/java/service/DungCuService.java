package service;

import java.util.List;
import dao.DungCuDAO;
import models.DungCu;

public class DungCuService {
    private DungCuDAO dungCuDAO = new DungCuDAO();
    
    public List<DungCu> getAllDungCu() { 
        return dungCuDAO.getAll(); 
    }

    // Cập nhật hàm này
    public boolean ghiNhanHaoHut(int id, double soLuongMat, double chiPhiThietHai, String lyDo) {
        return dungCuDAO.updateHaoHut(id, soLuongMat, chiPhiThietHai, lyDo);
    }
}