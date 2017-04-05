package integration.helper;

import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Phone;
import com.opinta.entity.PostOffice;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.service.AddressService;
import com.opinta.service.ClientService;
import com.opinta.service.CounterpartyService;
import com.opinta.service.PhoneService;
import com.opinta.service.PostOfficeService;
import com.opinta.service.PostcodePoolService;
import com.opinta.service.ShipmentGroupService;
import com.opinta.service.ShipmentService;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.opinta.entity.DeliveryType.D2D;
import static com.opinta.util.LogMessageUtil.deleteOnErrorLogEndpoint;

@Component
@Slf4j
public class TestHelper {
    public static final String SAME_REGION_COUNTRYSIDE = "03027";
    public static final String OTHER_REGION_COUNTRYSIDE = "07024";

    private final ClientService clientService;
    private final AddressService addressService;
    private final CounterpartyService counterpartyService;
    private final PostcodePoolService postcodePoolService;
    private final ShipmentService shipmentService;
    private final PostOfficeService postOfficeService;
    private final PhoneService phoneService;
    private final ShipmentGroupService shipmentGroupService;

    @Autowired
    public TestHelper(ClientService clientService, AddressService addressService,
                      CounterpartyService counterpartyService, PostcodePoolService postcodePoolService,
                      ShipmentService shipmentService, PostOfficeService postOfficeService,
                      PhoneService phoneService,
                      ShipmentGroupService shipmentGroupService) {
        this.clientService = clientService;
        this.addressService = addressService;
        this.counterpartyService = counterpartyService;
        this.postcodePoolService = postcodePoolService;
        this.shipmentService = shipmentService;
        this.postOfficeService = postOfficeService;
        this.phoneService = phoneService;
        this.shipmentGroupService = shipmentGroupService;
    }

    public PostOffice createPostOffice() {
        PostOffice postOffice = new PostOffice("Lviv post office", createAddress(), createPostcodePool());
        return postOfficeService.saveEntity(postOffice);
    }

    public void deletePostOffice(PostOffice postOffice) {
        try {
            postOfficeService.delete(postOffice.getId());
        } catch (IncorrectInputDataException e) {
            log.error(deleteOnErrorLogEndpoint(PostOffice.class, postOffice.getId()));
        }
        try {
            postcodePoolService.delete(postOffice.getPostcodePool().getUuid());
        } catch (IncorrectInputDataException e) {
            log.error(deleteOnErrorLogEndpoint(PostcodePool.class, postOffice.getPostcodePool().getUuid()));
        }
    }

    public void deletePostcodePool(PostcodePool postcodePool) {
        try {
            postcodePoolService.delete(postcodePool.getUuid());
        } catch (IncorrectInputDataException e) {
            log.error(deleteOnErrorLogEndpoint(PostcodePool.class, postcodePool.getUuid()));
        }
    }

    public Shipment createShipment() throws Exception {
        Shipment shipment = new Shipment(createClientAsSender(), createClient(),
                D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        return shipmentService.saveEntity(shipment, shipment.getSender().getCounterparty().getUser());
    }

    public Shipment createShipment(ShipmentGroup shipmentGroup) throws Exception {
        Shipment shipment = new Shipment(createClientAsSender(), createClient(),
                D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        shipment.setShipmentGroup(shipmentGroup);
        return shipmentService.saveEntity(shipment, shipment.getSender().getCounterparty().getUser());
    }

    public Shipment createShipmentWithSameCounterparty(ShipmentGroup shipmentGroup, Counterparty counterparty) throws Exception {
        Client client = createClientAsSenderWithSameCounterparty(counterparty);

        Shipment shipment = new Shipment(client, client,
                D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        shipment.setShipmentGroup(shipmentGroup);
        return shipmentService.saveEntity(shipment, shipment.getSender().getCounterparty().getUser());
    }

    public void deleteShipment(Shipment shipment) throws Exception {
        try {
            shipmentService.delete(shipment.getUuid(), shipment.getSender().getCounterparty().getUser());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        try {
            deleteClient(shipment.getSender());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        try {
            deleteClient(shipment.getRecipient());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    public Client createClient() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                createCounterparty());
        return clientService.saveEntityAsRecipient(client, client.getCounterparty().getUser());
    }

    public Client createClientWithDiscount() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                createCounterparty());
        client.setDiscount(10f);
        return clientService.saveEntityAsSender(client, client.getCounterparty().getUser());
    }

    public Client createClientAsSender() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                createCounterparty());
        return clientService.saveEntityAsSender(client, client.getCounterparty().getUser());
    }

    public Client createClientAsSenderWithSameCounterparty(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                null);
        client.setCounterparty(counterparty);
        return clientService.saveEntityAsSender(client, client.getCounterparty().getUser());
    }

