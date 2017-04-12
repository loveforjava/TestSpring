package com.opinta.service;

import java.util.Optional;
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

import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;

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
    public Discount saveEntity(Discount discount) {
        return discountDao.saveEntity(discount);
    }
    
    @Override
    @Transactional
    public Discount getEntityByUuid(UUID uuid) throws IncorrectInputDataException {
        Discount discount = discountDao.getEntityByUuid(uuid);
        if (discount == null) {
            String errorMessage = getByIdOnErrorLogEndpoint(Discount.class, uuid.toString());
            log.error(errorMessage);
            throw new IncorrectInputDataException(errorMessage);
        }
        return discount;
    }
    
    @Override
    @Transactional
    public Discount getEntityZeroValue() {
        return discountDao.getEntityZeroValue();
    }
    
    @Override
    @Transactional
    public Optional<Discount> getEntityByValue(float value) {
        return Optional.ofNullable(discountDao.getEntityByValue(value));
    }
    
    @Override
    @Transactional
    public DiscountDto save(DiscountDto dto) {
        return discountMapper.toDto(discountDao.saveEntity(discountMapper.toEntity(dto)));
    }
    
    @Override
    @Transactional
    public void delete(UUID uuid) throws IncorrectInputDataException {
        Discount discount = getEntityByUuid(uuid);
        discountDao.delete(discount);
    }
}
