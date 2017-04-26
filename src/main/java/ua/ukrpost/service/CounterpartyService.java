package ua.ukrpost.service;

import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import java.util.List;
import java.util.UUID;

import ua.ukrpost.dto.CounterpartyDto;

public interface CounterpartyService {

    List<Counterparty> getAllEntities();

    Counterparty getEntityByUuid(UUID uuid, User user) throws IncorrectInputDataException, AuthException;

    Counterparty getEntityByUuidAnonymous(UUID uuid) throws IncorrectInputDataException;
    
    List<Counterparty> getAllEntitiesByPostcodePoolUuid(UUID postcodePoolUuid) throws IncorrectInputDataException;

    Counterparty saveEntity(Counterparty counterparty) throws IncorrectInputDataException;
    
    List<CounterpartyDto> getAll();

    CounterpartyDto getByUuid(UUID uuid, User user) throws IncorrectInputDataException, AuthException;
    
    CounterpartyDto update(UUID uuid, CounterpartyDto source, User user) throws IncorrectInputDataException,
            AuthException, PerformProcessFailedException;
    
    CounterpartyDto save(CounterpartyDto counterpartyDto) throws IncorrectInputDataException;

    void delete(UUID uuid) throws IncorrectInputDataException;
}
