package com.opinta.service;

import com.opinta.entity.Address;
import com.opinta.entity.BarcodeInnerNumber;
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

import static com.opinta.util.LogMessageUtil.copyPropertiesOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateLogEndpoint;
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

        userService.authorizeForAction(shipment, user);

        return shipmentDao.getByUuid(uuid);
    }

    @Override
    @Transactional
    public Shipment saveEntity(Shipment shipment, User user) throws AuthException, IncorrectInputDataException {
        Client sender = clientService.getEntityByUuid(shipment.getSender().getUuid(), user);
        PostcodePool postcodePool = sender.getCounterparty().getPostcodePool();

        shipment.setSender(sender);
        shipment.setRecipient(clientService.getEntityByUuidAnonymous(shipment.getRecipient().getUuid()));
        shipment.setBarcodeInnerNumber(barcodeInnerNumberService.generateBarcodeInnerNumber(postcodePool));
        shipment.setPrice(calculatePrice(shipment));

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
        Shipment shipment = shipmentMapper.toEntity(shipmentDto);
        Client sender = clientService.saveOrGet(shipment.getSender(), user);
        shipment.setSender(sender);
        Client recipient = clientService.saveOrGet(shipment.getRecipient(), user);
        shipment.setRecipient(recipient);
        return shipmentMapper.toDto(saveEntity(shipment, user));
    }

    @Override
    @Transactional
    public ShipmentDto update(UUID uuid, ShipmentDto shipmentDto, User user) throws AuthException,
            PerformProcessFailedException, IncorrectInputDataException {
        Shipment source = shipmentMapper.toEntity(shipmentDto);
        Shipment target = getEntityByUuid(uuid, user);
        BarcodeInnerNumber barcodeInnerNumber = target.getBarcodeInnerNumber();
        
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error(copyPropertiesOnErrorLogEndpoint(Shipment.class, source, target, e));
            throw new PerformProcessFailedException(copyPropertiesOnErrorLogEndpoint(
                    Shipment.class, source, target, e));
        }

        target.setUuid(uuid);
        target.setBarcodeInnerNumber(barcodeInnerNumber);
        fillSenderAndRecipient(target, user);
        target.setPrice(calculatePrice(target));
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

    private void fillSenderAndRecipient(Shipment target, User user) throws AuthException, IncorrectInputDataException {
        target.setSender(clientService.getEntityByUuid(target.getSender().getUuid(), user));
        target.setRecipient(clientService.getEntityByUuidAnonymous(target.getRecipient().getUuid()));
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
            tariffGrid = tariffGridService.getEntityByDimension(shipment.getWeight(), shipment.getLength(), w2wVariation);
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
        float sumOfDiscount = price * shipment.getSender().getDiscount() / 100;
        price -= sumOfDiscount;

        return new BigDecimal(Float.toString(price));
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
