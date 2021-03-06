package ua.ukrpost.dao;

import ua.ukrpost.entity.classifier.TariffGrid;
import ua.ukrpost.entity.W2wVariation;

import java.util.List;

public interface TariffGridDao {

    List<TariffGrid> getAll();

    TariffGrid getById(long id);

    TariffGrid save(TariffGrid tariffGrid);

    void update(TariffGrid tariffGrid);

    void delete(TariffGrid tariffGrid);

    TariffGrid getByDimension(float weight, float length, W2wVariation w2wVariation);

    TariffGrid getLast(W2wVariation w2wVariation);
}
