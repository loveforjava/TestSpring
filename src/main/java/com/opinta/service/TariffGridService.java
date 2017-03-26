package com.opinta.service;

import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;
import java.util.List;

public interface TariffGridService {
    
    List<TariffGrid> getAll();

    TariffGrid getById(long id);

    TariffGrid save(TariffGrid tariffGrid);

    TariffGrid update(long id, TariffGrid tariffGrid);
    
    boolean delete(long id);

    TariffGrid getByDimension(float weight, float length, W2wVariation w2wVariation);

    TariffGrid getLast(W2wVariation w2wVariation);
}
