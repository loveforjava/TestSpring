package ua.ukrpost.dto;

import java.util.UUID;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ua.ukrpost.constraint.RegexPattern;

@Getter
@Setter
@ToString
public class PostcodePoolDto {
    private UUID uuid;
    @Pattern(regexp = RegexPattern.POSTCODE_REGEX)
    private String postcode;
    private boolean closed;
}
