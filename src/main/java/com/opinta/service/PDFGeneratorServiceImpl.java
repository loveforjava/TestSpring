package com.opinta.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.Code128Writer;
import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.*;

@Service
@Slf4j
public class PDFGeneratorServiceImpl implements PDFGeneratorService {
    private static final String PDF_LABEL_TEMPLATE = "pdfTemplate/label-template.pdf";
    private static final String PDF_POSTPAY_TEMPLATE = "pdfTemplate/postpay-template.pdf";
    private static final String FONT = "fonts/Roboto-Regular.ttf";

    private ShipmentService shipmentService;
    private PDDocument template;
    private PDTextField field;

    private BitMatrix bitMatrix;

    @Autowired
    public PDFGeneratorServiceImpl(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @Override
    public byte[] generatePostpay(long shipmentId) {
        Shipment shipment = shipmentService.getEntityById(shipmentId);
        byte[] data = null;
        try {
            File file = new File(getClass()
                    .getClassLoader()
                    .getResource(PDF_POSTPAY_TEMPLATE)
                    .getFile());
            File fontFile = new File(getClass()
                    .getClassLoader()
                    .getResource(FONT)
                    .getFile());
            template = PDDocument.load(file);
            PDAcroForm acroForm = template.getDocumentCatalog().getAcroForm();

            if (acroForm != null) {
                generateClientsData(fontFile, shipment, acroForm);

                String[] priceParts = String.valueOf(shipment.getPostPay()).split("\\.");

                field = (PDTextField) acroForm.getField("priceHryvnas");
                field.setValue(priceParts[0]);

                if (priceParts.length > 1) {
                    field = (PDTextField) acroForm.getField("priceKopiyky");
                    field.setValue(priceParts[1]);
                }
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            template.save(outputStream);
            data = outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error while parsing PDF template: " + e.getMessage());
        } catch (NullPointerException e) {
            log.error("Error while reading the template file %s", PDF_LABEL_TEMPLATE);
        }
        return data;
    }

    @Override
    public byte[] generateLabel(long shipmentId) {
        Shipment shipment = shipmentService.getEntityById(shipmentId);
        byte[] data = null;
        try {
            //Getting PDF template from the file
            File templateFile = new File(getClass()
                    .getClassLoader()
                    .getResource(PDF_LABEL_TEMPLATE)
                    .getFile());
            File fontFile = new File(getClass()
                    .getClassLoader()
                    .getResource(FONT)
                    .getFile());
            template = PDDocument.load(templateFile);
            PDAcroForm acroForm = template.getDocumentCatalog().getAcroForm();

            if (acroForm != null) {
                //Populating client data
                generateClientsData(fontFile, shipment, acroForm);

                //Populating rest of the fields
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

                //Creating content stream for the page to allow data appending
                PDPage page = template.getPage(0);
                PDPageContentStream contentStream =
                        new PDPageContentStream(template, page, PDPageContentStream.AppendMode.APPEND, true);

                //Constructing 12 digits of the barcode
                String barcode = shipment.getSender().getCounterparty().getPostcodePool().getPostcode() +
                        shipment.getBarcode().getNumber();

                //Generating first barcode
                bitMatrix = new Code128Writer().encode(barcode, BarcodeFormat.CODE_128, 170, 45, null);
                BufferedImage buffImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
                PDImageXObject ximage = JPEGFactory.createFromImage(template, buffImg);
                contentStream.drawImage(ximage, 242, 780);

                //Generate second barcode
                bitMatrix = new Code128Writer().encode(barcode, BarcodeFormat.CODE_128, 170, 45, null);
                buffImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
                ximage = JPEGFactory.createFromImage(template, buffImg);
                contentStream.drawImage(ximage, 242, 367);

                contentStream.close();

                //Generating barcode digits
                field = (PDTextField) acroForm.getField("barcodeText");
                field.setValue(barcode);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            template.save(outputStream);
            data = outputStream.toByteArray();
            outputStream.close();
        } catch (IOException e) {
            log.error("Error while parsing PDF template: " + e.getMessage());
        } catch (NullPointerException e) {
            log.error("Error while reading the template file: " + e.getMessage());
        } catch (WriterException e) {
            log.error("Error while generating barcode: " + e.getMessage());
        }
        return data;
    }

    private void populateField(File fontFile, PDAcroForm acroForm,
                               PDTextField field, String fieldName, String fieldValue) throws IOException {
        field = (PDTextField) acroForm.getField(fieldName);

        field.setDefaultAppearance("/TiRo 8.64 Tf 0 g");
        PDResources res = acroForm.getDefaultResources();
        if (res == null) {
            res = new PDResources();
        }
        InputStream fontStream = new FileInputStream(fontFile);
        PDType0Font font = PDType0Font.load(template, fontStream);
        String fontName = res.add(font).getName();
        acroForm.setDefaultResources(res);
        field.setDefaultAppearance(String.format("/%s 8.64 Tf 0 g", fontName));
        field.setValue(fieldValue);
    }

    private void generateClientsData(File fontFile, Shipment shipment, PDAcroForm acroForm) throws IOException {
        Client sender = shipment.getSender();

        populateField(fontFile, acroForm, field, "senderName", shipment.getSender().getName());
        populateField(fontFile, acroForm, field, "senderPhone", "+380673245212");
        populateField(fontFile, acroForm, field, "senderAddress", processAddress(sender.getAddress()));

        Client recipient = shipment.getRecipient();

        populateField(fontFile, acroForm, field, "recipientName", recipient.getName());
        populateField(fontFile, acroForm, field, "recipientPhone", "+380984122345");
        populateField(fontFile, acroForm, field, "recipientAddress", processAddress(recipient.getAddress()));

//        field = (PDTextField) acroForm.getField("senderPhone");
//        //TODO: Temporary value! Change later to the phone from the shipment
//        field.setValue("+380673245212");
//
//        field = (PDTextField) acroForm.getField("senderAddress");
//        field.setValue(processAddress(sender.getAddress()));
//
//        field = (PDTextField) acroForm.getField("recipientName");
//        field.setValue(recipient.getName());
//
//        field = (PDTextField) acroForm.getField("recipientPhone");
//        //TODO: Temporary value! Change later to the phone from the shipment.
//        field.setValue("+380984122345");
//
//        field = (PDTextField) acroForm.getField("recipientAddress");
//        field.setValue(processAddress(recipient.getAddress()));
    }

    private String processAddress(Address address) {
        return address.getStreet() + " st., " +
                address.getHouseNumber() + ", " +
                address.getCity() + "\n" +
                address.getPostcode();
    }
}
