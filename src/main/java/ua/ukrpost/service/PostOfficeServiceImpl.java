package ua.ukrpost.service;

import ua.ukrpost.dao.PostOfficeDao;
import ua.ukrpost.dto.PostOfficeDto;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import ua.ukrpost.mapper.PostOfficeMapper;
import ua.ukrpost.entity.PostOffice;
import ua.ukrpost.util.LogMessageUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.ukrpost.util.EnhancedBeanUtilsBean;

import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;

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
        log.info(LogMessageUtil.getByIdLogEndpoint(PostOffice.class, id));
        PostOffice postOffice = postOfficeDao.getById(id);
        if (postOffice == null) {
            log.error(LogMessageUtil.getByIdOnErrorLogEndpoint(PostOffice.class, id));
            throw new IncorrectInputDataException(LogMessageUtil.getByIdOnErrorLogEndpoint(PostOffice.class, id));
        }
        return postOffice;
    }

    @Override
    @Transactional
    public PostOffice saveEntity(PostOffice postOffice) {
        log.info(LogMessageUtil.saveLogEndpoint(PostOffice.class, postOffice));
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
            EnhancedBeanUtilsBean.copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(LogMessageUtil.copyPropertiesOnErrorLogEndpoint(PostOffice.class, source, target, e));
            throw new PerformProcessFailedException(LogMessageUtil.copyPropertiesOnErrorLogEndpoint(
                    PostOffice.class, source, target, e));
        }
        target.setId(id);
        log.info(LogMessageUtil.updateLogEndpoint(PostOffice.class, target));
        postOfficeDao.update(target);
        return postOfficeMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(long id) throws IncorrectInputDataException {
        log.info(LogMessageUtil.deleteLogEndpoint(PostOffice.class, id));
        PostOffice postOffice = getEntityById(id);
        postOfficeDao.delete(postOffice);
    }
}
