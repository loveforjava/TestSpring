package com.opinta.entity;

import java.util.UUID;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.opinta.exception.ClientConversionException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.DiscriminatorType.STRING;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Entity
@Table(name = "clients")
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(
        name = "discriminator",
        discriminatorType = STRING)
@DiscriminatorValue(value="CLIENT")
@Data
@NoArgsConstructor
@ToString(exclude = {"address", "counterparty"})
public class Client {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID uuid;
    private String name;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "phone_id")
    private Phone phone;
    @ManyToOne
    @JoinColumn(name = "counterparty_uuid")
    private Counterparty counterparty;
    private boolean sender;
    private boolean individual;
    private float discount;

    Client(Address address, Counterparty counterparty) {
        this.address = address;
        this.counterparty = counterparty;
    }

    Client(Address address, Phone phone, Counterparty counterparty) {
        this.address = address;
        this.phone = phone;
        this.counterparty = counterparty;
    }
    
    public JuridicalClient toJuridical() throws ClientConversionException {
        if (individual) {
            throw new ClientConversionException("Individual client can't be used as juridical one.");
        }
        return (JuridicalClient) this;
    }
    
    public IndividualClient toIndividual() throws ClientConversionException {
        if (!individual) {
            throw new ClientConversionException("Juridical client can't be used as individual one.");
        }
        return (IndividualClient) this;
    }
}
