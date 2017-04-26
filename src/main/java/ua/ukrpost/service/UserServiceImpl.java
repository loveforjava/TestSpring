package ua.ukrpost.service;

import ua.ukrpost.dao.UserDao;
import ua.ukrpost.dto.UserDto;
import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;

import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.time.LocalDateTime.now;

import static ua.ukrpost.util.LogMessageUtil.authenticationOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.deleteLogEndpoint;

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
        LocalDateTime now = now();
        user.setCreated(now);
        user.setLastModified(now);
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
        user.setLastModified(now());
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
