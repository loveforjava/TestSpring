package com.opinta.service;

import java.util.List;

import com.opinta.dto.BarcodeInnerNumberDto;

public interface BarcodeInnerNumberService {
    
    List<BarcodeInnerNumberDto> getAll(long postcodeId);
    
    BarcodeInnerNumberDto getById(long id);
    
    BarcodeInnerNumberDto save(long postcodeId, BarcodeInnerNumberDto barcodeInnerNumberDto);
    
    BarcodeInnerNumberDto update(long id, BarcodeInnerNumberDto barcodeInnerNumberDto);
    
    boolean delete(long id);
}
