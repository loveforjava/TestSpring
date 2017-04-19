package com.opinta.service;

import com.opinta.dao.ClientDao;
import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.util.LogMessageUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import com.opinta.dao.CounterpartyDao;
import com.opinta.dto.CounterpartyDto;
import com.opinta.mapper.CounterpartyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.opinta.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllByFieldLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByFieldOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class CounterpartyServiceImpl implements CounterpartyService {
    private final CounterpartyDao counterpartyDao;
    private final CounterpartyMapper counterpartyMapper;
    private final PostcodePoolService postcodePoolService;
    private final UserService userService;
    private final ClientDao clientDao;

    @Autowired
    public CounterpartyServiceImpl(CounterpartyDao counterpartyDao, CounterpartyMapper counterpartyMapper,
                                   PostcodePoolService postcodePoolService, UserService userService,
                                   ClientDao clientDao) {
        this.counterpartyDao = counterpartyDao;
        this.counterpartyMapper = counterpartyMapper;
        this.postcodePoolService = postcodePoolService;
        this.userService = userService;
        this.clientDao = clientDao;
    }

    @Override
    @Transactional
    public List<Counterparty> getAllEntities() {
        log.info(LogMessageUtil.getAllLogEndpoint(Counterparty.class));
        return counterpartyDao.getAll();
    }

    @Override
    @Transactional
    public Counterparty getEntityByUuid(UUID uuid, User user) throws IncorrectInputDataException, AuthException {
        log.info(getByIdLogEndpoint(Counterparty.class, uuid));
        Counterparty counterparty = counterpartyDao.getByUuid(uuid);
        if (counterparty == null) {
            log.error(getByIdOnErrorLogEndpoint(Counterparty.class, uuid));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(Counterparty.class, uuid));
        }
        userService.authorizeForAction(counterparty, user);
        return counterparty;
    }

    @Override
    @Transactional
    public Counterparty getEntityByUuidAnonymous(UUID uuid) throws IncorrectInputDataException {
        log.info(getByIdLogEndpoint(Counterparty.class, uuid));
        Counterparty counterparty = counterpartyDao.getByUuid(uuid);
        if (counterparty == null) {
            log.error(getByIdOnErrorLogEndpoint(Counterparty.class, uuid));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(Counterparty.class, uuid));
        }
        return counterparty;
    }

    @Override
    @Transactional
    public Counterparty getEntityByUser(User user) throws IncorrectInputDataException {
        Counterparty counterparty = counterpartyDao.getByUser(user);
        if (counterparty == null) {
            String errorMessage = getByFieldOnErrorLogEndpoint(Counterparty.class, User.class, user.getId());
            log.error(errorMessage);
            throw new IncorrectInputDataException(errorMessage);
        }
        return counterparty;
    }

    @Override
    @Transactional
    public List<Counterparty> getAllEntitiesByPostcodePoolUuid(UUID postcodePoolUuid)
            throws IncorrectInputDataException {
        PostcodePool postcodePool = postcodePoolService.getEntityByUuid(postcodePoolUuid);
        log.info(getAllByFieldLogEndpoint(Counterparty.class, PostcodePool.class, postcodePool));
        return counterpartyDao.getByPostcodePool(postcodePool);
    }

    @Override
    @Transactional
    public Counterparty saveEntity(Counterparty counterparty) throws IncorrectInputDataException {
//        User user = new User();
//        user.setUsername(counterparty.getName());
//        user.setToken(UUID.randomUUID());
//        user.setCounterparty(counterparty);

        log.info(saveLogEndpoint(Counterparty.class, counterparty));
        return counterpartyDao.save(counterparty);
    }

    @Override
    @Transactional
    public List<CounterpartyDto> getAll() {
        return counterpartyMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public CounterpartyDto getByUuid(UUID uuid, User user) throws IncorrectInputDataException, AuthException {
        return counterpartyMapper.toDto(getEntityByUuid(uuid, user));
    }

    @Override
    @Transactional
    public CounterpartyDto save(CounterpartyDto counterpartyDto) throws IncorrectInputDataException {
        return counterpartyMapper.toDto(saveEntity(counterpartyMapper.toEntity(counterpartyDto)));
    }

    @Override
    @Transactional
    public CounterpartyDto update(UUID uuid, CounterpartyDto counterpartyDto, User user)
            throws IncorrectInputDataException, AuthException, PerformProcessFailedException {
        Counterparty source = counterpartyMapper.toEntity(counterpartyDto);
        Counterparty target = getEntityByUuid(uuid, user);

//        source.setUser(target.getUser());
        source.setPostcodePool(target.getPostcodePool());

        try {
            copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(Counterparty.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    Counterparty.class, source, target, e));
        }
        target.setUuid(uuid);
        log.info(updateLogEndpoint(Counterparty.class, target));
        counterpartyDao.update(target);
        return counterpartyMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        log.info(deleteLogEndpoint(Counterparty.class, uuid));
        Counterparty counterparty = getEntityByUuid(uuid, user);
        userService.removeCounterpartyFromUser(user);
        counterpartyDao.delete(counterparty);
    }

    @Override
    @Transactional
    public void deleteAnomymous(UUID uuid) throws IncorrectInputDataException {
        if (!userService.getUsersByCounterparty(counterpartyDao.getByUuid(uuid)).isEmpty()) {
            //TODO: add proper logging
            throw new IncorrectInputDataException(deleteLogEndpoint(Counterparty.class, uuid));
        }
        log.info(deleteLogEndpoint(Counterparty.class, uuid));
        Counterparty counterparty = getEntityByUuidAnonymous(uuid);
        counterpartyDao.delete(counterparty);
    }
}
