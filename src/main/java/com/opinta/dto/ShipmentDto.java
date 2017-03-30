package com.opinta.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.opinta.constraint.EnumString;
import com.opinta.entity.DeliveryType;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ShipmentDto {
    private UUID uuid;
    private UUID senderUuid;
    private UUID recipientUuid;
    @EnumString(source = DeliveryType.class)
    private DeliveryType deliveryType;
    @Size(min = 13, max = 13)
    private String barcode;
    private float weight;
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    private BigDecimal postPay;
    @Size(max = 255)
    private String description;
}
