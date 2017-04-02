package com.opinta.service;

import com.opinta.dto.ShipmentTrackingDetailDto;
import com.opinta.entity.ShipmentTrackingDetail;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
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
