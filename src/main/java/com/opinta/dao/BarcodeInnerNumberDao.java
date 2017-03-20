package com.opinta.dao;

import com.opinta.model.BarcodeInnerNumber;

import java.util.List;

public interface BarcodeInnerNumberDao {
    List<BarcodeInnerNumber> getAll(long postcodeId);
    BarcodeInnerNumber getById(Long id);
    BarcodeInnerNumber save(BarcodeInnerNumber barcodeInnerNumber);
    void update(BarcodeInnerNumber barcodeInnerNumber);
    void delete(BarcodeInnerNumber barcodeInnerNumber);
}
