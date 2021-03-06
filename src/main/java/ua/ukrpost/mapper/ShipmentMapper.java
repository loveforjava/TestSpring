package ua.ukrpost.mapper;

import java.util.UUID;

import ua.ukrpost.dto.ShipmentDto;
import ua.ukrpost.entity.Shipment;
import ua.ukrpost.entity.ShipmentGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {ClientMapper.class, DiscountPerCounterpartyMapper.class})
public interface ShipmentMapper extends BaseMapper<ShipmentDto, Shipment> {

    @Override
    @Mappings({
            @Mapping(expression = "java(shipment.getBarcodeInnerNumber().stringify())", target = "barcode"),
            @Mapping(source = "shipmentGroup.uuid", target = "shipmentGroupUuid")})
    ShipmentDto toDto(Shipment shipment);

    @Override
    @Mappings({
            @Mapping(target = "shipmentGroup",
                    expression = "java(shipmentGroupUuidToShipmentGroupEntity(shipmentDto.getShipmentGroupUuid()))"),
            @Mapping(target = "barcodeInnerNumber", ignore = true),
            @Mapping(target = "discountPerCounterparty", ignore = true)})
    Shipment toEntity(ShipmentDto shipmentDto);

    default ShipmentGroup shipmentGroupUuidToShipmentGroupEntity(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        ShipmentGroup shipmentGroup = new ShipmentGroup();
        shipmentGroup.setUuid(uuid);
        return shipmentGroup;
    }
}
