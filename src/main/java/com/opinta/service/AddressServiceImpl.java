package com.opinta.service;

import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import com.opinta.dao.AddressDao;
import com.opinta.dto.AddressDto;
import com.opinta.mapper.AddressMapper;
import com.opinta.entity.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.time.LocalDateTime.now;

import static com.opinta.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;

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
        log.info(getAllLogEndpoint(Address.class));
        return addressDao.getAll();
    }

    @Override
    @Transactional
    public Address getEntityById(long id) throws IncorrectInputDataException {
        log.info(getByIdLogEndpoint(Address.class, id));
        Address address = addressDao.getById(id);
        if (address == null) {
            log.error(getByIdOnErrorLogEndpoint(Address.class, id));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(Address.class, id));
        }
        return address;
    }

    @Override
    @Transactional
    public Address saveEntity(Address address) {
        log.info(saveLogEndpoint(Address.class, address));
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
            copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(Address.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(Address.class, source, target, e));
        }
        target.setId(id);
        target.setCountryside(countrysidePostcodeService.isPostcodeInCountryside(target.getPostcode()));
        log.info(updateLogEndpoint(Address.class, target));
        target.setLastModified(now());
        addressDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public void delete(long id) throws IncorrectInputDataException {
        log.info(deleteLogEndpoint(Address.class, id));
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
