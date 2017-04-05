package com.opinta.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.Code128Writer;
import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Phone;
import com.opinta.entity.Shipment;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.util.MoneyToTextConverter;
import com.opinta.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static java.lang.Math.round;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.rightPad;
import java.util.UUID;

@Service
@Slf4j
public class PDFGeneratorServiceImpl implements PDFGeneratorService {
    private static final String PDF_LABEL_TEMPLATE = "pdfTemplate/label-template.pdf";
    private static final String PDF_POSTPAY_TEMPLATE = "pdfTemplate/postpay-template.pdf";
    private static final String FONT = "fonts/Roboto-Regular.ttf";

    private MoneyToTextConverter moneyToTextConverter;

    private final ShipmentService shipmentService;
    private final UserService userService;

    private PDDocument template;
    private PDTextField field;
    private String fontName;

    private BitMatrix bitMatrix;

    @Autowired
    public PDFGeneratorServiceImpl(ShipmentService shipmentService, UserService userService) {
        this.shipmentService = shipmentService;
        this.moneyToTextConverter = new MoneyToTextConverter();
        this.userService = userService;
    }

    @Override
    public byte[] generate(UUID shipmentId, User user) throws AuthException, IncorrectInputDataException, IOException {
        Shipment shipment = shipmentService.getEntityByUuid(shipmentId, user);

        userService.authorizeForAction(shipment, user);

        byte[] output = generateLabel(shipment);
        BigDecimal postPay = shipment.getPostPay();
        //Checking postPay value, if more than 0 append postpay form
        if (postPay.compareTo(BigDecimal.ZERO) > 0) {
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.addSource(new ByteArrayInputStream(output));
            merger.addSource(new ByteArrayInputStream(generatePostpay(shipment)));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            merger.setDestinationStream(outputStream);
            try {
                merger.mergeDocuments(null);
            } catch (IOException e) {
                log.error("Got an error while merging the documents, {}", e.getMessage());
                throw new IOException("Error while merging templates");
            }
            output = outputStream.toByteArray();
        }
        template.close();
        return output;
    }

    public byte[] generatePostpay(Shipment shipment) throws IOException {
        byte[] data;
        try {
            //Getting PDF template from the file
            File templateFile = new File(getClass()
                    .getClassLoader()
                    .getResource(PDF_POSTPAY_TEMPLATE)
                    .getFile());
            template = PDDocument.load(templateFile);
            PDAcroForm acroForm = template.getDocumentCatalog().getAcroForm();
            //Getting font from the file
            File fontFile = new File(getClass()
                    .getClassLoader()
                    .getResource(FONT)
                    .getFile());
            PDResources res = acroForm.getDefaultResources();
            if (res == null) {
                res = new PDResources();
            }
            //Adding font to the acro form's resources
            InputStream fontStream = new FileInputStream(fontFile);
            PDType0Font font = PDType0Font.load(template, fontStream);
            fontName = res.add(font).getName();
            acroForm.setDefaultResources(res);

            //Populating clients data
            generateClientsData(shipment, acroForm, true, false);

            //Splitting price to hryvnas and kopiykas
            BigDecimal postPay = shipment.getPostPay();
            String[] priceParts = String.valueOf(postPay).split("\\.");

            //Populating price fields
            populateField(acroForm, field, "priceHryvnas", priceParts[0]);
            if (priceParts.length > 1) {
                populateField(acroForm, field, "priceKopiyky", rightPad(priceParts[1], 2, '0'));
            } else {
                populateField(acroForm, field, "priceKopiyky", "00");
            }
            //Converting numerical value to text
            String priceInText = moneyToTextConverter.convert(postPay, false);
            //Populating text field for the price
            populateField(acroForm, field, "priceInText", priceInText);

            //Removing acrofields
            acroForm.flatten();

            //Saving PDF to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            template.save(outputStream);
            data = outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error while parsing and populating PDF template: {}", e.getMessage());
            throw new IOException("Error while parsing and populating PDF template");
        }
        return data;
    }

