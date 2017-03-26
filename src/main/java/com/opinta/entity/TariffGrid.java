package com.opinta.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TariffGrid {
    @Id
    @GeneratedValue
    private long id;
    private float weight;
    private float length;
    @Enumerated(EnumType.STRING)
    private W2wVariation w2wVariation;
    private float price;

    public TariffGrid(float weight, float length, W2wVariation w2wVariation, float price) {
        this.weight = weight;
        this.length = length;
        this.w2wVariation = w2wVariation;
        this.price = price;
    }
}
