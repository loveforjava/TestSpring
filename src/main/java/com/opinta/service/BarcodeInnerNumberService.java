package com.opinta.service;

import java.util.List;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;

public interface BarcodeInnerNumberService {

    BarcodeInnerNumber getEntityById(long id);

    List<BarcodeInnerNumberDto> getAll(long postcodeId);
    
    BarcodeInnerNumberDto getById(long id);
    
    boolean delete(long id);

    BarcodeInnerNumber generateBarcodeInnerNumber(PostcodePool postcodePool);
}
