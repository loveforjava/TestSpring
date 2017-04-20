package com.opinta.util;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.opinta.entity.ClientType;
import com.opinta.exception.IncorrectInputDataException;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;

public class AlphabetUtil {
    private static final List<Character> ALPHABET = asList(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z');
    
    public static String characterOf(ClientType clientType) throws IncorrectInputDataException {
        return valueOf(ALPHABET.get(indexOfClientTypeCharacter(clientType)));
    }
    
    private static int indexOfClientTypeCharacter(ClientType clientType) throws IncorrectInputDataException {
        switch (clientType) {
            case COMPANY:
                return ALPHABET.indexOf('L');
            case INDIVIDUAL:
                return ALPHABET.indexOf('P');
            case EMPLOYEE:
                return ALPHABET.indexOf('Z');
            default:
                throw new IncorrectInputDataException(
                        format("ClientType '%s' is not processable.", clientType.name()));
        }
    }
    
    public static String generateRandomChars(int length, boolean upperCase) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int charAlphabetIndex;
        for (int i = 0; i < length; i++) {
            charAlphabetIndex = random.nextInt(ALPHABET.size());
            sb.append(ALPHABET.get(charAlphabetIndex));
        }
        if (!upperCase) {
            return sb.toString().toLowerCase(ENGLISH);
        }
        return sb.toString();
    }
}
