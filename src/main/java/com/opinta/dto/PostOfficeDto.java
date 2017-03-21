package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostOfficeDto {
    private long id;
    private String name;
    private long addressId;
    private long postcodePoolId;
}
