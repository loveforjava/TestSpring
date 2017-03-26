package com.opinta.dao;

import java.util.List;

import com.opinta.entity.Address;

public interface AddressDao {

    List<Address> getAll();

    Address getById(long id);

    Address save(Address address);

    void update(Address address);

    void delete(Address address);
}
