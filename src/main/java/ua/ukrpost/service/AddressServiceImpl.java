package ua.ukrpost.service;

import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import ua.ukrpost.dao.AddressDao;
import ua.ukrpost.dto.AddressDto;
import ua.ukrpost.mapper.AddressMapper;
import ua.ukrpost.entity.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.ukrpost.util.EnhancedBeanUtilsBean;
import ua.ukrpost.util.LogMessageUtil;

import static java.time.LocalDateTime.now;

import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {
    private final AddressDao addressDao;
    private final AddressMapper addressMapper;
    private final CountrysidePostcodeService countrysidePostcodeService;

    @Autowired
    public AddressServiceImpl(AddressDao addressDao, AddressMapper addressMapper,
                              CountrysidePostcodeService countrysidePostcodeService) {
        this.addressDao = addressDao;
        this.addressMapper = addressMapper;
        this.countrysidePostcodeService = countrysidePostcodeService;
    }

    @Override
    @Transactional
    public List<Address> getAllEntities() {
        log.info(LogMessageUtil.getAllLogEndpoint(Address.class));
        return addressDao.getAll();
    }

    @Override
    @Transactional
    public Address getEntityById(long id) throws IncorrectInputDataException {
        log.info(LogMessageUtil.getByIdLogEndpoint(Address.class, id));
        Address address = addressDao.getById(id);
        if (address == null) {
            log.error(LogMessageUtil.getByIdOnErrorLogEndpoint(Address.class, id));
            throw new IncorrectInputDataException(LogMessageUtil.getByIdOnErrorLogEndpoint(Address.class, id));
        }
        return address;
    }

    @Override
    @Transactional
    public Address saveEntity(Address address) {
        log.info(LogMessageUtil.saveLogEndpoint(Address.class, address));
        address.setCountryside(countrysidePostcodeService.isPostcodeInCountryside(address.getPostcode()));
        LocalDateTime now = now();
        address.setCreated(now);
        address.setLastModified(now);
        return addressDao.save(address);
    }

    @Override
    @Transactional
    public Address updateEntity(long id, Address source) throws IncorrectInputDataException,
            PerformProcessFailedException {
        Address target = getEntityById(id);
        try {
            EnhancedBeanUtilsBean.copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(LogMessageUtil.copyPropertiesOnErrorLogEndpoint(Address.class, source, target, e));
            throw new PerformProcessFailedException(LogMessageUtil.copyPropertiesOnErrorLogEndpoint(Address.class, source, target, e));
        }
        target.setId(id);
        target.setCountryside(countrysidePostcodeService.isPostcodeInCountryside(target.getPostcode()));
        log.info(LogMessageUtil.updateLogEndpoint(Address.class, target));
        target.setLastModified(now());
        addressDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public void delete(long id) throws IncorrectInputDataException {
        log.info(LogMessageUtil.deleteLogEndpoint(Address.class, id));
        Address address = getEntityById(id);
        addressDao.delete(address);
    }

    @Override
    @Transactional
    public List<AddressDto> getAll() {
        return addressMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public AddressDto getById(long id) throws IncorrectInputDataException {
        return addressMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public AddressDto save(AddressDto addressDto) {
        return addressMapper.toDto(saveEntity(addressMapper.toEntity(addressDto)));
    }

    @Override
    @Transactional
    public AddressDto update(long id, AddressDto addressDto) throws PerformProcessFailedException,
            IncorrectInputDataException {
        return addressMapper.toDto(updateEntity(id, addressMapper.toEntity(addressDto)));
    }
}
