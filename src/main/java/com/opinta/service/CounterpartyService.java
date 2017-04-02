package com.opinta.service;

import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.CounterpartyDto;

public interface CounterpartyService {

    List<Counterparty> getAllEntities();

    Counterparty getEntityByUuid(UUID uuid, User user) throws IncorrectInputDataException, AuthException;

    List<Counterparty> getEntityByPostcodePoolUuid(UUID postcodePoolUuid) throws IncorrectInputDataException;

    Counterparty saveEntity(Counterparty counterparty) throws IncorrectInputDataException;
    
    List<CounterpartyDto> getAll();

    CounterpartyDto getByUuid(UUID uuid, User user) throws IncorrectInputDataException, AuthException;
    
    CounterpartyDto update(UUID uuid, CounterpartyDto source, User user) throws IncorrectInputDataException,
            AuthException, PerformProcessFailedException;
    
    CounterpartyDto save(CounterpartyDto counterpartyDto) throws IncorrectInputDataException;
    
    void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
}
