package com.opinta.mapper;

import com.opinta.dto.PostcodePoolDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.model.PostcodePool;
import com.opinta.model.Shipment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShipmentMapper extends BaseMapper<ShipmentDto, Shipment> {
}
