package com.opinta.service;

import com.opinta.dto.ShipmentTrackingDetailDto;
import java.util.List;

public interface ShipmentTrackingDetailService {
    
    List<ShipmentTrackingDetailDto> getAll();

    ShipmentTrackingDetailDto getById(long id);

    ShipmentTrackingDetailDto save(ShipmentTrackingDetailDto shipmentTrackingDetailDto);

    ShipmentTrackingDetailDto update(long id, ShipmentTrackingDetailDto shipmentTrackingDetailDto);
    
    boolean delete(long id);
}
