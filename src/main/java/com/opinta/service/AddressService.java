package com.opinta.service;

import com.opinta.dto.AddressDto;
import java.util.List;

public interface AddressService {
    List<AddressDto> getAll();
    AddressDto getById(Long id);
    AddressDto save(AddressDto addressDto);
    AddressDto update(Long id, AddressDto addressDto);
    boolean delete(Long id);
}
