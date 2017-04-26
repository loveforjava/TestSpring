package com.opinta.service;

import com.opinta.exception.PerformProcessFailedException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import com.opinta.dao.DiscountDao;
import com.opinta.dto.DiscountDto;
import com.opinta.entity.Discount;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.mapper.DiscountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.time.LocalDateTime.now;

import static com.opinta.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class DiscountServiceImpl implements DiscountService {
    private final DiscountDao discountDao;
    private final DiscountMapper discountMapper;
    
    @Autowired
    public DiscountServiceImpl(DiscountDao discountDao, DiscountMapper discountMapper) {
        this.discountDao = discountDao;
        this.discountMapper = discountMapper;
    }

    @Override
    @Transactional
    public List<Discount> getAllEntities() {
        return discountDao.getAll();
    }

    @Override
    @Transactional
    public Discount getEntityByUuid(UUID uuid) throws IncorrectInputDataException {
        Discount discount = discountDao.getByUuid(uuid);
        if (discount == null) {
            log.error(getByIdOnErrorLogEndpoint(Discount.class, uuid.toString()));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(Discount.class, uuid.toString()));
        }
        return discount;
    }

    @Override
    @Transactional
    public Discount saveEntity(Discount discount) {
        LocalDateTime now = now();
        discount.setCreated(now);
        discount.setLastModified(now);
        return discountDao.save(discount);
    }

    @Override
    @Transactional
    public Discount updateEntity(UUID uuid, Discount source) throws IncorrectInputDataException,
            PerformProcessFailedException {
        Discount target = getEntityByUuid(uuid);
        try {
            copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(Discount.class, source, target, e));
            throw new PerformProcessFailedException(
                    copyPropertiesOnErrorLogEndpoint(Discount.class, source, target, e));
        }
        target.setUuid(uuid);
        log.info(updateLogEndpoint(Discount.class, target));
        target.setLastModified(now());
        discountDao.update(target);
        return target;
    }
    
    @Override
    @Transactional
    public List<DiscountDto> getAll() {
        return discountMapper.toDto(getAllEntities());
    }
    
    @Override
    @Transactional
    public DiscountDto getByUuid(UUID uuid) throws IncorrectInputDataException {
        return discountMapper.toDto(getEntityByUuid(uuid));
    }
    
    @Override
    @Transactional
    public DiscountDto save(DiscountDto dto) {
        return discountMapper.toDto(saveEntity(discountMapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public DiscountDto update(UUID uuid, DiscountDto discountDto) throws PerformProcessFailedException,
            IncorrectInputDataException {
        return discountMapper.toDto(updateEntity(uuid, discountMapper.toEntity(discountDto)));
    }

    @Override
    @Transactional
    public void delete(UUID uuid) throws IncorrectInputDataException {
        log.info(deleteLogEndpoint(Discount.class, uuid));
        discountDao.delete(getEntityByUuid(uuid));
    }
}
