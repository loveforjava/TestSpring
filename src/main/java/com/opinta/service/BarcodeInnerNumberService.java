package com.opinta.service;

import java.util.List;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;
import java.util.UUID;

public interface BarcodeInnerNumberService {

    BarcodeInnerNumber getEntityById(long id);

    List<BarcodeInnerNumberDto> getAll(UUID postcodePoolUuid);
    
    BarcodeInnerNumberDto getById(long id);
    
    boolean delete(long id);

    BarcodeInnerNumber generateBarcodeInnerNumber(PostcodePool postcodePool);
}
