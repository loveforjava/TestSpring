package com.opinta.dao;

import java.util.List;

import com.opinta.entity.BarcodeInnerNumber;

public interface BarcodeInnerNumberDao {
    
    List<BarcodeInnerNumber> getAll(long postcodeId);
    
    BarcodeInnerNumber getById(long id);
    
    BarcodeInnerNumber save(BarcodeInnerNumber barcodeInnerNumber);
    
    void update(BarcodeInnerNumber barcodeInnerNumber);
    
    void delete(BarcodeInnerNumber barcodeInnerNumber);
}
