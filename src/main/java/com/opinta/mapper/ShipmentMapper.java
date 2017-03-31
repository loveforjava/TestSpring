package com.opinta.mapper;

import java.util.UUID;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ShipmentMapper extends BaseMapper<ShipmentDto, Shipment> {

    @Override
    @Mappings({
            @Mapping(expression = "java(stringifyBarcode(shipment))", target = "barcode"),
            @Mapping(source = "sender.uuid", target = "senderUuid"),
            @Mapping(source = "recipient.uuid", target = "recipientUuid"),
            @Mapping(source = "shipmentGroup.uuid", target = "shipmentGroupUuid")
    })
    ShipmentDto toDto(Shipment shipment);

    @Override
    @Mappings({
            @Mapping(expression = "java(barcodeStringToBarcodeEntity(shipmentDto.getBarcode()))", target = "barcode"),
            @Mapping(target = "sender", expression = "java(createClientById(shipmentDto.getSenderUuid()))"),
            @Mapping(target = "recipient", expression = "java(createClientById(shipmentDto.getRecipientUuid()))"),
            @Mapping(target = "shipmentGroup",
                    expression = "java(ShipmentGroupUuidToShipmentGroupEntity(shipmentDto.getShipmentGroupUuid()))")
    })
    Shipment toEntity(ShipmentDto shipmentDto);

    default Client createClientById(UUID id) {
        Client client = new Client();
        client.setUuid(id);
        return client;
    }

    default String stringifyBarcode(Shipment shipment) {
        Client sender = shipment.getSender();
        if (sender == null) {
            return null;
        }
        Counterparty counterparty = sender.getCounterparty();
        if (counterparty == null) {
            return null;
        }
        PostcodePool postcodePool = counterparty.getPostcodePool();
        if (postcodePool == null) {
            return null;
        }
        String postcode = postcodePool.getPostcode();
        if (postcode == null) {
            return null;
        }
        BarcodeInnerNumber barcodeInnerNumber = shipment.getBarcode();
        if (barcodeInnerNumber == null) {
            return null;
        }
        String innerNumber = barcodeInnerNumber.getInnerNumber();
        if (innerNumber == null) {
            return null;
        }
        String fullBarcode = postcode + innerNumber;
        if (fullBarcode.length() != 13) {
            throw new RuntimeException("incorrect barcode.");
        }
        return fullBarcode;
    }

    default BarcodeInnerNumber barcodeStringToBarcodeEntity(String barcode) {
        return null;
    }

    default ShipmentGroup ShipmentGroupUuidToShipmentGroupEntity(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        ShipmentGroup shipmentGroup = new ShipmentGroup();
        shipmentGroup.setUuid(uuid);
        return shipmentGroup;
    }
}
