package com.opinta.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.opinta.dto.DiscountDto;
import com.opinta.entity.Discount;
import com.opinta.exception.IncorrectInputDataException;

public interface DiscountService {
    
    Discount saveEntity(Discount discount);
    
    Discount getEntityByUuid(UUID uuid) throws IncorrectInputDataException;
    
    List<Discount> getAllEntities();
    
    DiscountDto save(DiscountDto dto);
    
    void deleteByUuid(UUID uuid) throws IncorrectInputDataException;
}
