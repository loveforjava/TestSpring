package ua.ukrpost.service;

import ua.ukrpost.dto.ShipmentGroupDto;
import ua.ukrpost.entity.ShipmentGroup;
import ua.ukrpost.entity.User;

import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;

import java.util.List;
import java.util.UUID;

public interface ShipmentGroupService {

    List<ShipmentGroup> getAllEntities(User user);

    ShipmentGroup getEntityById(UUID uuid, User user) throws IncorrectInputDataException, AuthException;

    ShipmentGroup saveEntity(ShipmentGroup shipmentGroup, User user) throws AuthException, IncorrectInputDataException;

    ShipmentGroup updateEntity(UUID uuid, ShipmentGroup source, User user) throws AuthException,
            IncorrectInputDataException, PerformProcessFailedException;

    List<ShipmentGroupDto> getAll(User user);

    ShipmentGroupDto getById(UUID uuid, User user) throws AuthException, IncorrectInputDataException;

    List<ShipmentGroupDto> getAllByCounterpartyId(UUID counterpartyUuid, User user) throws AuthException,
            IncorrectInputDataException;

    ShipmentGroupDto save(ShipmentGroupDto shipmentGroupDto, User user) throws AuthException,
            IncorrectInputDataException;

    ShipmentGroupDto update(UUID uuid, ShipmentGroupDto source, User user) throws IncorrectInputDataException,
            AuthException, PerformProcessFailedException;

    void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
}
