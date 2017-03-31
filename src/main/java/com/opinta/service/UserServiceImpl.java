package com.opinta.service;

import com.opinta.dao.UserDao;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import java.util.UUID;
import javax.naming.AuthenticationException;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public User getEntityByToken(UUID token) {
        log.info("Getting user by token {}", token);
        return userDao.getByToken(token);
    }

    @Override
    @Transactional
    public User authenticate(UUID token) throws AuthenticationException {
        User user = getEntityByToken(token);
        if (user == null) {
            log.error("Can't authenticate user with token {} ", token);
            throw new AuthenticationException(format("Can't authenticate user with token %s ", token));
        }
        return user;
    }

    @Override
    public void authorizeForAction(Counterparty counterparty, User user) throws AuthenticationException {
        if (user == null || counterparty.getUser().getToken() == null || user.getToken() == null
                || !counterparty.getUser().getToken().equals(user.getToken())) {
            assert user != null;
            throw new AuthenticationException(format("You are not authorized to perform this action (token: %s)!",
                    user.getToken()));
        }
    }

    @Override
    public void authorizeForAction(Client client, User user) throws AuthenticationException {
        if (user == null || client == null || client.getCounterparty() == null
                || client.getCounterparty().getUser().getToken() == null || user.getToken() == null
                || !client.getCounterparty().getUser().getToken().equals(user.getToken())) {
            assert user != null;
            throw new AuthenticationException(format("You are not authorized to perform this action (token: %s)!",
                    user.getToken()));
        }
    }

    @Override
    public void authorizeForAction(Shipment shipment, User user) throws AuthenticationException {
        if (user == null || shipment == null || shipment.getSender().getCounterparty() == null
                || shipment.getSender().getCounterparty().getUser().getToken() == null || user.getToken() == null
                || !shipment.getSender().getCounterparty().getUser().getToken().equals(user.getToken())) {
            assert user != null;
            throw new AuthenticationException(format("You are not authorized to perform this action (token: %s)!",
                    user.getToken()));
        }
    }

    @Override
    public void authorizeForAction(ShipmentGroup shipmentGroup, User user) throws AuthenticationException {
        if (user == null || shipmentGroup == null || shipmentGroup.getCounterparty() == null
                || shipmentGroup.getCounterparty().getUser().getToken() == null || user.getToken() == null
                || !shipmentGroup.getCounterparty().getUser().getToken().equals(user.getToken())) {
            assert user != null;
            throw new AuthenticationException(format("You are not authorized to perform this action (token: %s)!",
                    user.getToken()));
        }
    }
}
