package com.opinta.service;

import com.opinta.dao.BarcodeInnerNumberDao;
import com.opinta.dao.PostcodePoolDao;
import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import com.opinta.model.BarcodeInnerNumber;
import com.opinta.model.PostcodePool;
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
    private PostcodePoolDao postcodePoolDao;
    private BarcodeInnerNumberMapper barcodeInnerNumberMapper;

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
    public List<BarcodeInnerNumberDto> getAll() {
        log.info("Getting all barcodeInnerNumbers");
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberDao.getAll());
    }

    @Override
    @Transactional
    public BarcodeInnerNumberDto getById(Long id) {
        log.info("Getting barcodeInnerNumber by id {}", id);
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberDao.getById(id));
    }

    @Override
    @Transactional
    public boolean save(long postcodeId, BarcodeInnerNumberDto barcodeInnerNumberDto) {
//        log.info("Saving barcodeInnerNumber {}", barcodeInnerNumberDto);
//        barcodeInnerNumberDao.save(barcodeInnerNumberMapper.toEntity(barcodeInnerNumberDto));
        PostcodePool postcodePool = postcodePoolDao.getById(postcodeId);
        if (postcodePool == null) {
            log.debug("Can't add barcodeInnerNumberDto to postcodePool. PostCodePool {} doesn't exist", postcodeId);
            return false;
        }
        BarcodeInnerNumber barcodeInnerNumber = barcodeInnerNumberMapper.toEntity(barcodeInnerNumberDto);
        postcodePool.getBarcodeInnerNumbers().add(barcodeInnerNumber);
        log.info("Adding barcodeInnerNumberDto {} to postcodePool {}", barcodeInnerNumber, postcodePool);
        postcodePoolDao.update(postcodePool);
        return true;
    }

    @Override
    @Transactional
    public BarcodeInnerNumberDto update(Long id, BarcodeInnerNumberDto barcodeInnerNumberDto) {
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
        }
        target.setId(id);
        log.info("Updating barcodeInnerNumber {}", target);
        barcodeInnerNumberDao.update(target);
        return barcodeInnerNumberMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        BarcodeInnerNumber barcodeInnerNumber = barcodeInnerNumberDao.getById(id);
        if (barcodeInnerNumber == null) {
            log.debug("Can't delete barcodeInnerNumber. BarcodeInnerNumber doesn't exist {}", id);
            return false;
        }
        log.info("Deleting barcodeInnerNumber {}", barcodeInnerNumber);
        barcodeInnerNumberDao.delete(barcodeInnerNumber);
        return true;
    }
}
