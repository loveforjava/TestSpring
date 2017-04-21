package com.opinta.dto;

import java.time.LocalDateTime;
import java.util.UUID;
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
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
