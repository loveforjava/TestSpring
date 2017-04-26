package ua.ukrpost.service;

import ua.ukrpost.exception.PerformProcessFailedException;
import java.util.List;
import java.util.UUID;

import ua.ukrpost.dto.DiscountDto;
import ua.ukrpost.entity.Discount;
import ua.ukrpost.exception.IncorrectInputDataException;

public interface DiscountService {

    List<Discount> getAllEntities();

    Discount getEntityByUuid(UUID uuid) throws IncorrectInputDataException;

    Discount saveEntity(Discount discount);

    Discount updateEntity(UUID uuid, Discount source) throws IncorrectInputDataException, PerformProcessFailedException;
    
    List<DiscountDto> getAll();
    
    DiscountDto getByUuid(UUID uuid) throws IncorrectInputDataException;
    
    DiscountDto save(DiscountDto discountDto);

    DiscountDto update(UUID uuid, DiscountDto discountDto) throws PerformProcessFailedException,
            IncorrectInputDataException;

    void delete(UUID uuid) throws IncorrectInputDataException;
}
