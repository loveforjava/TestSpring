package com.opinta.service;

import com.opinta.dao.PostcodePoolDao;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import com.opinta.mapper.PostcodePoolMapper;
import com.opinta.entity.PostcodePool;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

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
    public List<PostcodePool> getAllEntities() {
        log.info("Getting all postcodePools");
        return postcodePoolDao.getAll();
    }

    @Override
    public PostcodePool getEntityByUuid(UUID uuid) {
        log.info("Getting postcodePool by uuid {}", uuid);
        return postcodePoolDao.getByUuid(uuid);
    }

    @Override
    @Transactional
    public PostcodePool saveEntity(PostcodePool postcodePool) {
        log.info("Saving postcodePool {}", postcodePool);
        return postcodePoolDao.save(postcodePool);
    }

    @Override
    @Transactional
    public List<PostcodePoolDto> getAll() {
        return postcodePoolMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public PostcodePoolDto getByUuid(UUID uuid) {
        return postcodePoolMapper.toDto(getEntityByUuid(uuid));
    }

    @Override
    @Transactional
    public PostcodePoolDto save(PostcodePoolDto postcodePoolDto) {
        return postcodePoolMapper.toDto(saveEntity(postcodePoolMapper.toEntity(postcodePoolDto)));
    }

    @Override
    @Transactional
    public PostcodePoolDto update(UUID uuid, PostcodePoolDto postcodePoolDto) {
        PostcodePool source = postcodePoolMapper.toEntity(postcodePoolDto);
        PostcodePool target = postcodePoolDao.getByUuid(uuid);
        if (target == null) {
            log.debug("Can't update postcodePool. PostCodePool doesn't exist {}", uuid);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for postcodePool", e);
        }
        target.setUuid(uuid);
        log.info("Updating postcodePool {}", target);
        postcodePoolDao.update(target);
        return postcodePoolMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(UUID uuid) {
        PostcodePool postcodePool = postcodePoolDao.getByUuid(uuid);
        if (postcodePool == null) {
            log.debug("Can't delete postcodePool. PostCodePool doesn't exist {}", uuid);
            return false;
        }
        postcodePool.setUuid(uuid);
        log.info("Deleting postcodePool {}", postcodePool);
        postcodePoolDao.delete(postcodePool);
        return true;
    }
}
