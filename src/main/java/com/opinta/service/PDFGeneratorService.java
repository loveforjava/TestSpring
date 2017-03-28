package com.opinta.service;

public interface PDFGeneratorService {

    byte[] generateLabel(String id);

    byte[] generatePostpay(String id);
}
