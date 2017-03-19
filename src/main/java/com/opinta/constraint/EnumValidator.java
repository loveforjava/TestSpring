package com.opinta.constraint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<EnumString, String> {
    private Set<String> AVAILABLE_ENUM_NAMES;

    private Set<String> getNamesSet(Class<? extends Enum<?>> e) {
        Enum<?>[] enums = e.getEnumConstants();
        String[] names = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            names[i] = enums[i].name();
        }
        return new HashSet<>(Arrays.asList(names));
    }

    @Override
    public void initialize(EnumString enumString) {
        AVAILABLE_ENUM_NAMES = getNamesSet(enumString.source());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return AVAILABLE_ENUM_NAMES.contains(value);
    }
}
