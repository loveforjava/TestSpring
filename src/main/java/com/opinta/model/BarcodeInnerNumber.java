package com.opinta.model;

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
    public static enum Status {
        RESERVED, USED
    }

    @Id
    @GeneratedValue
    private long id;
    @Size(min = 7, max = 7)
    private String number;
    @Enumerated(EnumType.STRING)
    private Status status;

    public BarcodeInnerNumber(String number, Status status) {
        this.number = number;
        this.status = status;
    }
}
