package com.opinta.service;

import com.opinta.model.Shipment;

import java.io.IOException;

public interface PDFGeneratorService {
    byte[] generateLabel(long id);
}
