package com.opinta.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Client sender;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private Client recipient;
    @OneToOne
    private BarcodeInnerNumber barcode;
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    private BigDecimal postPay;
    private String description;

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
