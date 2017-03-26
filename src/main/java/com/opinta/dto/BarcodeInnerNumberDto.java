package com.opinta.dto;

import com.opinta.constraint.EnumString;
import com.opinta.entity.BarcodeStatus;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import static com.opinta.constraint.RegexPattern.BARCODE_INNER_NUMBER_REGEX;

@Getter
@Setter
public class BarcodeInnerNumberDto {
    private long id;
    @Pattern(regexp = BARCODE_INNER_NUMBER_REGEX)
    private String number;
    @EnumString(source = BarcodeStatus.class)
    private BarcodeStatus status;
}
