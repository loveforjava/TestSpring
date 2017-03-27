package com.opinta.dao;

import com.opinta.entity.PostcodePool;
import java.util.List;

import com.opinta.entity.VirtualPostOffice;

public interface VirtualPostOfficeDao {
    
    List<VirtualPostOffice> getAll();
    
    VirtualPostOffice getById(long id);

    List<VirtualPostOffice> getByPostcodePool(PostcodePool postcodePool);

    VirtualPostOffice save(VirtualPostOffice virtualPostOffice);
    
    boolean update(VirtualPostOffice virtualPostOffice);
    
    boolean delete(VirtualPostOffice virtualPostOffice);
}
