package com.opinta.dao;

import java.util.List;

import com.opinta.model.VirtualPostOffice;

public interface VirtualPostOfficeDao {
    
    List<VirtualPostOffice> getAll();
    
    VirtualPostOffice getById(long id);
    
    VirtualPostOffice save(VirtualPostOffice virtualPostOffice);
    
    boolean update(VirtualPostOffice virtualPostOffice);
    
    boolean delete(VirtualPostOffice virtualPostOffice);
}
