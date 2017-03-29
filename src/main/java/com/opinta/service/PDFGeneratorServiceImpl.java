package com.opinta.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.Code128Writer;
import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Shipment;
import com.opinta.util.MoneyToTextConverter;
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

@Service
@Slf4j
public class PDFGeneratorServiceImpl implements PDFGeneratorService {
    private static final String PDF_LABEL_TEMPLATE = "pdfTemplate/label-template.pdf";
    private static final String PDF_POSTPAY_TEMPLATE = "pdfTemplate/postpay-template.pdf";
    private static final String FONT = "fonts/Roboto-Regular.ttf";

    private MoneyToTextConverter moneyToTextConverter;

    private ShipmentService shipmentService;
    private PDDocument template;
    private PDTextField field;
    private String fontName;

    private BitMatrix bitMatrix;

    @Autowired
    public PDFGeneratorServiceImpl(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
        moneyToTextConverter = new MoneyToTextConverter();
    }

    @Override
    public byte[] generate(long shipmentId) {
        Shipment shipment = shipmentService.getEntityById(shipmentId);
        byte[] labelForm = generateLabel(shipment);
        if (labelForm == null) {
            return labelForm;
        }
        BigDecimal postPay = shipment.getPostPay();
        //Checking postPay value, if more than 0 append postpay form
        if (postPay.compareTo(new BigDecimal(0)) > 0) {
            PDFMergerUtility merger = new PDFMergerUtility();
            merger.addSource(new ByteArrayInputStream(labelForm));
            merger.addSource(new ByteArrayInputStream(generatePostpay(shipment)));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            merger.setDestinationStream(outputStream);
            try {
                merger.mergeDocuments(null);
            } catch (IOException e) {
                log.error("Got an error while merging the documents");
            }
            return outputStream.toByteArray();
        } else {
            return labelForm;
        }
    }

    public byte[] generatePostpay(Shipment shipment) {
        byte[] data = null;
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
            generateClientsData(shipment, acroForm);

            //Splitting price to hryvnas and kopiykas
            BigDecimal postPay = shipment.getPostPay();
            String[] priceParts = String.valueOf(postPay).split("\\.");

            //Populating price fields
            populateField(acroForm, field, "priceHryvnas", priceParts[0]);
            if (priceParts.length > 1) {
                populateField(acroForm, field, "priceKopiyky", priceParts[1]);
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
            log.error("Error while parsing PDF template: " + e.getMessage());
        } catch (NullPointerException e) {
            log.error("Error while reading the template file %s", PDF_LABEL_TEMPLATE);
        }
        return data;
    }

    public byte[] generateLabel(Shipment shipment) {
        byte[] data = null;
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
            generateClientsData(shipment, acroForm);
            setCheckBoxes(shipment, acroForm);

            //Populating rest of the fields
            populateField(acroForm, field, "weight", String.valueOf(shipment.getWeight()));
            populateField(acroForm, field, "declaredPrice", String.valueOf(shipment.getDeclaredPrice()));
            populateField(acroForm, field, "postPay", String.valueOf(shipment.getPostPay()));
            populateField(acroForm, field, "price", String.valueOf(shipment.getPrice()));

            //TODO: Not interactive fields yet!
            populateField(acroForm, field, "sendingCost", String.valueOf(shipment.getPrice()));
            populateField(acroForm, field, "additionalCosts", "0");

            //Creating content stream for the page to allow data appending
            PDPage page = template.getPage(0);
            PDPageContentStream contentStream =
                    new PDPageContentStream(template, page, PDPageContentStream.AppendMode.APPEND, true);

            //Constructing 13 digits of the barcode
            String barcode = shipment.getSender().getCounterparty().getPostcodePool().getPostcode() +
                    shipment.getBarcode().getInnerNumber();

            //Generating first barcode
            bitMatrix = new Code128Writer().encode(barcode, BarcodeFormat.CODE_128, 170, 32, null);
            BufferedImage buffImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
            PDImageXObject ximage = JPEGFactory.createFromImage(template, buffImg);
            contentStream.drawImage(ximage, 242, 790);

            //Generating second barcode
            bitMatrix = new Code128Writer().encode(barcode, BarcodeFormat.CODE_128, 170, 32, null);
            buffImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ximage = JPEGFactory.createFromImage(template, buffImg);
            contentStream.drawImage(ximage, 242, 377);

            contentStream.close();

            //Generating barcode digits
            field = (PDTextField) acroForm.getField("barcodeText");
            field.setDefaultAppearance(String.format("/%s 14 Tf 0 g", fontName));
            field.setValue(barcode);

            //Removing acrofiels
            acroForm.flatten();

            //Saving PDF to byte array
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

    private void populateField(PDAcroForm acroForm,
                               PDTextField field, String fieldName, String fieldValue) throws IOException {
        field = (PDTextField) acroForm.getField(fieldName);
        field.setDefaultAppearance(String.format("/%s 8.64 Tf 0 g", fontName));
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

    private void generateClientsData(Shipment shipment, PDAcroForm acroForm) throws IOException {
        Client sender = shipment.getSender();

        populateField(acroForm, field, "senderName", shipment.getSender().getName());
        populateField(acroForm, field, "senderPhone", sender.getPhone().getPhoneNumber());
        populateField(acroForm, field, "senderAddress", processAddress(sender.getAddress()));

        Client recipient = shipment.getRecipient();

        populateField(acroForm, field, "recipientName", recipient.getName());
        populateField(acroForm, field, "recipientPhone", recipient.getPhone().getPhoneNumber());
        populateField(acroForm, field, "recipientAddress", processAddress(recipient.getAddress()));
    }

    private String processAddress(Address address) {
        return address.getStreet() + " st., " +
                address.getHouseNumber() + ", " +
                address.getCity() + ", " +
                address.getPostcode();
    }
}
