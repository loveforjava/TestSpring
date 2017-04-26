package ua.ukrpost.service;

import ua.ukrpost.entity.Address;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import java.util.List;

import ua.ukrpost.dto.AddressDto;

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
