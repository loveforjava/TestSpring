package com.opinta.service;

import com.opinta.entity.Address;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;

import com.opinta.dto.AddressDto;

public interface AddressService {

    List<Address> getAllEntities();

    Address getEntityById(long id) throws IncorrectInputDataException;

    Address saveEntity(Address address);

    Address updateEntity(long id, Address address) throws IncorrectInputDataException, PerformProcessFailedException;

    List<AddressDto> getAll();

    AddressDto getById(long id) throws IncorrectInputDataException;

    AddressDto save(AddressDto addressDto);

    AddressDto update(long id, AddressDto addressDto) throws PerformProcessFailedException, IncorrectInputDataException;

    void delete(long id) throws IncorrectInputDataException;
}
