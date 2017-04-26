package ua.ukrpost.dao;

import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.User;
import java.util.List;
import java.util.UUID;

import ua.ukrpost.entity.Client;

public interface ClientDao {

    List<Client> getAll(User user);

    List<Client> getAllByCounterparty(Counterparty counterparty);

    Client getByUuid(UUID uuid);
    
    Client getByPostId(String postId);
    
    String getNextPostIdInnerNumber();

    Client save(Client client);

    void update(Client client);

    void delete(Client client);
}
