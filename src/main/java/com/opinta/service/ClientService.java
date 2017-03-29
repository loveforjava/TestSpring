package com.opinta.service;

import java.util.List;
import java.util.UUID;

import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;

public interface ClientService {

    List<Client> getAllEntities();

    Client getEntityById(UUID id);

    Client saveEntity(Client client);
    
    List<ClientDto> getAll();

    List<ClientDto> getAllByCounterpartyId(UUID counterpartyId);

    ClientDto getById(UUID id);
    
    ClientDto save(ClientDto client) throws Exception;

    ClientDto update(UUID id, ClientDto source) throws Exception;

    boolean delete(UUID id);
}
