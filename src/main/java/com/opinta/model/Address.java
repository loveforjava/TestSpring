package com.opinta.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue
    private long id;
    private String postcode;
    private String region;
    private String district;
    private String city;
    private String street;
    private String houseNumber;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
