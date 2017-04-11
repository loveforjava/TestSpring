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
import static org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PDFGeneratorServiceImpl implements PDFGeneratorService {
    private static final String PDF_LABEL_TEMPLATE = "pdfTemplate/label-template.pdf";
    private static final String PDF_POSTPAY_TEMPLATE = "pdfTemplate/postpay-template.pdf";
    private static final String FONT = "fonts/Roboto-Regular.ttf";
    private static final float[] COLUMN_WIDTHS = {4, 17, 12, 11, 8, 8, 10, 10, 10, 10};

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
        InputStream fontStream = new FileInputStream(fontFile);

        //Initialize Document
        //Set margins
        float margin = 10;

        List<String[]> entries = getEntries();

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        PDType0Font font = PDType0Font.load(doc, fontStream);

        boolean firstPage = true;

        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = 650;
        float yStart = yStartNewPage;
        float bottomMargin = 30;
        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                true);

        float allowedTableSize = yStart - bottomMargin - 50;

        // creating header row for the first page
        createHeaderRow(font, table);
        Row<PDPage> row;
        Cell<PDPage> cell;

        PDPageContentStream contentStream;

        int index = 0;
        for (String[] entry : entries) {
            index++;
            float tableSize = table.getHeaderAndDataHeight();
            if (allowedTableSize < tableSize) {
                //Drawing the header
                contentStream = new PDPageContentStream(doc, table.getCurrentPage(), APPEND, true);
                if (firstPage) {
                    generateHeader(page, font, contentStream);
                    allowedTableSize += 80;
                }
                contentStream.close();
                //Drawing table for the first page
                table.draw();
                firstPage = false;
                //Creating table for the rest of the table
                page = new PDPage();
                doc.addPage(page);
                yStart = page.getMediaBox().getHeight() - (2 * margin);
                yStartNewPage = yStart;
                table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                        true);
                //Creating header for the rest of the table
                createHeaderRow(font, table);
            }
            row = table.createRow(10f);

            cell = row.createCell(COLUMN_WIDTHS[0], String.valueOf(index));
            cell.setFont(font);
            cell.setFontSize(7);

            for (int i = 1; i < entry.length; i++) {
                cell = row.createCell(COLUMN_WIDTHS[i], entry[i]);
                cell.setFont(font);
                cell.setFontSize(7);
            }
        }
        table.draw();

        //Check if there is enough of space for the footer
        float lastTableSize = table.getHeaderAndDataHeight();
        float footerStartY = page.getMediaBox().getHeight() - (2 * margin) - 50;
        //Create new page if there is no space
        if (lastTableSize > 560) {
            page = new PDPage();
            doc.addPage(page);
        } else {
            page = table.getCurrentPage();
            footerStartY = page.getMediaBox().getHeight() - lastTableSize - margin - 30;
        }
        contentStream = new PDPageContentStream(doc, page, APPEND, true);
        generateFooter(font, contentStream, footerStartY);
        contentStream.close();

        // Save the document
        File file = new File("form103_sample.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();
        return null;
    }

    private void generateFooter(PDType0Font font, PDPageContentStream contentStream, float footerStartY)
            throws IOException {
        BigDecimal price = new BigDecimal("7.56"); //TODO: Placeholder
        drawText(font, contentStream, 8, 120, footerStartY, "Разом (плата за пересилання)");
        drawText(font, contentStream, 10, 245, footerStartY, String.valueOf(price));
        drawText(font, contentStream, 10, 268, footerStartY, "(" + moneyToTextConverter.convert(price, false) + ")");
        drawText(font, contentStream, 8, 120, footerStartY - 15, "у т.ч. ПДВ (20%)");
        drawText(font, contentStream, 10, 245, footerStartY - 15,
                price.divide(new BigDecimal("5"), BigDecimal.ROUND_HALF_EVEN).toString());
        drawText(font, contentStream, 8, 120, footerStartY - 30, "за інші послуги ______________________" +
                "______________ на суму _________ грн. _________ коп."); //TODO: Placeholder
        drawText(font, contentStream, 8, 120, footerStartY - 45, "Недозволених до пересилання вкладень немає");
        drawText(font, contentStream, 8, 50, footerStartY - 60, "Керівник установи, підприємства , організації");
        drawText(font, contentStream, 8, 300, footerStartY - 61, "_________________");
        drawText(font, contentStream, 7, 320, footerStartY - 68, "(підпис)");
        drawText(font, contentStream, 8, 450, footerStartY - 61, "__________________________________");
        drawText(font, contentStream, 7, 477, footerStartY - 68, "(прізвище, ініціали)");
        drawText(font, contentStream, 8, 50, footerStartY - 90, "Головний бухгалтер");
        drawText(font, contentStream, 8, 300, footerStartY - 91, "_________________");
        drawText(font, contentStream, 7, 320, footerStartY - 98, "(підпис)");
        drawText(font, contentStream, 8, 450, footerStartY - 91, "__________________________________");
        drawText(font, contentStream, 7, 477, footerStartY - 98, "(прізвище, ініціали)");
        drawText(font, contentStream, 8, 50, footerStartY - 110, "№ розрахункового документа (ів) ___________________");
        drawText(font, contentStream, 8, 50, footerStartY - 125, "Доплата за готівку на суму " +
                "________________________________________грн");
        drawText(font, contentStream, 8, 350, footerStartY - 125, "№ розрахункового  документа " +
                "_____________________________");
        drawText(font, contentStream, 8, 150, footerStartY - 145, "Прийняв __________________________________________" +
                "_________________________________________________________________");
        drawText(font, contentStream, 7, 275, footerStartY - 152, "(прізвище, ініціали, підпис працівника " +
                "поштового зв'язку)");
    }

    private void generateHeader(PDPage page, PDType0Font font, PDPageContentStream contentStream) throws IOException {
        drawText(font, contentStream, 15, 230, 750, "Список \"Group name\"");
        drawText(font, contentStream, 15, 520, 750, "ф. 103");
        drawText(font, contentStream, 10, 50, 730, "згрупованих поштових відправлень");
        drawText(font, contentStream, 10, 250, 729, "_____________________________________________" +
                "______________________");
        drawText(font, contentStream, 10, 320, 730, "посилок з оголошенною цінністю");
        drawText(font, contentStream, 8, 360, 722, "(вид, категорія)");
        drawText(font, contentStream, 10, 50, 710, "поданих в");

        String gottenIn = "Київ 70"; //TODO: Placeholder?
        float textWidth = font.getStringWidth(gottenIn) / 1000 * 10; //10 is font size


        drawText(font, contentStream, 10, 390 - (textWidth / 2), 710, gottenIn);
        drawText(font, contentStream, 10, 200, 709, "_____________________________________________" +
                "_________________________________");

        drawText(font, contentStream, 10, 50, 690, "Відправник");

        String sender = "ООО Тест"; //TODO: Placeholder
        textWidth = font.getStringWidth(sender) / 1000 * 10;

        drawText(font, contentStream, 10, 390 - (textWidth / 2), 690, sender);
        drawText(font, contentStream, 10, 200, 689, "_____________________________________________" +
                "_________________________________");
        drawText(font, contentStream, 8, 320, 682, "(повне найменування відправника)");

        String bankData = "ЄДРПОУ 12345678, МФО 123456, р/р 1234567890123"; //TODO: Placeholder
        textWidth = font.getStringWidth(bankData) / 1000 * 10;

        drawText(font, contentStream, 10, page.getMediaBox().getWidth() / 2 - (textWidth / 2), 660, bankData);
    }

    private void drawText(PDType0Font font, PDPageContentStream contentStream, int fontSize, float x, float y, String text) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private void createHeaderRow(PDType0Font font, BaseTable table) {
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
        entries.add(new String[]{"2", "вулиця Хмельницького, буд. 22, кв. 333, м. Київ, Київ обл., 01001",
                "Петро Петрович Петров", "+380961234567", "", "125", "12000", "25", "50", "346457457845"});
        entries.add(new String[]{"2", "вулиця Лесі Українки, буд. 22, кв. 333, м. Київ, Київ обл., 01001",
                "Іван Іванович Іванов", "+380961234567", "", "125", "12000", "25", "50", "346457457845"});
        entries.add(new String[]{"2", "вулиця Мостицька, буд. 22, кв. 333, м. Київ, Київ обл., 01001",
                "Джон Джонович Джонсон", "+380961234567", "", "125", "12000", "25", "50", "346457457845"});
        entries.add(new String[]{"2", "вулиця Вишгородська, буд. 22, кв. 333, м. Київ, Київ обл., 01001",
                "Петро Іванович Петров", "+380961234567", "", "125", "12000", "25", "50", "346457457845"});
        entries.add(new String[]{"2", "вулиця Полтавська, буд. 22, кв. 333, м. Київ, Київ обл., 01001",
                "Іван Петрович Іванов", "+380961234567", "", "125", "12000", "25", "50", "346457457845"});

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
                    new PDPageContentStream(template, page, APPEND, true);

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
