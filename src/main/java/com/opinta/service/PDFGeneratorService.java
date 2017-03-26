package com.opinta.service;

public interface PDFGeneratorService {

    byte[] generateLabel(long id);

    byte[] generatePostpay(long id);
}
