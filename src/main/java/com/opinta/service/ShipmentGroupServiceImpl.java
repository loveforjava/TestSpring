package com.opinta.service;

import com.opinta.dao.ShipmentGroupDao;
import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.mapper.ShipmentGroupMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllLogEndpoint;
import static com.opinta.util.LogMessageUtil.getOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateOnErrorLogEndpoint;
import static org.apache.commons.beanutils.PropertyUtils.copyProperties;

@Service
@Slf4j
public class ShipmentGroupServiceImpl implements ShipmentGroupService {
    private final ShipmentGroupDao shipmentGroupDao;
    private final CounterpartyService counterpartyService;
    private final ShipmentGroupMapper shipmentGroupMapper;
    private final UserService userService;

    @Autowired
    public ShipmentGroupServiceImpl(ShipmentGroupDao shipmentGroupDao, CounterpartyService counterpartyService,
                                    ShipmentGroupMapper shipmentGroupMapper, UserService userService) {
        this.shipmentGroupDao = shipmentGroupDao;
        this.counterpartyService = counterpartyService;
        this.shipmentGroupMapper = shipmentGroupMapper;
        this.userService = userService;
    }

    @Override
    @Transactional
    public List<ShipmentGroup> getAllEntities(User user) {
        log.info(getAllLogEndpoint(ShipmentGroup.class));
        return shipmentGroupDao.getAll(user);
    }

    @Override
    @Transactional
    public List<ShipmentGroupDto> getAll(User user) {
        return shipmentGroupMapper.toDto(getAllEntities(user));
    }

    @Override
    @Transactional
    public List<ShipmentGroupDto> getAllByCounterpartyId(UUID counterpartyUuid) {
        Counterparty counterparty = counterpartyService.getEntityByUuid(counterpartyUuid);
        if (counterparty == null) {
            log.debug("Can't get client list by counterparty. Counterparty {} doesn't exist", counterpartyUuid);
            return null;
        }
        log.info("Getting all clients by counterparty {}", counterparty);
        return shipmentGroupMapper.toDto(shipmentGroupDao.getAllByCounterparty(counterparty));
    }

    @Override
    @Transactional
    public ShipmentGroup getEntityById(UUID uuid, User user) throws Exception {
        log.info(getAllByIdLogEndpoint(ShipmentGroup.class, uuid));
        ShipmentGroup shipmentGroup = shipmentGroupDao.getById(uuid);

        userService.authorizeForAction(shipmentGroup, user);

        return shipmentGroup;
    }

    @Override
    @Transactional
    public ShipmentGroupDto getById(UUID uuid, User user) throws Exception {
        return shipmentGroupMapper.toDto(getEntityById(uuid, user));
    }

    @Override
    @Transactional
    public ShipmentGroup saveEntity(ShipmentGroup shipmentGroup, User user) throws Exception {
        validateInnerReferenceAndFillObjectFromDB(shipmentGroup);

        userService.authorizeForAction(shipmentGroup, user);
        log.info(saveLogEndpoint(ShipmentGroup.class, shipmentGroup));
        return shipmentGroupDao.save(shipmentGroup);
    }

    @Override
    @Transactional
    public ShipmentGroupDto save(ShipmentGroupDto shipmentGroupDto, User user) throws Exception {
        return shipmentGroupMapper.toDto(saveEntity(shipmentGroupMapper.toEntity(shipmentGroupDto), user));
    }

    @Override
    @Transactional
    public ShipmentGroup updateEntity(UUID uuid, ShipmentGroup source, User user) throws Exception {
        ShipmentGroup target = shipmentGroupDao.getById(uuid);

        userService.authorizeForAction(target, user);

        validateInnerReferenceAndFillObjectFromDB(source);

        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for client", e);
            throw new Exception("Can't get properties from object to updatable object for client", e);
        }

        target.setUuid(uuid);
        target.setCounterparty(source.getCounterparty());

        log.info(updateOnErrorLogEndpoint(ShipmentGroup.class, target));
        shipmentGroupDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public ShipmentGroupDto update(UUID uuid, ShipmentGroupDto shipmentGroupDto, User user) throws Exception {
        return shipmentGroupMapper.toDto(updateEntity(uuid, shipmentGroupMapper.toEntity(shipmentGroupDto), user));
    }

    @Override
    @Transactional
    public void delete(UUID uuid, User user) throws Exception{
        log.info(deleteLogEndpoint(ShipmentGroup.class, uuid));
        shipmentGroupDao.delete(getEntityById(uuid, user));
    }

    private void validateInnerReferenceAndFillObjectFromDB(ShipmentGroup source) throws Exception {
        Counterparty counterparty = counterpartyService.getEntityByUuid(source.getCounterparty().getUuid());
        if (counterparty == null) {
            log.error(getOnErrorLogEndpoint(Counterparty.class, source.getCounterparty().getUser()));
            throw new Exception(getOnErrorLogEndpoint(Counterparty.class, source.getCounterparty().getUuid()));
        }
        source.setCounterparty(counterparty);
    }
}
