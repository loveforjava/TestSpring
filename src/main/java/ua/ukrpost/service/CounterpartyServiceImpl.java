package ua.ukrpost.service;

import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.PostcodePool;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import ua.ukrpost.util.LogMessageUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import ua.ukrpost.dao.CounterpartyDao;
import ua.ukrpost.dto.CounterpartyDto;
import ua.ukrpost.mapper.CounterpartyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.time.LocalDateTime.now;

import static ua.ukrpost.util.AuthorizationUtil.authorizeForAction;
import static ua.ukrpost.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static ua.ukrpost.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.deleteLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getAllByFieldLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.saveLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class CounterpartyServiceImpl implements CounterpartyService {
    private final CounterpartyDao counterpartyDao;
    private final CounterpartyMapper counterpartyMapper;
    private final PostcodePoolService postcodePoolService;

    @Autowired
    public CounterpartyServiceImpl(CounterpartyDao counterpartyDao, CounterpartyMapper counterpartyMapper,
                                   PostcodePoolService postcodePoolService) {
        this.counterpartyDao = counterpartyDao;
        this.counterpartyMapper = counterpartyMapper;
        this.postcodePoolService = postcodePoolService;
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
        authorizeForAction(counterparty, user);
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
    public List<Counterparty> getAllEntitiesByPostcodePoolUuid(UUID postcodePoolUuid)
            throws IncorrectInputDataException {
        PostcodePool postcodePool = postcodePoolService.getEntityByUuid(postcodePoolUuid);
        log.info(getAllByFieldLogEndpoint(Counterparty.class, PostcodePool.class, postcodePool));
        return counterpartyDao.getByPostcodePool(postcodePool);
    }

    @Override
    @Transactional
    public Counterparty saveEntity(Counterparty counterparty) throws IncorrectInputDataException {
        counterparty.setCreated(now());
        counterparty.setLastModified(now());
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
        target.setLastModified(now());
        target.setLastModifier(user);
        counterpartyDao.update(target);
        return counterpartyMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(UUID uuid) throws IncorrectInputDataException {
        log.info(deleteLogEndpoint(Counterparty.class, uuid));
        Counterparty counterparty = getEntityByUuidAnonymous(uuid);
        counterpartyDao.delete(counterparty);
    }
}
