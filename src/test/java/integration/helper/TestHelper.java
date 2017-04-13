package integration.helper;

import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Discount;
import com.opinta.entity.DiscountPerCounterparty;
import com.opinta.entity.Phone;
import com.opinta.entity.PostOffice;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.service.AddressService;
import com.opinta.service.ClientService;
import com.opinta.service.CounterpartyService;
import com.opinta.service.DiscountPerCounterpartyService;
import com.opinta.service.DiscountService;
import com.opinta.service.PhoneService;
import com.opinta.service.PostOfficeService;
import com.opinta.service.PostcodePoolService;
import com.opinta.service.ShipmentGroupService;
import com.opinta.service.ShipmentService;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.time.LocalDateTime.now;

import static com.opinta.entity.DeliveryType.D2D;
import static com.opinta.util.LogMessageUtil.deleteOnErrorLogEndpoint;

@Component
@Slf4j
public class TestHelper {
    public static final String SAME_REGION_COUNTRYSIDE = "03027";
    public static final String OTHER_REGION_COUNTRYSIDE = "07024";
    public static final float DISCOUNT = 24.5f;

    private final ClientService clientService;
    private final AddressService addressService;
    private final CounterpartyService counterpartyService;
    private final PostcodePoolService postcodePoolService;
    private final ShipmentService shipmentService;
    private final PostOfficeService postOfficeService;
    private final PhoneService phoneService;
    private final ShipmentGroupService shipmentGroupService;
    private final DiscountService discountService;
    private final DiscountPerCounterpartyService discountPerCounterpartyService;

    @Autowired
    public TestHelper(ClientService clientService, AddressService addressService,
                      CounterpartyService counterpartyService, PostcodePoolService postcodePoolService,
                      ShipmentService shipmentService, PostOfficeService postOfficeService,
                      PhoneService phoneService, DiscountService discountService,
                      ShipmentGroupService shipmentGroupService,
                      DiscountPerCounterpartyService discountPerCounterpartyService) {
        this.clientService = clientService;
        this.addressService = addressService;
        this.counterpartyService = counterpartyService;
        this.postcodePoolService = postcodePoolService;
        this.shipmentService = shipmentService;
        this.postOfficeService = postOfficeService;
        this.phoneService = phoneService;
        this.shipmentGroupService = shipmentGroupService;
        this.discountService = discountService;
        this.discountPerCounterpartyService = discountPerCounterpartyService;
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
        Shipment shipment = new Shipment(createClient(), createClient(),
                D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        return shipmentService.saveEntity(shipment, shipment.getSender().getCounterparty().getUser());
    }

    public Shipment createShipment(ShipmentGroup shipmentGroup) throws Exception {
        Shipment shipment = new Shipment(createClient(), createClient(),
                D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        shipment.setShipmentGroup(shipmentGroup);
        return shipmentService.saveEntity(shipment, shipment.getSender().getCounterparty().getUser());
    }

    public Shipment createShipmentWithSameCounterparty(ShipmentGroup shipmentGroup, Counterparty counterparty) throws Exception {
        Client sender = createSenderFor(counterparty);
        Client recipient = createRecipientFor(counterparty);

        Shipment shipment = new Shipment(sender, recipient,
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
        if(shipment.getShipmentGroup() != null) {
            try {
                deleteShipmentGroup(shipment.getShipmentGroup());
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJsonWithUuid(Client client) {
        JSONObject clientUuidJsonObject = new JSONObject();
        clientUuidJsonObject.put("uuid", client.getUuid().toString());
        return clientUuidJsonObject;
    }

    @SuppressWarnings("unchecked")
    public void mergeClientNames(JSONObject target, Client source) {
        target.put("uuid", source.getUuid().toString());
        target.put("name", source.getName());
        target.put("firstName", source.getFirstName());
        target.put("middleName", source.getMiddleName());
        target.put("lastName", source.getLastName());
    }

    @SuppressWarnings("unchecked")
    public void adjustClientData(JSONObject target, Address address) {
        target.put("addressId", address.getId());
        target.remove("uuid");
    }

    public Client createClient() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                createCounterparty());
        return clientService.saveEntity(client, client.getCounterparty().getUser());
    }
    
    public Client createSenderWithoutDiscount() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                createCounterpartyWithoutDiscount());
        return clientService.saveEntity(client, client.getCounterparty().getUser());
    }
    
    public Client createSenderFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Sidorov", "456", createAddress(), createPhone(), counterparty);
        return clientService.saveEntity(client, client.getCounterparty().getUser());
    }
    
    public Client createRecipientFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Petrov", "123", createAddress(), createPhone(), counterparty);
        return clientService.saveEntity(client, client.getCounterparty().getUser());
    }

