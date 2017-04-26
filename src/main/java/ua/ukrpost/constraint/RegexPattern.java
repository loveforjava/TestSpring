package ua.ukrpost.constraint;

public class RegexPattern {
    public static final int POSTCODE_LENGTH = 5;
    public static final int BANK_CODE_LENGTH = 6;
    public static final int BARCODE_INNER_NUMBER_LENGTH = 8;
    public static final int BARCODE_LENGTH = POSTCODE_LENGTH + BARCODE_INNER_NUMBER_LENGTH;
    public static final int POST_ID_LENGTH = 13;
    public static final int EXTERNAL_ID_LENGTH = 64;
    public static final int BANK_ACCOUNT_LENGTH = 255;
    public static final int PHONE_NUMBER_LENGTH = 25;
    public static final int CLIENT_NAME_LENGTH = 255;
    public static final int CLIENT_UNIQUE_REGISTRATION_NUMBER_LENGTH = 25;

    public static final String POSTCODE_REGEX = "^$|\\d{" + POSTCODE_LENGTH + "}";
    public static final String BARCODE_INNER_NUMBER_REGEX = "^$|\\d{" + BARCODE_INNER_NUMBER_LENGTH + "}";
    public static final String BARCODE_REGEX = "^$|\\d{" + BARCODE_LENGTH + "}";

    public static final String DIGIT_REGEX = "[^\\d]";
    public static final String PHONE_NUMBER_REGEX = "^[0-9()+ -]*$";
    
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
}
