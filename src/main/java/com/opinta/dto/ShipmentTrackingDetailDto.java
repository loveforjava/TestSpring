package com.opinta.dto;

import com.opinta.constraint.EnumString;
import com.opinta.model.PostOffice;
import com.opinta.model.Shipment;
import com.opinta.model.ShipmentStatus;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
