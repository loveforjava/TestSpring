package com.opinta.dto;

import com.opinta.entity.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ShipmentTrackingDetailDto {
    private long id;
    private UUID shipmentUuid;
    private long postOfficeId;
    private ShipmentStatus shipmentStatus;
    private LocalDateTime date;
}
