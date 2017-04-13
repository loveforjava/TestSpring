package com.opinta.dao;

import java.util.List;
import java.util.UUID;

import com.opinta.entity.Discount;

public interface DiscountDao {

    List<Discount> getAll();

    Discount getByUuid(UUID uuid);

    Discount save(Discount discount);

    void update(Discount discount);

    void delete(Discount discount);
}
