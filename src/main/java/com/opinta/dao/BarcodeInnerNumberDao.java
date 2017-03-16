package com.opinta.dao;

import com.opinta.model.BarcodeInnerNumber;

import java.util.List;

public interface BarcodeInnerNumberDao {
    List<BarcodeInnerNumber> getAll();
    BarcodeInnerNumber getById(Long id);
    void save(BarcodeInnerNumber barcodeInnerNumber);
    void update(BarcodeInnerNumber barcodeInnerNumber);
    void delete(BarcodeInnerNumber barcodeInnerNumber);
}
