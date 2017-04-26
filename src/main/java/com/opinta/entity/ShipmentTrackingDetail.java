package com.opinta.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import static javax.persistence.EnumType.STRING;

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
    private LocalDateTime statusDate;

    public ShipmentTrackingDetail(Shipment shipment, PostOffice postOffice, ShipmentStatus shipmentStatus,
                                  LocalDateTime statusDate) {
        this.shipment = shipment;
        this.postOffice = postOffice;
        this.shipmentStatus = shipmentStatus;
        this.statusDate = statusDate;
    }
}
