package com.opinta.service;

import java.util.List;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.model.BarcodeInnerNumber;
import com.opinta.model.PostcodePool;

public interface BarcodeInnerNumberService {
    
    List<BarcodeInnerNumberDto> getAll(long postcodeId);
    
    BarcodeInnerNumberDto getById(long id);
    
    BarcodeInnerNumber generateForPostcodePool(PostcodePool postcodePool);
    
    BarcodeInnerNumberDto save(long postcodeId, BarcodeInnerNumberDto barcodeInnerNumberDto);
    
    BarcodeInnerNumberDto update(long id, BarcodeInnerNumberDto barcodeInnerNumberDto);
    
    boolean delete(long id);
}
