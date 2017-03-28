package com.opinta.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"address", "counterparty"})
public class Client {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String uniqueRegistrationNumber;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "phone_id")
    private Phone phone;
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private Counterparty counterparty;

    public Client(String name, String uniqueRegistrationNumber, Address address,
                  Counterparty counterparty) {
        this.name = name;
        this.uniqueRegistrationNumber = uniqueRegistrationNumber;
        this.address = address;
        this.counterparty = counterparty;
    }

    public Client(String name, String uniqueRegistrationNumber, Address address, Phone phone,
                  Counterparty counterparty) {
        this.name = name;
        this.uniqueRegistrationNumber = uniqueRegistrationNumber;
        this.address = address;
        this.phone = phone;
        this.counterparty = counterparty;
    }
}
