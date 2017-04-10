package com.opinta.service;

import com.opinta.dao.PostOfficeDao;
import com.opinta.dto.PostOfficeDto;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.mapper.PostOfficeMapper;
import com.opinta.entity.PostOffice;
import com.opinta.util.LogMessageUtil;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.opinta.util.CustomBeanUtils.copyNonNullProperties;
import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class PostOfficeServiceImpl implements PostOfficeService {
    private PostOfficeDao postOfficeDao;
    private PostOfficeMapper postOfficeMapper;

    @Autowired
    public PostOfficeServiceImpl(PostOfficeDao postOfficeDao, PostOfficeMapper postOfficeMapper) {
        this.postOfficeDao = postOfficeDao;
        this.postOfficeMapper = postOfficeMapper;
    }

    @Override
    @Transactional
    public List<PostOffice> getAllEntities() {
        log.info(LogMessageUtil.getAllLogEndpoint(PostOffice.class));
        return postOfficeDao.getAll();
    }

    @Override
    @Transactional
    public PostOffice getEntityById(long id) throws IncorrectInputDataException {
        log.info(getByIdLogEndpoint(PostOffice.class, id));
        PostOffice postOffice = postOfficeDao.getById(id);
        if (postOffice == null) {
            log.error(getByIdOnErrorLogEndpoint(PostOffice.class, id));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(PostOffice.class, id));
        }
        return postOffice;
    }

    @Override
    @Transactional
    public PostOffice saveEntity(PostOffice postOffice) {
        log.info(saveLogEndpoint(PostOffice.class, postOffice));
        return postOfficeDao.save(postOffice);
    }

    @Override
    @Transactional
    public List<PostOfficeDto> getAll() {
        return postOfficeMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public PostOfficeDto getById(long id) throws IncorrectInputDataException {
        return postOfficeMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public PostOfficeDto save(PostOfficeDto postOfficeDto) {
        return postOfficeMapper.toDto(saveEntity(postOfficeMapper.toEntity(postOfficeDto)));
    }

    @Override
    @Transactional
    public PostOfficeDto update(long id, PostOfficeDto postOfficeDto) throws IncorrectInputDataException,
            PerformProcessFailedException {
        PostOffice source = postOfficeMapper.toEntity(postOfficeDto);
        PostOffice target = getEntityById(id);
        try {
            copyNonNullProperties(target, source);
        } catch (Exception e) {
            log.error(copyPropertiesOnErrorLogEndpoint(PostOffice.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    PostOffice.class, source, target, e));
        }
        target.setId(id);
        log.info(updateLogEndpoint(PostOffice.class, target));
        postOfficeDao.update(target);
        return postOfficeMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(long id) throws IncorrectInputDataException {
        log.info(deleteLogEndpoint(PostOffice.class, id));
        PostOffice postOffice = getEntityById(id);
        postOfficeDao.delete(postOffice);
    }
}
