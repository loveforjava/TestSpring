package com.opinta.dto;

import java.util.UUID;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static com.opinta.constraint.RegexPattern.BANK_ACCOUNT_LENGTH;
import static com.opinta.constraint.RegexPattern.BANK_CODE_LENGTH;
import static com.opinta.constraint.RegexPattern.CLIENT_NAME_LENGTH;
import static com.opinta.constraint.RegexPattern.CLIENT_UNIQUE_REGISTRATION_NUMBER_LENGTH;
import static com.opinta.constraint.RegexPattern.EXTERNAL_ID_LENGTH;
import static com.opinta.constraint.RegexPattern.PHONE_NUMBER_LENGTH;
import static com.opinta.constraint.RegexPattern.PHONE_NUMBER_REGEX;
import static com.opinta.constraint.RegexPattern.POST_ID_LENGTH;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClientDto {
    private UUID uuid;
    @Size(max = CLIENT_NAME_LENGTH)
    private String name;
    @Size(max = CLIENT_NAME_LENGTH)
    private String firstName;
    @Size(max = CLIENT_NAME_LENGTH)
    private String middleName;
    @Size(max = CLIENT_NAME_LENGTH)
    private String lastName;
    @Size(min = POST_ID_LENGTH, max = POST_ID_LENGTH)
    private String postId;
    @Size(max = EXTERNAL_ID_LENGTH)
    @JsonProperty(access = WRITE_ONLY)
    private String externalId;
    @Size(max = CLIENT_UNIQUE_REGISTRATION_NUMBER_LENGTH)
    private String uniqueRegistrationNumber;
    private UUID counterpartyUuid;
    private long addressId;
    @Size(max = PHONE_NUMBER_LENGTH)
    @Pattern(message = "Phone contains not allowed symbols", regexp = PHONE_NUMBER_REGEX)
    private String phoneNumber;
    private boolean individual;
    @Size(message = "Bank code should contain " + BANK_CODE_LENGTH + " digits",
            min = BANK_CODE_LENGTH, max = BANK_CODE_LENGTH)
    private String bankCode;
    @Size(max = BANK_ACCOUNT_LENGTH)
    private String bankAccount;
}
