package com.opinta.service;

import com.opinta.dao.PostcodePoolDao;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import com.opinta.mapper.PostcodePoolMapper;
import com.opinta.entity.PostcodePool;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.opinta.util.CustomBeanUtils.copyNonNullProperties;
import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class PostcodePoolServiceImpl implements PostcodePoolService {
    private PostcodePoolDao postcodePoolDao;
    private PostcodePoolMapper postcodePoolMapper;
    private BarcodeInnerNumberMapper barcodeInnerNumberMapper;

    @Autowired
    public PostcodePoolServiceImpl(PostcodePoolDao postcodePoolDao, PostcodePoolMapper postcodePoolMapper,
                                   BarcodeInnerNumberMapper barcodeInnerNumberMapper) {
        this.postcodePoolDao = postcodePoolDao;
        this.postcodePoolMapper = postcodePoolMapper;
        this.barcodeInnerNumberMapper = barcodeInnerNumberMapper;
    }

    @Override
    @Transactional
    public List<PostcodePool> getAllEntities() {
        log.info(getAllLogEndpoint(PostcodePool.class));
        return postcodePoolDao.getAll();
    }

    @Override
    @Transactional
    public PostcodePool getEntityByUuid(UUID uuid) throws IncorrectInputDataException {
        log.info(getByIdLogEndpoint(PostcodePool.class, uuid));
        PostcodePool postcodePool = postcodePoolDao.getByUuid(uuid);
        if (postcodePool == null) {
            log.error(getByIdOnErrorLogEndpoint(PostcodePool.class, uuid));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(PostcodePool.class, uuid));
        }
        return postcodePool;
    }

    @Override
    @Transactional
    public PostcodePool saveEntity(PostcodePool postcodePool) {
        log.info(saveLogEndpoint(PostcodePool.class, postcodePool));
        return postcodePoolDao.save(postcodePool);
    }

    @Override
    @Transactional
    public List<PostcodePoolDto> getAll() {
        return postcodePoolMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public PostcodePoolDto getByUuid(UUID uuid) throws IncorrectInputDataException {
        return postcodePoolMapper.toDto(getEntityByUuid(uuid));
    }

    @Override
    @Transactional
    public PostcodePoolDto save(PostcodePoolDto postcodePoolDto) {
        return postcodePoolMapper.toDto(saveEntity(postcodePoolMapper.toEntity(postcodePoolDto)));
    }

    @Override
    @Transactional
    public PostcodePoolDto update(UUID uuid, PostcodePoolDto postcodePoolDto) throws IncorrectInputDataException,
            PerformProcessFailedException {
        PostcodePool source = postcodePoolMapper.toEntity(postcodePoolDto);
        PostcodePool target = getEntityByUuid(uuid);
        try {
            copyNonNullProperties(target, source);
        } catch (Exception e) {
            log.error(copyPropertiesOnErrorLogEndpoint(PostcodePool.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    PostcodePool.class, source, target, e));
        }
        target.setUuid(uuid);
        log.info(updateLogEndpoint(PostcodePool.class, target));
        postcodePoolDao.update(target);
        return postcodePoolMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(UUID uuid) throws IncorrectInputDataException {
        log.info(deleteLogEndpoint(PostcodePool.class, uuid));
        PostcodePool postcodePool = getEntityByUuid(uuid);
        postcodePoolDao.delete(postcodePool);
    }
}
