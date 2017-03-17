package com.opinta.service;

import com.opinta.dao.VirtualPostOfficeDao;
import com.opinta.model.VirtualPostOffice;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class VirtualPostOfficeServiceImpl implements VirtualPostOfficeService {
    private VirtualPostOfficeDao virtualPostOfficeDao;

    @Autowired
    public VirtualPostOfficeServiceImpl(VirtualPostOfficeDao virtualPostOfficeDao) {
        this.virtualPostOfficeDao = virtualPostOfficeDao;
    }

    @Override
    @Transactional
    public List<VirtualPostOffice> getAll() {
        log.info("Getting all virtualPostOffices");
        return virtualPostOfficeDao.getAll();
    }

    @Override
    @Transactional
    public VirtualPostOffice getById(Long id) {
        log.info("Getting virtualPostOffice by id " + id);
        return virtualPostOfficeDao.getById(id);
    }

    @Override
    @Transactional
    public void save(VirtualPostOffice virtualPostOffice) {
        log.info("Saving virtualPostOffices " + virtualPostOffice);
        virtualPostOfficeDao.save(virtualPostOffice);
    }

    @Override
    @Transactional
    public VirtualPostOffice update(Long id, VirtualPostOffice source) {
        VirtualPostOffice target = getById(id);
        if (target == null) {
            log.info("Can't update virtualPostOffices. VirtualPostOffices doesn't exist " + id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for virtualPostOffices", e);
        }
        target.setId(id);
        log.info("Updating virtualPostOffices " + target);
        virtualPostOfficeDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        VirtualPostOffice virtualPostOffice = getById(id);
        if (virtualPostOffice == null) {
            log.debug("Can't delete virtualPostOffices. VirtualPostOffices doesn't exist " + id);
            return false;
        }
        log.info("Deleting virtualPostOffices " + virtualPostOffice);
        virtualPostOfficeDao.delete(virtualPostOffice);
        return true;
    }
}
