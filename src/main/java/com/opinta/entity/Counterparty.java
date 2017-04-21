package com.opinta.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import static javax.persistence.TemporalType.TIMESTAMP;


/**
 * Counterparty is the group of clients with the same postcode
 */
@Entity
@Data
@NoArgsConstructor
public class Counterparty {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID uuid;
    private String name;
    @ManyToOne
    @NotNull
    private PostcodePool postcodePool;
    private String description;

    @Temporal(TIMESTAMP)
    private Date created;
    @Temporal(TIMESTAMP)
    private Date lastModified;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne
    @JoinColumn(name = "lastModifier_id")
    private User lastModifier;

    public Counterparty(String name, PostcodePool postcodePool) {
        this.name = name;
        this.postcodePool = postcodePool;
    }
}
