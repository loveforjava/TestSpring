package com.opinta.service;

import java.util.List;
import java.util.UUID;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Shipment;

public interface ShipmentService {

    List<Shipment> getAllEntities();

    Shipment getEntityById(UUID id);

    Shipment saveEntity(Shipment shipment);
    
    List<ShipmentDto> getAll();

    List<ShipmentDto> getAllByClientId(UUID clientId);
    
    ShipmentDto getById(UUID id);
    
    ShipmentDto save(ShipmentDto shipmentDto);
    
    ShipmentDto update(UUID id, ShipmentDto shipmentDto);
    
    boolean delete(UUID id);
}
