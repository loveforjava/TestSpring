package com.opinta.service;

import com.opinta.model.BarcodeInnerNumber;

import java.util.List;

public interface BarcodeInnerNumberService {
    List<BarcodeInnerNumber> getAll();
    BarcodeInnerNumber getById(Long id);
    void save(BarcodeInnerNumber barcodeInnerNumber);
    BarcodeInnerNumber update(Long id, BarcodeInnerNumber barcodeInnerNumber);
    boolean delete(Long id);
}
