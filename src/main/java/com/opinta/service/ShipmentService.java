package com.opinta.service;

import java.util.List;

import com.opinta.dto.ShipmentDto;
import com.opinta.model.Shipment;

public interface ShipmentService {
    
    List<ShipmentDto> getAll();
    
    ShipmentDto getById(long id);
    
    ShipmentDto save(ShipmentDto shipmentDto);
    
    ShipmentDto update(long id, ShipmentDto shipmentDto);
    
    boolean delete(long id);

}
