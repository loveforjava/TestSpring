package com.opinta.service;

import com.opinta.dao.CustomerDao;
import com.opinta.dao.PostcodePoolDao;
import com.opinta.model.Customer;
import com.opinta.model.PostcodePool;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class PostcodePoolServiceImpl implements PostcodePoolService {
    @Autowired
    private PostcodePoolDao postcodePoolDao;

    @Override
    @Transactional
    public List<PostcodePool> getAll() {
        log.info("Getting all postcodePools");
        return postcodePoolDao.getAll();
    }

    @Override
    @Transactional
    public PostcodePool getById(Long id) {
        log.info("Getting postcodePool by id " + id);
        return postcodePoolDao.getById(id);
    }

    @Override
    @Transactional
    public void save(PostcodePool postcodePool) {
        log.info("Saving postcodePool " + postcodePool);
        postcodePoolDao.save(postcodePool);
    }

    @Override
    @Transactional
    public PostcodePool update(Long id, PostcodePool source) {
        PostcodePool target = getById(id);
        if (target == null) {
            log.info("Can't update postcodePool. PostCodePool doesn't exist " + id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for postcodePool", e);
        }
        target.setId(id);
        log.info("Updating postcodePool " + target);
        postcodePoolDao.update(target);
        return source;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        PostcodePool postcodePool = getById(id);
        if (postcodePool == null) {
            log.debug("Can't delete postcodePool. PostCodePool doesn't exist " + id);
            return false;
        }
        postcodePool.setId(id);
        log.info("Deleting postcodePool " + postcodePool);
        postcodePoolDao.delete(postcodePool);
        return true;
    }
}
