package com.opinta.temp;

import com.opinta.dto.AddressDto;
import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.mapper.AddressMapper;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import com.opinta.mapper.PostcodePoolMapper;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.model.Address;
import com.opinta.model.BarcodeInnerNumber;
import com.opinta.model.Client;
import com.opinta.model.DeliveryType;
import com.opinta.model.PostcodePool;
import com.opinta.model.Shipment;
import com.opinta.model.VirtualPostOffice;
import com.opinta.service.AddressService;
import com.opinta.service.BarcodeInnerNumberService;
import com.opinta.service.ClientService;
import com.opinta.service.PostcodePoolService;
import com.opinta.service.ShipmentService;
import com.opinta.service.VirtualPostOfficeService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.opinta.model.BarcodeStatus.RESERVED;
import static com.opinta.model.BarcodeStatus.USED;

@Service
public class InitDbService {
    private BarcodeInnerNumberService barcodeInnerNumberService;
    private PostcodePoolService postcodePoolService;
    private ClientService clientService;
    private AddressService addressService;
    private ShipmentService shipmentService;
    private VirtualPostOfficeService virtualPostOfficeService;
    private AddressMapper addressMapper;
    private PostcodePoolMapper postcodePoolMapper;
    private BarcodeInnerNumberMapper barcodeInnerNumberMapper;
    private ShipmentMapper shipmentMapper;

    @Autowired
    public InitDbService(BarcodeInnerNumberService barcodeInnerNumberService,
                         PostcodePoolService postcodePoolService, ClientService clientService,
                         AddressService addressService, ShipmentService shipmentService,
                         VirtualPostOfficeService virtualPostOfficeService, AddressMapper addressMapper,
                         PostcodePoolMapper postcodePoolMapper, BarcodeInnerNumberMapper barcodeInnerNumberMapper,
                         ShipmentMapper shipmentMapper) {
        this.barcodeInnerNumberService = barcodeInnerNumberService;
        this.postcodePoolService = postcodePoolService;
        this.clientService = clientService;
        this.addressService = addressService;
        this.shipmentService = shipmentService;
        this.virtualPostOfficeService = virtualPostOfficeService;
        this.addressMapper = addressMapper;
        this.postcodePoolMapper = postcodePoolMapper;
        this.barcodeInnerNumberMapper = barcodeInnerNumberMapper;
        this.shipmentMapper = shipmentMapper;
    }

    @PostConstruct
    public void init() {
        populateClients();
    }

    public void populateClients() {
        // create PostcodePool with BarcodeInnerNumber
        PostcodePoolDto postcodePoolDto = postcodePoolMapper.toDto(new PostcodePool("00001", false));
        final long postcodePoolId = postcodePoolService.save(postcodePoolDto).getId();

        List<BarcodeInnerNumberDto> barcodeInnerNumbers = new ArrayList<>();
        barcodeInnerNumbers.add(barcodeInnerNumberMapper.toDto(new BarcodeInnerNumber("0000001", USED)));
        barcodeInnerNumbers.add(barcodeInnerNumberMapper.toDto(new BarcodeInnerNumber("0000002", RESERVED)));
        barcodeInnerNumbers.add(barcodeInnerNumberMapper.toDto(new BarcodeInnerNumber("0000003", RESERVED)));

        postcodePoolService.addBarcodeInnerNumbers(postcodePoolId, barcodeInnerNumbers);

        // create Address
        List<AddressDto> addresses = new ArrayList<>();
        List<AddressDto> addressesSaved = new ArrayList<>();
        addresses.add(addressMapper.toDto(new Address("00001", "Ternopil", "Monastiriska", "Monastiriska", "Sadova", "51", "")));
        addresses.add(addressMapper.toDto(new Address("00002", "Kiev", "", "Kiev", "Khreschatik", "121", "37")));
        addresses.forEach((AddressDto addressDto) -> addressesSaved.add(addressService.save(addressDto)));

        // create Client with VirtualPostOffice
        VirtualPostOffice virtualPostOffice = new VirtualPostOffice("Modna kasta", postcodePoolMapper.toEntity(postcodePoolDto));
        virtualPostOfficeService.save(virtualPostOffice);
        List<Client> clients = new ArrayList<>();
        List<Client> clientsSaved = new ArrayList<>();
        clients.add(new Client("FOP Ivanov", "001",
                addressMapper.toEntity(addressesSaved.get(0)), virtualPostOffice));
        clients.add(new Client("Petrov PP", "002",
                addressMapper.toEntity(addressesSaved.get(1)), virtualPostOffice));
        clients.forEach((Client client) -> clientsSaved.add(clientService.save(client)));

        Shipment shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(1), DeliveryType.W2W, 1, 1,
                new BigDecimal("12.5"), new BigDecimal("2.5"), new BigDecimal("15"));
        shipmentService.save(shipmentMapper.toDto(shipment));
    }
}
