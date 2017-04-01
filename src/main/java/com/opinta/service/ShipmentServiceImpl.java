package com.opinta.service;

import com.opinta.dao.TariffGridDao;
import com.opinta.entity.Address;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.User;
import com.opinta.entity.W2wVariation;
import com.opinta.util.AddressUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.naming.AuthenticationException;
import javax.transaction.Transactional;

import com.opinta.dao.ShipmentDao;
import com.opinta.dto.ShipmentDto;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.Client;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import com.opinta.entity.Counterparty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentDao shipmentDao;
    private final ClientService clientService;
    private final UserService userService;
    private final TariffGridService tariffGridService;
    private final ShipmentMapper shipmentMapper;
    private final BarcodeInnerNumberService barcodeInnerNumberService;
    private final ShipmentGroupService shipmentGroupService;

    @Autowired
    public ShipmentServiceImpl(ShipmentDao shipmentDao, ClientService clientService, UserService userService,
                               TariffGridService tariffGridService, ShipmentMapper shipmentMapper,
                               BarcodeInnerNumberService barcodeInnerNumberService,
                               ShipmentGroupService shipmentGroupService) {
        this.shipmentDao = shipmentDao;
        this.clientService = clientService;
        this.userService = userService;
        this.tariffGridService = tariffGridService;
        this.shipmentMapper = shipmentMapper;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
        this.shipmentGroupService = shipmentGroupService;
    }

    @Override
    @Transactional
    public List<Shipment> getAllEntities(User user) {
        log.info("Getting all shipments");
        return shipmentDao.getAll(user);
    }

    @Override
    @Transactional
    public Shipment getEntityByUuid(UUID uuid, User user) throws AuthenticationException {
        log.info("Getting postcodePool by uuid {}", uuid);
        Shipment shipment = shipmentDao.getByUuid(uuid);

        userService.authorizeForAction(shipment, user);

        return shipmentDao.getByUuid(uuid);
    }

    @Override
    @Transactional
    public Shipment saveEntity(Shipment shipment) {
        log.info("Saving shipment {}", shipment);
        PostcodePool postcodePool = shipment.getSender().getCounterparty().getPostcodePool();
        BarcodeInnerNumber newBarcode = barcodeInnerNumberService.generateBarcodeInnerNumber(postcodePool);
        postcodePool.getBarcodeInnerNumbers().add(newBarcode);
        shipment.setBarcode(newBarcode);
        return shipmentDao.save(shipment);
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAll(User user) {
        return shipmentMapper.toDto(getAllEntities(user));
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAllByClientUuid(UUID clientUuid, User user) throws AuthenticationException {
        Client client = clientService.getEntityByUuid(clientUuid, user);
        log.info("Getting all shipments by client {}", client);
        return shipmentMapper.toDto(shipmentDao.getAllByClient(client, user));
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAllByShipmentGroupId(UUID uuid, User user) throws Exception {
        ShipmentGroup shipmentGroup = shipmentGroupService.getEntityById(uuid, user);
        log.info("Getting all shipments by shipmentGroup {}", shipmentGroup);
        return shipmentMapper.toDto(shipmentDao.getAllByShipmentGroup(shipmentGroup, user));
    }

    @Override
    @Transactional
    public ShipmentDto getByUuid(UUID uuid, User user) throws AuthenticationException {
        return shipmentMapper.toDto(getEntityByUuid(uuid, user));
    }

    @Override
    @Transactional
    public ShipmentDto save(ShipmentDto shipmentDto, User user) throws AuthenticationException {
        Client existingClient = clientService.getEntityByUuid(shipmentDto.getSenderUuid(), user);
        Counterparty counterparty = existingClient.getCounterparty();
        PostcodePool postcodePool = counterparty.getPostcodePool();
        BarcodeInnerNumber newBarcode = barcodeInnerNumberService.generateBarcodeInnerNumber(postcodePool);
        postcodePool.getBarcodeInnerNumbers().add(newBarcode);
        Shipment shipment = shipmentMapper.toEntity(shipmentDto);

        Client sender = clientService.getEntityByUuid(shipment.getSender().getUuid(), user);

        userService.authorizeForAction(sender, user);

        shipment.setSender(sender);
        shipment.setRecipient(clientService.getEntityByUuidAnonymous(shipment.getRecipient().getUuid()));
        shipment.setBarcode(newBarcode);
        shipment.setPrice(calculatePrice(shipment));

        log.info("Saving shipment with assigned barcode", shipmentMapper.toDto(shipment));
        return shipmentMapper.toDto(shipmentDao.save(shipment));
    }

    @Override
    @Transactional
    public ShipmentDto update(UUID uuid, ShipmentDto shipmentDto, User user) throws Exception {
        Shipment source = shipmentMapper.toEntity(shipmentDto);
        Shipment target = shipmentDao.getByUuid(uuid);
        if (target == null) {
            log.debug("Can't update shipment. Shipment doesn't exist {}", uuid);
            throw new Exception(format("Can't update shipment. Shipment doesn't exist %s", uuid));
        }

        userService.authorizeForAction(target, user);

        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for shipment", e);
            throw new Exception("Can't get properties from object to updatable object for shipment", e);
        }
        target.setUuid(uuid);
        try {
            fillSenderAndRecipient(target, user);
        } catch (IllegalArgumentException e) {
            log.error("Can't update shipment {}. Sender or recipient doesn't exist", target, e);
            throw new Exception(format("Can't update shipment %s. Sender or recipient doesn't exist", target), e);
        }
        target.setPrice(calculatePrice(target));
        log.info("Updating shipment {}", target);
        shipmentDao.update(target);
        return shipmentMapper.toDto(target);
    }

    @Override
    @Transactional
    public void delete(UUID uuid, User user) throws Exception {
        Shipment shipment = shipmentDao.getByUuid(uuid);
        if (shipment == null) {
            log.debug("Can't delete shipment. Shipment doesn't exist {}", uuid);
            throw new Exception(format("Can't delete shipment. Shipment doesn't exist %s", uuid));
        }

        userService.authorizeForAction(shipment, user);

        shipment.setUuid(uuid);
        log.info("Deleting shipment {}", shipment);
        shipmentDao.delete(shipment);
    }

    private void fillSenderAndRecipient(Shipment target, User user) throws Exception {
        target.setSender(clientService.getEntityByUuid(target.getSender().getUuid(), user));
        target.setRecipient(clientService.getEntityByUuidAnonymous(target.getRecipient().getUuid()));
        if (target.getSender() == null) {
            throw new IllegalArgumentException(
                    format("Can't calculate price for shipment %s. Sender doesn't exist", target));
        }
        if (target.getRecipient() == null) {
            throw new IllegalArgumentException(
                    format("Can't calculate price for shipment %s. Recipient doesn't exist", target));
        }
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

        TariffGrid tariffGrid = tariffGridService.getLast(w2wVariation);
        if (tariffGrid == null) {
            return BigDecimal.ZERO;
        }

        if (shipment.getWeight() < tariffGrid.getWeight() &&
                shipment.getLength() < tariffGrid.getLength()) {
            tariffGrid = tariffGridService.getByDimension(shipment.getWeight(), shipment.getLength(), w2wVariation);
        }

        log.info("TariffGrid for weight {} per length {} and type {}: {}",
                shipment.getWeight(), shipment.getLength(), w2wVariation, tariffGrid);

        if (tariffGrid == null) {
            return BigDecimal.ZERO;
        }

        float price = tariffGrid.getPrice() + getSurcharges(shipment);

        return new BigDecimal(Float.toString(price));
    }

    private float getSurcharges(Shipment shipment) {
        float surcharges = 0;
        if (shipment.getDeliveryType().equals(DeliveryType.D2W) ||
                shipment.getDeliveryType().equals(DeliveryType.W2D)) {
            surcharges += 9;
        } else if (shipment.getDeliveryType().equals(DeliveryType.D2D)) {
            surcharges += 12;
        }
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
