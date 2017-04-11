package com.opinta.dao;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.User;
import java.util.List;
import java.util.UUID;


public interface CounterpartyDao {
    
    List<Counterparty> getAll();
    
    Counterparty getByUuid(UUID uuid);
    
    Counterparty getByUser(User user);

    List<Counterparty> getByPostcodePool(PostcodePool postcodePool);

    Counterparty save(Counterparty counterparty);
    
    void update(Counterparty counterparty);
    
    void delete(Counterparty counterparty);
}
