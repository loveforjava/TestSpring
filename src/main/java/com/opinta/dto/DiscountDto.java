package com.opinta.dto;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.opinta.constraint.RegexPattern.DATE_PATTERN;

@Getter
@Setter
@ToString
public class DiscountDto {
    private UUID uuid;
    @Size(max = 255)
    private String name;
    @JsonFormat(shape = STRING, pattern = DATE_PATTERN)
    private LocalDate fromDate;
    @JsonFormat(shape = STRING, pattern = DATE_PATTERN)
    private LocalDate toDate;
    private Float value;
}
