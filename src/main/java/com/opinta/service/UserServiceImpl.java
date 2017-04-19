package com.opinta.service;

import com.opinta.dao.CounterpartyDao;
import com.opinta.dao.UserDao;
import com.opinta.dto.UserDto;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.DiscountPerCounterparty;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;

import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;

import com.opinta.exception.IncorrectInputDataException;
import com.opinta.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.opinta.util.LogMessageUtil.authenticationOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.authorizationOnErrorLogEndpoint;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final CounterpartyDao counterpartyDao;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserDao userDao, CounterpartyDao counterpartyDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.counterpartyDao = counterpartyDao;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public User getEntityByToken(UUID token) {
        log.info(getByIdLogEndpoint(User.class, token));
        return userDao.getByToken(token);
    }

    @Override
    @Transactional
    public User getEntityById(long id) {
        log.info(getByIdLogEndpoint(User.class, id));
        return userDao.getById(id);
    }

    @Override
    @Transactional
    public List<User> getUsersByCounterparty(Counterparty counterparty) {
        return userDao.getAllByCounterparty(counterparty);
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) throws IncorrectInputDataException {
        userDto.setToken(UUID.randomUUID());
        return userMapper.toDto(saveEntity(userMapper.toEntity(userDto)));
    }

    @Override
    @Transactional
    public User saveEntity(User user) throws IncorrectInputDataException {
        user.setCounterparty(counterpartyDao.getByUuid(user.getCounterparty().getUuid()));
        return userDao.save(user);
    }

    @Override
    @Transactional
    public User updateEntity(User user) throws IncorrectInputDataException, AuthException {
        log.info(updateLogEndpoint(User.class, user));
        userDao.update(user);
        return user;
    }

    @Override
    @Transactional
    public void delete(long id) throws IncorrectInputDataException {
        log.info(deleteLogEndpoint(User.class, id));
        User user = userDao.getById(id);
        userDao.delete(user);
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
        if (user == null || user.getCounterparty() == null
                || !user.getCounterparty().getUuid().equals(counterparty.getUuid())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), counterparty));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), counterparty));
        }
    }

    @Override
    public void authorizeForAction(Client client, User user) throws AuthException {
        if (user == null || client == null || user.getCounterparty() == null || client.getCounterparty() == null
                || client.getCounterparty() == null
                || !client.getCounterparty().getUuid().equals(user.getCounterparty().getUuid())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), client));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), client));
        }
    }

    @Override
    public void authorizeForAction(Shipment shipment, User user) throws AuthException {
        if (user == null || user.getCounterparty() == null || shipment == null
                || shipment.getSender().getCounterparty() == null || shipment.getSender().getCounterparty() == null
                || !shipment.getSender().getCounterparty().getUuid().equals(user.getCounterparty().getUuid())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), shipment));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), shipment));
        }
    }

    @Override
    public void authorizeForAction(ShipmentGroup shipmentGroup, User user) throws AuthException {
        if (user == null || user.getCounterparty() == null
                || shipmentGroup == null || shipmentGroup.getCounterparty() == null
                || shipmentGroup.getCounterparty() == null
                || !shipmentGroup.getCounterparty().getUuid().equals(user.getCounterparty().getUuid())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), shipmentGroup));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), shipmentGroup));
        }
    }

    @Override
    public void authorizeForAction(DiscountPerCounterparty discountPerCounterparty, User user) throws AuthException {
        authorizeForAction(discountPerCounterparty.getCounterparty(), user);
    }
}
