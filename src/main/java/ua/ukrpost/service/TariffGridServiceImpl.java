package ua.ukrpost.service;

import ua.ukrpost.dao.TariffGridDao;
import ua.ukrpost.dto.classifier.TariffGridDto;
import ua.ukrpost.entity.classifier.TariffGrid;
import ua.ukrpost.entity.W2wVariation;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.transaction.Transactional;

import ua.ukrpost.mapper.TariffGridMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class TariffGridServiceImpl implements TariffGridService {
    private final TariffGridDao tariffGridDao;
    private final TariffGridMapper tariffGridMapper;

    @Autowired
    public TariffGridServiceImpl(TariffGridDao tariffGridDao, TariffGridMapper tariffGridMapper) {
        this.tariffGridDao = tariffGridDao;
        this.tariffGridMapper = tariffGridMapper;
    }
    
    @Override
    @Transactional
    public List<TariffGrid> getAllEntities() {
        log.info(getAllLogEndpoint(TariffGrid.class));
        return tariffGridDao.getAll();
    }

    @Override
    @Transactional
    public TariffGrid getEntityById(long id) {
        log.info(getByIdLogEndpoint(TariffGrid.class, id));
        return tariffGridDao.getById(id);
    }
    
    @Override
    @Transactional
    public TariffGrid getEntityByDimension(float weight, float length, W2wVariation w2wVariation) {
        return tariffGridDao.getByDimension(weight, length, w2wVariation);
    }

    @Override
    @Transactional
    public TariffGrid getMaxTariffEntity(W2wVariation w2wVariation) {
        return tariffGridDao.getLast(w2wVariation);
    }
    
    @Override
    @Transactional
    public List<TariffGridDto> getAll() {
        return tariffGridMapper.toDto(tariffGridDao.getAll());
    }
    
    @Override
    @Transactional
    public TariffGridDto getByDimension(float weight, float length, W2wVariation w2wVariation) {
        return tariffGridMapper.toDto(tariffGridDao.getByDimension(weight, length, w2wVariation));
    }
    
    @Override
    @Transactional
    public TariffGridDto getById(long id) throws IncorrectInputDataException {
        TariffGrid tariff = tariffGridDao.getById(id);
        if (tariff == null) {
            log.error(getByIdOnErrorLogEndpoint(TariffGrid.class, id));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(TariffGrid.class, id));
        }
        return tariffGridMapper.toDto(tariff);
    }
    
    @Override
    @Transactional
    public TariffGrid save(TariffGrid tariffGrid) {
        log.info(saveLogEndpoint(TariffGrid.class, tariffGrid));
        return tariffGridDao.save(tariffGrid);
    }
    
    @Override
    @Transactional
    public TariffGrid update(long id, TariffGrid source) throws PerformProcessFailedException {
        TariffGrid target = tariffGridDao.getById(id);
        if (target == null) {
            log.info(getByIdOnErrorLogEndpoint(TariffGrid.class, id));
            return null;
        }
        try {
            copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(TariffGrid.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    TariffGrid.class, source, target, e));
        }
        target.setId(id);
        log.info(updateLogEndpoint(TariffGrid.class, target));
        tariffGridDao.update(target);
        return target;
    }
    
    @Override
    @Transactional
    public boolean delete(long id) {
        TariffGrid tariffGrid = tariffGridDao.getById(id);
        if (tariffGrid == null) {
            log.debug(getByIdOnErrorLogEndpoint(TariffGrid.class, id));
            return false;
        }
        log.info(deleteLogEndpoint(TariffGrid.class, id));
        tariffGridDao.delete(tariffGrid);
        return true;
    }
}
