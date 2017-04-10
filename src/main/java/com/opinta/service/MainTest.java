package com.opinta.service;

import java.io.IOException;

public class MainTest {

    public static void main(String[] args) {

        PDFGeneratorServiceImpl generator = new PDFGeneratorServiceImpl(null, null);
        try {
            generator.generateForm103();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
