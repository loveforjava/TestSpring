package com.opinta.service;

import java.util.Optional;
import java.util.UUID;

import com.opinta.dto.DiscountDto;
import com.opinta.entity.Discount;
import com.opinta.exception.IncorrectInputDataException;

public interface DiscountService {
    
    Discount saveEntity(Discount discount);
    
    Discount getEntityByUuid(UUID uuid) throws IncorrectInputDataException;
    
    Discount getEntityZeroValue();
    
    Optional<Discount> getEntityByValue(float value);
    
    DiscountDto save(DiscountDto dto);
    
    void delete(UUID uuid) throws IncorrectInputDataException;
}
