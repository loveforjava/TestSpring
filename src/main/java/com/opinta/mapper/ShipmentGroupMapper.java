package com.opinta.mapper;

import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.ShipmentGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShipmentGroupMapper extends BaseMapper<ShipmentGroupDto, ShipmentGroup> {
}
