package com.opinta.service;

import java.util.List;
import java.util.UUID;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Shipment;

public interface ShipmentService {

    List<Shipment> getAllEntities();

    Shipment getEntityByUuid(UUID uuid);

    Shipment saveEntity(Shipment shipment);
    
    List<ShipmentDto> getAll();

    List<ShipmentDto> getAllByClientUuid(UUID clientUuid);
    
    ShipmentDto getByUuid(UUID uuid);
    
    ShipmentDto save(ShipmentDto shipmentDto);
    
    ShipmentDto update(UUID uuid, ShipmentDto shipmentDto);
    
    boolean delete(UUID uuid);
}
