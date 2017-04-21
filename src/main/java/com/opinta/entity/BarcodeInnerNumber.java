package com.opinta.entity;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.opinta.constraint.RegexPattern.BARCODE_INNER_NUMBER_LENGTH;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.TIMESTAMP;

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

    @Temporal(TIMESTAMP)
    private Date created;
    @Temporal(TIMESTAMP)
    private Date lastModified;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    public String stringify() {
        return postcodePool.getPostcode() + innerNumber;
    }
}
