package ua.ukrpost.service;

import ua.ukrpost.dao.PostcodePoolDao;
import ua.ukrpost.dto.PostcodePoolDto;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import ua.ukrpost.mapper.PostcodePoolMapper;
import ua.ukrpost.entity.PostcodePool;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.time.LocalDateTime.now;

import static ua.ukrpost.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static ua.ukrpost.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.deleteLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getAllLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.saveLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class PostcodePoolServiceImpl implements PostcodePoolService {
    private PostcodePoolDao postcodePoolDao;
    private PostcodePoolMapper postcodePoolMapper;

    @Autowired
    public PostcodePoolServiceImpl(PostcodePoolDao postcodePoolDao, PostcodePoolMapper postcodePoolMapper) {
        this.postcodePoolDao = postcodePoolDao;
        this.postcodePoolMapper = postcodePoolMapper;
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
        LocalDateTime now = now();
        postcodePool.setCreated(now);
        postcodePool.setLastModified(now);
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
            copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(PostcodePool.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    PostcodePool.class, source, target, e));
        }
        target.setUuid(uuid);
        target.setLastModified(now());
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
