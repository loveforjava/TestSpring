package ua.ukrpost.service;

import ua.ukrpost.dao.ShipmentGroupDao;
import ua.ukrpost.dto.ShipmentGroupDto;
import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.ShipmentGroup;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import ua.ukrpost.mapper.ShipmentGroupMapper;
import java.lang.reflect.InvocationTargetException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;

import static ua.ukrpost.util.AuthorizationUtil.authorizeForAction;
import static ua.ukrpost.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static ua.ukrpost.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.deleteLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getAllByFieldLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getAllLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.saveLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateOnErrorLogEndpoint;

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

        authorizeForAction(shipmentGroup, user);

        return shipmentGroup;
    }

    @Override
    @Transactional
    public ShipmentGroup saveEntity(ShipmentGroup shipmentGroup, User user) throws AuthException,
            IncorrectInputDataException {
        validateInnerReferenceAndFillObjectFromDB(shipmentGroup, user);

        authorizeForAction(shipmentGroup, user);
        shipmentGroup.setCreator(user);
        shipmentGroup.setLastModifier(user);
        LocalDateTime now = now();
        shipmentGroup.setCreated(now);
        shipmentGroup.setLastModified(now);
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
            copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(ShipmentGroup.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    ShipmentGroup.class, source, target, e));
        }

        target.setUuid(uuid);
        target.setCounterparty(source.getCounterparty());
        target.setLastModified(now());
        target.setLastModifier(user);

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
