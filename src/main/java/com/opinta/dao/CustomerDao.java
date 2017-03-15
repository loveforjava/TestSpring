package com.opinta.dao;

import com.opinta.model.Customer;

import java.util.List;

public interface CustomerDao {
    List<Customer> getAll();
    Customer getById(Long id);
    void save(Customer customer);
    void update(Customer customer);
    void delete(Customer customer);
}
