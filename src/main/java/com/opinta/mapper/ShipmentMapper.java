package com.opinta.mapper;

import java.util.UUID;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ShipmentMapper extends BaseMapper<ShipmentDto, Shipment> {

    @Override
    @Mappings({
            @Mapping(expression = "java(stringifyBarcode(shipment))", target = "barcode"),
            @Mapping(source = "sender.uuid", target = "senderId"),
            @Mapping(source = "recipient.uuid", target = "recipientId"),
            @Mapping(source = "uuid", target = "id")
    })
    ShipmentDto toDto(Shipment shipment);
    
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

    @Override
    @Mappings({
            @Mapping(expression = "java(barcodeStringToBarcodeEntity(shipmentDto.getBarcode()))", target = "barcode"),
            @Mapping(target = "sender", expression = "java(createClientById(shipmentDto.getSenderId()))"),
            @Mapping(target = "recipient", expression = "java(createClientById(shipmentDto.getRecipientId()))"),
            @Mapping(source = "id", target = "uuid")
    })
    Shipment toEntity(ShipmentDto shipmentDto);

    default Client createClientById(UUID id) {
        Client client = new Client();
        client.setUuid(id);
        return client;
    }
    
    default BarcodeInnerNumber barcodeStringToBarcodeEntity(String barcode) {
        return null;
    }
}
