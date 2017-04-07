package com.opinta.entity.classifier;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CityPostcode {
    @Id
    private long id;
    private String postcode;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

}