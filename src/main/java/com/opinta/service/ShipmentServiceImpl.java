package com.opinta.service;

import com.opinta.entity.Address;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.classifier.TariffGrid;
import com.opinta.entity.User;
import com.opinta.entity.W2wVariation;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.util.AddressUtil;
import com.opinta.util.LogMessageUtil;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import com.opinta.dao.ShipmentDao;
import com.opinta.dto.ShipmentDto;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.entity.Client;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;

import static com.opinta.util.AuthorizationUtil.authorizeForAction;
import static com.opinta.util.EnhancedBeanUtilsBean.copyNotNullProperties;
import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;

@Service
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentDao shipmentDao;
    private final ClientService clientService;
    private final TariffGridService tariffGridService;
    private final ShipmentMapper shipmentMapper;
    private final BarcodeInnerNumberService barcodeInnerNumberService;
    private final ShipmentGroupService shipmentGroupService;
    private final DiscountPerCounterpartyService discountPerCounterpartyService;

    @Autowired
    public ShipmentServiceImpl(ShipmentDao shipmentDao, ClientService clientService,
                               TariffGridService tariffGridService, ShipmentMapper shipmentMapper,
                               BarcodeInnerNumberService barcodeInnerNumberService,
                               ShipmentGroupService shipmentGroupService,
                               DiscountPerCounterpartyService discountPerCounterpartyService) {
        this.shipmentDao = shipmentDao;
        this.clientService = clientService;
        this.tariffGridService = tariffGridService;
        this.shipmentMapper = shipmentMapper;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
        this.shipmentGroupService = shipmentGroupService;
        this.discountPerCounterpartyService = discountPerCounterpartyService;
    }

    @Override
    @Transactional
    public List<Shipment> getAllEntities(User user) {
        log.info(LogMessageUtil.getAllLogEndpoint(Shipment.class));
        return shipmentDao.getAll(user);
    }

    @Override
    @Transactional
    public Shipment getEntityByUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        log.info(LogMessageUtil.getByIdLogEndpoint(Shipment.class, uuid));
        Shipment shipment = shipmentDao.getByUuid(uuid);
        if (shipment == null) {
            log.error(getByIdOnErrorLogEndpoint(Shipment.class, uuid));
            throw new IncorrectInputDataException(getByIdOnErrorLogEndpoint(Shipment.class, uuid));
        }

        authorizeForAction(shipment, user);

        return shipmentDao.getByUuid(uuid);
    }

    @Override
    @Transactional
    public Shipment saveEntity(Shipment shipment, User user) throws AuthException, IncorrectInputDataException {
        shipment.setSender(clientService.saveOrGetEntity(shipment.getSender(), user));
        shipment.setRecipient(clientService.saveOrGetEntityAnonymous(shipment.getRecipient(), user));
        PostcodePool postcodePool = shipment.getSender().getCounterparty().getPostcodePool();
        shipment.setBarcodeInnerNumber(barcodeInnerNumberService.generateBarcodeInnerNumber(postcodePool));
        shipment.setLastModified(now());
        shipment.setDiscountPerCounterparty(discountPerCounterpartyService
                .getEntityWithHighestDiscount(user, shipment.getLastModified()));
        shipment.setPrice(calculatePrice(shipment));
        shipment.setCreated(now());
        shipment.setLastModified(now());
        shipment.setCreator(user);
        shipment.setLastModifier(user);
        log.info(LogMessageUtil.saveLogEndpoint(Shipment.class, shipment));
        return shipmentDao.save(shipment);
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAll(User user) {
        return shipmentMapper.toDto(getAllEntities(user));
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAllByClientUuid(UUID clientUuid, User user) throws AuthException,
            IncorrectInputDataException {
        Client client = clientService.getEntityByUuid(clientUuid, user);
        log.info(LogMessageUtil.getAllByFieldLogEndpoint(Shipment.class, Client.class, clientUuid));
        return shipmentMapper.toDto(shipmentDao.getAllByClient(client, user));
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAllByShipmentGroupUuid(UUID uuid, User user) throws AuthException,
            IncorrectInputDataException {
        log.info(LogMessageUtil.getAllByFieldLogEndpoint(Shipment.class, ShipmentGroup.class, uuid));
        ShipmentGroup shipmentGroup = shipmentGroupService.getEntityById(uuid, user);
        return shipmentMapper.toDto(shipmentDao.getAllByShipmentGroup(shipmentGroup, user));
    }

    @Override
    @Transactional
    public List<Shipment> getAllEntitiesByShipmentGroupUuid(UUID uuid, User user) throws AuthException,
            IncorrectInputDataException {
        ShipmentGroup shipmentGroup = shipmentGroupService.getEntityById(uuid, user);
        return shipmentDao.getAllByShipmentGroup(shipmentGroup, user);
    }

    @Override
    @Transactional
    public ShipmentDto getByUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        return shipmentMapper.toDto(getEntityByUuid(uuid, user));
    }

    @Override
    @Transactional
    public ShipmentDto save(ShipmentDto shipmentDto, User user) throws AuthException, IncorrectInputDataException {
        return shipmentMapper.toDto(saveEntity(shipmentMapper.toEntity(shipmentDto), user));
    }

    @Override
    @Transactional
    public ShipmentDto update(UUID uuid, ShipmentDto shipmentDto, User user) throws AuthException,
            PerformProcessFailedException, IncorrectInputDataException {
        Shipment source = shipmentMapper.toEntity(shipmentDto);
        Shipment target = getEntityByUuid(uuid, user);
        
        try {
            copyNotNullProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(copyPropertiesOnErrorLogEndpoint(Shipment.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    Shipment.class, source, target, e));
        }

        target.setUuid(uuid);
        target.setSender(clientService.getEntityByUuid(target.getSender().getUuid(), user));
        target.setRecipient(clientService.getEntityByUuidAnonymous(target.getRecipient().getUuid()));
        target.setLastModified(now());
        target.setDiscountPerCounterparty(discountPerCounterpartyService
                .getEntityWithHighestDiscount(user, target.getLastModified()));
        target.setPrice(calculatePrice(target));
        target.setLastModified(now());
        target.setLastModifier(user);
        log.info(updateLogEndpoint(Shipment.class, target));
        shipmentDao.update(target);
        return shipmentMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException {
        log.info(deleteLogEndpoint(Shipment.class, uuid));
        Shipment shipment = getEntityByUuid(uuid, user);
        shipmentDao.delete(shipment);
    }

    @Override
    @Transactional
    public ShipmentDto removeShipmentGroupFromShipment(UUID uuid, User user)
            throws IncorrectInputDataException, AuthException {
        Shipment shipment = getEntityByUuid(uuid, user);
        shipment.setShipmentGroup(null);
        log.info(updateLogEndpoint(Shipment.class, shipment));
        shipmentDao.update(shipment);
        return shipmentMapper.toDto(shipment);
    }

    private BigDecimal calculatePrice(Shipment shipment) {
        log.info("Calculating price for shipment {}", shipment);

        Address senderAddress = shipment.getSender().getAddress();
        Address recipientAddress = shipment.getRecipient().getAddress();
        W2wVariation w2wVariation = W2wVariation.COUNTRY;
        if (AddressUtil.isSameTown(senderAddress, recipientAddress)) {
            w2wVariation = W2wVariation.TOWN;
        } else if (AddressUtil.isSameRegion(senderAddress, recipientAddress)) {
            w2wVariation = W2wVariation.REGION;
        }

        TariffGrid tariffGrid = tariffGridService.getMaxTariffEntity(w2wVariation);
        TariffGrid maxTariffGrid = tariffGrid;
        if (tariffGrid == null) {
            return BigDecimal.ZERO;
        }

        if (shipment.getWeight() < tariffGrid.getWeight() &&
                shipment.getLength() < tariffGrid.getLength()) {
            tariffGrid = tariffGridService
                    .getEntityByDimension(shipment.getWeight(), shipment.getLength(), w2wVariation);
        }

        log.info("TariffGrid for weight {} per length {} and type {}: {}",
                shipment.getWeight(), shipment.getLength(), w2wVariation, tariffGrid);

        if (tariffGrid == null) {
            return BigDecimal.ZERO;
        }

        float price = tariffGrid.getPrice() + getSurcharges(shipment);
        if (shipment.getLength() > 70.0f) {
            float overpayForLength = shipment.getLength() / 70.0f;
            price = maxTariffGrid.getPrice() * overpayForLength;
            log.info("Shipment length exceeds 70 cm - using tariff grid: " + maxTariffGrid);
            log.info(format("Overpay ratio for exceeding length is: %s, price is: %s ", overpayForLength, price));
        }

        price -= getSumOfDiscount(shipment, price);

        return new BigDecimal(Float.toString(price));
    }

    private float getSumOfDiscount(Shipment shipment, float price) {
        if (shipment.getDiscountPerCounterparty() == null) {
            return 0.0f;
        }
        return price * shipment.getDiscountPerCounterparty().getDiscount().getValue() / 100;
    }

    private float getSurcharges(Shipment shipment) {
        float surcharges = 0;
        // address delivery surcharges
        if (shipment.getDeliveryType().equals(DeliveryType.D2W) ||
                shipment.getDeliveryType().equals(DeliveryType.W2D)) {
            surcharges += 9;
        } else if (shipment.getDeliveryType().equals(DeliveryType.D2D)) {
            surcharges += 12;
        }

        // countryside surcharges
        if (shipment.getRecipient().getAddress().isCountryside()) {
            if (AddressUtil.isSameRegion(shipment.getSender().getAddress(), shipment.getRecipient().getAddress())) {
                surcharges += 9;
            } else {
                surcharges += 30;
            }
        }

        // declared price more that 500 surcharges
        BigDecimal declaredPrice = shipment.getDeclaredPrice();
        BigDecimal topBound = new BigDecimal("500");
        if (declaredPrice.compareTo(topBound) == 1) {
            BigDecimal difference = declaredPrice.subtract(topBound);
            BigDecimal declaredPriceCharge = difference.multiply(new BigDecimal("0.005"));
            declaredPriceCharge = declaredPriceCharge.setScale(2, BigDecimal.ROUND_HALF_UP);
            surcharges += declaredPriceCharge.floatValue();
        }
        return surcharges;
    }
}
