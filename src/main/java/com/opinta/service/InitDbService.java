package com.opinta.service;

import com.opinta.dto.AddressDto;
import com.opinta.mapper.AddressMapper;
import com.opinta.model.Address;
import com.opinta.model.BarcodeInnerNumber;
import com.opinta.model.BarcodeStatus;
import com.opinta.model.Client;
import com.opinta.model.Customer;
import com.opinta.model.PostcodePool;
import com.opinta.model.VirtualPostOffice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.opinta.model.BarcodeStatus.RESERVED;
import static com.opinta.model.BarcodeStatus.USED;

@Service
public class InitDbService {
    private CustomerService customerService;
    private BarcodeInnerNumberService barcodeInnerNumberService;
    private PostcodePoolService postcodePoolService;
    private ClientService clientService;
    private AddressService addressService;
    private AddressMapper addressMapper;

    @Autowired
    public InitDbService(CustomerService customerService, BarcodeInnerNumberService barcodeInnerNumberService,
                         PostcodePoolService postcodePoolService, ClientService clientService,
                         AddressService addressService, AddressMapper addressMapper) {
        this.customerService = customerService;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
        this.postcodePoolService = postcodePoolService;
        this.clientService = clientService;
        this.addressService = addressService;
        this.addressMapper = addressMapper;
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
        postcodePool.getBarcodeInnerNumbers().add(new BarcodeInnerNumber("0000001", USED));
        postcodePool.getBarcodeInnerNumbers().add(new BarcodeInnerNumber("0000002", RESERVED));
        postcodePool.getBarcodeInnerNumbers().add(new BarcodeInnerNumber("0000003", RESERVED));
        postcodePoolService.save(postcodePool);

        // create Address
        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(addressMapper.toDto(new Address("00001", "Ternopil", "Monastiriska", "Monastiriska", "Sadova", "51", "")));
        addresses.add(addressMapper.toDto(new Address("00002", "Kiev", "", "Kiev", "Khreschatik", "121", "37")));
        addresses.forEach(addressService::save);

//        // create Client with VirtualPostOffice
//        VirtualPostOffice virtualPostOffice = new VirtualPostOffice("Modna kasta", postcodePool);
//        Client client = new Client("FOP Ivanov", "001", addressMapper.toEntity(addresses.get(0)), virtualPostOffice);
//        clientService.save(client);
//        client = new Client("Petrov PP", "002", addressMapper.toEntity(addresses.get(1)), virtualPostOffice);
//        clientService.save(client);
    }
}
