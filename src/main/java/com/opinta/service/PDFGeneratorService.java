package com.opinta.service;

import java.util.UUID;

public interface PDFGeneratorService {

    byte[] generateLabel(UUID id);

    byte[] generatePostpay(UUID id);
}
