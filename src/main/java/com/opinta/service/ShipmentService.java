package com.opinta.service;

import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Shipment;

public interface ShipmentService {

    List<Shipment> getAllEntities(User user);

    Shipment getEntityByUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException;

    Shipment saveEntity(Shipment shipment, User user) throws AuthException, IncorrectInputDataException;
    
    List<ShipmentDto> getAll(User user);

    List<ShipmentDto> getAllByClientUuid(UUID clientUuid, User user) throws AuthException, IncorrectInputDataException;

    List<ShipmentDto> getAllByShipmentGroupUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException;

    List<Shipment> getAllEntitiesByShipmentGroupUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException;

    ShipmentDto getByUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
    
    ShipmentDto save(ShipmentDto shipmentDto, User user) throws AuthException, IncorrectInputDataException;
    
    ShipmentDto update(UUID uuid, ShipmentDto shipmentDto, User user) throws AuthException,
            PerformProcessFailedException, IncorrectInputDataException;
    
    void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
}
