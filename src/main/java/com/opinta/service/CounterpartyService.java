package com.opinta.service;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import java.util.List;

import com.opinta.dto.CounterpartyDto;

public interface CounterpartyService {

    List<Counterparty> getAllEntities();

    Counterparty getEntityById(long id);

    List<Counterparty> getEntityByPostcodePool(PostcodePool postcodePool);

    Counterparty saveEntity(Counterparty counterparty);
    
    List<CounterpartyDto> getAll();
    
    CounterpartyDto getById(long id);
    
    CounterpartyDto update(long id, CounterpartyDto source);
    
    CounterpartyDto save(CounterpartyDto counterparty);
    
    boolean delete(long id);
}
