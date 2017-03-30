package com.opinta.service;

import com.opinta.dao.ShipmentGroupDao;
import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.ShipmentGroup;
import com.opinta.mapper.ShipmentGroupMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static com.opinta.util.LogMessageUtil.getAllByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllLogEndpoint;
import static java.lang.String.format;

@Service
@Slf4j
public class ShipmentGroupServiceImpl implements ShipmentGroupService {
    private final ShipmentGroupDao shipmentGroupDao;
    private final CounterpartyService counterpartyService;
    private final ShipmentGroupMapper shipmentGroupMapper;

    @Autowired
    public ShipmentGroupServiceImpl(ShipmentGroupDao shipmentGroupDao, CounterpartyService counterpartyService,
                                    ShipmentGroupMapper shipmentGroupMapper) {
        this.shipmentGroupDao = shipmentGroupDao;
        this.counterpartyService = counterpartyService;
        this.shipmentGroupMapper = shipmentGroupMapper;
    }

    @Override
    @Transactional
    public List<ShipmentGroup> getAllEntities() {
        log.info(getAllLogEndpoint(ShipmentGroup.class));
        return shipmentGroupDao.getAll();
    }

    @Override
    @Transactional
    public ShipmentGroup getEntityById(UUID uuid) {
        // TODO finish testing
        long someLong = 100L;
        log.info(getAllByIdLogEndpoint(ShipmentGroup.class, uuid));
        log.info(getAllByIdLogEndpoint(ShipmentGroup.class, someLong));
        return shipmentGroupDao.getById(uuid);
    }

    @Override
    @Transactional
    public ShipmentGroup saveEntity(ShipmentGroup shipmentGroup) {
        log.info("Saving address {}", shipmentGroup);
        return shipmentGroupDao.save(shipmentGroup);
    }

    @Override
    @Transactional
    public ShipmentGroup updateEntity(UUID uuid, ShipmentGroup source) throws Exception {
        log.info("Updating address {}", source);
        ShipmentGroup target = shipmentGroupDao.getById(uuid);
        if (target == null) {
            log.debug("Can't update address. Address doesn't exist {}", uuid);
            throw new Exception(format("Can't update address. Address doesn't exist %s", uuid));
        }
        try {
            BeanUtils.copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for address", e);
            throw new Exception("Can't get properties from object to updatable object for address", e);
        }
        target.setUuid(uuid);
        shipmentGroupDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public List<ShipmentGroupDto> getAll() {
        log.info("Getting all clients");
        List<ShipmentGroup> shipmentGroups = shipmentGroupDao.getAll();
        return shipmentGroupMapper.toDto(shipmentGroups);
    }

    @Override
    @Transactional
    public List<ShipmentGroupDto> getAllByCounterpartyId(long counterpartyId) {
        Counterparty counterparty = counterpartyService.getEntityById(counterpartyId);
        if (counterparty == null) {
            log.debug("Can't get client list by counterparty. Counterparty {} doesn't exist", counterpartyId);
            return null;
        }
        log.info("Getting all clients by counterparty {}", counterparty);
        return shipmentGroupMapper.toDto(shipmentGroupDao.getAllByCounterparty(counterparty));
    }

    @Override
    @Transactional
    public ShipmentGroupDto getById(UUID uuid) {
        log.info("Getting client by id {}", uuid);
        ShipmentGroup shipmentGroup = shipmentGroupDao.getById(uuid);
        return shipmentGroupMapper.toDto(shipmentGroup);
    }

    @Override
    @Transactional
    public ShipmentGroupDto save(ShipmentGroupDto shipmentGroupDto) throws Exception {
        return shipmentGroupMapper.toDto(saveEntity(shipmentGroupMapper.toEntity(shipmentGroupDto)));
    }

    @Override
    @Transactional
    public ShipmentGroupDto update(UUID uuid, ShipmentGroupDto shipmentGroupDto) throws Exception {
        return shipmentGroupMapper.toDto(updateEntity(uuid, shipmentGroupMapper.toEntity(shipmentGroupDto)));
    }

    @Override
    @Transactional
    public boolean delete(UUID uuid) {
        ShipmentGroup shipmentGroup = shipmentGroupDao.getById(uuid);
        if (shipmentGroup == null) {
            log.error("Can't delete client. Client doesn't exist {}", uuid);
            return false;
        }
        log.info("Deleting client {}", shipmentGroup);
        shipmentGroupDao.delete(shipmentGroup);
        return true;
    }
}
