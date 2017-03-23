package com.opinta.dao;

public interface BarcodeNextIndexGenerationStrategy {
    
    String newInnerNumberFor(String postcode);
    
    default String indexToProperFormat(int index) {
        String barcodeNumber = String.format("%07d", index);
        if (barcodeNumber.length() > 7) {
            throw new RuntimeException(String.format("Barcode '%d%' is too large", barcodeNumber));
        }
        return barcodeNumber;
    }
}
