package com.opinta.dto.classifier;

import com.opinta.entity.W2wVariation;
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
public class TariffGridDto {
    private long id;
    private float weight;
    private float length;
    private W2wVariation w2wVariation;
    private float price;
}
