package com.opinta.service;

import java.util.List;

import com.opinta.dto.AddressDto;

public interface AddressService {
    List<AddressDto> getAll();
    AddressDto getById(Long id);
    AddressDto save(AddressDto addressDto);
    AddressDto update(Long id, AddressDto addressDto);
    boolean delete(Long id);
}
