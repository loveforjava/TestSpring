package com.opinta.service;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.CounterpartyDto;

public interface CounterpartyService {

    List<Counterparty> getAllEntities();

    Counterparty getEntityByUuid(UUID uuid);

    List<Counterparty> getEntityByPostcodePool(PostcodePool postcodePool);

    Counterparty saveEntity(Counterparty counterparty);
    
    List<CounterpartyDto> getAll();
    
    CounterpartyDto getByUuid(UUID uuid);
    
    CounterpartyDto update(UUID uuid, CounterpartyDto source);
    
    CounterpartyDto save(CounterpartyDto counterparty);
    
    boolean delete(UUID uuid);
}
