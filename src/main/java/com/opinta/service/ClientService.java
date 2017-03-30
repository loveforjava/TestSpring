package com.opinta.service;

import java.util.List;
import java.util.UUID;

import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;

public interface ClientService {

    List<Client> getAllEntities();

    Client getEntityByUuid(UUID uuid);

    Client saveEntity(Client client);
    
    List<ClientDto> getAll();

    List<ClientDto> getAllByCounterpartyUuid(UUID counterpartyUuid);

    ClientDto getByUuid(UUID uuid);
    
    ClientDto save(ClientDto client) throws Exception;

    ClientDto update(UUID uuid, ClientDto source) throws Exception;

    boolean delete(UUID uuid);
}
