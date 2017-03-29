package com.opinta.service;

import com.opinta.entity.*;
import com.opinta.entity.Counterparty;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PDFGeneratorServiceTest {
    @Mock
    private ShipmentService shipmentService;
    private PDFGeneratorService pdfGeneratorService;
    private Shipment shipment;

    @Before
    public void setUp() throws Exception {
        pdfGeneratorService = new PDFGeneratorServiceImpl(shipmentService);

        Address senderAddress = new Address("00001", "Ternopil", "Monastiriska",
                        "Monastiriska", "Sadova", "51", "");
        Address recipientAddress = new Address("00002", "Kiev", "", "Kiev", "Khreschatik", "121", "37");
        Counterparty counterparty = new Counterparty("Modna kasta",
                new PostcodePool("00003", false));
        Client sender = new Client("FOP Ivanov", "001", senderAddress, counterparty);
        Client recipient = new Client("Petrov PP", "002", recipientAddress, counterparty);
        shipment = new Shipment(sender, recipient, DeliveryType.W2W, 1, 1,
                new BigDecimal("12.5"), new BigDecimal("2.5"), new BigDecimal("15.25"));
        shipment.setBarcode(new BarcodeInnerNumber("12345678", BarcodeStatus.RESERVED));
    }

    @Test
    public void generateLabel_and_generatePostpay_ShouldReturnNotEmptyFile() {
        when(shipmentService.getEntityById(1L)).thenReturn(shipment);
        assertNotEquals("PDFGenerator returned an empty label",
                pdfGeneratorService.generateLabel(1L).length, 0);
        assertNotEquals("PDFGenerator returned an empty postpay form",
                pdfGeneratorService.generatePostpay(1L).length, 0);
        verify(shipmentService, atLeast(2)).getEntityById(1L);
    }
}
