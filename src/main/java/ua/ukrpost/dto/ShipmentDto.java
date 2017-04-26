package ua.ukrpost.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import ua.ukrpost.entity.DeliveryType;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ua.ukrpost.constraint.RegexPattern;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@ToString
public class ShipmentDto {
    private UUID uuid;
    private ClientDto sender;
    private ClientDto recipient;
    private UUID shipmentGroupUuid;
    private DeliveryType deliveryType;
    @Size(min = RegexPattern.BARCODE_LENGTH, max = RegexPattern.BARCODE_LENGTH)
    private String barcode;
    private Float weight;
    private Float length;
    private Float width;
    private Float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;
    private BigDecimal postPay;
    private DiscountPerCounterpartyDto discountPerCounterparty;
    @JsonFormat(shape = STRING, pattern = RegexPattern.DATE_TIME_PATTERN)
    private LocalDateTime lastModified;
    @Size(max = 255)
    private String description;
}
