package com.opinta.dao;

import java.util.UUID;

import com.opinta.entity.Counterparty;
import com.opinta.entity.DiscountPerCounterparty;

public interface DiscountPerCounterpartyDao {
    
    DiscountPerCounterparty saveEntity(DiscountPerCounterparty entity);
    
    DiscountPerCounterparty getEntityByUuid(UUID uuid);
    
    DiscountPerCounterparty getEntityByCounterparty(Counterparty counterparty);
    
    void delete(DiscountPerCounterparty entity);
}
