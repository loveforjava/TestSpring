package com.opinta.dto;

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
    private String id;
    @Size(max = 255)
    private String name;
    @Size(max = 25)
    private String uniqueRegistrationNumber;
    private String counterpartyId;
    private long addressId;
    private String phoneNumber;
}
