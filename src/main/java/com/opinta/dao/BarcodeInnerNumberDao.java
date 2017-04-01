package com.opinta.dao;

import java.util.List;

import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;

public interface BarcodeInnerNumberDao {
    
    List<BarcodeInnerNumber> getAll(PostcodePool postcodePool);
    
    BarcodeInnerNumber generateForPostcodePool(PostcodePool postcodePool);
    
    BarcodeInnerNumber getById(long id);
    
    BarcodeInnerNumber save(BarcodeInnerNumber barcodeInnerNumber);
    
    void update(BarcodeInnerNumber barcodeInnerNumber);
    
    void delete(BarcodeInnerNumber barcodeInnerNumber);
}
