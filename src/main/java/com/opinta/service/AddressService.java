package com.opinta.service;

import java.util.List;

import com.opinta.dto.AddressDto;

public interface AddressService {
    List<AddressDto> getAll();
    AddressDto getById(long id);
    AddressDto save(AddressDto addressDto);
    AddressDto update(long id, AddressDto addressDto);
    boolean delete(long id);
}
