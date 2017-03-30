package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.opinta.constraint.RegexPattern.POSTCODE_REGEX;

@Getter
@Setter
@ToString
public class PhoneDto {
    private long id;
    private String phoneNumber;
}
