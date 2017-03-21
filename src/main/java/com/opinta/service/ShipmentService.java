package com.opinta.service;

import java.util.List;

import com.opinta.dto.ShipmentDto;

public interface ShipmentService {
    List<ShipmentDto> getAll();
    ShipmentDto getById(Long id);
    ShipmentDto save(ShipmentDto shipmentDto);
    ShipmentDto update(Long id, ShipmentDto shipmentDto);
    boolean delete(Long id);
}
