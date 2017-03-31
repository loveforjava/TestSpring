package com.opinta.service;

import com.opinta.entity.BarcodeStatus;
import java.util.List;

import java.util.Random;
import javax.transaction.Transactional;

import com.opinta.dao.BarcodeInnerNumberDao;
import com.opinta.dao.PostcodePoolDao;
import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class BarcodeInnerNumberServiceImpl implements BarcodeInnerNumberService {
    
    private final BarcodeInnerNumberDao barcodeInnerNumberDao;
    private final PostcodePoolDao postcodePoolDao;
    private final BarcodeInnerNumberMapper barcodeInnerNumberMapper;

    @Autowired
    public BarcodeInnerNumberServiceImpl(BarcodeInnerNumberDao barcodeInnerNumberDao,
                                         BarcodeInnerNumberMapper barcodeInnerNumberMapper,
                                         PostcodePoolDao postcodePoolDao) {
        this.barcodeInnerNumberDao = barcodeInnerNumberDao;
        this.barcodeInnerNumberMapper = barcodeInnerNumberMapper;
        this.postcodePoolDao = postcodePoolDao;
    }

    @Override
    @Transactional
    public BarcodeInnerNumber getEntityById(long id) {
        return barcodeInnerNumberDao.getById(id);
    }

    @Override
    @Transactional
    public BarcodeInnerNumber saveEntity(BarcodeInnerNumber barcodeInnerNumber) {
        return barcodeInnerNumberDao.save(barcodeInnerNumber);
    }

    @Override
    @Transactional
    public List<BarcodeInnerNumberDto> getAll(long postcodeId) {
        PostcodePool postcodePool = postcodePoolDao.getById(postcodeId);
        if (postcodePool == null) {
            log.debug("Can't get barcodeInnerNumberDto list by postcodePool. PostCodePool {} doesn't exist", postcodeId);
            return null;
        }
        log.info("Getting all barcodeInnerNumbers by postcodeId {}", postcodeId);
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberDao.getAll(postcodeId));
    }

    @Override
    @Transactional
    public BarcodeInnerNumberDto getById(long id) {
        log.info("Getting barcodeInnerNumber by uuid {}", id);
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberDao.getById(id));
    }
    
    @Override
    @Transactional
    public BarcodeInnerNumberDto save(long postcodeId, BarcodeInnerNumberDto barcodeInnerNumberDto) {
        PostcodePool postcodePool = postcodePoolDao.getById(postcodeId);
        if (postcodePool == null) {
            log.debug("Can't add barcodeInnerNumberDto to postcodePool. PostCodePool {} doesn't exist", postcodeId);
            return null;
        }
        BarcodeInnerNumber barcodeInnerNumber = barcodeInnerNumberMapper.toEntity(barcodeInnerNumberDto);
        BarcodeInnerNumber barcodeInnerNumberSaved = barcodeInnerNumberDao.save(barcodeInnerNumber);
        // TODO not to get, but set previously created list and check in db if previous values not erased
        postcodePool.getBarcodeInnerNumbers().add(barcodeInnerNumberSaved);
        log.info("Adding barcodeInnerNumber {} to postcodePool {}", barcodeInnerNumber, postcodePool);
        postcodePoolDao.update(postcodePool);
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberSaved);
    }

    @Override
    @Transactional
    public BarcodeInnerNumberDto update(long id, BarcodeInnerNumberDto barcodeInnerNumberDto) {
        BarcodeInnerNumber source = barcodeInnerNumberMapper.toEntity(barcodeInnerNumberDto);
        BarcodeInnerNumber target = barcodeInnerNumberDao.getById(id);
        if (target == null) {
            log.info("Can't update barcodeInnerNumber. BarcodeInnerNumber doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for barcodeInnerNumber", e);
            return null;
        }
        target.setId(id);
        log.info("Updating barcodeInnerNumber {}", target);
        barcodeInnerNumberDao.update(target);
        return barcodeInnerNumberMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        BarcodeInnerNumber barcodeInnerNumber = barcodeInnerNumberDao.getById(id);
        if (barcodeInnerNumber == null) {
            log.debug("Can't delete barcodeInnerNumber. BarcodeInnerNumber doesn't exist {}", id);
            return false;
        }
        log.info("Deleting barcodeInnerNumber {}", barcodeInnerNumber);
        barcodeInnerNumberDao.delete(barcodeInnerNumber);
        return true;
    }
    
    @Override
    @Transactional
    public BarcodeInnerNumber generateBarcodeInnerNumber(PostcodePool postcodePool) {
        BarcodeInnerNumber barcode = barcodeInnerNumberDao.generateForPostcodePool(postcodePool);
        log.info("generated barcode: " + barcode.toString());
        return barcode;

//        // for inmemory DB
//        Random random = new Random();
//        int min = 11111111;
//        int max = 99999999;
//        Integer randomNum = random.nextInt((max - min) + 1) + min;
//        BarcodeInnerNumber barcodeInnerNumber = new BarcodeInnerNumber(randomNum.toString(), BarcodeStatus.RESERVED);
//        barcodeInnerNumber = barcodeInnerNumberDao.save(barcodeInnerNumber);
//        log.info("generated barcode: {}", randomNum);
//        return barcodeInnerNumber;
    }
}
