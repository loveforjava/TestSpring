package com.opinta.mapper;

import com.opinta.dto.ShipmentDto;
import com.opinta.model.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ShipmentMapper extends BaseMapper<ShipmentDto, Shipment> {
    @Override
    @Mappings({
            @Mapping(source = "sender.id", target = "senderId"),
            @Mapping(source = "recipient.id", target = "recipientId")
    })
    ShipmentDto toDto(Shipment shipment);

    @Override
    @Mappings({
            @Mapping(source = "senderId", target = "sender.id"),
            @Mapping(source = "recipientId", target = "recipient.id")
    })
    Shipment toEntity(ShipmentDto shipmentDto);
}
