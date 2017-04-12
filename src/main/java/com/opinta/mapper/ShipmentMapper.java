package com.opinta.mapper;

import java.util.UUID;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.Client;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = ClientMapper.class)
public interface ShipmentMapper extends BaseMapper<ShipmentDto, Shipment> {

    @Override
    @Mappings({
            @Mapping(expression = "java(stringifyBarcode(shipment.getBarcodeInnerNumber()))", target = "barcode"),
            @Mapping(source = "shipmentGroup.uuid", target = "shipmentGroupUuid")})
    ShipmentDto toDto(Shipment shipment);

    @Override
    @Mappings({
            @Mapping(target = "shipmentGroup",
                    expression = "java(ShipmentGroupUuidToShipmentGroupEntity(shipmentDto.getShipmentGroupUuid()))")})
    Shipment toEntity(ShipmentDto shipmentDto);

    default String stringifyBarcode(BarcodeInnerNumber barcodeInnerNumber) {
        if (barcodeInnerNumber == null) {
            return null;
        }
        return barcodeInnerNumber.stringify();
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
