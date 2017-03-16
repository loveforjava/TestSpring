package com.opinta.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "postcode_pool_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "postcode_pool_id", nullable = false)
    private PostcodePool postcodePool;
    @Enumerated(EnumType.STRING)
    private Status status;

    public BarcodeInnerNumber(String number, PostcodePool postcodePool, Status status) {
        this.number = number;
        this.postcodePool = postcodePool;
        this.status = status;
    }
}
