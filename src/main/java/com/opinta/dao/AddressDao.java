package com.opinta.dao;

import java.util.List;

import com.opinta.model.Address;

public interface AddressDao {
    List<Address> getAll();
    Address getById(Long id);
    Address save(Address address);
    void update(Address address);
    void delete(Address address);
}
