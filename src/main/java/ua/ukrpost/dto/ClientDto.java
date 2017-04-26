package ua.ukrpost.dto;

import java.util.UUID;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ua.ukrpost.constraint.RegexPattern;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClientDto {
    private UUID uuid;
    @Size(max = RegexPattern.CLIENT_NAME_LENGTH)
    private String name;
    @Size(max = RegexPattern.CLIENT_NAME_LENGTH)
    private String firstName;
    @Size(max = RegexPattern.CLIENT_NAME_LENGTH)
    private String middleName;
    @Size(max = RegexPattern.CLIENT_NAME_LENGTH)
    private String lastName;
    @Size(min = RegexPattern.POST_ID_LENGTH, max = RegexPattern.POST_ID_LENGTH)
    private String postId;
    @Size(max = RegexPattern.EXTERNAL_ID_LENGTH)
    @JsonProperty(access = WRITE_ONLY)
    private String externalId;
    @Size(max = RegexPattern.CLIENT_UNIQUE_REGISTRATION_NUMBER_LENGTH)
    private String uniqueRegistrationNumber;
    private UUID counterpartyUuid;
    private long addressId;
    @Size(max = RegexPattern.PHONE_NUMBER_LENGTH)
    @Pattern(message = "Phone contains not allowed symbols", regexp = RegexPattern.PHONE_NUMBER_REGEX)
    private String phoneNumber;
    private boolean individual;
    @Size(message = "Bank code should contain " + RegexPattern.BANK_CODE_LENGTH + " digits",
            min = RegexPattern.BANK_CODE_LENGTH, max = RegexPattern.BANK_CODE_LENGTH)
    private String bankCode;
    @Size(max = RegexPattern.BANK_ACCOUNT_LENGTH)
    private String bankAccount;
}
