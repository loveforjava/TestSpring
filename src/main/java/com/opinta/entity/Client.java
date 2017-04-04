package com.opinta.entity;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import static java.lang.String.join;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"address", "counterparty"})
public class Client {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID uuid;
    private String name;
    private String firstName;
    private String middleName;
    private String lastName;
    private String uniqueRegistrationNumber;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "phone_id")
    private Phone phone;
    @ManyToOne
    @JoinColumn(name = "counterparty_uuid")
    private Counterparty counterparty;
    private boolean individual;
    private boolean sender;
    private float discount;

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
    
    private void generateName() {
        if (individual) {
            name = join(" ",
                    StringUtils.isEmpty(lastName) ? "" : lastName,
                    StringUtils.isEmpty(firstName) ? "" : firstName,
                    StringUtils.isEmpty(middleName) ? "" : middleName)
                    // regular expression to replace possible multiple spaces with exactly one space
                    .replaceAll("\\s+", " ");
        }
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        generateName();
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
        generateName();
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        generateName();
    }
    
    public void setName(String name) {
        this.name = name;
        generateName();
    }
    
    public void setIndividual(boolean individual) {
        this.individual = individual;
        generateName();
        if (!this.individual) {
            this.firstName = "";
            this.middleName = "";
            this.lastName = "";
        }
    }
}
