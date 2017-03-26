package com.opinta.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PostOffice {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @OneToOne(cascade = CascadeType.REMOVE)
    @NotNull
    private PostcodePool postcodePool;

    public PostOffice(String name, Address address, PostcodePool postcodePool) {
        this.name = name;
        this.address = address;
        this.postcodePool = postcodePool;
    }
}
