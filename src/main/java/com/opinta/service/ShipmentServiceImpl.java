package com.opinta.service;

import com.opinta.dao.ShipmentDao;
import com.opinta.dto.ShipmentDto;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.model.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {
    private ShipmentDao shipmentDao;
    private ShipmentMapper shipmentMapper;

    @Autowired
    public ShipmentServiceImpl(ShipmentDao shipmentDao, ShipmentMapper shipmentMapper) {
        this.shipmentDao = shipmentDao;
        this.shipmentMapper = shipmentMapper;
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAll() {
        log.info("Getting all shipments");
        return shipmentMapper.toDto(shipmentDao.getAll());
    }

    @Override
    @Transactional
    public ShipmentDto getById(long id) {
        log.info("Getting postcodePool by id {}", id);
        return shipmentMapper.toDto(shipmentDao.getById(id));
    }

    @Override
    @Transactional
    public ShipmentDto save(ShipmentDto shipmentDto) {
        log.info("Saving shipment {}", shipmentDto);
        return shipmentMapper.toDto(shipmentDao.save(shipmentMapper.toEntity(shipmentDto)));
    }

    @Override
    @Transactional
    public ShipmentDto update(long id, ShipmentDto shipmentDto) {
        Shipment source = shipmentMapper.toEntity(shipmentDto);
        Shipment target = shipmentDao.getById(id);
        if (target == null) {
            log.debug("Can't update shipment. Shipment doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for shipment", e);
        }
        target.setId(id);
        log.info("Updating shipment {}", target);
        shipmentDao.update(target);
        return shipmentMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Shipment shipment = shipmentDao.getById(id);
        if (shipment == null) {
            log.debug("Can't delete shipment. Shipment doesn't exist {}", id);
            return false;
        }
        shipment.setId(id);
        log.info("Deleting shipment {}", shipment);
        shipmentDao.delete(shipment);
        return true;
    }
}
