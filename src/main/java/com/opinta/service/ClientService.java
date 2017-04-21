package com.opinta.service;

import com.opinta.dto.postid.ClientTypeDto;
import com.opinta.entity.ClientType;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;

public interface ClientService {

    List<Client> getAllEntities(User user);

    List<Client> getAllEntitiesByCounterpartyUuid(UUID counterpartyUuid, User user)
            throws IncorrectInputDataException, AuthException;
    
    Client saveOrGetEntity(Client client, User user) throws IncorrectInputDataException, AuthException;

    Client saveOrGetEntityAnonymous(Client recipient, User user) throws IncorrectInputDataException, AuthException;

    Client getEntityByUuid(UUID uuid, User user) throws IncorrectInputDataException, AuthException;
    
    Client getEntityByPostId(String postId, User user) throws IncorrectInputDataException, AuthException;
    
    Client getEntityByUuidAnonymous(UUID uuid) throws IncorrectInputDataException;

    Client saveEntity(Client client, User user) throws IncorrectInputDataException, AuthException;
        
    Client updateEntity(Client client, User user) throws IncorrectInputDataException, AuthException;
    
    List<ClientDto> getAll(User user);

    List<ClientDto> getAllByCounterpartyUuid(UUID counterpartyUuid, User user)
            throws IncorrectInputDataException, AuthException;

    ClientDto getByUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
    
    ClientDto save(ClientDto client, User user) throws AuthException, IncorrectInputDataException;

    ClientDto update(UUID uuid, ClientDto source, User user) throws AuthException, IncorrectInputDataException,
            PerformProcessFailedException;
    
    ClientDto updatePostId(UUID uuid, ClientTypeDto clientTypeDto, User user)
            throws IncorrectInputDataException, AuthException, PerformProcessFailedException;
    
    void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
}
