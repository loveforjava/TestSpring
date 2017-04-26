package ua.ukrpost.service;

import ua.ukrpost.exception.PerformProcessFailedException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import ua.ukrpost.dao.DiscountDao;
import ua.ukrpost.dto.DiscountDto;
import ua.ukrpost.entity.Discount;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.mapper.DiscountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.ukrpost.util.EnhancedBeanUtilsBean;

import static java.time.LocalDateTime.now;

import static ua.ukrpost.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.deleteLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateLogEndpoint;

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
            EnhancedBeanUtilsBean.copyNotNullProperties(target, source);
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
