package ua.ukrpost.dao;

import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.PostcodePool;

import java.util.List;
import java.util.UUID;


public interface CounterpartyDao {
    
    List<Counterparty> getAll();
    
    Counterparty getByUuid(UUID uuid);

    List<Counterparty> getByPostcodePool(PostcodePool postcodePool);

    Counterparty save(Counterparty counterparty);
    
    void update(Counterparty counterparty);
    
    void delete(Counterparty counterparty);
}
