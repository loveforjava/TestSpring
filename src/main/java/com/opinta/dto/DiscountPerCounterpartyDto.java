package com.opinta.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.opinta.constraint.RegexPattern.DATE_PATTERN;

@Getter
@Setter
@ToString
public class DiscountPerCounterpartyDto {
    private UUID uuid;
    private UUID counterpartyUuid;
    private UUID discountUuid;
    @JsonFormat(shape = STRING, pattern = DATE_PATTERN)
    private LocalDate fromDate;
    @JsonFormat(shape = STRING, pattern = DATE_PATTERN)
    private LocalDate toDate;
}
