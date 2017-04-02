package com.opinta.service;

import com.opinta.exception.IncorrectInputDataException;
import java.util.List;

import java.util.UUID;
import javax.transaction.Transactional;

import com.opinta.dao.BarcodeInnerNumberDao;
import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllByFieldLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;

@Service
@Slf4j
public class BarcodeInnerNumberServiceImpl implements BarcodeInnerNumberService {
    private final BarcodeInnerNumberDao barcodeInnerNumberDao;
    private final PostcodePoolService postcodePoolService;
    private final BarcodeInnerNumberMapper barcodeInnerNumberMapper;
    private final BarcodeInnerNumberGenerator barcodeInnerNumberGenerator;

    @Autowired
    public BarcodeInnerNumberServiceImpl(BarcodeInnerNumberDao barcodeInnerNumberDao,
                                         BarcodeInnerNumberMapper barcodeInnerNumberMapper,
                                         PostcodePoolService postcodePoolService,
                                         BarcodeInnerNumberGenerator barcodeInnerNumberGenerator) {
        this.barcodeInnerNumberDao = barcodeInnerNumberDao;
        this.barcodeInnerNumberMapper = barcodeInnerNumberMapper;
        this.postcodePoolService = postcodePoolService;
        this.barcodeInnerNumberGenerator = barcodeInnerNumberGenerator;
    }

    @Override
    @Transactional
    public BarcodeInnerNumber getEntityById(long id) throws IncorrectInputDataException {
        log.info(getByIdLogEndpoint(BarcodeInnerNumber.class, id));
        BarcodeInnerNumber barcodeInnerNumber = barcodeInnerNumberDao.getById(id);
        if (barcodeInnerNumber == null) {
            log.error(getByIdOnErrorLogEndpoint(BarcodeInnerNumber.class, id));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(BarcodeInnerNumber.class, id));
        }
        return barcodeInnerNumber;
    }

    @Override
    @Transactional
    public List<BarcodeInnerNumberDto> getAll(UUID postcodePoolUuid) throws IncorrectInputDataException {
        log.info(getAllByFieldLogEndpoint(BarcodeInnerNumber.class, PostcodePool.class, postcodePoolUuid));
        PostcodePool postcodePool = postcodePoolService.getEntityByUuid(postcodePoolUuid);
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberDao.getAll(postcodePool));
    }

    @Override
    @Transactional
    public BarcodeInnerNumberDto getById(long id) throws IncorrectInputDataException {
        return barcodeInnerNumberMapper.toDto(getEntityById(id));
    }
    
    @Override
    @Transactional
    public void delete(long id) throws IncorrectInputDataException {
        log.info(deleteLogEndpoint(BarcodeInnerNumber.class, id));
        BarcodeInnerNumber barcodeInnerNumber = getEntityById(id);
        barcodeInnerNumberDao.delete(barcodeInnerNumber);
    }
    
    @Override
    @Transactional
    public BarcodeInnerNumber generateBarcodeInnerNumber(PostcodePool postcodePool) {
        return barcodeInnerNumberGenerator.generate(postcodePool);
    }
}
