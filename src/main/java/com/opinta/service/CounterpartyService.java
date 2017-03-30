package com.opinta.service;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.User;
import java.util.List;

import com.opinta.dto.CounterpartyDto;

public interface CounterpartyService {

    List<Counterparty> getAllEntities();

    Counterparty getEntityById(long id);

    List<Counterparty> getEntityByPostcodePool(PostcodePool postcodePool);

    Counterparty saveEntity(Counterparty counterparty) throws Exception;
    
    List<CounterpartyDto> getAll();
    
    CounterpartyDto getById(long id);
    
    CounterpartyDto update(long id, CounterpartyDto source, User user) throws Exception;
    
    CounterpartyDto save(CounterpartyDto counterparty) throws Exception;

    void delete(long id, User user) throws Exception;
}
