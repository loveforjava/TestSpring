package com.opinta.service;

import com.opinta.dao.CustomerDao;
import com.opinta.dao.PostcodePoolDao;
import com.opinta.model.Customer;
import com.opinta.model.PostcodePool;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostcodePoolServiceImpl implements PostcodePoolService {
    @Autowired
    private PostcodePoolDao postcodePoolDao;

    @Override
    @Transactional
    public List<PostcodePool> getAll() {
        log.info("Getting all PostCodePools");
        return postcodePoolDao.getAll();
    }

    @Override
    @Transactional
    public PostcodePool getById(Long id) {
        log.info("Getting PostCodePool by id " + id);
        return postcodePoolDao.getById(id);
    }

    @Override
    @Transactional
    public void save(PostcodePool postcodePool) {
        log.info("Saving PostCodePool " + postcodePool);
        postcodePoolDao.save(postcodePool);
    }

    @Override
    @Transactional
    public PostcodePool update(Long id, PostcodePool postcodePool) {
        if (getById(id) == null) {
            log.info("Can't update PostCodePool. PostCodePools doesn't exist " + id);
            return null;
        }
        postcodePool.setId(id);
        log.info("Updating PostCodePools " + postcodePool);
        postcodePoolDao.update(postcodePool);
        return postcodePool;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (getById(id) == null) {
            log.debug("Can't delete PostCodePools. PostCodePools doesn't exist " + id);
            return false;
        }
        PostcodePool postcodePool = new PostcodePool();
        postcodePool.setId(id);
        log.info("Deleting PostCodePools " + postcodePool);
        postcodePoolDao.delete(postcodePool);
        return true;
    }
}
