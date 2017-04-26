package com.opinta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.opinta.entity.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.opinta.constraint.RegexPattern.DATE_TIME_PATTERN;

@Getter
@Setter
@ToString
public class ShipmentTrackingDetailDto {
    private long id;
    private UUID shipmentUuid;
    private long postOfficeId;
    private ShipmentStatus shipmentStatus;
    @JsonFormat(shape = STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime date;
}
