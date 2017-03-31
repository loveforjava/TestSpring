package com.opinta.dto;

import com.opinta.constraint.EnumString;
import com.opinta.entity.ShipmentStatus;
import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ShipmentTrackingDetailDto {
    private long id;
    private UUID shipmentId;
    private long postOfficeId;
    @EnumString(source = ShipmentStatus.class)
    private ShipmentStatus shipmentStatus;
    private Date date;
}
