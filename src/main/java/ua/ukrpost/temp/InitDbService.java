//package ua.ukrpost.temp;
//
//import AddressDto;
//import BarcodeInnerNumberDto;
//import CounterpartyDto;
//import PostOfficeDto;
//import PostcodePoolDto;
//import ShipmentDto;
//import Counterparty;
//import Phone;
//import ShipmentTrackingDetailMapper;
//import ShipmentStatus;
//import ShipmentTrackingDetail;
//import ShipmentTrackingDetailService;
//import TariffGridService;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//import javax.annotation.PostConstruct;
//
//import AddressMapper;
//import BarcodeInnerNumberMapper;
//import ClientMapper;
//import PostOfficeMapper;
//import PostcodePoolMapper;
//import ShipmentMapper;
//import CounterpartyMapper;
//import Address;
//import BarcodeInnerNumber;
//import Client;
//import DeliveryType;
//import PostOffice;
//import PostcodePool;
//import Shipment;
//import AddressService;
//import BarcodeInnerNumberService;
//import ClientService;
//import PostOfficeService;
//import PostcodePoolService;
//import ShipmentService;
//import CounterpartyService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import static BarcodeStatus.RESERVED;
//import static BarcodeStatus.USED;
//
//@Service
//@Slf4j
//public class InitDbService {
//    private BarcodeInnerNumberService barcodeInnerNumberService;
//    private PostcodePoolService postcodePoolService;
//    private ClientService clientService;
//    private AddressService addressService;
//    private ShipmentService shipmentService;
//    private CounterpartyService counterpartyService;
//    private PostOfficeService postOfficeService;
//    private ShipmentTrackingDetailService shipmentTrackingDetailService;
//
//    private ClientMapper clientMapper;
//    private AddressMapper addressMapper;
//    private PostcodePoolMapper postcodePoolMapper;
//    private BarcodeInnerNumberMapper barcodeInnerNumberMapper;
//    private ShipmentMapper shipmentMapper;
//    private PostOfficeMapper postOfficeMapper;
//    private CounterpartyMapper counterpartyMapper;
//    private ShipmentTrackingDetailMapper shipmentTrackingDetailMapper;
//
//    @Autowired
//    public InitDbService(
//            BarcodeInnerNumberService barcodeInnerNumberService, PostcodePoolService postcodePoolService,
//            ClientService clientService, AddressService addressService, ShipmentService shipmentService,
//            CounterpartyService counterpartyService, PostOfficeService postOfficeService,
//            ShipmentTrackingDetailService shipmentTrackingDetailService, TariffGridService tariffGridService,
//            ClientMapper clientMapper, AddressMapper addressMapper, PostcodePoolMapper postcodePoolMapper,
//            BarcodeInnerNumberMapper barcodeInnerNumberMapper, ShipmentMapper shipmentMapper,
//            PostOfficeMapper postOfficeMapper, CounterpartyMapper counterpartyMapper,
//            ShipmentTrackingDetailMapper shipmentTrackingDetailMapper) {
//        this.barcodeInnerNumberService = barcodeInnerNumberService;
//        this.postcodePoolService = postcodePoolService;
//        this.clientService = clientService;
//        this.addressService = addressService;
//        this.shipmentService = shipmentService;
//        this.counterpartyService = counterpartyService;
//        this.postOfficeService = postOfficeService;
//        this.shipmentTrackingDetailService = shipmentTrackingDetailService;
//        this.clientMapper = clientMapper;
//        this.addressMapper = addressMapper;
//        this.postcodePoolMapper = postcodePoolMapper;
//        this.barcodeInnerNumberMapper = barcodeInnerNumberMapper;
//        this.shipmentMapper = shipmentMapper;
//        this.postOfficeMapper = postOfficeMapper;
//        this.counterpartyMapper = counterpartyMapper;
//        this.shipmentTrackingDetailMapper = shipmentTrackingDetailMapper;
//    }
//
//    @PostConstruct
//    public void init() throws Exception {
//        //populateDb();
//    }
//
//    private void populateDb() throws Exception {
//        // create PostcodePool with BarcodeInnerNumber
//        PostcodePoolDto postcodePoolDto = postcodePoolMapper.toDto(new PostcodePool("00001", false));
//        final long postcodePoolUuid = postcodePoolService.save(postcodePoolDto).getUuid();
//
//        List<BarcodeInnerNumberDto> barcodeInnerNumbers = new ArrayList<>();
//        barcodeInnerNumbers.add(barcodeInnerNumberMapper.toDto(new BarcodeInnerNumber("0000001", USED)));
//        barcodeInnerNumbers.add(barcodeInnerNumberMapper.toDto(new BarcodeInnerNumber("0000002", RESERVED)));
//        barcodeInnerNumbers.add(barcodeInnerNumberMapper.toDto(new BarcodeInnerNumber("0000003", RESERVED)));
//
//        postcodePoolService.addBarcodeInnerNumbers(postcodePoolUuid, barcodeInnerNumbers);
//
//        // create Address
//        List<AddressDto> addresses = new ArrayList<>();
//        List<AddressDto> addressesSaved = new ArrayList<>();
//        addresses.add(addressMapper.toDto(
//                new Address("00001", "Ternopil", "Monastiriska", "Monastiriska", "Sadova", "51", "")));
//        addresses.add(addressMapper.toDto(new Address("00002", "Kiev", "Kiev", "Kiev", "Khreschatik", "121", "37")));
//        addresses.forEach((AddressDto addressDto) -> addressesSaved.add(addressService.save(addressDto)));
//
//        // create Phone
//        Phone phone = new Phone("0934314522");
//        Phone phoneReserved = new Phone("0954623442");
//
//
//        // create Client with Counterparty
//        PostcodePoolDto postcodePoolDto1 = postcodePoolMapper.toDto(new PostcodePool("00003", false));
//        PostcodePoolDto postcodePoolDtoSaved1 = postcodePoolService.save(postcodePoolDto1);
//        Counterparty counterparty = new Counterparty("Modna kasta",
//                postcodePoolMapper.toEntity(postcodePoolDtoSaved1));
//        CounterpartyDto counterpartyDto = this.counterpartyMapper.toDto(counterparty);
//        counterpartyDto = counterpartyService.save(counterpartyDto);
//        counterparty = counterpartyMapper.toEntity(counterpartyDto);
//        List<Client> clients = new ArrayList<>();
//        clients.add(new Client("FOP Ivanov", "001",
//                addressMapper.toEntity(addressesSaved.get(0)), phone, counterparty));
//        clients.add(new Client("Petrov PP", "002",
//                addressMapper.toEntity(addressesSaved.get(1)), phoneReserved, counterparty));
//        clients.forEach((client) -> {
//            log.info("saving client: " + client);
//        });
//        List<Client> clientsSaved = clients
//                .stream()
//                .map(client -> {
//                    try {
//                        return clientService.save(clientMapper.toDto(client), client.getCounterparty().getUser());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .map(clientDto -> clientMapper.toEntity(clientDto))
//                .collect(Collectors.toList());
//
//        // create Shipment
//        List<ShipmentDto> shipmentsSaved = new ArrayList<>();
//        Shipment shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(1), DeliveryType.W2W, 1, 1,
//                new BigDecimal("12.5"), new BigDecimal("2.5"), new BigDecimal("15"));
//        shipmentsSaved.add(shipmentService.save(
//                shipmentMapper.toDto(shipment), shipment.getSender().getCounterparty().getUser()));
//        shipment = new Shipment(clientsSaved.get(0), clientsSaved.get(0), DeliveryType.W2D, 2, 2,
//                new BigDecimal("19.5"), new BigDecimal("0.5"), new BigDecimal("20.5"));
//        shipmentsSaved.add(shipmentService.save(
//                shipmentMapper.toDto(shipment), shipment.getSender().getCounterparty().getUser()));
//        shipment = new Shipment(clientsSaved.get(1), clientsSaved.get(0), DeliveryType.D2D, 3, 3,
//                new BigDecimal("8.5"), new BigDecimal("2.25"), new BigDecimal("13.5"));
//        shipmentsSaved.add(shipmentService.save(
//                shipmentMapper.toDto(shipment), shipment.getSender().getCounterparty().getUser()));
//
//        // create PostOffice
//        PostcodePoolDto postcodePoolDto2 = postcodePoolMapper.toDto(new PostcodePool("00002", false));
//        PostcodePoolDto postcodePoolDtoSaved = postcodePoolService.save(postcodePoolDto2);
//        PostOffice postOffice = new PostOffice("Lviv post office", addressMapper.toEntity(addressesSaved.get(0)),
//                postcodePoolMapper.toEntity(postcodePoolDtoSaved));
//        PostOfficeDto postOfficeSaved = postOfficeService.save(postOfficeMapper.toDto(postOffice));
//
//        // create ShipmentTrackingDetail
//        ShipmentTrackingDetail shipmentTrackingDetail =
//                new ShipmentTrackingDetail(shipmentMapper.toEntity(shipmentsSaved.get(0)),
//                        postOfficeMapper.toEntity(postOfficeSaved), ShipmentStatus.PREPARED, new Date());
//        shipmentTrackingDetailService.save(shipmentTrackingDetailMapper.toDto(shipmentTrackingDetail));
//    }
//}
