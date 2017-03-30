package com.opinta.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class BarcodeInnerNumber {
    @Id
    @GeneratedValue
    private long id;
    @Size(min = 8, max = 8)
    private String innerNumber;
    @Enumerated(EnumType.STRING)
    private BarcodeStatus status;
    
    public BarcodeInnerNumber(String innerNumber, BarcodeStatus status) {
        this.innerNumber = innerNumber;
        this.status = status;
    }
}
