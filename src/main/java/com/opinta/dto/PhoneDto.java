package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.opinta.constraint.RegexPattern.POSTCODE_REGEX;

@Getter
@Setter
public class PhoneDto {
    private long id;
    private String phoneNumber;
}
