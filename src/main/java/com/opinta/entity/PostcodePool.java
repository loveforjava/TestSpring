package com.opinta.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.opinta.constraint.RegexPattern.POSTCODE_LENGTH;

/**
 * PostcodePool holds all postcodes ("00000"-"99999") and pool of the inner numbers for each postcode
 * It shouldn't have field like Client or PostOffice.
 * Client and PostOffice should have reference to it instead
 */
@Entity
@Data
@NoArgsConstructor
public class PostcodePool {
    @Id
    @GeneratedValue
    private long id;
    @NotNull
    @Size(min = POSTCODE_LENGTH, max = POSTCODE_LENGTH)
    private String postcode;
    private boolean closed;

    public PostcodePool(String postcode, boolean closed) {
        this.postcode = postcode;
        this.closed = closed;
    }
}
