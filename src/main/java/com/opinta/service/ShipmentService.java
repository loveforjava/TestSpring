package com.opinta.service;

import java.util.List;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Shipment;

public interface ShipmentService {

    List<Shipment> getAllEntities();

    Shipment getEntityById(String id);

    Shipment saveEntity(Shipment shipment);
    
    List<ShipmentDto> getAll();

    List<ShipmentDto> getAllByClientId(String clientId);
    
    ShipmentDto getById(String id);
    
    ShipmentDto save(ShipmentDto shipmentDto);
    
    ShipmentDto update(String id, ShipmentDto shipmentDto);
    
    boolean delete(String id);
}
