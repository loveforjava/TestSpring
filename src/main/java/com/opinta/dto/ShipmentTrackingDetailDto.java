package com.opinta.dto;

import com.opinta.constraint.EnumString;
import com.opinta.entity.ShipmentStatus;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentTrackingDetailDto {
    private long id;
    private long shipmentId;
    private long postOfficeId;
    @EnumString(source = ShipmentStatus.class)
    private ShipmentStatus shipmentStatus;
    private Date date;
}
