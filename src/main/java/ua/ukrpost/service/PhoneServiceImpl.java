package ua.ukrpost.service;

import ua.ukrpost.dao.PhoneDao;
import ua.ukrpost.dto.PhoneDto;
import ua.ukrpost.entity.Phone;
import ua.ukrpost.mapper.PhoneMapper;
import ua.ukrpost.util.LogMessageUtil;
import java.lang.reflect.InvocationTargetException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static ua.ukrpost.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static ua.ukrpost.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.deleteLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.deleteOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.saveLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateLogEndpoint;

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
        log.info(LogMessageUtil.getAllLogEndpoint(Phone.class));
        return phoneDao.getAll();
    }

    @Override
    @Transactional
    public Phone getEntityById(long id) {
        log.info(getByIdLogEndpoint(Phone.class, id));
        return phoneDao.getById(id);
    }

    @Override
    @Transactional
    public Phone getEntityByPhoneNumber(String phoneNumber) {
        log.info(getByIdLogEndpoint(Phone.class, phoneNumber));
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
        phone.removeNonNumericalCharacters();
        log.info(saveLogEndpoint(Phone.class, phone));
        return phoneDao.save(phone);
    }

    @Override
    @Transactional
    public Phone updateEntity(long id, Phone source) {
        Phone target = phoneDao.getById(id);
        if (target == null) {
            log.error(getByIdOnErrorLogEndpoint(Phone.class, id));
            return null;
        }
        try {
            copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(Phone.class, source, target, e));
        }
        target.setId(id);
        log.info(updateLogEndpoint(Phone.class, target));
        phoneDao.update(target.removeNonNumericalCharacters());
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
            log.error(deleteOnErrorLogEndpoint(Phone.class, id));
            return false;
        }
        log.info(deleteLogEndpoint(Phone.class, id));
        phoneDao.delete(phone);
        return true;
    }
}
