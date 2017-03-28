package com.opinta.dto;

import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CounterpartyDto {
    private long id;
    @Size(max = 255)
    private String name;
    private long postcodePoolId;
    @Size(max = 255)
    private String description;
}
