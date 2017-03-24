package com.opinta.dao;

import com.opinta.model.TariffGrid;
import java.util.List;

public interface TariffGridDao {
    List<TariffGrid> getAll();
    TariffGrid getById(long id);
    TariffGrid save(TariffGrid tariffGrid);
    void update(TariffGrid tariffGrid);
    void delete(TariffGrid tariffGrid);
    TariffGrid getPriceByDimension(float weight, float length);
}
