package com.opinta.service;

import com.opinta.dao.BarcodeInnerNumberDao;
import com.opinta.model.BarcodeInnerNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
        log.info("Getting all Customers");
        return barcodeInnerNumberDao.getAll();
    }

    @Override
    @Transactional
    public BarcodeInnerNumber getById(Long id) {
        log.info("Getting Customer by id " + id);
        return barcodeInnerNumberDao.getById(id);
    }

    @Override
    @Transactional
    public void save(BarcodeInnerNumber barcodeInnerNumber) {
        log.info("Saving customer " + barcodeInnerNumber);
        barcodeInnerNumberDao.save(barcodeInnerNumber);
    }

    @Override
    @Transactional
    public BarcodeInnerNumber update(Long id, BarcodeInnerNumber barcodeInnerNumber) {
        if (getById(id) == null) {
            log.info("Can't update barcode inner number. Inner number doesn't exist " + id);
            return null;
        }
        barcodeInnerNumber.setId(id);
        log.info("Updating customer " + barcodeInnerNumber);
        barcodeInnerNumberDao.update(barcodeInnerNumber);
        return barcodeInnerNumber;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (getById(id) == null) {
            log.debug("Can't delete customer. Customer doesn't exist " + id);
            return false;
        }
        BarcodeInnerNumber barcodeInnerNumber = new BarcodeInnerNumber();
        barcodeInnerNumber.setId(id);
        log.info("Deleting customer " + barcodeInnerNumber);
        barcodeInnerNumberDao.delete(barcodeInnerNumber);
        return true;
    }
}
