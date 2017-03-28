package com.opinta.dto;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostOfficeDto {
    private long id;
    @Size(max = 255)
    private String name;
    private long addressId;
    private long postcodePoolId;
}
