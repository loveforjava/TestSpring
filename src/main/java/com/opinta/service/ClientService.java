package com.opinta.service;

import com.opinta.entity.Client;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.ClientDto;

public interface ClientService {

    List<Client> getAllEntities(User user);

    Client getEntityByUuid(UUID uuid, User user) throws IncorrectInputDataException, AuthException;

    Client getEntityByUuidAnonymous(UUID uuid) throws IncorrectInputDataException;

    Client saveEntityAsRecipient(Client client, User user) throws IncorrectInputDataException, AuthException;

    Client saveEntityAsSender(Client client, User user) throws IncorrectInputDataException, AuthException;
    
    List<ClientDto> getAll(User user);

    List<ClientDto> getAllByCounterpartyUuid(UUID counterpartyUuid, User user)
            throws IncorrectInputDataException, AuthException;

    ClientDto getByUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
    
    ClientDto save(ClientDto client, User user) throws AuthException, IncorrectInputDataException;

    ClientDto saveAsSender(ClientDto clientDto, User user) throws AuthException, IncorrectInputDataException;

    ClientDto update(UUID uuid, ClientDto source, User user) throws AuthException, IncorrectInputDataException,
            PerformProcessFailedException;

    void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
}
