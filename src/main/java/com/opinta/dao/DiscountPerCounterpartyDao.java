package com.opinta.dao;

import com.opinta.entity.User;
import java.util.List;
import java.util.UUID;

import com.opinta.entity.DiscountPerCounterparty;

public interface DiscountPerCounterpartyDao {

    List<DiscountPerCounterparty> getAll(User user);

    DiscountPerCounterparty getByUuid(UUID uuid);

    DiscountPerCounterparty save(DiscountPerCounterparty discountPerCounterparty);

    void update(DiscountPerCounterparty discountPerCounterparty);

    void delete(DiscountPerCounterparty discountPerCounterparty);
}
