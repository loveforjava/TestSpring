package com.opinta.dto;

import java.util.UUID;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.opinta.constraint.RegexPattern.POSTCODE_REGEX;

@Getter
@Setter
@ToString
public class PostcodePoolDto {
    private UUID uuid;
    @Pattern(regexp = POSTCODE_REGEX)
    private String postcode;
    private boolean closed;
}
