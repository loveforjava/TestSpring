package com.opinta.service;

import com.opinta.entity.User;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Shipment;
import javax.naming.AuthenticationException;

public interface ShipmentService {

    List<Shipment> getAllEntities(User user);

    Shipment getEntityByUuid(UUID uuid, User user)  throws AuthenticationException;

    Shipment saveEntity(Shipment shipment, User user) throws Exception;
    
    List<ShipmentDto> getAll(User user);

    List<ShipmentDto> getAllByClientUuid(UUID clientUuid, User user)  throws AuthenticationException;

    List<ShipmentDto> getAllByShipmentGroupId(UUID uuid, User user) throws Exception;

    ShipmentDto getByUuid(UUID uuid, User user)  throws AuthenticationException;
    
    ShipmentDto save(ShipmentDto shipmentDto, User user) throws Exception;
    
    ShipmentDto update(UUID uuid, ShipmentDto shipmentDto, User user) throws Exception;
    
    void delete(UUID uuid, User user) throws Exception;
}
