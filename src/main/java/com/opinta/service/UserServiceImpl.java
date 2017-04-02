package com.opinta.service;

import com.opinta.dao.UserDao;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.opinta.util.LogMessageUtil.authenticationOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.authorizationOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdLogEndpoint;

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
        log.info(getByIdLogEndpoint(User.class, token));
        return userDao.getByToken(token);
    }

    @Override
    @Transactional
    public User authenticate(UUID token) throws AuthException {
        User user = getEntityByToken(token);
        if (user == null) {
            log.error(authenticationOnErrorLogEndpoint(token));
            throw new AuthException(authenticationOnErrorLogEndpoint(token));
        }
        return user;
    }

    @Override
    public void authorizeForAction(Counterparty counterparty, User user) throws AuthException {
        if (user == null || counterparty.getUser().getToken() == null || user.getToken() == null
                || !counterparty.getUser().getToken().equals(user.getToken())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), counterparty));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), counterparty));
        }
    }

    @Override
    public void authorizeForAction(Client client, User user) throws AuthException {
        if (user == null || client == null || client.getCounterparty() == null
                || client.getCounterparty().getUser().getToken() == null || user.getToken() == null
                || !client.getCounterparty().getUser().getToken().equals(user.getToken())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), client));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), client));
        }
    }

    @Override
    public void authorizeForAction(Shipment shipment, User user) throws AuthException {
        if (user == null || shipment == null || shipment.getSender().getCounterparty() == null
                || shipment.getSender().getCounterparty().getUser().getToken() == null || user.getToken() == null
                || !shipment.getSender().getCounterparty().getUser().getToken().equals(user.getToken())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), shipment));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), shipment));
        }
    }

    @Override
    public void authorizeForAction(ShipmentGroup shipmentGroup, User user) throws AuthException {
        if (user == null || shipmentGroup == null || shipmentGroup.getCounterparty() == null
                || shipmentGroup.getCounterparty().getUser().getToken() == null || user.getToken() == null
                || !shipmentGroup.getCounterparty().getUser().getToken().equals(user.getToken())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), shipmentGroup));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), shipmentGroup));
        }
    }
}
