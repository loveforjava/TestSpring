package com.opinta.service;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import com.google.common.io.Files;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.Code128Writer;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Phone;
import com.opinta.entity.Shipment;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
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

import java.awt.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PDFGeneratorServiceImpl implements PDFGeneratorService {
    private static final String PDF_LABEL_TEMPLATE = "pdfTemplate/label-template.pdf";
    private static final String PDF_POSTPAY_TEMPLATE = "pdfTemplate/postpay-template.pdf";
    private static final String FONT = "fonts/Roboto-Regular.ttf";
    private static final float[] COLUMN_WIDTHS = {6, 14, 12, 12, 8, 8, 10, 10, 10, 10};

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
        this.userService = userService;
        this.moneyToTextConverter = new MoneyToTextConverter();
    }

    public byte[] generateForm103() throws IOException {

        File fontFile = new File(getClass()
                .getClassLoader()
                .getResource(FONT)
                .getFile());
        //Adding font to the acro form's resources
        InputStream fontStream = new FileInputStream(fontFile);

        //Initialize Document
        // Set margins
        float margin = 10;

        List<String[]> entries = getEntries();

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        PDType0Font font = PDType0Font.load(doc, fontStream);

        boolean firstPage = true;

        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 70;
        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                true);

        float remainingSize = page.getMediaBox().getHeight() - yStart - bottomMargin - 30;

        // Create Header row
        createHeaderRow(font, table);
        Row<PDPage> row;
        Cell<PDPage> cell;

        for (String[] entry : entries) {
            float tableSize = table.getHeaderAndDataHeight();
            if (remainingSize < tableSize && firstPage) {
                firstPage = false;
                page = new PDPage();
                doc.addPage(page);
                yStart = 500;
                table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                        true);
                createHeaderRow(font, table);
            }

            row = table.createRow(10f);

            for (int i = 0; i < entry.length; i++) {
                cell = row.createCell(COLUMN_WIDTHS[i], entry[i]);
                cell.setFont(font);
                cell.setFontSize(7);
            }
        }
        table.draw();

        // Create document outline

        // Save the document
        File file = new File("BoxableSample2.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();
        return null;
    }

    private void createHeaderRow(PDType0Font font, BaseTable table) {
//        Row<PDPage> headerRow = table.createRow(10f);
//        Cell<PDPage> cell = headerRow.createCell((100 / 3.0f) * 2, "№ п/п");
//        cell.setFont(font);
//        cell.setFontSize(10);
//        cell.setFillColor(Color.lightGray);

        Row<PDPage> headerRow = table.createRow(10f);
        Cell<PDPage> cell;


        String[] header = {"№ п/п", "Куди (поштова адреса)", "Кому (найменування адресата)", "№ телефону (адресата)",
                "Особливі відмітки", "Маса (г)", "Оголошена цінність відправлення, (грн.)**", "Сума післяплати, (грн.)",
                "Плата за пересилання з ПДВ, (грн.)", "№ відправлення (ШКІ)"};
        for (int i = 0; i < header.length; i++) {
            cell = headerRow.createCell(COLUMN_WIDTHS[i], header[i]);
            cell.setFont(font);
            cell.setFontSize(7);
            cell.setFillColor(Color.lightGray);
        }
        table.addHeaderRow(headerRow);
    }

    private static List<String[]> getEntries() {
        List<String[]> entries = new ArrayList<>();
        entries.add(new String[]{"2", "вулиця Хмельницького, буд. 22, кв. 333, м. Київ, Київ обл., 01001", "Іван Іванович Іванов",
                "+380961234567", "", "125", "12000", "25", "50", "346457457845"});
        entries.add(new String[]{"2", "вулиця Лесі Українки, буд. 22, кв. 333, м. Київ, Київ обл., 01001", "Іван Іванович Іванов",
                "+380961234567", "", "125", "12000", "25", "50", "346457457845"});
        entries.add(new String[]{"2", "вулиця Степана Бандери, буд. 22, кв. 333, м. Київ, Київ обл., 01001", "Іван Іванович Іванов",
                "+380961234567", "", "125", "12000", "25", "50", "346457457845"});
        entries.add(new String[]{"2", "вулиця Вишгородська, буд. 22, кв. 333, м. Київ, Київ обл., 01001", "Іван Іванович Іванов",
                "+380961234567", "", "125", "12000", "25", "50", "346457457845"});
        entries.add(new String[]{"2", "вулиця Полтавська, буд. 22, кв. 333, м. Київ, Київ обл., 01001", "Іван Іванович Іванов",
                "+380961234567", "", "125", "12000", "25", "50", "346457457845"});

        entries.addAll(entries);
        entries.addAll(entries);
        entries.addAll(entries);
        entries.addAll(entries);

        return entries;
    }

    @Override
    public byte[] generateShipmentForm(UUID shipmentId, User user) throws AuthException, IncorrectInputDataException, IOException {
        byte[] output = generateLabelAndPostpayForms(shipmentId, user);
        ByteArrayOutputStream compressedOutput = compress(output);
        return compressedOutput.toByteArray();
    }

    @Override
    public byte[] generateShipmentGroupForms(UUID shipmentGroupUuid, User user) throws AuthException,
            IncorrectInputDataException, IOException {
        List<Shipment> shipments = shipmentService.getAllEntitiesByShipmentGroupUuid(shipmentGroupUuid, user);

        if (shipments.isEmpty()) {
            log.error("Shipment group contains no shipments");
            throw new IncorrectInputDataException("Shipments group contains no shipments");
        }

        PDFMergerUtility merger = new PDFMergerUtility();
        for (Shipment shipment : shipments) {
            byte[] shipmentForm = generateLabelAndPostpayForms(shipment.getUuid(), user);
            ByteArrayInputStream source = new ByteArrayInputStream(shipmentForm);
            merger.addSource(source);
            source.close();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        merger.setDestinationStream(outputStream);
        try {
            merger.mergeDocuments(null);
        } catch (IOException e) {
            log.error("Got an error while merging the documents for shipment group, {}", e.getMessage());
            throw e;
        }
        ByteArrayOutputStream compressedOutput = compress(outputStream.toByteArray());
        template.close();
        return compressedOutput.toByteArray();
    }

    private ByteArrayOutputStream compress(byte[] pdfToCompress) throws IOException {
        PdfReader reader = new PdfReader(pdfToCompress);
        ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();
        try {
            PdfStamper stamper = new PdfStamper(reader, compressedOutput);
            stamper.setFullCompression();
            stamper.close();
        } catch (DocumentException e) {
            log.error("Error occurred during compression of the document, {}", e.getMessage());
        }
        return compressedOutput;
    }

    private byte[] generateLabelAndPostpayForms(UUID shipmentId, User user) throws AuthException, IncorrectInputDataException, IOException {
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
        return output;
    }

    private byte[] generatePostpay(Shipment shipment) throws IOException {
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
            outputStream.close();
        } catch (IOException e) {
            log.error("Error while parsing and populating PDF template: {}", e.getMessage());
            throw new IOException("Error while parsing and populating PDF template");
        } finally {
            template.close();
        }
        return data;
    }

    private byte[] generateLabel(Shipment shipment) throws IOException {
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
        } finally {
            template.close();
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
        populateField(acroForm, field, "senderAddress", processAddress(sender.getAddress(), postcodeOnNextLine));


        Phone phone = sender.getPhone();
        if (phone != null) {
            populateField(acroForm, field, "senderPhone", phone.getPhoneNumber());
        }

        populateField(acroForm, field, "recipientName", recipient.getName());
        populateField(acroForm, field, "recipientAddress", processAddress(recipient.getAddress(), postcodeOnNextLine));

        phone = recipient.getPhone();
        if (phone != null) {
            populateField(acroForm, field, "recipientPhone", phone.getPhoneNumber());
        }
    }

    private String processAddress(Address address, boolean postcodeOnNextLine) {
        if (address == null) {
            return "";
        }

        String street = address.getStreet();
        String houseNumber = address.getHouseNumber();
        String apartmentNumber = address.getApartmentNumber();
        String city = address.getCity();
        String district = address.getDistrict();
        String region = address.getRegion();
        String postcode = address.getPostcode();

        String output = (street == null ? "" : "вул. " + street + ", ") +
                (houseNumber == null ? "" : houseNumber + ", ") +
                (apartmentNumber == null ? "" : "кв. " + apartmentNumber + ", ") +
                (city == null ? "" : city + ", ") +
                (district == null ? "" : district + " р-н ") +
                (region == null ? "" : region + " обл.") +
                (postcodeOnNextLine ? "\n" : " ") +
                (postcode == null ? "" : postcode);

        return output;
    }
}
