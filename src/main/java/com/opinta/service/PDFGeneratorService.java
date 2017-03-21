package com.opinta.service;

import com.opinta.model.Shipment;

import java.io.IOException;

/**
 * Created by dponomarenko on 20.03.2017.
 */
public interface PDFGeneratorService {
    byte[] generateLabel(Long id);

    byte[] generateForm(Shipment shipment) throws IOException;

}
