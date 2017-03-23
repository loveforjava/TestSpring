package com.opinta.service;

import com.opinta.dao.ShipmentDao;
import com.opinta.model.Address;
import com.opinta.model.Client;
import com.opinta.model.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class PDFGeneratorServiceImpl implements PDFGeneratorService {
    private static final String PDF_LABEL_TEMPLATE = "pdfTemplate/label-template.pdf";
    private static final String PDF_POSTPAY_TEMPLATE = "pdfTemplate/postpay-template.pdf";

    private ShipmentDao shipmentDao;
    private PDDocument template;
    private PDTextField field;

    @Autowired
    public PDFGeneratorServiceImpl(ShipmentDao shipmentDao) {
        this.shipmentDao = shipmentDao;
    }

    @Override
    @Transactional
    public byte[] generatePostpay(long shipmentId) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        File file = new File(getClass()
                .getClassLoader()
                .getResource(PDF_POSTPAY_TEMPLATE)
                .getFile());
        byte[] data = null;
        try {
            template = PDDocument.load(file);
            PDAcroForm acroForm = template.getDocumentCatalog().getAcroForm();
            if (acroForm != null) {
                generateClientsData(shipment, acroForm);

                String[] priceParts = String.valueOf(shipment.getPostPay()).split("\\.");

                field = (PDTextField) acroForm.getField("priceHryvnas");
                field.setValue(priceParts[0]);

                if(priceParts.length > 1) {
                    field = (PDTextField) acroForm.getField("priceKopiyky");
                    field.setValue(priceParts[1]);
                }
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
    @Transactional
    public byte[] generateLabel(long shipmentId) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        File file = new File(getClass()
                .getClassLoader()
                .getResource(PDF_LABEL_TEMPLATE)
                .getFile());
        byte[] data = null;
        try {
            template = PDDocument.load(file);
            PDAcroForm acroForm = template.getDocumentCatalog().getAcroForm();
            if (acroForm != null) {
                generateClientsData(shipment, acroForm);

                field = (PDTextField) acroForm.getField("mass");
                field.setValue(String.valueOf(shipment.getWeight()));

                field = (PDTextField) acroForm.getField("value");
                field.setValue(String.valueOf(shipment.getDeclaredPrice()));

                field = (PDTextField) acroForm.getField("sendingCost");
                field.setValue(String.valueOf(shipment.getPrice()));

                field = (PDTextField) acroForm.getField("postPrice");
                field.setValue(String.valueOf(shipment.getPostPay()));

                field = (PDTextField) acroForm.getField("totalCost");
                field.setValue(String.valueOf(shipment.getPostPay()));
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            template.save(outputStream);
            data = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void generateClientsData(Shipment shipment, PDAcroForm acroForm) throws IOException {
        Client sender = shipment.getSender();

        field = (PDTextField) acroForm.getField("senderName");
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
    }

    public String processAddress(Address address) {
        return address.getStreet() + " st., " +
                address.getHouseNumber() + ", " +
                address.getCity() + "\n" +
                address.getPostcode();
    }

}
