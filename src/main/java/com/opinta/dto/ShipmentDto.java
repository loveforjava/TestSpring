package com.opinta.dto;

import com.opinta.constraint.EnumString;
import com.opinta.model.DeliveryType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShipmentDto {
    private long id;
    private long senderId;
    private long recipientId;
    @EnumString(source = DeliveryType.class)
    private DeliveryType deliveryType;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    private BigDecimal postPay;
    private String description;
}
