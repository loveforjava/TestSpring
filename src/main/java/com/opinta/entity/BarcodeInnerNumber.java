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
    @Size(min = 7, max = 7)
    private String number;
    @Enumerated(EnumType.STRING)
    private BarcodeStatus status;
    
    public BarcodeInnerNumber(String number, BarcodeStatus status) {
        this.number = number;
        this.status = status;
    }
}
