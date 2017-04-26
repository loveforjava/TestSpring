package ua.ukrpost.service;

import ua.ukrpost.dao.DiscountPerCounterpartyDao;
import ua.ukrpost.dto.DiscountPerCounterpartyDto;
import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.Discount;
import ua.ukrpost.entity.DiscountPerCounterparty;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import ua.ukrpost.mapper.DiscountPerCounterpartyMapper;
import ua.ukrpost.util.LogMessageUtil;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.ukrpost.util.EnhancedBeanUtilsBean;

import static ua.ukrpost.util.AuthorizationUtil.authorizeForAction;
import static ua.ukrpost.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.deleteLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.saveLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateLogEndpoint;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;

@Service
@Slf4j
public class DiscountPerCounterpartyServiceImpl implements DiscountPerCounterpartyService {
    private final DiscountPerCounterpartyDao discountPerCounterpartyDao;
    private final DiscountPerCounterpartyMapper discountPerCounterpartyMapper;
    private final CounterpartyService counterpartyService;
    private final DiscountService discountService;

    @Autowired
    public DiscountPerCounterpartyServiceImpl(DiscountPerCounterpartyDao discountPerCounterpartyDao,
                                              DiscountPerCounterpartyMapper discountPerCounterpartyMapper,
                                              CounterpartyService counterpartyService,
                                              DiscountService discountService) {
        this.discountPerCounterpartyDao = discountPerCounterpartyDao;
        this.discountPerCounterpartyMapper = discountPerCounterpartyMapper;
        this.counterpartyService = counterpartyService;
        this.discountService = discountService;
    }

    @Override
    @Transactional
    public List<DiscountPerCounterparty> getAllEntities(User user) {
        log.info(LogMessageUtil.getAllLogEndpoint(DiscountPerCounterparty.class));
        return discountPerCounterpartyDao.getAll(user);
    }

    @Override
    @Transactional
    public DiscountPerCounterparty getEntityByUuid(UUID uuid, User user) throws AuthException,
            IncorrectInputDataException {
        log.info(LogMessageUtil.getByIdLogEndpoint(DiscountPerCounterparty.class, uuid));
        DiscountPerCounterparty discountPerCounterparty = discountPerCounterpartyDao.getByUuid(uuid);
        if (discountPerCounterparty == null) {
            log.error(getByIdOnErrorLogEndpoint(DiscountPerCounterparty.class, uuid));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(DiscountPerCounterparty.class, uuid));
        }

        authorizeForAction(discountPerCounterparty, user);

        return discountPerCounterpartyDao.getByUuid(uuid);
    }

    @Override
    @Transactional
    public DiscountPerCounterparty getEntityWithHighestDiscount(User user, LocalDateTime date) {
        log.info(format("Get the highest value of DiscountPerCounterparty for the %s for user %s", date, user));
        return discountPerCounterpartyDao.getHighestDiscount(user, date);
    }

    @Override
    @Transactional
    public DiscountPerCounterparty saveEntity(DiscountPerCounterparty discountPerCounterparty, User user)
            throws AuthException, IncorrectInputDataException, PerformProcessFailedException {
        Counterparty counterparty = counterpartyService.getEntityByUuid(
                discountPerCounterparty.getCounterparty().getUuid(), user);
        Discount discount = discountService.getEntityByUuid(discountPerCounterparty.getDiscount().getUuid());

        discountPerCounterparty.setCounterparty(counterparty);
        discountPerCounterparty.setDiscount(discount);
        discountPerCounterparty.validate();
        LocalDateTime now = now();
        discountPerCounterparty.setCreated(now);
        discountPerCounterparty.setLastModified(now);
        discountPerCounterparty.setCreator(user);
        discountPerCounterparty.setLastModifier(user);
        log.info(saveLogEndpoint(DiscountPerCounterparty.class, discountPerCounterparty));
        return discountPerCounterpartyDao.save(discountPerCounterparty);
    }

    @Override
    @Transactional
    public DiscountPerCounterparty updateEntity(UUID uuid, DiscountPerCounterparty source, User user)
            throws IncorrectInputDataException, PerformProcessFailedException, AuthException {
        source.setDiscount(discountService.getEntityByUuid(source.getDiscount().getUuid()));
        source.validate();
        DiscountPerCounterparty target = getEntityByUuid(uuid, user);
        try {
            EnhancedBeanUtilsBean.copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(Discount.class, source, target, e));
            throw new PerformProcessFailedException(
                    copyPropertiesOnErrorLogEndpoint(Discount.class, source, target, e));
        }
        target.setUuid(uuid);
        log.info(updateLogEndpoint(Discount.class, target));
        target.setLastModified(now());
        target.setLastModifier(user);
        discountPerCounterpartyDao.update(target);
        return target;
    }
    
    @Override
    @Transactional
    public List<DiscountPerCounterpartyDto> getAll(User user) {
        return discountPerCounterpartyMapper.toDto(getAllEntities(user));
    }

    @Override
    @Transactional
    public DiscountPerCounterpartyDto getByUuid(UUID uuid, User user) throws IncorrectInputDataException,
            AuthException {
        return discountPerCounterpartyMapper.toDto(getEntityByUuid(uuid, user));
    }

    @Override
    @Transactional
    public DiscountPerCounterpartyDto save(DiscountPerCounterpartyDto discountPerCounterpartyDto, User user)
            throws IncorrectInputDataException, AuthException, PerformProcessFailedException {
        return discountPerCounterpartyMapper
                .toDto(saveEntity(discountPerCounterpartyMapper.toEntity(discountPerCounterpartyDto), user));
    }

    @Override
    @Transactional
    public DiscountPerCounterpartyDto update(UUID uuid, DiscountPerCounterpartyDto discountPerCounterpartyDto,
                                             User user)
            throws IncorrectInputDataException, AuthException, PerformProcessFailedException {
        return discountPerCounterpartyMapper
                .toDto(updateEntity(uuid, discountPerCounterpartyMapper.toEntity(discountPerCounterpartyDto), user));
    }

    @Override
    @Transactional
    public void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        log.info(deleteLogEndpoint(DiscountPerCounterparty.class, uuid));
        discountPerCounterpartyDao.delete(getEntityByUuid(uuid, user));
    }
}
