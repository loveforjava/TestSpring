package com.opinta.service;

import java.util.List;

import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;

public interface ClientService {

    List<Client> getAllEntities();

    Client getEntityById(String id);

    Client saveEntity(Client client);
    
    List<ClientDto> getAll();

    List<ClientDto> getAllByCounterpartyId(String counterpartyId);

    ClientDto getById(String id);
    
    ClientDto update(String id, ClientDto source);
    
    boolean delete(String id);
    
    ClientDto save(ClientDto client);
}
