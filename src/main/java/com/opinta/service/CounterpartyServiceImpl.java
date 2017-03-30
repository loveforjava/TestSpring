package com.opinta.service;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.User;
import java.util.List;

import java.util.UUID;
import javax.transaction.Transactional;

import com.opinta.dao.CounterpartyDao;
import com.opinta.dto.CounterpartyDto;
import com.opinta.mapper.CounterpartyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class CounterpartyServiceImpl implements CounterpartyService {
    private final CounterpartyDao counterpartyDao;
    private final CounterpartyMapper counterpartyMapper;
    private final PostcodePoolService postcodePoolService;
    private final UserService userService;

    @Autowired
    public CounterpartyServiceImpl(CounterpartyDao counterpartyDao, CounterpartyMapper counterpartyMapper,
                                   PostcodePoolService postcodePoolService, UserService userService) {
        this.counterpartyDao = counterpartyDao;
        this.counterpartyMapper = counterpartyMapper;
        this.postcodePoolService = postcodePoolService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public List<Counterparty> getAllEntities() {
        log.info("Getting all counterparties");
        return counterpartyDao.getAll();
    }

    @Override
    @Transactional
    public Counterparty getEntityById(long id) {
        log.info("Getting counterparty {}", id);
        return counterpartyDao.getById(id);
    }

    @Override
    @Transactional
    public List<Counterparty> getEntityByPostcodePool(PostcodePool postcodePool) {
        log.info("Getting counterparty by postcodePool {}", postcodePool);
        return counterpartyDao.getByPostcodePool(postcodePool);
    }

    @Override
    @Transactional
    public Counterparty saveEntity(Counterparty counterparty) throws Exception {
        PostcodePool postcodePool = postcodePoolService.getEntityById(counterparty.getPostcodePool().getId());
        if (postcodePool == null) {
            log.error("PostcodePool {} doesn't exist", counterparty.getPostcodePool().getId());
            throw new Exception(format("PostcodePool %d doesn't exist", counterparty.getPostcodePool().getId()));
        }
        List<Counterparty> counterpartiesByPostcodePool = getEntityByPostcodePool(postcodePool);
        if (counterpartiesByPostcodePool.size() != 0) {
            log.error("PostcodePool {} is already used in the counterparty {}", postcodePool,
                    counterpartiesByPostcodePool);
            throw new Exception(format("PostcodePool %s is already used in the counterparty %s",
                    postcodePool, counterpartiesByPostcodePool));
        }

        User user = new User();
        user.setUsername(counterparty.getName());
        user.setToken(UUID.randomUUID());
        counterparty.setUser(user);

        log.info("Saving counterparty {}", counterparty);
        return counterpartyDao.save(counterparty);
    }

    @Override
    @Transactional
    public List<CounterpartyDto> getAll() {
        log.info("Getting all counterparties");
        List<Counterparty> counterparties =  counterpartyDao.getAll();
        return counterpartyMapper.toDto(counterparties);
    }

    @Override
    @Transactional
    public CounterpartyDto getById(long id) {
        log.info("Getting counterparty by id " + id);
        Counterparty counterparty = counterpartyDao.getById(id);
        return counterpartyMapper.toDto(counterparty);
    }

    @Override
    @Transactional
    public CounterpartyDto save(CounterpartyDto counterpartyDto) throws Exception {
        log.info("Saving counterparty {}", counterpartyDto);
        Counterparty counterparty = counterpartyMapper.toEntity(counterpartyDto);
        return counterpartyMapper.toDto(saveEntity(counterparty));
    }

    @Override
    @Transactional
    public CounterpartyDto update(long id, CounterpartyDto counterpartyDto, User user) throws Exception {
        Counterparty source = counterpartyMapper.toEntity(counterpartyDto);
        Counterparty target = counterpartyDao.getById(id);
        if (target == null) {
            log.error("Can't update counterparty. Counterparty doesn't exist {}", id);
            throw new Exception(format("Can't update counterparty. Counterparty doesn't exist %d", id));
        }

        userService.authorizeForAction(target, user);

        source.setUser(target.getUser());
        source.setPostcodePool(target.getPostcodePool());

        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for counterparty", e);
            throw new Exception("Can't get properties from object to updatable object for counterparty", e);
        }
        target.setId(id);
        log.info("Updating counterparty {}", target);
        counterpartyDao.update(target);
        return counterpartyMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(long id, User user) throws Exception {
        Counterparty counterparty = counterpartyDao.getById(id);
        if (counterparty == null) {
            log.error("Can't delete counterparty. Counterparty doesn't exist {}", id);
            throw new Exception(format("Can't delete counterparty. Counterparty doesn't exist %d", id));
        }

        userService.authorizeForAction(counterparty, user);

        log.info("Deleting counterparty {}", counterparty);
        counterpartyDao.delete(counterparty);
    }
}
