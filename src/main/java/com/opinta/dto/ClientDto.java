package com.opinta.dto;

import java.util.UUID;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static com.opinta.constraint.RegexPattern.BANK_CODE_LENGTH;
import static com.opinta.constraint.RegexPattern.PHONE_NUMBER_REGEX;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClientDto {
    private UUID uuid;
    @Size(max = 255)
    private String name;
    @Size(max = 255)
    private String firstName;
    @Size(max = 255)
    private String middleName;
    @Size(max = 255)
    private String lastName;
    @Size(max = 36)
    private String postId;
    @Size(max = 64)
    @JsonProperty(access = WRITE_ONLY)
    private String customId;
    @Size(max = 25)
    private String uniqueRegistrationNumber;
    private UUID counterpartyUuid;
    private long addressId;
    @Size(max = 25)
    @Pattern(message = "Phone contains not allowed symbols", regexp = PHONE_NUMBER_REGEX)
    private String phoneNumber;
    private boolean individual;
    @Size(message = "Bank code should contain " + BANK_CODE_LENGTH + " digits",
            min = BANK_CODE_LENGTH, max = BANK_CODE_LENGTH)
    private String bankCode;
    @Size(max = 255)
    private String bankAccount;
}
