package com.opinta.service;

import com.opinta.model.Address;
import java.util.List;

public interface AddressService {
    List<Address> getAll();
    Address getById(Long id);
    void save(Address address);
    Address update(Long id, Address source);
    boolean delete(Long id);
}
