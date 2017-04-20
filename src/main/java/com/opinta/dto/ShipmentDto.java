package com.opinta.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import com.opinta.entity.DeliveryType;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.opinta.constraint.RegexPattern.BARCODE_LENGTH;

@Getter
@Setter
@ToString
public class ShipmentDto {
    private UUID uuid;
    private ClientDto sender;
    private ClientDto recipient;
    private UUID shipmentGroupUuid;
    private DeliveryType deliveryType;
    @Size(min = BARCODE_LENGTH, max = BARCODE_LENGTH)
    private String barcode;
    private Float weight;
    private Float length;
    private Float width;
    private Float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    private BigDecimal postPay;
    private DiscountPerCounterpartyDto discountPerCounterparty;
    private Date lastModified;
    @Size(max = 255)
    private String description;
}
