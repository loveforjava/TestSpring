package com.opinta.dao;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import java.util.List;

import com.opinta.entity.Counterparty;

public interface CounterpartyDao {
    
    List<Counterparty> getAll();
    
    Counterparty getById(long id);

    List<Counterparty> getByPostcodePool(PostcodePool postcodePool);

    Counterparty save(Counterparty counterparty);
    
    boolean update(Counterparty counterparty);
    
    boolean delete(Counterparty counterparty);
}
