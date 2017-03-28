package com.opinta.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@NoArgsConstructor
public class Client {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String uuid;
    private String name;
    private String uniqueRegistrationNumber;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @ManyToOne
    @JoinColumn(name = "counterparty_id")
    private Counterparty counterparty;

    public Client(String name, String uniqueRegistrationNumber, Address address, Counterparty counterparty) {
        this.name = name;
        this.uniqueRegistrationNumber = uniqueRegistrationNumber;
        this.address = address;
        this.counterparty = counterparty;
    }
}
