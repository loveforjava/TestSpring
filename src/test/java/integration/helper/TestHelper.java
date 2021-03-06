package integration.helper;

import ua.ukrpost.dto.postid.ClientTypeDto;
import ua.ukrpost.entity.Address;
import ua.ukrpost.entity.Client;
import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.Discount;
import ua.ukrpost.entity.DiscountPerCounterparty;
import ua.ukrpost.entity.Phone;
import ua.ukrpost.entity.PostOffice;
import ua.ukrpost.entity.PostcodePool;
import ua.ukrpost.entity.Shipment;
import ua.ukrpost.entity.ShipmentGroup;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ua.ukrpost.service.AddressService;
import ua.ukrpost.service.ClientService;
import ua.ukrpost.service.CounterpartyService;
import ua.ukrpost.service.DiscountPerCounterpartyService;
import ua.ukrpost.service.DiscountService;
import ua.ukrpost.service.PhoneService;
import ua.ukrpost.service.PostOfficeService;
import ua.ukrpost.service.PostcodePoolService;
import ua.ukrpost.service.ShipmentGroupService;
import ua.ukrpost.service.ShipmentService;
import ua.ukrpost.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static ua.ukrpost.util.LogMessageUtil.updateLogEndpoint;
import static java.time.LocalDate.now;

import static ua.ukrpost.entity.ClientType.COMPANY;
import static ua.ukrpost.entity.ClientType.INDIVIDUAL;
import static ua.ukrpost.entity.DeliveryType.D2D;
import static ua.ukrpost.util.LogMessageUtil.deleteOnErrorLogEndpoint;

@Component
@Slf4j
public class TestHelper {
    public static final String SAME_REGION_COUNTRYSIDE = "03027";
    public static final String OTHER_REGION_COUNTRYSIDE = "07024";
    public static final float DISCOUNT = 24.5f;

    public static final String WRONG_CREATED_MESSAGE = "Entity has wrong created time!";
    public static final String WRONG_LAST_MODIFIED_MESSAGE = "Entity has wrong last modified time!";
    public static final String NO_CREATOR_MESSAGE = "Entity doesn't have a creator!";
    public static final String NO_LAST_MODIFIER_MESSAGE = "Entity doesn't have a last modifier!";
    public static final String WRONG_CREATOR_MESSAGE = "Entity was saved with wrong creator!";
    public static final String WRONG_LAST_MODIFIER_MESSAGE = "Entity was update with wrong last modifier!";

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
    private final UserService userService;

