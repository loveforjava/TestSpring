package ua.ukrpost.service;

import ua.ukrpost.dto.postid.ClientTypeDto;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import java.util.List;
import java.util.UUID;

import ua.ukrpost.dto.ClientDto;
import ua.ukrpost.entity.Client;

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
