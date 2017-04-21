package com.opinta.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import lombok.Data;
import lombok.NoArgsConstructor;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Data
@NoArgsConstructor
public class ShipmentTrackingDetail {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "shipment_uuid")
    private Shipment shipment;
    @ManyToOne
    @JoinColumn(name = "post_office_id")
    private PostOffice postOffice;
    @Enumerated(STRING)
    private ShipmentStatus shipmentStatus;
    @Temporal(TIMESTAMP)
    private Date statusDate;

    public ShipmentTrackingDetail(Shipment shipment, PostOffice postOffice, ShipmentStatus shipmentStatus,
                                  Date statusDate) {
        this.shipment = shipment;
        this.postOffice = postOffice;
        this.shipmentStatus = shipmentStatus;
        this.statusDate = statusDate;
    }
}
