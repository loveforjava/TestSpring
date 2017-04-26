package ua.ukrpost.service;

import ua.ukrpost.exception.IncorrectInputDataException;
import java.util.List;

import ua.ukrpost.dto.BarcodeInnerNumberDto;
import ua.ukrpost.entity.BarcodeInnerNumber;
import ua.ukrpost.entity.PostcodePool;

import java.util.UUID;

public interface BarcodeInnerNumberService {

    BarcodeInnerNumber getEntityById(long id) throws IncorrectInputDataException;

    List<BarcodeInnerNumberDto> getAll(UUID postcodePoolUuid) throws IncorrectInputDataException;
    
    BarcodeInnerNumberDto getById(long id) throws IncorrectInputDataException;
    
    void delete(long id) throws IncorrectInputDataException;

    BarcodeInnerNumber generateBarcodeInnerNumber(PostcodePool postcodePool);
}
