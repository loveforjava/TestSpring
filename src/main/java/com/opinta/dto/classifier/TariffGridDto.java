package com.opinta.dto.classifier;

import com.opinta.constraint.EnumString;
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
    @EnumString(source = W2wVariation.class)
    private W2wVariation w2wVariation;
    private float price;
}
