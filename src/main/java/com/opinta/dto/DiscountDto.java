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
public class DiscountDto {
    private UUID uuid;
    @Size(max = 255)
    private String name;
    private Date fromDate;
    private Date toDate;
    private Float value;
}
