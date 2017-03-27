package com.opinta.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Counterparty is the group of clients with the same postcode
 */
@Entity
@Data
@NoArgsConstructor
public class Counterparty {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    @OneToOne
    @NotNull
    private PostcodePool activePostcodePool;
    // TODO add field private List<PostcodePool> closedPostcodePools (unidirectional)
    private String description;

    public Counterparty(String name, PostcodePool activePostcodePool) {
        this.name = name;
        this.activePostcodePool = activePostcodePool;
    }
}
