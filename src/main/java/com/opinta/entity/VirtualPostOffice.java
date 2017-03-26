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
 * VirtualPostOffice is the group of clients with the same postcode
 */
@Entity
@Data
@NoArgsConstructor
public class VirtualPostOffice {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    @OneToOne(cascade = CascadeType.REMOVE)
    @NotNull
    private PostcodePool activePostcodePool;
    // TODO add field private List<PostcodePool> closedPostcodePools (unidirectional)
    private String description;

    public VirtualPostOffice(String name, PostcodePool activePostcodePool) {
        this.name = name;
        this.activePostcodePool = activePostcodePool;
    }
}
