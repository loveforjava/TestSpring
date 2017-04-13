package com.opinta.constraint;

public class RegexPattern {
    public static final int POSTCODE_LENGTH = 5;
    public static final int BARCODE_INNER_NUMBER_LENGTH = 8;
    public static final int BARCODE_LENGTH = POSTCODE_LENGTH + BARCODE_INNER_NUMBER_LENGTH;

    public static final String POSTCODE_REGEX = "^$|\\d{" + POSTCODE_LENGTH + "}";
    public static final String BARCODE_INNER_NUMBER_REGEX = "^$|\\d{" + BARCODE_INNER_NUMBER_LENGTH + "}";
    public static final String BARCODE_REGEX = "^$|\\d{" + BARCODE_LENGTH + "}";

    public static final String DIGIT_REGEX = "[^\\d]";
    public static final String PHONE_NUMBER_REGEX = "^[0-9()+ -]*$";
}
