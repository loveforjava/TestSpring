package com.opinta.service;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.CounterpartyDto;

public interface CounterpartyService {

    List<Counterparty> getAllEntities();

    Counterparty getEntityById(UUID id);

    List<Counterparty> getEntityByPostcodePool(PostcodePool postcodePool);

    Counterparty saveEntity(Counterparty counterparty);
    
    List<CounterpartyDto> getAll();
    
    CounterpartyDto getById(UUID id);
    
    CounterpartyDto update(UUID id, CounterpartyDto source);
    
    CounterpartyDto save(CounterpartyDto counterparty);
    
    boolean delete(UUID id);
}
