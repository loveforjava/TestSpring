package ua.ukrpost.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
import org.hibernate.annotations.GenericGenerator;

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

    private LocalDateTime created;
    private LocalDateTime lastModified;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne
    @JoinColumn(name = "lastModifier_id")
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
