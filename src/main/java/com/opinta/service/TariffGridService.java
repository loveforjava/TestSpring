package com.opinta.service;

import com.opinta.entity.TariffGrid;
import java.util.List;

public interface TariffGridService {
    
    List<TariffGrid> getAll();

    TariffGrid getById(long id);

    TariffGrid save(TariffGrid tariffGrid);

    TariffGrid update(long id, TariffGrid tariffGrid);
    
    boolean delete(long id);

    TariffGrid getPriceByDimension(float weight, float length);
}
