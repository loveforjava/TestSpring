package com.opinta.service;

import com.opinta.dao.AddressDao;
import com.opinta.dto.AddressDto;
import com.opinta.mapper.AddressMapper;
import com.opinta.model.Address;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {
    private AddressDao addressDao;
    private AddressMapper addressMapper;

    @Autowired
    public AddressServiceImpl(AddressDao addressDao, AddressMapper addressMapper) {
        this.addressDao = addressDao;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional
    public List<AddressDto> getAll() {
        log.info("Getting all addresses");
        return addressMapper.toDto(addressDao.getAll());
    }

    @Override
    @Transactional
    public AddressDto getById(Long id) {
        log.info(format("Getting address by id %d", id));
        return addressMapper.toDto(addressDao.getById(id));
    }

    @Override
    @Transactional
    public void save(AddressDto addressDto) {
        log.info(format("Saving address %s", addressDto));
        addressDao.save(addressMapper.toEntity(addressDto));
    }

    @Override
    @Transactional
    public AddressDto update(Long id, AddressDto addressDto) {
        Address source = addressMapper.toEntity(addressDto);
        Address target = addressDao.getById(id);
        if (target == null) {
            log.info(format("Can't update address. Address doesn't exist %d", id));
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for address", e);
        }
        target.setId(id);
        log.info(format("Updating client %s", target));
        addressDao.update(target);
        return addressMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Address address = addressDao.getById(id);
        if (address == null) {
            log.debug("Can't delete address. Address doesn't exist " + id);
            return false;
        }
        log.info("Deleting address " + address);
        addressDao.delete(address);
        return true;
    }
}
