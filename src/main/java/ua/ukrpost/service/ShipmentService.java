package ua.ukrpost.service;

import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import java.util.List;
import java.util.UUID;

import ua.ukrpost.dto.ShipmentDto;
import ua.ukrpost.entity.Shipment;

public interface ShipmentService {

    List<Shipment> getAllEntities(User user);

    Shipment getEntityByUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException;

    Shipment saveEntity(Shipment shipment, User user) throws AuthException, IncorrectInputDataException;
    
    List<ShipmentDto> getAll(User user);

    List<ShipmentDto> getAllByClientUuid(UUID clientUuid, User user) throws AuthException, IncorrectInputDataException;

    List<ShipmentDto> getAllByShipmentGroupUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException;

    List<Shipment> getAllEntitiesByShipmentGroupUuid(UUID uuid, User user)
            throws AuthException, IncorrectInputDataException;

    ShipmentDto getByUuid(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
    
    ShipmentDto save(ShipmentDto shipmentDto, User user) throws AuthException, IncorrectInputDataException;
    
    ShipmentDto update(UUID uuid, ShipmentDto shipmentDto, User user) throws AuthException,
            PerformProcessFailedException, IncorrectInputDataException;
    
    void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException;

    ShipmentDto removeShipmentGroupFromShipment(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
}
