package com.opinta.service;

import com.opinta.dao.BarcodeInnerNumberDao;
import com.opinta.model.BarcodeInnerNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class BarcodeInnerNumberServiceImpl implements BarcodeInnerNumberService {
    private BarcodeInnerNumberDao barcodeInnerNumberDao;

    @Autowired
    public BarcodeInnerNumberServiceImpl(BarcodeInnerNumberDao barcodeInnerNumberDao) {
        this.barcodeInnerNumberDao = barcodeInnerNumberDao;
    }

    @Override
    @Transactional
    public List<BarcodeInnerNumber> getAll() {
        log.info("Getting all barcodeInnerNumbers");
        return barcodeInnerNumberDao.getAll();
    }

    @Override
    @Transactional
    public BarcodeInnerNumber getById(Long id) {
        log.info("Getting barcodeInnerNumber by id " + id);
        return barcodeInnerNumberDao.getById(id);
    }

    @Override
    @Transactional
    public void save(BarcodeInnerNumber barcodeInnerNumber) {
        log.info("Saving barcodeInnerNumber " + barcodeInnerNumber);
        barcodeInnerNumberDao.save(barcodeInnerNumber);
    }

    @Override
    @Transactional
    public BarcodeInnerNumber update(Long id, BarcodeInnerNumber source) {
        BarcodeInnerNumber target = getById(id);
        if (target == null) {
            log.info("Can't update barcodeInnerNumber. BarcodeInnerNumber doesn't exist " + id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for barcodeInnerNumber", e);
        }
        target.setId(id);
        log.info("Updating barcodeInnerNumber " + target);
        barcodeInnerNumberDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        BarcodeInnerNumber barcodeInnerNumber = getById(id);
        if (barcodeInnerNumber == null) {
            log.debug("Can't delete barcodeInnerNumber. BarcodeInnerNumber doesn't exist " + id);
            return false;
        }
        log.info("Deleting barcodeInnerNumber " + barcodeInnerNumber);
        barcodeInnerNumberDao.delete(barcodeInnerNumber);
        return true;
    }
}
