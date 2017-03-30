package com.opinta.service;

import com.opinta.entity.Address;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.BarcodeStatus;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.Phone;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.Shipment;
import com.opinta.entity.Client;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.UUID;

import static com.opinta.entity.BarcodeStatus.RESERVED;
import static org.junit.Assert.assertEquals;
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
    private PDFGeneratorService pdfGeneratorService;

    private Shipment shipment;
    private UUID shipmentId;

    @Before
    public void setUp() throws Exception {
        pdfGeneratorService = new PDFGeneratorServiceImpl(shipmentService, userService);

        Address senderAddress = new Address("00001", "Ternopil", "Monastiriska",
                        "Monastiriska", "Sadova", "51", "");
        Address recipientAddress = new Address("00002", "Kiev", "", "Kiev", "Khreschatik", "121", "37");
        Counterparty counterparty = new Counterparty("Modna kasta",
                new PostcodePool("00003", false));
        Client sender = new Client("FOP Ivanov", "001", senderAddress, counterparty);
        Client recipient = new Client("Petrov PP", "002", recipientAddress, counterparty);
        shipment = new Shipment(sender, recipient, DeliveryType.W2W, 1, 1,
                new BigDecimal("12.5"), new BigDecimal("2.5"), new BigDecimal("15.25"));
        shipmentId = UUID.randomUUID();
        shipment.setBarcode(new BarcodeInnerNumber("12345678", RESERVED));
    }

    @Test
    public void generateLabel_and_generatePostpay_ShouldReturnNotEmptyFile() throws Exception {
        // TODO
        User user = new User();

        when(shipmentService.getEntityByUuid(shipmentId, user)).thenReturn(shipment);
        assertNotEquals("PDFGenerator returned an empty label",
                pdfGeneratorService.generate(shipmentId, user).length, 0);
        verify(shipmentService, atLeast(1)).getEntityByUuid(shipmentId, user);
    }
}
