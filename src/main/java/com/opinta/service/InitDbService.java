package com.opinta.service;

import com.opinta.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class InitDbService {
    @Autowired
    private CustomerService customerService;

    @PostConstruct
    public void init() {
        populateClients();
    }

    public void populateClients() {
        List<Customer> customers = new ArrayList<>();
		customers.add(new Customer("John", "Doe", "djohn@gmail.com", "121-232-3435"));
		customers.add(new Customer("Russ", "Smith", "sruss@gmail.com", "343-545-2345"));
		customers.add(new Customer("Kate", "Williams", "kwilliams@gmail.com", "876-237-2987"));
		customers.add(new Customer("Viral", "Patel", "vpatel@gmail.com", "356-758-8736"));

        customers.stream().forEach(customerService::save);
    }
}
