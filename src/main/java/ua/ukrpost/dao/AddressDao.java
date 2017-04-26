package ua.ukrpost.dao;

import java.util.List;

import ua.ukrpost.entity.Address;

public interface AddressDao {

    List<Address> getAll();

    Address getById(long id);

    Address save(Address address);

    void update(Address address);

    void delete(Address address);
}
