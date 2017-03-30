package com.opinta.mapper;

import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.ShipmentGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipmentGroupMapper extends BaseMapper<ShipmentGroupDto, ShipmentGroup> {

    @Override
    @Mapping(source = "counterparty.id", target = "counterpartyId")
    ShipmentGroupDto toDto(ShipmentGroup shipmentGroup);

    @Override
    @Mapping(source = "counterpartyId", target = "counterparty.id")
    ShipmentGroup toEntity(ShipmentGroupDto shipmentGroupDto);
}
