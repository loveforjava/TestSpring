package com.opinta.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Client {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String uniqueRegistrationNumber;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @ManyToOne
    @JoinColumn(name = "virtual_post_office_id")
    private VirtualPostOffice virtualPostOffice;

    public Client(String name, String uniqueRegistrationNumber, Address address, VirtualPostOffice virtualPostOffice) {
        this.name = name;
        this.uniqueRegistrationNumber = uniqueRegistrationNumber;
        this.address = address;
        this.virtualPostOffice = virtualPostOffice;
    }
}
