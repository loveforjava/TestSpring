package com.opinta.service;

import com.opinta.exception.IncorrectInputDataException;
import java.util.List;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;
import java.util.UUID;

public interface BarcodeInnerNumberService {

    BarcodeInnerNumber getEntityById(long id) throws IncorrectInputDataException;

    List<BarcodeInnerNumberDto> getAll(UUID postcodePoolUuid) throws IncorrectInputDataException;
    
    BarcodeInnerNumberDto getById(long id) throws IncorrectInputDataException;
    
    void delete(long id) throws IncorrectInputDataException;

    BarcodeInnerNumber generateBarcodeInnerNumber(PostcodePool postcodePool);
}
