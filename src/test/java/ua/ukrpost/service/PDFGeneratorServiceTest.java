package ua.ukrpost.service;

import ua.ukrpost.entity.Address;
import ua.ukrpost.entity.BarcodeInnerNumber;
import ua.ukrpost.entity.Client;
import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.DeliveryType;
import ua.ukrpost.entity.PostcodePool;
import ua.ukrpost.entity.Shipment;
import ua.ukrpost.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.UUID.randomUUID;

import static ua.ukrpost.entity.BarcodeStatus.RESERVED;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PDFGeneratorServiceTest {
    @Mock
    private ShipmentService shipmentService;
    @Mock
    private UserService userService;
    @Mock
    private ShipmentGroupService shipmentGroupService;
    private PDFGeneratorService pdfGeneratorService;

    private Shipment shipment;
    private UUID shipmentUuid;
    private User user;

    @Before
    public void setUp() throws Exception {
        pdfGeneratorService = new PDFGeneratorServiceImpl(shipmentService, shipmentGroupService);

        Address senderAddress = new Address("00001", "Ternopil", "Monastiriska", "Monastiriska", "Sadova", "51", "");
        senderAddress.setId(1);
        Address recipientAddress = new Address("00002", "Kiev", "", "Kiev", "Khreschatik", "121", "37");
        recipientAddress.setId(2);
        Counterparty counterparty = new Counterparty("Modna kasta", new PostcodePool("00003", false));
        counterparty.setUuid(randomUUID());
        user = new User("User", counterparty, randomUUID());
        user.setId(123);
        Client sender = new Client("FOP Ivanov", "001", senderAddress, counterparty);
        sender.setUuid(randomUUID());
        Client recipient = new Client("Petrov PP", "002", recipientAddress, counterparty);
        recipient.setUuid(randomUUID());
        shipment = new Shipment(sender, recipient, DeliveryType.W2W, 1, 1,
                new BigDecimal("12.5"), new BigDecimal("2.5"), new BigDecimal("15.25"));
        shipmentUuid = randomUUID();
        shipment.setUuid(shipmentUuid);
        BarcodeInnerNumber barcodeInnerNumber = new BarcodeInnerNumber();
        barcodeInnerNumber.setPostcodePool(counterparty.getPostcodePool());
        barcodeInnerNumber.setInnerNumber("12345678");
        barcodeInnerNumber.setStatus(RESERVED);
        shipment.setBarcodeInnerNumber(barcodeInnerNumber);
    }

    @Test
    public void generateLabel_and_generatePostpay_ShouldReturnNotEmptyFile() throws Exception {
        when(shipmentService.getEntityByUuid(shipmentUuid, user)).thenReturn(shipment);
        assertNotEquals("PDFGenerator returned an empty label",
                pdfGeneratorService.generateShipmentForm(shipmentUuid, user).length, 0);
        verify(shipmentService, atLeast(1)).getEntityByUuid(shipmentUuid, user);
    }
}
