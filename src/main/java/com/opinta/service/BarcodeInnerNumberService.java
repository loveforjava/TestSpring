package com.opinta.service;

import com.opinta.dto.BarcodeInnerNumberDto;

import java.util.List;

public interface BarcodeInnerNumberService {
    List<BarcodeInnerNumberDto> getAll();
    BarcodeInnerNumberDto getById(Long id);
    boolean save(long postcodeId, BarcodeInnerNumberDto barcodeInnerNumberDto);
    BarcodeInnerNumberDto update(Long id, BarcodeInnerNumberDto barcodeInnerNumberDto);
    boolean delete(Long id);
}
