package com.opinta.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import javax.persistence.Temporal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Data
@NoArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID uuid;
    @ManyToOne
    @JoinColumn(name = "sender_uuid")
    private Client sender;
    @ManyToOne
    @JoinColumn(name = "recipient_uuid")
    private Client recipient;
    @ManyToOne
    @JoinColumn(name = "shipment_group_uuid")
    private ShipmentGroup shipmentGroup;
    @OneToOne
    private BarcodeInnerNumber barcodeInnerNumber;
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    private BigDecimal postPay;
    @ManyToOne
    @JoinColumn(name = "discount_per_counterparty_uuid")
    private DiscountPerCounterparty discountPerCounterparty;
    private String description;

    @Temporal(TIMESTAMP)
    private Date created;
    @Temporal(TIMESTAMP)
    private Date lastModified;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne
    @JoinColumn(name = "modifier_id")
    private User lastModifier;

    public Shipment(Client sender, Client recipient, DeliveryType deliveryType, float weight, float length,
                    BigDecimal declaredPrice, BigDecimal price, BigDecimal postPay) {
        this.sender = sender;
        this.recipient = recipient;
        this.deliveryType = deliveryType;
        this.weight = weight;
        this.length = length;
        this.declaredPrice = declaredPrice;
        this.price = price;
        this.postPay = postPay;
    }
}
