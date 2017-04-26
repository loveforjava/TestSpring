package ua.ukrpost.service;

import ua.ukrpost.dto.classifier.TariffGridDto;
import ua.ukrpost.entity.classifier.TariffGrid;
import ua.ukrpost.entity.W2wVariation;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;

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
