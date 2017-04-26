package ua.ukrpost.dao;

import java.util.List;

import ua.ukrpost.entity.BarcodeInnerNumber;
import ua.ukrpost.entity.PostcodePool;

public interface BarcodeInnerNumberDao {
    
    List<BarcodeInnerNumber> getAll(PostcodePool postcodePool);
    
    BarcodeInnerNumber generateForPostcodePool(PostcodePool postcodePool);
    
    BarcodeInnerNumber getById(long id);
    
    BarcodeInnerNumber save(BarcodeInnerNumber barcodeInnerNumber);
    
    void update(BarcodeInnerNumber barcodeInnerNumber);
    
    void delete(BarcodeInnerNumber barcodeInnerNumber);
}
