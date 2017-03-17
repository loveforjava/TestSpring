package com.opinta.dao;

import com.opinta.model.VirtualPostOffice;
import java.util.List;

public interface VirtualPostOfficeDao {
    List<VirtualPostOffice> getAll();
    VirtualPostOffice getById(Long id);
    void save(VirtualPostOffice virtualPostOffice);
    void update(VirtualPostOffice virtualPostOffice);
    void delete(VirtualPostOffice virtualPostOffice);
}
