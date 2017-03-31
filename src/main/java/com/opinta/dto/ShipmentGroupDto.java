package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ShipmentGroupDto {
    private UUID uuid;
    private String name;
    private UUID counterpartyUuid;
}
