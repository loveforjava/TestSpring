package com.opinta.service;

import java.util.List;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;

public interface BarcodeInnerNumberService {
    
    List<BarcodeInnerNumberDto> getAll(long postcodeId);
    
    BarcodeInnerNumberDto getById(long id);
    
    BarcodeInnerNumberDto save(long postcodeId, BarcodeInnerNumberDto barcodeInnerNumberDto);
    
    BarcodeInnerNumberDto update(long id, BarcodeInnerNumberDto barcodeInnerNumberDto);
    
    boolean delete(long id);

    BarcodeInnerNumber generateBarcodeInnerNumber(PostcodePool postcodePool);
}
