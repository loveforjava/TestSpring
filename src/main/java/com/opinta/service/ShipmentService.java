package com.opinta.service;

import com.opinta.entity.User;
import java.util.List;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Shipment;
import javax.naming.AuthenticationException;

public interface ShipmentService {

    List<Shipment> getAllEntities(User user);

    Shipment getEntityById(long id, User user) throws AuthenticationException;

    Shipment saveEntity(Shipment shipment);
    
    List<ShipmentDto> getAll(User user);

    List<ShipmentDto> getAllByClientId(long clientId, User user) throws AuthenticationException;
    
    ShipmentDto getById(long id, User user) throws AuthenticationException;
    
    ShipmentDto save(ShipmentDto shipmentDto, User user) throws AuthenticationException;
    
    ShipmentDto update(long id, ShipmentDto shipmentDto, User user) throws Exception;
    
    void delete(long id, User user) throws Exception;
}
