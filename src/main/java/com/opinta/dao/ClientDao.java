package com.opinta.dao;

import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import java.util.List;
import java.util.UUID;

import com.opinta.entity.Client;

public interface ClientDao {

    List<Client> getAll(User user);

    List<Client> getAllByCounterparty(Counterparty counterparty);

    Client getByUuid(UUID uuid);

    Client save(Client client);

    void update(Client client);

    void delete(Client client);
}
