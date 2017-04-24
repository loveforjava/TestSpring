package com.opinta.dto;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@ToString
public class DiscountDto {
    private UUID uuid;
    @Size(max = 255)
    private String name;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDate;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDate;
    private Float value;
}
