package com.opinta.service;

import com.opinta.entity.User;
import java.util.List;

import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;
import javax.naming.AuthenticationException;

public interface ClientService {

    List<Client> getAllEntities(User user);

    Client getEntityById(long id, User user) throws AuthenticationException;

    Client getEntityByIdAnonymous(long id);

    Client saveEntity(Client client, User user) throws Exception;
    
    List<ClientDto> getAll(User user);

    List<ClientDto> getAllByCounterpartyId(long counterpartyId);

    ClientDto getById(long id, User user) throws AuthenticationException;
    
    ClientDto save(ClientDto client, User user) throws Exception;

    ClientDto update(long id, ClientDto source, User user) throws Exception;

    void delete(long id, User user) throws AuthenticationException;
}
