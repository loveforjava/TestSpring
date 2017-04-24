package com.opinta.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.opinta.constraint.RegexPattern.BARCODE_INNER_NUMBER_LENGTH;
import static javax.persistence.EnumType.STRING;

@Entity
@Data
@NoArgsConstructor
public class BarcodeInnerNumber {
    @Id
    @GeneratedValue
    private long id;
    @Size(min = BARCODE_INNER_NUMBER_LENGTH, max = BARCODE_INNER_NUMBER_LENGTH)
    private String innerNumber;
    @Enumerated(STRING)
    private BarcodeStatus status;
    @ManyToOne
    @JoinColumn(name = "postcode_pool_uuid")
    private PostcodePool postcodePool;

    private LocalDateTime created;

    public String stringify() {
        return postcodePool.getPostcode() + innerNumber;
    }
}
