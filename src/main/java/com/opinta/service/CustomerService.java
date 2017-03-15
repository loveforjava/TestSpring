package com.opinta.service;

import com.opinta.model.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> getAll();
    Customer getById(Long id);
    void save(Customer customer);
    Customer update(Long id, Customer customer);
    boolean delete(Long id);
}
