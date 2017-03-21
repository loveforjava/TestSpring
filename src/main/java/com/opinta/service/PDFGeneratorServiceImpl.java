package com.opinta.service;

import com.opinta.model.Address;
import com.opinta.model.Client;
import com.opinta.model.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by dponomarenko on 20.03.2017.
 */

@Service
@Slf4j
public class PDFGeneratorServiceImpl implements PDFGeneratorService {
    private ShipmentService shipmentService;
    private static final String PDF_TEMPLATE = "pdfTemplate/post-template.pdf";

    @Autowired
    public PDFGeneratorServiceImpl(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @Override
    public byte[] generateLabel(Long shipmentId) {
        Shipment shipment = shipmentService.getEntityById(shipmentId);

        PDDocument template;

        File file = new File(getClass().getClassLoader().getResource(PDF_TEMPLATE).getFile());
        byte[] data = null;
        try {
            template = PDDocument.load(file);
            PDAcroForm acroForm = template.getDocumentCatalog().getAcroForm();
            List<PDField> fields = acroForm.getFields();
            logFieldsList(fields);
            if (acroForm != null) {
                Client sender = shipment.getSender();

                PDTextField field = (PDTextField) acroForm.getField("senderName");
                field.setValue(sender.getName());

                field = (PDTextField) acroForm.getField("senderPhone");
                //TODO: Temporary value! Change later to the phone from the shipment
                field.setValue("+380673245212");

                field = (PDTextField) acroForm.getField("senderAddress");
                field.setValue(processAddress(sender.getAddress()));

                Client recipient = shipment.getRecipient();

                field = (PDTextField) acroForm.getField("recipientName");
                field.setValue(recipient.getName());

                field = (PDTextField) acroForm.getField("recipientPhone");
                //TODO: Temporary value! Change later to the phone from the shipment.
                field.setValue("+380984122345");

                field = (PDTextField) acroForm.getField("recipientAddress");
                field.setValue(processAddress(recipient.getAddress()));

                field = (PDTextField) acroForm.getField("mass");
                field.setValue("" + shipment.getWeight());

                field = (PDTextField) acroForm.getField("value");
                field.setValue("" + shipment.getDeclaredPrice());

                field = (PDTextField) acroForm.getField("sendingCost");
                field.setValue("" + shipment.getPrice());

                field = (PDTextField) acroForm.getField("postPrice");
                field.setValue("" + shipment.getPostPay());

                field = (PDTextField) acroForm.getField("totalCost");
                field.setValue("" + shipment.getPostPay());
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            template.save(outputStream);
            data = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public byte[] generateForm(Shipment shipment) throws IOException {
        return new byte[0];
    }

    public String processAddress(Address address) {
        return address.getStreet() + " st., " +
                address.getHouseNumber() + "," +
                address.getAppartmentNumber() + ", " +
                address.getCity() + "\n" +
                address.getPostcode();
    }

    private void logFieldsList(List<PDField> fields) {
        StringBuilder sb = new StringBuilder();
        for (PDField field : fields) {
            sb.append(field.getFullyQualifiedName()).append(" ");
            if (field instanceof PDNonTerminalField) {
                PDNonTerminalField nonTerminalField = (PDNonTerminalField) field;
                logFieldsList(nonTerminalField.getChildren());
            }
        }
        System.out.println("Received PDF template with fields: " + sb.toString());
        log.info("Received PDF template with fields: {}", sb);
    }

}
