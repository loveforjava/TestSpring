package com.opinta.dto;

import com.opinta.entity.BarcodeStatus;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.opinta.constraint.RegexPattern.BARCODE_INNER_NUMBER_REGEX;

@Getter
@Setter
@ToString
public class BarcodeInnerNumberDto {
    private long id;
    @Pattern(regexp = BARCODE_INNER_NUMBER_REGEX)
    private String number;
    private BarcodeStatus status;
}
