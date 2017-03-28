package com.opinta.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
    private String uuid;
    private String name;
    @OneToOne
    @NotNull
    private PostcodePool postcodePool;
    private String description;

    public Counterparty(String name, PostcodePool postcodePool) {
        this.name = name;
        this.postcodePool = postcodePool;
    }
}
