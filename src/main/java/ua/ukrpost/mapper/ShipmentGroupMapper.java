package ua.ukrpost.mapper;

import ua.ukrpost.dto.ShipmentGroupDto;
import ua.ukrpost.entity.ShipmentGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipmentGroupMapper extends BaseMapper<ShipmentGroupDto, ShipmentGroup> {

    @Override
    @Mapping(source = "counterparty.uuid", target = "counterpartyUuid")
    ShipmentGroupDto toDto(ShipmentGroup shipmentGroup);

    @Override
    @Mapping(source = "counterpartyUuid", target = "counterparty.uuid")
    ShipmentGroup toEntity(ShipmentGroupDto shipmentGroupDto);
}
