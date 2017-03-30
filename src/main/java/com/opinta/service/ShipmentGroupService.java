package com.opinta.service;

import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;

import java.util.List;
import java.util.UUID;

public interface ShipmentGroupService {

    List<ShipmentGroup> getAllEntities(User user);

    List<ShipmentGroupDto> getAll(User user);

    ShipmentGroup getEntityById(UUID uuid, User user) throws Exception;

    ShipmentGroupDto getById(UUID uuid, User user) throws Exception;

    List<ShipmentGroupDto> getAllByCounterpartyId(long counterpartyId);

    ShipmentGroup saveEntity(ShipmentGroup shipmentGroup, User user) throws Exception;

    ShipmentGroupDto save(ShipmentGroupDto shipmentGroupDto, User user) throws Exception;

    ShipmentGroup updateEntity(UUID uuid, ShipmentGroup source, User user) throws Exception;

    ShipmentGroupDto update(UUID uuid, ShipmentGroupDto source, User user) throws Exception;

    void delete(UUID uuid, User user) throws Exception;
}
