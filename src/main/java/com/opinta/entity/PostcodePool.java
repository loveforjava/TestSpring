package com.opinta.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID uuid;
    @NotNull
    @Size(min = POSTCODE_LENGTH, max = POSTCODE_LENGTH)
    private String postcode;
    private boolean closed;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    public PostcodePool(String postcode, boolean closed) {
        this.postcode = postcode;
        this.closed = closed;
    }
}
