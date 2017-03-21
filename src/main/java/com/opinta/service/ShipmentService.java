package com.opinta.service;

import com.opinta.dto.ShipmentDto;
import com.opinta.model.Shipment;

import java.util.List;

public interface ShipmentService {
    List<ShipmentDto> getAll();
    ShipmentDto getById(Long id);
    ShipmentDto save(ShipmentDto shipmentDto);
    ShipmentDto update(Long id, ShipmentDto shipmentDto);
    boolean delete(Long id);
    Shipment getEntityById(Long id);
}
