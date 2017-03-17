package com.opinta.dao;

import com.opinta.model.Client;
import java.util.List;

public interface ClientDao {
    List<Client> getAll();
    Client getById(Long id);
    void save(Client client);
    void update(Client client);
    void delete(Client client);
}
