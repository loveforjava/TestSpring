package com.opinta.service;

import java.util.List;

import com.opinta.dto.ClientDto;

public interface ClientService {
    
    List<ClientDto> getAll();
    
    ClientDto getById(long id);
    
    ClientDto update(long id, ClientDto source);
    
    boolean delete(long id);
    
    ClientDto save(ClientDto client);
}