    public Client createClientSameRegion() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressSameRegion(), createPhone(),
                createCounterparty());
        return clientService.saveEntityAsRecipient(client, client.getCounterparty().getUser());
    }

    public Client createClientOtherRegion() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressOtherRegion(), createPhone(),
                createCounterparty());
        return clientService.saveEntityAsRecipient(client, client.getCounterparty().getUser());
    }

    public Client createClientSameRegionCountryside() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressSameRegionCountryside(), createPhone(),
                createCounterparty());
        return clientService.saveEntityAsRecipient(client, client.getCounterparty().getUser());
    }

    public Client createClientOtherRegionCountryside() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressOtherRegionCountryside(), createPhone(),
                createCounterparty());
        return clientService.saveEntityAsRecipient(client, client.getCounterparty().getUser());
    }

    public void deleteClient(Client client) throws Exception {
        try {
            clientService.delete(client.getUuid(), client.getCounterparty().getUser());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        try {
            addressService.delete(client.getAddress().getId());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        try {
            deleteCounterpartyWithPostcodePool(client.getCounterparty());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    public ShipmentGroup createShipmentGroup() throws Exception {
        ShipmentGroup shipmentGroup = new ShipmentGroup();
        shipmentGroup.setName("Group 1");
        shipmentGroup.setCounterparty(createCounterparty());
        ShipmentGroup newShipmentGroup = shipmentGroupService.saveEntity(shipmentGroup, shipmentGroup.getCounterparty().getUser());
//        createShipment(newShipmentGroup);
        return newShipmentGroup;
    }

    public void deleteShipmentGroup(ShipmentGroup shipmentGroup) throws Exception {
        try {
            shipmentGroupService.delete(shipmentGroup.getUuid(), shipmentGroup.getCounterparty().getUser());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        try {
            deleteCounterpartyWithPostcodePool(shipmentGroup.getCounterparty());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    private Phone createPhone() {
        Random random = new Random();
        int min = 100000000;
        int max = 999999999;
        Integer randomNum = random.nextInt((max - min) + 1) + min;
        return phoneService.saveEntity(new Phone(randomNum.toString()));
    }

    public Phone createCustomPhone(String phoneNumber) {
        return phoneService.saveEntity(new Phone(phoneNumber));
    }

    public Address createAddress() {
        Address address = new Address("00001", "Ternopil", "Monastiriska",
                "Monastiriska", "Sadova", "51", "");
        return addressService.saveEntity(address);
    }

    public Address createAddressSameRegion() {
        Address address = new Address("00002", "Ternopil", "Berezhany",
                "Berezhany", "Rogatynska", "107", "");
        return addressService.saveEntity(address);
    }

    public Address createAddressOtherRegion() {
        Address address = new Address("01001", "Kiev", "Kiev",
                "Kiev", "Khreschatik", "21", "7");
        return addressService.saveEntity(address);
    }

    public Address createAddressSameRegionCountryside() {
        Address address = new Address(SAME_REGION_COUNTRYSIDE, "Ternopil", "Monastiriska",
                "Goryglyady", "Shevchenka", "8", "");
        return addressService.saveEntity(address);
    }

    public Address createAddressOtherRegionCountryside() {
        Address address = new Address(OTHER_REGION_COUNTRYSIDE, "Kiev", "Boyarka",
                "Vesele", "Franka", "21", "");
        return addressService.saveEntity(address);
    }

    public Counterparty createCounterparty() throws Exception {
        Counterparty counterparty = new Counterparty("Modna kasta", createPostcodePool());
        return counterpartyService.saveEntity(counterparty);
    }

    public PostcodePool createPostcodePool() {
        return postcodePoolService.saveEntity(new PostcodePool("12345", false));
    }

    public void deleteCounterpartyWithPostcodePool(Counterparty counterparty) throws Exception{
        try {
            counterpartyService.delete(counterparty.getUuid(), counterparty.getUser());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    public JSONObject getJsonObjectFromFile(String filePath) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(new FileReader(getFileFromResources(filePath)));
    }

    public String getJsonFromFile(String filePath) throws IOException, ParseException {
        return getJsonObjectFromFile(filePath).toString();
    }

    public File getFileFromResources(String path) {
        return new File(getClass().getClassLoader().getResource(path).getFile());
    }
}
