package ua.ukrpost.service;

import ua.ukrpost.dto.ShipmentTrackingDetailDto;
import ua.ukrpost.entity.ShipmentTrackingDetail;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;

import java.util.List;

public interface ShipmentTrackingDetailService {

    ShipmentTrackingDetail getEntityByUuid(long id) throws IncorrectInputDataException;

    List<ShipmentTrackingDetailDto> getAll();

    ShipmentTrackingDetailDto getById(long id) throws IncorrectInputDataException;

    ShipmentTrackingDetailDto save(ShipmentTrackingDetailDto shipmentTrackingDetailDto);

    ShipmentTrackingDetailDto update(long id, ShipmentTrackingDetailDto shipmentTrackingDetailDto)
            throws IncorrectInputDataException, PerformProcessFailedException;
    
    void delete(long id) throws IncorrectInputDataException;
}
