package com.opinta.dao;

import com.opinta.entity.Counterparty;
import com.opinta.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserDao {

    User getByToken(UUID token);

    List<User> getAllByCounterparty(Counterparty counterparty);

    User save(User user);

    void update(User user);

    User getById(long id);

    void delete(User user);
}
