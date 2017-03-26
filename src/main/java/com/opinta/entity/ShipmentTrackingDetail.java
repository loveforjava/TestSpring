package com.opinta.entity;

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
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ShipmentTrackingDetail {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
    @ManyToOne
    @JoinColumn(name = "post_office_id")
    private PostOffice postOffice;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public ShipmentTrackingDetail(Shipment shipment, PostOffice postOffice, ShipmentStatus shipmentStatus,
                                  Date date) {
        this.shipment = shipment;
        this.postOffice = postOffice;
        this.shipmentStatus = shipmentStatus;
        this.date = date;
    }
}
