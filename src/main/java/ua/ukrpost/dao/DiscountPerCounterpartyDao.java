package ua.ukrpost.dao;

import ua.ukrpost.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import ua.ukrpost.entity.DiscountPerCounterparty;

public interface DiscountPerCounterpartyDao {

    List<DiscountPerCounterparty> getAll(User user);

    DiscountPerCounterparty getByUuid(UUID uuid);

    DiscountPerCounterparty getHighestDiscount(User user, LocalDateTime date);

    DiscountPerCounterparty save(DiscountPerCounterparty discountPerCounterparty);

    void update(DiscountPerCounterparty discountPerCounterparty);

    void delete(DiscountPerCounterparty discountPerCounterparty);
}
