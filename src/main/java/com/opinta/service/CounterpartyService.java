package com.opinta.service;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.User;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.CounterpartyDto;
import javax.transaction.Transactional;

public interface CounterpartyService {

    List<Counterparty> getAllEntities();

    Counterparty getEntityByUuid(UUID uuid);

    List<Counterparty> getEntityByPostcodePool(PostcodePool postcodePool);

    Counterparty saveEntity(Counterparty counterparty) throws Exception;
    
    List<CounterpartyDto> getAll();
    
    CounterpartyDto getByUuid(UUID uuid);
    
    CounterpartyDto update(UUID uuid, CounterpartyDto source, User user) throws Exception;
    
    CounterpartyDto save(CounterpartyDto counterpartyDto) throws Exception;
    
    void delete(UUID uuid, User user) throws Exception;
}
