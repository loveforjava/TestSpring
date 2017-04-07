package com.opinta.service;

import com.opinta.dto.classifier.TariffGridDto;
import com.opinta.entity.classifier.TariffGrid;
import com.opinta.entity.W2wVariation;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;

public interface TariffGridService {
    
    List<TariffGrid> getAllEntities();

    TariffGrid getEntityById(long id);
    
    TariffGrid getEntityByDimension(float weight, float length, W2wVariation w2wVariation);

    TariffGrid getMaxTariffEntity(W2wVariation w2wVariation);
    
    List<TariffGridDto> getAll();
    
    TariffGridDto getByDimension(float weight, float length, W2wVariation w2wVariation);
    
    TariffGridDto getById(long id) throws IncorrectInputDataException;
    
    TariffGrid save(TariffGrid tariffGrid);
    
    TariffGrid update(long id, TariffGrid tariffGrid) throws PerformProcessFailedException;
    
    boolean delete(long id);
}
