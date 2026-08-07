package service;

import java.util.List;
import dao.VatTuDAO;
import models.VatTu;

public class VatTuService {
    private VatTuDAO vatTuDAO = new VatTuDAO();
    
    public List<VatTu> getAllVatTu() { return vatTuDAO.getAll(); }
    public boolean addVatTu(VatTu vt) { return vatTuDAO.insert(vt); }
    public boolean updateVatTu(VatTu vt) { return vatTuDAO.update(vt); }
    public boolean deleteVatTu(int id) { return vatTuDAO.delete(id); }
}