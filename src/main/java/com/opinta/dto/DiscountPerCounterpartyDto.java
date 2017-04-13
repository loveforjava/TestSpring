package com.opinta.dto;

import java.util.Date;
import java.util.UUID;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DiscountPerCounterpartyDto {
    private UUID uuid;
    private UUID counterpartyUuid;
    private UUID discountUuid;
    private Date fromDate;
    private Date toDate;
}
