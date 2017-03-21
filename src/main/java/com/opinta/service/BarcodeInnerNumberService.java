package com.opinta.service;

import com.opinta.dto.BarcodeInnerNumberDto;

import java.util.List;

public interface BarcodeInnerNumberService {
    List<BarcodeInnerNumberDto> getAll(long postcodeId);
    BarcodeInnerNumberDto getById(long id);
    BarcodeInnerNumberDto save(long postcodeId, BarcodeInnerNumberDto barcodeInnerNumberDto);
    BarcodeInnerNumberDto update(long id, BarcodeInnerNumberDto barcodeInnerNumberDto);
    boolean delete(long id);
}
