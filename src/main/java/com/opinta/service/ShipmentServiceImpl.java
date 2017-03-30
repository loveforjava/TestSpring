package com.opinta.service;

import com.opinta.dao.TariffGridDao;
import com.opinta.entity.Address;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;
import com.opinta.util.AddressUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import com.opinta.dao.ClientDao;
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
    private final ClientDao clientDao;
    private final TariffGridDao tariffGridDao;
    private final ShipmentMapper shipmentMapper;
    private final BarcodeInnerNumberService barcodeInnerNumberService;

    @Autowired
    public ShipmentServiceImpl(ShipmentDao shipmentDao, ClientDao clientDao, TariffGridDao tariffGridDao,
                               ShipmentMapper shipmentMapper, BarcodeInnerNumberService barcodeInnerNumberService) {
        this.shipmentDao = shipmentDao;
        this.clientDao = clientDao;
        this.tariffGridDao = tariffGridDao;
        this.shipmentMapper = shipmentMapper;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
    }

    @Override
    @Transactional
    public List<Shipment> getAllEntities() {
        log.info("Getting all shipments");
        return shipmentDao.getAll();
    }

    @Override
    @Transactional
    public Shipment getEntityByUuid(UUID uuid) {
        log.info("Getting postcodePool by uuid {}", uuid);
        return shipmentDao.getByUuid(uuid);
    }

    @Override
    @Transactional
    public Shipment saveEntity(Shipment shipment) {
        log.info("Saving shipment {}", shipment);
        return shipmentDao.save(shipment);
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAll() {
        return shipmentMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public List<ShipmentDto> getAllByClientUuid(UUID clientUuid) {
        Client client = clientDao.getByUuid(clientUuid);
        if (client == null) {
            log.debug("Can't get shipment list by client. Client {} doesn't exist", clientUuid);
            return null;
        }
        log.info("Getting all shipments by client {}", client);
        return shipmentMapper.toDto(shipmentDao.getAllByClient(client));
    }

    @Override
    @Transactional
    public ShipmentDto getByUuid(UUID uuid) {
        return shipmentMapper.toDto(getEntityByUuid(uuid));
    }

    @Override
    @Transactional
    public ShipmentDto save(ShipmentDto shipmentDto) {
        log.info("saving new Shipment for Client: " + shipmentDto.getSenderUuid());
        Client existingClient = clientDao.getByUuid(shipmentDto.getSenderUuid());
        Counterparty counterparty = existingClient.getCounterparty();
        PostcodePool postcodePool = counterparty.getPostcodePool();
        BarcodeInnerNumber newBarcode = barcodeInnerNumberService.generateBarcodeInnerNumber(postcodePool);
        postcodePool.getBarcodeInnerNumbers().add(newBarcode);
        Shipment shipment = shipmentMapper.toEntity(shipmentDto);
        shipment.setBarcode(newBarcode);
        shipment.setSender(clientDao.getByUuid(shipment.getSender().getUuid()));
        shipment.setRecipient(clientDao.getByUuid(shipment.getRecipient().getUuid()));
        shipment.setPrice(calculatePrice(shipment));
        log.info("Saving shipment ", shipmentMapper.toDto(shipment));

        return shipmentMapper.toDto(shipmentDao.save(shipment));
    }

    @Override
    @Transactional
    public ShipmentDto update(UUID uuid, ShipmentDto shipmentDto) {
        Shipment source = shipmentMapper.toEntity(shipmentDto);
        Shipment target = shipmentDao.getByUuid(uuid);
        if (target == null) {
            log.debug("Can't update shipment. Shipment doesn't exist {}", uuid);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (Exception e) {
            log.error("Can't get properties from object to updatable object for shipment", e);
        }
        target.setUuid(uuid);
        try {
            fillSenderAndRecipient(target);
        } catch (IllegalArgumentException e) {
            log.error("Can't update shipment {}. Sender or recipient doesn't exist", target, e);
            return null;
        }
        target.setPrice(calculatePrice(target));
        log.info("Updating shipment {}", target);
        shipmentDao.update(target);
        return shipmentMapper.toDto(target);
    }

    private void fillSenderAndRecipient(Shipment target) throws IllegalArgumentException {
        target.setSender(clientDao.getByUuid(target.getSender().getUuid()));
        target.setRecipient(clientDao.getByUuid(target.getRecipient().getUuid()));
        if (target.getSender() == null) {
            throw new IllegalArgumentException(
                    format("Can't calculate price for shipment %s. Sender doesn't exist", target));
        }
        if (target.getRecipient() == null) {
            throw new IllegalArgumentException(
                    format("Can't calculate price for shipment %s. Recipient doesn't exist", target));
        }
    }

    @Override
    @Transactional
    public boolean delete(UUID uuid) {
        Shipment shipment = shipmentDao.getByUuid(uuid);
        if (shipment == null) {
            log.debug("Can't delete shipment. Shipment doesn't exist {}", uuid);
            return false;
        }
        shipment.setUuid(uuid);
        log.info("Deleting shipment {}", shipment);
        shipmentDao.delete(shipment);
        return true;
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

        TariffGrid tariffGrid = tariffGridDao.getLast(w2wVariation);
        if (shipment.getWeight() < tariffGrid.getWeight() &&
                shipment.getLength() < tariffGrid.getLength()) {
            tariffGrid = tariffGridDao.getByDimension(shipment.getWeight(), shipment.getLength(), w2wVariation);
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
