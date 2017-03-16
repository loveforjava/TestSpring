package com.opinta.service;

import com.opinta.model.BarcodeInnerNumber;
import com.opinta.model.Customer;
import com.opinta.model.PostcodePool;
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

    @Autowired
    public InitDbService(CustomerService customerService, BarcodeInnerNumberService barcodeInnerNumberService,
                         PostcodePoolService postcodePoolService) {
        this.customerService = customerService;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
        this.postcodePoolService = postcodePoolService;
    }

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

        PostcodePool postcodePool = new PostcodePool("00001", false);

        List<BarcodeInnerNumber> barcodeInnerNumbers = new ArrayList<>();
        barcodeInnerNumbers.add(new BarcodeInnerNumber("0000001", postcodePool, BarcodeInnerNumber.Status.USED));
        barcodeInnerNumbers.add(new BarcodeInnerNumber("0000002", postcodePool, BarcodeInnerNumber.Status.RESERVED));
        barcodeInnerNumbers.add(new BarcodeInnerNumber("0000003", postcodePool, BarcodeInnerNumber.Status.RESERVED));
        barcodeInnerNumbers.stream().forEach(barcodeInnerNumberService::save);

        // TODO why this code throws StackOverflowError???
//        postcodePool.setBarcodeInnerNumbers(barcodeInnerNumbers);
//        postcodePoolService.save(postcodePool);
    }
}
