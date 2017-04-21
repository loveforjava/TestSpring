package com.opinta.service;

import com.opinta.dao.UserDao;
import com.opinta.dto.UserDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;

import java.util.Date;
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

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final CounterpartyService counterpartyService;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserDao userDao, CounterpartyService counterpartyService, UserMapper userMapper) {
        this.userDao = userDao;
        this.counterpartyService = counterpartyService;
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
    public User saveEntity(User user) throws IncorrectInputDataException, AuthException {
        user.setCounterparty(counterpartyService.getEntityByUuid(user.getCounterparty().getUuid(), user));
        Date date = new Date();
        user.setCreated(date);
        user.setLastModified(date);
        return userDao.save(user);
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) throws IncorrectInputDataException, AuthException {
        userDto.setToken(UUID.randomUUID());
        return userMapper.toDto(saveEntity(userMapper.toEntity(userDto)));
    }

    @Override
    @Transactional
    public User updateEntity(User user) throws IncorrectInputDataException, AuthException {
        log.info(updateLogEndpoint(User.class, user));
        user.setLastModified(new Date());
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
}
