package com.opinta.mapper;

import java.util.UUID;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Client;
import com.opinta.entity.Shipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ShipmentMapper extends BaseMapper<ShipmentDto, Shipment> {

    @Override
    @Mappings({
            @Mapping(source = "sender.uuid", target = "senderUuid"),
            @Mapping(source = "recipient.uuid", target = "recipientUuid")
    })
    ShipmentDto toDto(Shipment shipment);

    @Override
    @Mappings({
            @Mapping(target = "sender", expression = "java(createClientById(shipmentDto.getSenderUuid()))"),
            @Mapping(target = "recipient", expression = "java(createClientById(shipmentDto.getRecipientUuid()))")
    })
    Shipment toEntity(ShipmentDto shipmentDto);

    default Client createClientById(UUID id) {
        Client client = new Client();
        client.setUuid(id);
        return client;
    }
}