    @Autowired
    public TestHelper(ClientService clientService, AddressService addressService,
                      CounterpartyService counterpartyService, PostcodePoolService postcodePoolService,
                      ShipmentService shipmentService, PostOfficeService postOfficeService,
                      PhoneService phoneService, DiscountService discountService,
                      ShipmentGroupService shipmentGroupService,
                      DiscountPerCounterpartyService discountPerCounterpartyService,
                      UserService userService) {
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
        this.userService = userService;
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
        Counterparty counterparty = createCounterparty();
        Shipment shipment = new Shipment(createSenderFor(counterparty), createRecipient(),
                D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        return shipmentService.saveEntity(shipment, userService.getUsersByCounterparty(counterparty).get(0));
    }

    public Shipment createShipment(ShipmentGroup shipmentGroup) throws Exception {
        Counterparty counterparty = shipmentGroup.getCounterparty();
        Shipment shipment = new Shipment(createSenderFor(counterparty), createRecipientFor(counterparty),
                D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        shipment.setShipmentGroup(shipmentGroup);
        return shipmentService.saveEntity(shipment, userService.getUsersByCounterparty(counterparty).get(0));
    }

    public Shipment createShipmentFor(Counterparty counterparty) throws Exception {
        Shipment shipment = new Shipment(createSenderFor(counterparty), createRecipientFor(counterparty),
                D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        return shipmentService.saveEntity(shipment, userService.getUsersByCounterparty(counterparty).get(0));
    }

    public Shipment createShipmentFor(ShipmentGroup shipmentGroup, User user) throws Exception {
        Shipment shipment = new Shipment(createSenderFor(user), createRecipientFor(user),
                D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        shipment.setShipmentGroup(shipmentGroup);
        return shipmentService.saveEntity(shipment, user);
    }

    public Shipment createShipmentWithSameCounterparty(ShipmentGroup shipmentGroup, Counterparty counterparty) throws Exception {
        Client sender = createSenderFor(counterparty);
        Client recipient = createRecipientFor(counterparty);

        Shipment shipment = new Shipment(sender, recipient,
                D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        shipment.setShipmentGroup(shipmentGroup);
        return shipmentService.saveEntity(shipment,
                userService.getUsersByCounterparty(shipment.getSender().getCounterparty()).get(0));
    }

    public void deleteShipment(Shipment shipment) throws Exception {
        try {
            shipmentService.delete(shipment.getUuid(),
                    userService.getUsersByCounterparty(shipment.getSender().getCounterparty()).get(0));
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
    
    public JSONObject toJsonWithPostId(Client client) {
        JSONObject clientPostIdJsonObject = new JSONObject();
        clientPostIdJsonObject.put("postId", client.getPostId());
        return clientPostIdJsonObject;
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
        client.setExternalId("123-fff-000-888-zxc");
        return clientService.saveEntity(client, userService.getUsersByCounterparty(client.getCounterparty()).get(0));
    }

    public Client createClientFor(User user) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(), user.getCounterparty());
        return clientService.saveEntity(client, user);
    }
    
    public Client createSenderWithoutDiscount() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                createCounterparty());
        return clientService.saveEntity(client, userService.getUsersByCounterparty(client.getCounterparty()).get(0));
    }

    public Client createSenderFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Sidorov", "456", createAddress(), createPhone(), counterparty);
        return clientService.saveEntity(client, userService.getUsersByCounterparty(client.getCounterparty()).get(0));
    }
    
    public void assignPostIdTo(Client client) throws Exception {
        ClientTypeDto clientTypeDto = new ClientTypeDto();
        if (client.isIndividual()) {
            clientTypeDto.setType(INDIVIDUAL);
        } else {
            clientTypeDto.setType(COMPANY);
        }
        User user = userService.getUsersByCounterparty(client.getCounterparty()).get(0);
        String postId = clientService.updatePostId(client.getUuid(), clientTypeDto, user).getPostId();
        client.setPostId(postId);
    }

    public Client createRecipientFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Petrov", "123", createAddress(), createPhone(), counterparty);
        return clientService.saveEntity(client, userService.getUsersByCounterparty(client.getCounterparty()).get(0));
    }

    public Client createSenderFor(User user) throws Exception {
        Client client = new Client("FOP Sidorov", "456", createAddress(), createPhone(), user.getCounterparty());
        return clientService.saveEntity(client, user);
    }

    public Client createRecipientFor(User user) throws Exception {
        Client client = new Client("FOP Petrov", "123", createAddress(), createPhone(), user.getCounterparty());
        return clientService.saveEntity(client, user);
    }

    public Client createRecipient() throws Exception {
        Client client = new Client("FOP Petrov Anonimous", "123", createAddress(), createPhone(), createCounterparty());
        User user = createUser(client.getCounterparty());
        return clientService.saveEntity(client, user);
    }

    public Client createClient(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                counterparty);
        return clientService.saveEntity(client, userService.getUsersByCounterparty(client.getCounterparty()).get(0));
    }

