package util;

import com.opinta.dao.ClientDao;
import com.opinta.entity.*;
import com.opinta.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DBHelper  {
    @Autowired
    private ClientService clientService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private VirtualPostOfficeService virtualPostOfficeService;
    @Autowired
    private PostcodePoolService postcodePoolService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private PostOfficeService postOfficeService;

    public PostOffice createPostOffice() {
        PostOffice postOffice = new PostOffice("Lviv post office", createAddress(), createPostcodePool());
        return postOfficeService.saveEntity(postOffice);
    }

    public Shipment createShipment() {
        Shipment shipment = new Shipment(createClient(), createClient(),
                DeliveryType.D2D, 4.0F, 3.8F, new BigDecimal(200), new BigDecimal(30), new BigDecimal(35.2));
        return shipmentService.saveEntity(shipment);
    }

    public Client createClient() {
        Client newClient = new Client("FOP Ivanov", "001", createAddress(), createVirtualPostOffice());
        return clientService.saveEntity(newClient);
    }

    public Address createAddress() {
        Address address = new Address("00001", "Ternopil", "Monastiriska",
                "Monastiriska", "Sadova", "51", "");
        return addressService.saveEntity(address);
    }

    public VirtualPostOffice createVirtualPostOffice() {
        VirtualPostOffice virtualPostOffice = new VirtualPostOffice("Modna kasta", createPostcodePool());
        return virtualPostOfficeService.saveEntity(virtualPostOffice);
    }

    public PostcodePool createPostcodePool() {
        return postcodePoolService.saveEntity(new PostcodePool("12345", false));
    }



}
