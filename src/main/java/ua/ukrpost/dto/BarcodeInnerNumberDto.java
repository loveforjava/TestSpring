package ua.ukrpost.dto;

import ua.ukrpost.entity.BarcodeStatus;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ua.ukrpost.constraint.RegexPattern;

@Getter
@Setter
@ToString
public class BarcodeInnerNumberDto {
    private long id;
    @Pattern(regexp = RegexPattern.BARCODE_INNER_NUMBER_REGEX)
    private String number;
    private BarcodeStatus status;
}
