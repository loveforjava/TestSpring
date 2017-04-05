package com.opinta.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "clients")
@DiscriminatorValue("JURIDICAL_CLIENT")
@Data
@NoArgsConstructor
@ToString(exclude = {"address", "counterparty"})
public class JuridicalClient extends Client {
    private String uniqueRegistrationNumber;
    
    public JuridicalClient(String name, String uniqueRegistrationNumber, Address address, Counterparty counterparty) {
        super(address, counterparty);
        super.setName(name);
        this.uniqueRegistrationNumber = uniqueRegistrationNumber;
    }
    
    public JuridicalClient(String name, String uniqueRegistrationNumber, Address address, Phone phone, Counterparty counterparty) {
        super(address, phone, counterparty);
        super.setName(name);
        this.uniqueRegistrationNumber = uniqueRegistrationNumber;
    }
}
