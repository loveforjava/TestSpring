package com.opinta.service;

import com.opinta.dao.AddressDao;
import com.opinta.dao.PhoneDao;
import com.opinta.dto.AddressDto;
import com.opinta.dto.PhoneDto;
import com.opinta.entity.Address;
import com.opinta.entity.Phone;
import com.opinta.mapper.AddressMapper;
import com.opinta.mapper.PhoneMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class PhoneServiceImpl implements PhoneService {
    private final PhoneDao phoneDao;
    private final PhoneMapper phoneMapper;

    @Autowired
    public PhoneServiceImpl(PhoneDao phoneDao, PhoneMapper phoneMapper) {
        this.phoneDao = phoneDao;
        this.phoneMapper = phoneMapper;
    }

    @Override
    @Transactional
    public List<Phone> getAllEntities() {
        log.info("Getting all phones");
        return phoneDao.getAll();
    }

    @Override
    @Transactional
    public Phone getEntityById(long id) {
        log.info("Getting phone by id {}", id);
        return phoneDao.getById(id);
    }

    @Override
    @Transactional
    public Phone getEntityByPhoneNumber(String phoneNumber) {
        log.info("Getting phone by phoneNumber {}", phoneNumber);
        return phoneDao.getByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional
    public Phone getOrCreateEntityByPhoneNumber(String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) {
            log.debug("Can't create phone. Phonenumber is empty");
            return null;
        }
        Phone phone = phoneDao.getByPhoneNumber(phoneNumber);
        if (phone != null) {
            return phone;
        }
        return phoneDao.save(new Phone(phoneNumber));
    }

    @Override
    @Transactional
    public Phone saveEntity(Phone phone) {
        log.info("Saving phone {}", phone);
        return phoneDao.save(phone);
    }

    @Override
    @Transactional
    public Phone updateEntity(long id, Phone source) {
        Phone target = phoneDao.getById(id);
        if (target == null) {
            log.debug("Can't update phone. Phone doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for phone", e);
        }
        target.setId(id);
        log.info("Updating phone {}", target);
        phoneDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public List<PhoneDto> getAll() {
        return phoneMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public PhoneDto getById(long id) {
        return phoneMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public PhoneDto save(PhoneDto phoneDto) {
        return phoneMapper.toDto(saveEntity(phoneMapper.toEntity(phoneDto)));
    }

    @Override
    @Transactional
    public PhoneDto update(long id, PhoneDto phoneDto) {
        Phone phone = updateEntity(id, phoneMapper.toEntity(phoneDto));
        return (phone == null ? null : phoneMapper.toDto(phone));
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Phone phone = phoneDao.getById(id);
        if (phone == null) {
            log.debug("Can't delete phone. Phone doesn't exist {}", id);
            return false;
        }
        log.info("Deleting phone {}", phone);
        phoneDao.delete(phone);
        return true;
    }
}
