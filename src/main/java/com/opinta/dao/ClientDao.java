package com.opinta.dao;

import java.util.List;

import com.opinta.model.Client;
import com.opinta.model.VirtualPostOffice;

public interface ClientDao {

    List<Client> getAll();

    List<Client> getAllByVirtualPostOffice(VirtualPostOffice virtualPostOffice);

    Client getById(long id);

    Client save(Client client);

    void update(Client client);

    void delete(Client client);

}
