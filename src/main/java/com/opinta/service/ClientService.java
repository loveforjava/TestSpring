package com.opinta.service;

import com.opinta.model.BarcodeInnerNumber;
import com.opinta.model.Client;
import java.util.List;

public interface ClientService {
    List<Client> getAll();
    Client getById(Long id);
    Client save(Client client);
    Client update(Long id, Client source);
    boolean delete(Long id);
}