    public Client createClient(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                counterparty);
        return clientService.saveEntity(client, client.getCounterparty().getUser());
    }

    public Client createSenderWithDiscount() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                createCounterparty());
        client.setDiscount(10f);
        return clientService.saveEntity(client, client.getCounterparty().getUser());
    }

    public Client createRecipientSameRegionFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressSameRegion(), createPhone(),
                counterparty);
        return clientService.saveEntity(client, client.getCounterparty().getUser());
    }

    public Client createRecipientOtherRegionFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressOtherRegion(), createPhone(),
                counterparty);
        return clientService.saveEntity(client, client.getCounterparty().getUser());
    }

    public Client createRecipientSameRegionCountrysideFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressSameRegionCountryside(), createPhone(),
                counterparty);
        return clientService.saveEntity(client, client.getCounterparty().getUser());
    }

    public Client createRecipientOtherRegionCountrysideFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressOtherRegionCountryside(), createPhone(),
                counterparty);
        return clientService.saveEntity(client, client.getCounterparty().getUser());
    }
    
    public void deleteClientWithoutDeletingCounterparty(Client client) throws Exception {
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
            deleteCounterparty(client.getCounterparty());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    public ShipmentGroup createShipmentGroup() throws Exception {
        ShipmentGroup shipmentGroup = new ShipmentGroup();
        shipmentGroup.setName("Group 1");
        shipmentGroup.setCounterparty(createCounterparty());
        ShipmentGroup newShipmentGroup = shipmentGroupService.saveEntity(shipmentGroup, shipmentGroup.getCounterparty().getUser());
        return newShipmentGroup;
    }

    public void deleteShipmentGroup(ShipmentGroup shipmentGroup) throws Exception {
        try {
            shipmentGroupService.delete(shipmentGroup.getUuid(), shipmentGroup.getCounterparty().getUser());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        try {
            deleteCounterparty(shipmentGroup.getCounterparty());
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
        Address address = new Address("00001", "Ternopil", "Monastiriska", "Monastiriska", "Sadova", "51", "");
        return addressService.saveEntity(address);
    }

    public Address createAddressSameRegion() {
        Address address = new Address("00002", "Ternopil", "Berezhany", "Berezhany", "Rogatynska", "107", "");
        return addressService.saveEntity(address);
    }

    public Address createAddressOtherRegion() {
        Address address = new Address("01001", "Kiev", "Kiev", "Kiev", "Khreschatik", "21", "7");
        return addressService.saveEntity(address);
    }

    public Address createAddressSameRegionCountryside() {
        Address address = new Address(SAME_REGION_COUNTRYSIDE, "Ternopil", "Monastiriska", "Goryglyady", "Shevchenka",
                "8", "");
        return addressService.saveEntity(address);
    }

    public Address createAddressOtherRegionCountryside() {
        Address address = new Address(OTHER_REGION_COUNTRYSIDE, "Kiev", "Boyarka", "Vesele", "Franka", "21", "");
        return addressService.saveEntity(address);
    }

    public Counterparty createCounterparty() throws Exception {
        Counterparty counterparty = new Counterparty("Modna kasta", createPostcodePool());
        counterparty.setDiscount(DISCOUNT);
        return counterpartyService.saveEntity(counterparty);
    }
    
    public Counterparty createCounterpartyWithoutDiscount() throws Exception {
        Counterparty counterparty = new Counterparty("Modna kasta", createPostcodePool());
        return counterpartyService.saveEntity(counterparty);
    }

    public PostcodePool createPostcodePool() {
        return postcodePoolService.saveEntity(new PostcodePool("12345", false));
    }

    public void deleteCounterparty(Counterparty counterparty) {
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
    
    public List<Discount> createDiscounts() {
        List<Discount> created = new ArrayList<>();
        
        Discount discount1 = new Discount("first",
                Date.from(now().minusMonths(1).toInstant(ZoneOffset.UTC)),
                Date.from(now().plusMonths(3).toInstant(ZoneOffset.UTC)), 10F);
        Discount discount2 = new Discount("second",
                Date.from(now().minusMonths(3).toInstant(ZoneOffset.UTC)),
                Date.from(now().plusMonths(1).toInstant(ZoneOffset.UTC)), 10F);
        Discount discount3 = new Discount("third",
                Date.from(now().minusMonths(1).toInstant(ZoneOffset.UTC)),
                Date.from(now().plusMonths(6).toInstant(ZoneOffset.UTC)), 10F);

        created.add(discountService.saveEntity(discount1));
        created.add(discountService.saveEntity(discount2));
        created.add(discountService.saveEntity(discount3));
        
        return created;
    }
    
    public Discount createDiscount() {
        Discount discount = new Discount("one more discount",
                Date.from(now().minusMonths(2).toInstant(ZoneOffset.UTC)),
                Date.from(now().plusMonths(4).toInstant(ZoneOffset.UTC)), 5F);
        return discountService.saveEntity(discount);
    }
    
    public Discount createExpiredDiscount() {
        Discount discount = new Discount("one more discount",
                Date.from(now().minusMonths(6).toInstant(ZoneOffset.UTC)),
                Date.from(now().minusMonths(2).toInstant(ZoneOffset.UTC)), 5F);
        return discountService.saveEntity(discount);
    }
    
    public DiscountPerCounterparty createDiscountPerCounterparty(Discount discount,
            Counterparty counterparty) throws Exception {
        DiscountPerCounterparty discountPerCounterparty = new DiscountPerCounterparty(counterparty, discount,
                Date.from(now().minusDays(20).toInstant(ZoneOffset.UTC)),
                Date.from(now().plusDays(20).toInstant(ZoneOffset.UTC)));
        return discountPerCounterpartyService.saveEntity(discountPerCounterparty, counterparty.getUser());
    }
    
    public DiscountPerCounterparty createDiscountPerCounterparty(Counterparty counterparty, Discount discount)
            throws Exception {
        DiscountPerCounterparty discountPerCounterparty = new DiscountPerCounterparty(counterparty, discount,
                Date.from(now().minusDays(15).toInstant(ZoneOffset.UTC)),
                Date.from(now().plusDays(25).toInstant(ZoneOffset.UTC)));
        return discountPerCounterpartyService.saveEntity(discountPerCounterparty, counterparty.getUser());
    }

    public void deleteDiscounts(List<Discount> discounts) {
        discounts.forEach((this::deleteDiscount));
    }
    
    public void deleteDiscount(Discount discount) {
        try {
            discountService.delete(discount.getUuid());
        } catch (IncorrectInputDataException e) {
            log.debug(e.getMessage());
        }
    }
    
    public void deleteDiscountPerCounterparty(DiscountPerCounterparty discountPerCounterparty) {
        try {
            discountPerCounterpartyService.delete(discountPerCounterparty.getUuid(),
                    discountPerCounterparty.getCounterparty().getUser());
        } catch (AuthException | IncorrectInputDataException e) {
            log.debug(e.getMessage());
        }
        if (discountPerCounterparty.getDiscount() != null) {
            deleteDiscount(discountPerCounterparty.getDiscount());
        }
        if (discountPerCounterparty.getCounterparty() != null) {
            deleteCounterparty(discountPerCounterparty.getCounterparty());
        }
    }
}
