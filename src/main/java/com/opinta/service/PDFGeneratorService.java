package com.opinta.service;

import com.opinta.model.Client;
import com.opinta.model.PDFForm;

import java.io.File;
import java.io.IOException;

/**
 * Created by dponomarenko on 20.03.2017.
 */
public interface PDFGeneratorService {

    //TODO: Change to Shipment when it'll be reade
    PDFForm generate(Client client) throws IOException;
}
