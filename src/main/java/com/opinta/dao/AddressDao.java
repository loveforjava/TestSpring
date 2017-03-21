package com.opinta.dao;

import com.opinta.model.Address;
import java.util.List;

public interface AddressDao {
    List<Address> getAll();
    Address getById(Long id);
    Address save(Address address);
    void update(Address address);
    void delete(Address address);
}
