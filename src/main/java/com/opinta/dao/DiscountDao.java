package com.opinta.dao;

import java.util.UUID;

import com.opinta.entity.Discount;

public interface DiscountDao {
    
    Discount saveEntity(Discount entity);
    
    Discount getEntityByUuid(UUID uuid);
    
    Discount getEntityZeroValue();
    
    Discount getEntityByValue(float value);
    
    void delete(Discount entity);
}
