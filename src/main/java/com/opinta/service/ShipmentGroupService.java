package com.opinta.service;

import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.ShipmentGroup;

import java.util.List;
import java.util.UUID;

public interface ShipmentGroupService {

    List<ShipmentGroup> getAllEntities();

    ShipmentGroup getEntityById(UUID uuid);

    ShipmentGroup saveEntity(ShipmentGroup shipmentGroup);

    ShipmentGroup updateEntity(UUID uuid, ShipmentGroup source) throws Exception;
    
    List<ShipmentGroupDto> getAll();

    List<ShipmentGroupDto> getAllByCounterpartyId(long counterpartyId);

    ShipmentGroupDto getById(UUID uuid);

    ShipmentGroupDto save(ShipmentGroupDto shipmentGroupDto) throws Exception;

    ShipmentGroupDto update(UUID uuid, ShipmentGroupDto source) throws Exception;

    boolean delete(UUID uuid);
}
