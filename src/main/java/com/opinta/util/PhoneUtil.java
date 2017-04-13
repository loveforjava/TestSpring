package com.opinta.util;

import com.opinta.entity.Phone;

import java.util.regex.Pattern;

import static com.opinta.constraint.RegexPattern.PHONE_NUMBER_SYMBOLS_REGEX;
import static com.opinta.constraint.RegexPattern.REMOVE_NON_DIGIT_SYMBOLS_REGEX;

public class PhoneUtil {

    public static boolean phoneNumberIsValid(String phoneNumber) {
        return Pattern.compile(PHONE_NUMBER_SYMBOLS_REGEX)
                .matcher(phoneNumber)
                .matches();
    }

    public static void removeNonNumericalCharactersFromPhone(Phone phone) {
        phone.setPhoneNumber(phone.getPhoneNumber().replaceAll(REMOVE_NON_DIGIT_SYMBOLS_REGEX, ""));
    }
}
