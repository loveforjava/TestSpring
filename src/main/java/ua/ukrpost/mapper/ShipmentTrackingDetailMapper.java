package ua.ukrpost.mapper;

import ua.ukrpost.dto.ShipmentTrackingDetailDto;
import ua.ukrpost.entity.ShipmentTrackingDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ShipmentTrackingDetailMapper extends BaseMapper<ShipmentTrackingDetailDto, ShipmentTrackingDetail> {

    @Override
    @Mappings({
            @Mapping(source = "shipment.uuid", target = "shipmentUuid"),
            @Mapping(source = "postOffice.id", target = "postOfficeId")
    })
    ShipmentTrackingDetailDto toDto(ShipmentTrackingDetail shipmentTrackingDetail);

    @Override
    @Mappings({
            @Mapping(source = "shipmentUuid", target = "shipment.uuid"),
            @Mapping(source = "postOfficeId", target = "postOffice.id")
    })
    ShipmentTrackingDetail toEntity(ShipmentTrackingDetailDto shipmentTrackingDetailDto);
}
