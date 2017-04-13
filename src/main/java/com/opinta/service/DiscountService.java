package com.opinta.service;

import com.opinta.exception.PerformProcessFailedException;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.DiscountDto;
import com.opinta.entity.Discount;
import com.opinta.exception.IncorrectInputDataException;

public interface DiscountService {

    List<Discount> getAllEntities();

    Discount getEntityByUuid(UUID uuid) throws IncorrectInputDataException;

    Discount saveEntity(Discount discount);

    Discount updateEntity(UUID uuid, Discount source) throws IncorrectInputDataException, PerformProcessFailedException;

    DiscountDto save(DiscountDto discountDto);

    DiscountDto update(UUID uuid, DiscountDto discountDto) throws PerformProcessFailedException,
            IncorrectInputDataException;

    void delete(UUID uuid) throws IncorrectInputDataException;
}
