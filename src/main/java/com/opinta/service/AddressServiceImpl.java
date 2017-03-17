package com.opinta.service;

import com.opinta.dao.AddressDao;
import com.opinta.model.Address;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {
    private AddressDao addressDao;

    @Autowired
    public AddressServiceImpl(AddressDao addressDao) {
        this.addressDao = addressDao;
    }

    @Override
    @Transactional
    public List<Address> getAll() {
        log.info("Getting all addresss");
        return addressDao.getAll();
    }

    @Override
    @Transactional
    public Address getById(Long id) {
        log.info("Getting address by id " + id);
        return addressDao.getById(id);
    }

    @Override
    @Transactional
    public void save(Address address) {
        log.info("Saving address " + address);
        addressDao.save(address);
    }

    @Override
    @Transactional
    public Address update(Long id, Address source) {
        Address target = getById(id);
        if (target == null) {
            log.info("Can't update address. Address doesn't exist " + id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for address", e);
        }
        target.setId(id);
        log.info("Updating client " + target);
        addressDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Address address = getById(id);
        if (address == null) {
            log.debug("Can't delete address. Address doesn't exist " + id);
            return false;
        }
        log.info("Deleting address " + address);
        addressDao.delete(address);
        return true;
    }
}
