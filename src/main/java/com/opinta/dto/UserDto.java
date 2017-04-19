package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UserDto {
    @Size(max = 32)
    private String username;
    private UUID counterpartyUuid;
    private UUID token;
}
