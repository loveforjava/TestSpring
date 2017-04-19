package com.opinta.entity;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;


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

    public Counterparty(String name, PostcodePool postcodePool) {
        this.name = name;
        this.postcodePool = postcodePool;
    }
}
