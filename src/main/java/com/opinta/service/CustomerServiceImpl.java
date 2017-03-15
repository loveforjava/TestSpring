package com.opinta.service;

import com.opinta.dao.CustomerDao;
import com.opinta.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerDao customerDao;

    @Override
    @Transactional
    public List<Customer> getAll() {
        log.info("Getting all Customers");
        return customerDao.getAll();
    }

    @Override
    @Transactional
    public Customer getById(Long id) {
        log.info("Getting Customer by id " + id);
        return customerDao.getById(id);
    }

    @Override
    @Transactional
    public void save(Customer customer) {
        log.info("Saving customer " + customer);
        customerDao.save(customer);
    }

    @Override
    @Transactional
    public Customer update(Long id, Customer customer) {
        if (getById(id) == null) {
            log.info("Can't update customer. Customer doesn't exist " + id);
            return null;
        }
        customer.setId(id);
        log.info("Updating customer " + customer);
        customerDao.update(customer);
        return customer;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (getById(id) == null) {
            log.debug("Can't delete customer. Customer doesn't exist " + id);
            return false;
        }
        Customer customer = new Customer();
        customer.setId(id);
        log.info("Deleting customer " + customer);
        customerDao.delete(customer);
        return true;
    }
}
