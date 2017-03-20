package com.opinta.service;

import com.opinta.model.Address;
import com.opinta.model.Client;
import com.opinta.model.PDFForm;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;

/**
 * Created by dponomarenko on 20.03.2017.
 */

public class PDFGeneratorServiceImpl implements PDFGeneratorService {

    private static final String PDF_TEMPLATE = "pdfTemplate/post-template.pdf";

    @Override
    public PDFForm generate(Client client) throws IOException {
        PDDocument template;

        File file = new File(getClass().getClassLoader().getResource(PDF_TEMPLATE).getFile());
        template = PDDocument.load(file);
        PDAcroForm acroForm = template.getDocumentCatalog().getAcroForm();
        if (acroForm != null) {
            PDTextField field = (PDTextField) acroForm.getField("senderName");
            field.setValue(client.getName());

            field = (PDTextField) acroForm.getField("senderPhone");
            field.setValue("");

            field = (PDTextField) acroForm.getField("senderAddress");
            field.setValue(processAddress(client.getAddress()));
        }
        PDStream stream = new PDStream(template);
        stream.toByteArray();
        return null;
    }

    public String processAddress(Address address) {
        return address.getStreet() + " st., " +
                address.getAppartmentNumber() + ", " +
                address.getCity() + "\n" +
                address.getPostcode();
    }
}
