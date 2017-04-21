package com.opinta.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DiscountDto {
    private UUID uuid;
    @Size(max = 255)
    private String name;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Float value;
}
