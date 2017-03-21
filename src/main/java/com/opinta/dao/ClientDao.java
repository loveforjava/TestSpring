package com.opinta.dao;

import java.util.List;

import com.opinta.model.Client;

public interface ClientDao {
    List<Client> getAll();
    Client getById(Long id);
    Client save(Client client);
    void update(Client client);
    void delete(Client client);
}
