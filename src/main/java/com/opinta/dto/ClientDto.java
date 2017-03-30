package com.opinta.dto;

import java.util.UUID;

import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClientDto {
    private UUID uuid;
    @Size(max = 255)
    private String name;
    @Size(max = 25)
    private String uniqueRegistrationNumber;
    private UUID counterpartyUuid;
    private long addressId;
    private String phoneNumber;
}
