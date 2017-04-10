package com.opinta.service;

import com.opinta.dao.ShipmentTrackingDetailDao;
import com.opinta.dto.ShipmentTrackingDetailDto;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.mapper.ShipmentTrackingDetailMapper;
import com.opinta.entity.ShipmentTrackingDetail;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.opinta.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;

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
            copyNotNullProperties(target, source);
        } catch (Exception e) {
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
