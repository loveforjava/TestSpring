package com.opinta.mapper;

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
            @Mapping(source = "sender.uuid", target = "senderId"),
            @Mapping(source = "recipient.uuid", target = "recipientId"),
            @Mapping(source = "uuid", target = "id")
    })
    ShipmentDto toDto(Shipment shipment);

    @Override
    @Mappings({
            @Mapping(target = "sender", expression = "java(createClientById(shipmentDto.getSenderId()))"),
            @Mapping(target = "recipient", expression = "java(createClientById(shipmentDto.getRecipientId()))"),
            @Mapping(source = "id", target = "uuid")
    })
    Shipment toEntity(ShipmentDto shipmentDto);

    default Client createClientById(String id) {
        Client client = new Client();
        client.setUuid(id);
        return client;
    }
}
