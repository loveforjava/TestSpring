package com.opinta.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import static com.opinta.constraint.RegexPattern.POSTCODE_REGEX;

@Getter
@Setter
public class AddressDto {
    private long id;
    @Pattern(regexp = POSTCODE_REGEX)
    private String postcode;
    @Size(max = 25)
    private String region;
    @Size(max = 45)
    private String district;
    @Size(max = 45)
    private String city;
    @Size(max = 255)
    private String street;
    @Size(max = 15)
    private String houseNumber;
    @Size(max = 15)
    private String apartmentNumber;
    @Size(max = 255)
    private String description;
}
