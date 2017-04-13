package com.opinta.service;

import com.opinta.dao.DiscountPerCounterpartyDao;
import com.opinta.dto.DiscountPerCounterpartyDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Discount;
import com.opinta.entity.DiscountPerCounterparty;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.mapper.DiscountPerCounterpartyMapper;
import com.opinta.util.LogMessageUtil;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

import static com.opinta.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class DiscountPerCounterpartyServiceImpl implements DiscountPerCounterpartyService {
    private final DiscountPerCounterpartyDao discountPerCounterpartyDao;
    private final DiscountPerCounterpartyMapper discountPerCounterpartyMapper;
    private final CounterpartyService counterpartyService;
    private final DiscountService discountService;
    private final UserService userService;
    private final Format dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Autowired
    public DiscountPerCounterpartyServiceImpl(DiscountPerCounterpartyDao discountPerCounterpartyDao,
                                              DiscountPerCounterpartyMapper discountPerCounterpartyMapper,
                                              CounterpartyService counterpartyService,
                                              DiscountService discountService, UserService userService) {
        this.discountPerCounterpartyDao = discountPerCounterpartyDao;
        this.discountPerCounterpartyMapper = discountPerCounterpartyMapper;
        this.counterpartyService = counterpartyService;
        this.discountService = discountService;
        this.userService = userService;
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

        userService.authorizeForAction(discountPerCounterparty, user);

        return discountPerCounterpartyDao.getByUuid(uuid);
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
        validateDiscountTime(discountPerCounterparty);
        log.info(LogMessageUtil.saveLogEndpoint(DiscountPerCounterparty.class, discountPerCounterparty));
        return discountPerCounterpartyDao.save(discountPerCounterparty);
    }
    
    @Override
    public DiscountPerCounterparty updateEntity(DiscountPerCounterparty discountPerCounterparty, User user)
            throws AuthException, IncorrectInputDataException, PerformProcessFailedException {
        userService.authorizeForAction(discountPerCounterparty, user);
        validateDiscountTime(discountPerCounterparty);
        discountPerCounterpartyDao.update(discountPerCounterparty);
        return discountPerCounterparty;
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
        DiscountPerCounterparty discountPerCounterparty = discountPerCounterpartyMapper
                .toEntity(discountPerCounterpartyDto);
        discountPerCounterparty.setDiscount(discountService.getEntityByUuid(discountPerCounterpartyDto.getDiscountUuid()));
        validateDiscountTime(discountPerCounterparty);
        return discountPerCounterpartyMapper.toDto(saveEntity(discountPerCounterparty, user));
    }
    
    private void validateDiscountTime(DiscountPerCounterparty discountPerCounterparty)
            throws PerformProcessFailedException {
        if (!discountPerCounterparty.isDiscountValidNow()) {
            throw new PerformProcessFailedException(format(
                    "DiscountPerCounterparty time from: %s to: %s is not valid!",
                    dateFormat.format(discountPerCounterparty.getFromDate()),
                    dateFormat.format(discountPerCounterparty.getToDate())));
        }
        if (!discountPerCounterparty.isDiscountPerCounterpartyValidForAssignedDiscount()) {
            throw new PerformProcessFailedException(format(
                    "DiscountPerCounterparty time from %s to: %s is not valid for assigned " +
                            "Discount %s time from %s to: %s ",
                    dateFormat.format(discountPerCounterparty.getFromDate()),
                    dateFormat.format(discountPerCounterparty.getToDate()),
                    discountPerCounterparty.getDiscount().getUuid().toString(),
                    dateFormat.format(discountPerCounterparty.getDiscount().getFromDate()),
                    dateFormat.format(discountPerCounterparty.getDiscount().getToDate())));
        }
    }

    @Override
    @Transactional
    public DiscountPerCounterpartyDto update(UUID uuid, DiscountPerCounterpartyDto discountPerCounterpartyDto,
                                             User user) throws IncorrectInputDataException, AuthException,
            PerformProcessFailedException {
        DiscountPerCounterparty source = discountPerCounterpartyMapper.toEntity(discountPerCounterpartyDto);
        source.setDiscount(discountService.getEntityByUuid(source.getDiscount().getUuid()));
        DiscountPerCounterparty target = getEntityByUuid(uuid, user);
        validateDiscountTime(source);
        source.setCounterparty(target.getCounterparty());

        try {
            copyNotNullProperties(target, source);
        } catch (Exception e) {
            log.error(copyPropertiesOnErrorLogEndpoint(DiscountPerCounterparty.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    DiscountPerCounterparty.class, source, target, e));
        }
        target.setUuid(uuid);
        log.info(updateLogEndpoint(DiscountPerCounterparty.class, target));
        discountPerCounterpartyDao.update(target);
        return discountPerCounterpartyMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        log.info(deleteLogEndpoint(DiscountPerCounterparty.class, uuid));
        DiscountPerCounterparty discountPerCounterparty = getEntityByUuid(uuid, user);
        discountPerCounterpartyDao.delete(discountPerCounterparty);
    }
}