    public byte[] generateLabel(Shipment shipment) throws IOException {
        byte[] data;
        try {
            //Getting PDF template from the file
            File templateFile = new File(getClass()
                    .getClassLoader()
                    .getResource(PDF_LABEL_TEMPLATE)
                    .getFile());
            template = PDDocument.load(templateFile);
            PDAcroForm acroForm = template.getDocumentCatalog().getAcroForm();
            //Getting font from the file
            File fontFile = new File(getClass()
                    .getClassLoader()
                    .getResource(FONT)
                    .getFile());
            PDResources res = acroForm.getDefaultResources();
            if (res == null) {
                res = new PDResources();
            }
            //Adding font to the acro form's resources
            InputStream fontStream = new FileInputStream(fontFile);
            PDType0Font font = PDType0Font.load(template, fontStream);
            fontName = res.add(font).getName();
            acroForm.setDefaultResources(res);

            //Populating client data
            generateClientsData(shipment, acroForm, false, true);
            setCheckBoxes(shipment, acroForm);

            //Populating rest of the fields
            populateField(acroForm, field, "weight", String.valueOf(round(shipment.getWeight())));
            populateField(acroForm, field, "declaredPrice", String.valueOf(shipment.getDeclaredPrice()));
            populateField(acroForm, field, "postPay", String.valueOf(shipment.getPostPay()));
            populateField(acroForm, field, "price", String.valueOf(shipment.getPrice()));

            populateField(acroForm, field, "sendingCost", String.valueOf(shipment.getPrice()));
            populateField(acroForm, field, "additionalCosts", "0");

            //Creating content stream for the page to allow data appending
            PDPage page = template.getPage(0);
            PDPageContentStream contentStream =
                    new PDPageContentStream(template, page, PDPageContentStream.AppendMode.APPEND, true);

            //Constructing 13 digits of the barcodeInnerNumber
            String barcode = shipment.getBarcodeInnerNumber().getPostcodePool().getPostcode() +
                    shipment.getBarcodeInnerNumber().getInnerNumber();

            //Generating first barcodeInnerNumber
            bitMatrix = new Code128Writer().encode(barcode, BarcodeFormat.CODE_128, 170, 32, null);
            BufferedImage buffImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
            PDImageXObject ximage = JPEGFactory.createFromImage(template, buffImg);
            contentStream.drawImage(ximage, 242, 790);

            //Generating second barcodeInnerNumber
            bitMatrix = new Code128Writer().encode(barcode, BarcodeFormat.CODE_128, 170, 32, null);
            buffImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ximage = JPEGFactory.createFromImage(template, buffImg);
            contentStream.drawImage(ximage, 242, 377);

            contentStream.close();

            //Generating barcodeInnerNumber digits
            field = (PDTextField) acroForm.getField("barcodeText");
            field.setDefaultAppearance(format("/%s 14 Tf 0 g", fontName));
            field.setValue(barcode);

            //Removing acrofiels
            acroForm.flatten();

            //Saving PDF to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            template.save(outputStream);
            data = outputStream.toByteArray();
            outputStream.close();
        } catch (IOException e) {
            log.error("Error while parsing and populating PDF template: {}", e);
            throw new IOException("Error while parsing and populating PDF template");
        } catch (WriterException e) {
            log.error("Error while generating barcodeInnerNumber: {}", e);
            throw new IOException("Error during barcodeInnerNumber generating.");
        }
        return data;
    }

    private void populateField(PDAcroForm acroForm, PDTextField field, String fieldName, String fieldValue)
            throws IOException {
        field = (PDTextField) acroForm.getField(fieldName);
        field.setDefaultAppearance(format("/%s 8.64 Tf 0 g", fontName));
        field.setValue(fieldValue);
    }

    private void setCheckBoxes(Shipment shipment, PDAcroForm acroForm) throws IOException {
        PDCheckBox checkBox;
        Client sender = shipment.getSender();
        if (sender.isIndividual()) {
            checkBox = (PDCheckBox) acroForm.getField("senderIsIndividual");
            checkBox.check();
        } else {
            checkBox = (PDCheckBox) acroForm.getField("senderIsEntity");
            checkBox.check();
        }
        Client recipient = shipment.getRecipient();
        if (recipient.isIndividual()) {
            checkBox = (PDCheckBox) acroForm.getField("recipientIsIndividual");
            checkBox.check();
        } else {
            checkBox = (PDCheckBox) acroForm.getField("recipientIsEntity");
            checkBox.check();
        }
    }

    private void generateClientsData(Shipment shipment, PDAcroForm acroForm, boolean swapSenderWithRecipient,
                                     boolean postcodeOnNextLine) throws IOException {
        Client sender;
        Client recipient;
        if (swapSenderWithRecipient) {
            sender = shipment.getRecipient();
            recipient = shipment.getSender();
        } else {
            sender = shipment.getSender();
            recipient = shipment.getRecipient();
        }


        populateField(acroForm, field, "senderName", sender.getName());

        //Format sender's address for the PDF form and check if postcode should be on the new line
        Address senderAddress = sender.getAddress();
        String formattedAddress = processAddress(senderAddress);
        if (postcodeOnNextLine) {
            formattedAddress += "\n";
        }
        formattedAddress += (senderAddress.getPostcode() == null ? "" : senderAddress.getPostcode());
        populateField(acroForm, field, "senderAddress", formattedAddress);


        Phone phone = sender.getPhone();
        if (phone != null) {
            populateField(acroForm, field, "senderPhone", phone.getPhoneNumber());
        }

        populateField(acroForm, field, "recipientName", recipient.getName());

        //Format recipient's address for the PDF form and check if postcode should be on the new line
        Address recipientAddress = recipient.getAddress();
        formattedAddress = processAddress(recipientAddress);
        if (postcodeOnNextLine) {
            formattedAddress += "\n";
        }
        formattedAddress += (recipientAddress.getPostcode() == null ? "" : recipientAddress.getPostcode());
        populateField(acroForm, field, "recipientAddress", formattedAddress);

        phone = recipient.getPhone();
        if (phone != null) {
            populateField(acroForm, field, "recipientPhone", phone.getPhoneNumber());
        }
    }

    private String processAddress(Address address) {
        if (address == null) {
            return "";
        }

        String street = address.getStreet();
        String houseNumber = address.getHouseNumber();
        String apartmentNumber = address.getApartmentNumber();
        String city = address.getCity();
        String district = address.getDistrict();
        String region = address.getRegion();

        String output = (street == null ? "" : "вул. " + street + ", ") +
                (houseNumber == null ? "" : houseNumber + ", ") +
                (apartmentNumber == null ? "" : "кв. " + apartmentNumber + ", ") +
                (city == null ? "" : city + ", ") +
                (district == null ? "" : district + " р-н ") +
                (region == null ? "" : region + " обл. ");

        return output;
    }
}
