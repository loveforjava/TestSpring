package com.opinta.service;

import com.opinta.model.Address;
import com.opinta.model.BarcodeInnerNumber;
import com.opinta.model.Client;
import com.opinta.model.Customer;
import com.opinta.model.PostcodePool;
import com.opinta.model.VirtualPostOffice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class InitDbService {
    private CustomerService customerService;
    private BarcodeInnerNumberService barcodeInnerNumberService;
    private PostcodePoolService postcodePoolService;
    private ClientService clientService;
    private AddressService addressService;

    @Autowired
    public InitDbService(CustomerService customerService, BarcodeInnerNumberService barcodeInnerNumberService,
                         PostcodePoolService postcodePoolService, ClientService clientService,
                         AddressService addressService) {
        this.customerService = customerService;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
        this.postcodePoolService = postcodePoolService;
        this.clientService = clientService;
        this.addressService = addressService;
    }

    @PostConstruct
    public void init() {
        populateClients();
    }

    public void populateClients() {
//        List<Customer> customers = new ArrayList<>();
//        customers.add(new Customer("John", "Doe", "djohn@gmail.com", "121-232-3435"));
//        customers.add(new Customer("Russ", "Smith", "sruss@gmail.com", "343-545-2345"));
//        customers.add(new Customer("Kate", "Williams", "kwilliams@gmail.com", "876-237-2987"));
//        customers.add(new Customer("Viral", "Patel", "vpatel@gmail.com", "356-758-8736"));
//        customers.stream().forEach(customerService::save);

        // create PostcodePool with BarcodeInnerNumber
        PostcodePool postcodePool = new PostcodePool("00001", false);
        postcodePool.getBarcodeInnerNumbers().add(new BarcodeInnerNumber("0000001", BarcodeInnerNumber.Status.USED));
        postcodePool.getBarcodeInnerNumbers().add(new BarcodeInnerNumber("0000002", BarcodeInnerNumber.Status.RESERVED));
        postcodePool.getBarcodeInnerNumbers().add(new BarcodeInnerNumber("0000003", BarcodeInnerNumber.Status.RESERVED));
        postcodePoolService.save(postcodePool);

        // create Address
        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address("00001", "Ternopil", "Monastiriska", "Monastiriska", "Sadova", "51", ""));
        addresses.add(new Address("00002", "Kiev", "", "Kiev", "Khreschatik", "121", "37"));
        addresses.forEach(addressService::save);

        // create Client with VirtualPostOffice
        VirtualPostOffice virtualPostOffice = new VirtualPostOffice("Modna kasta", postcodePool);
        Client client = new Client("FOP Ivanov", "001", addresses.get(0), virtualPostOffice);
        clientService.save(client);
        client = new Client("Petrov PP", "002", addresses.get(1), virtualPostOffice);
        clientService.save(client);
    }
}
