package com.opinta.service;

import com.opinta.dao.ShipmentGroupDao;
import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.mapper.ShipmentGroupMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllByFieldLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
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
    public ShipmentGroup getEntityById(UUID uuid, User user) throws IncorrectInputDataException, AuthException {
        log.info(getByIdLogEndpoint(ShipmentGroup.class, uuid));
        ShipmentGroup shipmentGroup = shipmentGroupDao.getById(uuid);
        if (shipmentGroup == null) {
            log.error(getByIdOnErrorLogEndpoint(ShipmentGroup.class, uuid));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(ShipmentGroup.class, uuid));
        }

        userService.authorizeForAction(shipmentGroup, user);

        return shipmentGroup;
    }

    @Override
    @Transactional
    public ShipmentGroup saveEntity(ShipmentGroup shipmentGroup, User user) throws AuthException,
            IncorrectInputDataException {
        validateInnerReferenceAndFillObjectFromDB(shipmentGroup, user);

        userService.authorizeForAction(shipmentGroup, user);
        log.info(saveLogEndpoint(ShipmentGroup.class, shipmentGroup));
        return shipmentGroupDao.save(shipmentGroup);
    }

    @Override
    @Transactional
    public ShipmentGroup updateEntity(UUID uuid, ShipmentGroup source, User user) throws AuthException,
            IncorrectInputDataException, PerformProcessFailedException {
        ShipmentGroup target = getEntityById(uuid, user);

        validateInnerReferenceAndFillObjectFromDB(source, user);

        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error(copyPropertiesOnErrorLogEndpoint(ShipmentGroup.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    ShipmentGroup.class, source, target, e));
        }

        target.setUuid(uuid);
        target.setCounterparty(source.getCounterparty());

        log.info(updateOnErrorLogEndpoint(ShipmentGroup.class, target));
        shipmentGroupDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public List<ShipmentGroupDto> getAll(User user) {
        return shipmentGroupMapper.toDto(getAllEntities(user));
    }

    @Override
    @Transactional
    public List<ShipmentGroupDto> getAllByCounterpartyId(UUID counterpartyUuid, User user) throws AuthException,
            IncorrectInputDataException {
        Counterparty counterparty = counterpartyService.getEntityByUuid(counterpartyUuid, user);
        log.info(getAllByFieldLogEndpoint(ShipmentGroup.class, Counterparty.class, counterpartyUuid));
        return shipmentGroupMapper.toDto(shipmentGroupDao.getAllByCounterparty(counterparty));
    }

    @Override
    @Transactional
    public ShipmentGroupDto getById(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        return shipmentGroupMapper.toDto(getEntityById(uuid, user));
    }

    @Override
    @Transactional
    public ShipmentGroupDto save(ShipmentGroupDto shipmentGroupDto, User user) throws AuthException,
            IncorrectInputDataException {
        return shipmentGroupMapper.toDto(saveEntity(shipmentGroupMapper.toEntity(shipmentGroupDto), user));
    }

    @Override
    @Transactional
    public ShipmentGroupDto update(UUID uuid, ShipmentGroupDto shipmentGroupDto, User user)
            throws IncorrectInputDataException, AuthException, PerformProcessFailedException {
        return shipmentGroupMapper.toDto(updateEntity(uuid, shipmentGroupMapper.toEntity(shipmentGroupDto), user));
    }

    @Override
    @Transactional
    public void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        log.info(deleteLogEndpoint(ShipmentGroup.class, uuid));
        shipmentGroupDao.delete(getEntityById(uuid, user));
    }

    private void validateInnerReferenceAndFillObjectFromDB(ShipmentGroup source, User user) throws AuthException,
            IncorrectInputDataException {
        Counterparty counterparty = counterpartyService.getEntityByUuid(source.getCounterparty().getUuid(), user);
        source.setCounterparty(counterparty);
    }
}
