package com.opinta.service;

import com.opinta.entity.User;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;
import javax.naming.AuthenticationException;

public interface ClientService {

    List<Client> getAllEntities(User user);

    Client getEntityByUuid(UUID uuid, User user) throws AuthenticationException;
    
    Client getEntityByUuidAnonymous(UUID uuid);

    Client saveEntity(Client client, User user) throws Exception;
    
    List<ClientDto> getAll(User user);

    List<ClientDto> getAllByCounterpartyUuid(UUID counterpartyUuid);

    ClientDto getByUuid(UUID uuid, User user) throws AuthenticationException;
    
    ClientDto save(ClientDto client, User user) throws Exception;

    ClientDto update(UUID uuid, ClientDto source, User user) throws Exception;

    void delete(UUID uuid, User user) throws AuthenticationException;
}
