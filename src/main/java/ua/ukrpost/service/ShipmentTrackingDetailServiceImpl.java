package ua.ukrpost.service;

import ua.ukrpost.dao.ShipmentTrackingDetailDao;
import ua.ukrpost.dto.ShipmentTrackingDetailDto;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import ua.ukrpost.mapper.ShipmentTrackingDetailMapper;
import ua.ukrpost.entity.ShipmentTrackingDetail;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.ukrpost.util.EnhancedBeanUtilsBean;

import static ua.ukrpost.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.deleteLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getAllLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.saveLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class ShipmentTrackingDetailServiceImpl implements ShipmentTrackingDetailService {
    private ShipmentTrackingDetailDao shipmentTrackingDetailDao;
    private ShipmentTrackingDetailMapper shipmentTrackingDetailMapper;

    @Autowired
    public ShipmentTrackingDetailServiceImpl(ShipmentTrackingDetailDao shipmentTrackingDetailDao,
                                             ShipmentTrackingDetailMapper shipmentTrackingDetailMapper) {
        this.shipmentTrackingDetailDao = shipmentTrackingDetailDao;
        this.shipmentTrackingDetailMapper = shipmentTrackingDetailMapper;
    }

    @Override
    public ShipmentTrackingDetail getEntityByUuid(long id) throws IncorrectInputDataException {
        log.info(getByIdLogEndpoint(ShipmentTrackingDetail.class, id));
        ShipmentTrackingDetail shipmentTrackingDetail = shipmentTrackingDetailDao.getById(id);
        if (shipmentTrackingDetail == null) {
            log.error(getByIdOnErrorLogEndpoint(ShipmentTrackingDetail.class, id));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(ShipmentTrackingDetail.class, id));
        }
        return shipmentTrackingDetail;
    }

    @Override
    @Transactional
    public List<ShipmentTrackingDetailDto> getAll() {
        log.info(getAllLogEndpoint(ShipmentTrackingDetail.class));
        return shipmentTrackingDetailMapper.toDto(shipmentTrackingDetailDao.getAll());
    }

    @Override
    @Transactional
    public ShipmentTrackingDetailDto getById(long id) throws IncorrectInputDataException {
        return shipmentTrackingDetailMapper.toDto(getEntityByUuid(id));
    }

    @Override
    @Transactional
    public ShipmentTrackingDetailDto save(ShipmentTrackingDetailDto shipmentTrackingDetailDto) {
        log.info(saveLogEndpoint(ShipmentTrackingDetail.class, shipmentTrackingDetailDto));
        return shipmentTrackingDetailMapper.toDto(shipmentTrackingDetailDao.save(
                shipmentTrackingDetailMapper.toEntity(shipmentTrackingDetailDto)));
    }

    @Override
    @Transactional
    public ShipmentTrackingDetailDto update(long id, ShipmentTrackingDetailDto shipmentTrackingDetailDto)
            throws IncorrectInputDataException, PerformProcessFailedException {
        ShipmentTrackingDetail source = shipmentTrackingDetailMapper.toEntity(shipmentTrackingDetailDto);
        ShipmentTrackingDetail target = getEntityByUuid(id);
        try {
            EnhancedBeanUtilsBean.copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(ShipmentTrackingDetail.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    ShipmentTrackingDetail.class, source, target, e));
        }
        target.setId(id);
        log.info(updateLogEndpoint(ShipmentTrackingDetail.class, target));
        shipmentTrackingDetailDao.update(target);
        return shipmentTrackingDetailMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(long id) throws IncorrectInputDataException {
        log.info(deleteLogEndpoint(ShipmentTrackingDetail.class, id));
        ShipmentTrackingDetail shipmentTrackingDetail = getEntityByUuid(id);
        shipmentTrackingDetailDao.delete(shipmentTrackingDetail);
    }
}
