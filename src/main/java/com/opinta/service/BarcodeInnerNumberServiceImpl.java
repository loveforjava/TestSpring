package com.opinta.service;

import java.util.List;

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
    private final BarcodeInnerNumberGenerator barcodeInnerNumberGenerator;

    @Autowired
    public BarcodeInnerNumberServiceImpl(BarcodeInnerNumberDao barcodeInnerNumberDao,
                                         BarcodeInnerNumberMapper barcodeInnerNumberMapper,
                                         PostcodePoolDao postcodePoolDao,
                                         BarcodeInnerNumberGenerator barcodeInnerNumberGenerator) {
        this.barcodeInnerNumberDao = barcodeInnerNumberDao;
        this.barcodeInnerNumberMapper = barcodeInnerNumberMapper;
        this.postcodePoolDao = postcodePoolDao;
        this.barcodeInnerNumberGenerator = barcodeInnerNumberGenerator;
    }

    @Override
    @Transactional
    public BarcodeInnerNumber getEntityById(long id) {
        return barcodeInnerNumberDao.getById(id);
    }

    @Override
    @Transactional
    public List<BarcodeInnerNumberDto> getAll(long postcodePoolId) {
        PostcodePool postcodePool = postcodePoolDao.getById(postcodePoolId);
        if (postcodePool == null) {
            log.debug("Can't get barcodeInnerNumberDto list by postcodePool. PostCodePool {} doesn't exist",
                    postcodePoolId);
            return null;
        }
        log.info("Getting all barcodeInnerNumbers by postcodePoolId {}", postcodePoolId);
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberDao.getAll(postcodePool));
    }

    @Override
    @Transactional
    public BarcodeInnerNumberDto getById(long id) {
        log.info("Getting barcodeInnerNumber by uuid {}", id);
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberDao.getById(id));
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
        return barcodeInnerNumberGenerator.generate(postcodePool);
    }
}
