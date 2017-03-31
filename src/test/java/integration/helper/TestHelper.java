package integration.helper;

import com.opinta.entity.*;
import com.opinta.service.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TestHelper {
    private final ClientService clientService;
    private final AddressService addressService;
    private final CounterpartyService counterpartyService;
    private final PostcodePoolService postcodePoolService;
    private final ShipmentService shipmentService;
    private final PostOfficeService postOfficeService;
    private final PhoneService phoneService;
    private final TariffGridService tariffGridService;
    private final ShipmentGroupService shipmentGroupService;

    @Autowired
    public TestHelper(ClientService clientService, AddressService addressService,
                      CounterpartyService counterpartyService, PostcodePoolService postcodePoolService,
                      ShipmentService shipmentService, PostOfficeService postOfficeService,
                      PhoneService phoneService, TariffGridService tariffGridService,
                      ShipmentGroupService shipmentGroupService) {
        this.clientService = clientService;
        this.addressService = addressService;
        this.counterpartyService = counterpartyService;
        this.postcodePoolService = postcodePoolService;
        this.shipmentService = shipmentService;
        this.postOfficeService = postOfficeService;
        this.phoneService = phoneService;
        this.tariffGridService = tariffGridService;
        this.shipmentGroupService = shipmentGroupService;
    }

    public PostOffice createPostOffice() {
        PostOffice postOffice = new PostOffice("Lviv post office", createAddress(), createPostcodePool());
        return postOfficeService.saveEntity(postOffice);
    }

    public void deletePostOffice(PostOffice postOffice) {
        postOfficeService.delete(postOffice.getId());
        postcodePoolService.delete(postOffice.getPostcodePool().getId());
    }

    public Shipment createShipment() throws Exception {
        Shipment shipment = new Shipment(createClient(), createClient(),
                DeliveryType.D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        return shipmentService.saveEntity(shipment);
    }

    public void deleteShipment(Shipment shipment) throws Exception {
        try {
            shipmentService.delete(shipment.getUuid(), shipment.getSender().getCounterparty().getUser());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        try {
            clientService.delete(shipment.getSender().getUuid(), shipment.getSender().getCounterparty().getUser());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        try {
            clientService.delete(shipment.getRecipient().getUuid(), shipment.getRecipient().getCounterparty().getUser());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    public Client createClient() throws Exception {
        Client client = new Client("FOP Ivanov", "001", createAddress(), createPhone(),
                createCounterparty());
        return clientService.saveEntity(client, client.getCounterparty().getUser());
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
        return shipmentGroupService.saveEntity(shipmentGroup, shipmentGroup.getCounterparty().getUser());
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
        return phoneService.saveEntity(new Phone("0934314522"));
    }

    public Address createAddress() {
        Address address = new Address("00001", "Ternopil", "Monastiriska",
                "Monastiriska", "Sadova", "51", "");
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
    
    public List<TariffGrid> populateTariffGrid() {
        List<TariffGrid> tariffGrids = new ArrayList<>();
        
        tariffGrids.add(new TariffGrid(0.25f, 30f, W2wVariation.TOWN, 12f));
        tariffGrids.add(new TariffGrid(0.25f, 30f, W2wVariation.REGION, 15f));
        tariffGrids.add(new TariffGrid(0.25f, 30f, W2wVariation.COUNTRY, 21f));
        
        tariffGrids.add(new TariffGrid(0.5f, 30f, W2wVariation.TOWN, 15f));
        tariffGrids.add(new TariffGrid(0.5f, 30f, W2wVariation.REGION, 18f));
        tariffGrids.add(new TariffGrid(0.5f, 30f, W2wVariation.COUNTRY, 24f));
        
        tariffGrids.add(new TariffGrid(1f, 30f, W2wVariation.TOWN, 18f));
        tariffGrids.add(new TariffGrid(1f, 30f, W2wVariation.REGION, 21f));
        tariffGrids.add(new TariffGrid(1f, 30f, W2wVariation.COUNTRY, 27f));
        
        tariffGrids.add(new TariffGrid(2f, 30f, W2wVariation.TOWN, 21f));
        tariffGrids.add(new TariffGrid(2f, 30f, W2wVariation.REGION, 24f));
        tariffGrids.add(new TariffGrid(2f, 30f, W2wVariation.COUNTRY, 30f));
        
        tariffGrids.add(new TariffGrid(5f, 70f, W2wVariation.TOWN, 24f));
        tariffGrids.add(new TariffGrid(5f, 70f, W2wVariation.REGION, 27f));
        tariffGrids.add(new TariffGrid(5f, 70f, W2wVariation.COUNTRY, 36f));
        
        tariffGrids.add(new TariffGrid(10f, 70f, W2wVariation.TOWN, 27f));
        tariffGrids.add(new TariffGrid(10f, 70f, W2wVariation.REGION, 30f));
        tariffGrids.add(new TariffGrid(10f, 70f, W2wVariation.COUNTRY, 42f));
        
        tariffGrids.add(new TariffGrid(15f, 70f, W2wVariation.TOWN, 30f));
        tariffGrids.add(new TariffGrid(15f, 70f, W2wVariation.REGION, 36f));
        tariffGrids.add(new TariffGrid(15f, 70f, W2wVariation.COUNTRY, 48f));
        
        tariffGrids.add(new TariffGrid(20f, 70f, W2wVariation.TOWN, 36f));
        tariffGrids.add(new TariffGrid(20f, 70f, W2wVariation.REGION, 42f));
        tariffGrids.add(new TariffGrid(20f, 70f, W2wVariation.COUNTRY, 54f));
        
        tariffGrids.add(new TariffGrid(30f, 70f, W2wVariation.TOWN, 42f));
        tariffGrids.add(new TariffGrid(30f, 70f, W2wVariation.REGION, 48f));
        tariffGrids.add(new TariffGrid(30f, 70f, W2wVariation.COUNTRY, 60f));
        
        tariffGrids = tariffGrids
                .stream()
                .map(unsavedGrid -> tariffGridService.save(unsavedGrid))
                .collect(Collectors.toList());
        
        return tariffGrids;
    }
    
    public void deleteTariffGrids(List<TariffGrid> tariffGrids) {
        tariffGridService.deleteGrids(tariffGrids);
    }
}
