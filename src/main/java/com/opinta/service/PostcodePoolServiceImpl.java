package com.opinta.service;

import com.opinta.dao.PostcodePoolDao;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import com.opinta.mapper.PostcodePoolMapper;
import com.opinta.entity.PostcodePool;
import java.util.List;
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
    public PostcodePool getEntityById(long id) {
        log.info("Getting postcodePool by id {}", id);
        return postcodePoolDao.getById(id);
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
    public PostcodePoolDto getById(long id) {
        return postcodePoolMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public PostcodePoolDto save(PostcodePoolDto postcodePoolDto) {
        return postcodePoolMapper.toDto(saveEntity(postcodePoolMapper.toEntity(postcodePoolDto)));
    }

    @Override
    @Transactional
    public PostcodePoolDto update(long id, PostcodePoolDto postcodePoolDto) {
        PostcodePool source = postcodePoolMapper.toEntity(postcodePoolDto);
        PostcodePool target = postcodePoolDao.getById(id);
        if (target == null) {
            log.debug("Can't update postcodePool. PostCodePool doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for postcodePool", e);
        }
        target.setId(id);
        log.info("Updating postcodePool {}", target);
        postcodePoolDao.update(target);
        return postcodePoolMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        PostcodePool postcodePool = postcodePoolDao.getById(id);
        if (postcodePool == null) {
            log.debug("Can't delete postcodePool. PostCodePool doesn't exist {}", id);
            return false;
        }
        postcodePool.setId(id);
        log.info("Deleting postcodePool {}", postcodePool);
        postcodePoolDao.delete(postcodePool);
        return true;
    }
}