    public Client createSenderWithDiscount(User user) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(), user.getCounterparty());
        createDiscountPerCounterparty(createDiscount(), user.getCounterparty());
        return clientService.saveEntity(client, user);
    }

    public Client createRecipientSameRegionFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressSameRegion(), createPhone(),
                counterparty);
        return clientService.saveEntity(client, userService.getUsersByCounterparty(client.getCounterparty()).get(0));
    }

    public Client createRecipientOtherRegionFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressOtherRegion(), createPhone(),
                counterparty);
        return clientService.saveEntity(client, userService.getUsersByCounterparty(client.getCounterparty()).get(0));
    }

    public Client createRecipientSameRegionCountrysideFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressSameRegionCountryside(), createPhone(),
                counterparty);
        return clientService.saveEntity(client, userService.getUsersByCounterparty(client.getCounterparty()).get(0));
    }

    public Client createRecipientOtherRegionCountrysideFor(Counterparty counterparty) throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddressOtherRegionCountryside(), createPhone(),
                counterparty);
        return clientService.saveEntity(client, userService.getUsersByCounterparty(client.getCounterparty()).get(0));
    }
    
    public void deleteClientWithoutDeletingCounterparty(Client client) throws Exception {
        try {
            clientService.delete(client.getUuid(), userService.getUsersByCounterparty(client.getCounterparty()).get(0));
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
            clientService.delete(client.getUuid(), userService.getUsersByCounterparty(client.getCounterparty()).get(0));
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
        return createShipmentGroupFor(createCounterparty());
    }

    public ShipmentGroup createShipmentGroupFor(Counterparty counterparty) throws Exception {
        ShipmentGroup shipmentGroup = new ShipmentGroup();
        shipmentGroup.setName("Group 1");
        shipmentGroup.setCounterparty(counterparty);
        return shipmentGroupService.saveEntity(shipmentGroup,
                userService.getUsersByCounterparty(shipmentGroup.getCounterparty()).get(0));
    }

    public ShipmentGroup createShipmentGroupFor(User user) throws Exception {
        ShipmentGroup shipmentGroup = new ShipmentGroup();
        shipmentGroup.setName("Group 1");
        shipmentGroup.setCounterparty(user.getCounterparty());
        return shipmentGroupService.saveEntity(shipmentGroup, user);
    }

    public void deleteShipmentGroup(ShipmentGroup shipmentGroup) throws Exception {
        try {
            shipmentGroupService.delete(shipmentGroup.getUuid(),
                    userService.getUsersByCounterparty(shipmentGroup.getCounterparty()).get(0));
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
        return counterpartyService.saveEntity(new Counterparty("Modna kasta", createPostcodePool()));
    }

    public User createUser(Counterparty counterparty) throws Exception {
        return userService.saveEntity(new User("Sameperson", counterparty, UUID.randomUUID()));
    }
    
    public PostcodePool createPostcodePool() {
        return postcodePoolService.saveEntity(new PostcodePool("12345", false));
    }

    public void deleteCounterparty(Counterparty counterparty) {
        try {
            List<User> users = userService.getUsersByCounterparty(counterparty);
            for(User user : users) {
                user.setCounterparty(null);
                log.info(updateLogEndpoint(User.class, user));
                userService.updateEntity(user);
            }
            counterpartyService.delete(counterparty.getUuid());
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
        
        Discount discount1 = new Discount("first", now().minusMonths(1), now().plusMonths(3), 10F);
        Discount discount2 = new Discount("second", now().minusMonths(3), now().plusMonths(1), 10F);
        Discount discount3 = new Discount("third", now().minusMonths(1), now().plusMonths(6), 10F);

        created.add(discountService.saveEntity(discount1));
        created.add(discountService.saveEntity(discount2));
        created.add(discountService.saveEntity(discount3));
        
        return created;
    }
    
    public Discount createDiscount() {
        Discount discount = new Discount("one more discount",
                now().minusMonths(2), now().plusMonths(4),
                DISCOUNT);
        return discountService.saveEntity(discount);
    }
    
    public Discount createExpiredDiscount() {
        Discount discount = new Discount("one more discount",
                now().minusMonths(6), now().minusMonths(2), 5F);
        return discountService.saveEntity(discount);
    }
    
    public DiscountPerCounterparty createDiscountPerCounterparty(Discount discount,
            Counterparty counterparty) throws Exception {
        DiscountPerCounterparty discountPerCounterparty = new DiscountPerCounterparty(counterparty, discount,
                now().minusDays(20), now().plusDays(20));
        return discountPerCounterpartyService.saveEntity(discountPerCounterparty,
                userService.getUsersByCounterparty(counterparty).get(0));
    }
    
    public DiscountPerCounterparty createDiscountPerCounterparty(Counterparty counterparty, Discount discount)
            throws Exception {
        DiscountPerCounterparty discountPerCounterparty = new DiscountPerCounterparty(counterparty, discount,
                now().minusDays(15), now().plusDays(25));
        return discountPerCounterpartyService.saveEntity(discountPerCounterparty,
                userService.getUsersByCounterparty(counterparty).get(0));
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
                    userService.getUsersByCounterparty(discountPerCounterparty.getCounterparty()).get(0));
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

    public void deleteUser(User createdUser) {
        try {
            userService.delete(createdUser.getId());
        } catch (IncorrectInputDataException e) {
            log.debug(e.getMessage());
        }
    }
}
