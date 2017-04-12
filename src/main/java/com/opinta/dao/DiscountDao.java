package com.opinta.dao;

import java.util.List;
import java.util.UUID;

import com.opinta.entity.Discount;

public interface DiscountDao {
    
    Discount saveEntity(Discount entity);
    
    Discount getEntityByUuid(UUID uuid);
    
    List<Discount> getAllEntities();
    
    void delete(Discount entity);
}
