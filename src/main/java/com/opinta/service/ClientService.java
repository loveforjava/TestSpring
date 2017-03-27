package com.opinta.service;

import java.util.List;

import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;

public interface ClientService {

    List<Client> getAllEntities();

    Client getEntityById(long id);

    Client saveEntity(Client client);
    
    List<ClientDto> getAll();

    List<ClientDto> getAllByCounterpartyId(long counterpartyId);

    ClientDto getById(long id);
    
    ClientDto update(long id, ClientDto source);
    
    boolean delete(long id);
    
    ClientDto save(ClientDto client);
}
