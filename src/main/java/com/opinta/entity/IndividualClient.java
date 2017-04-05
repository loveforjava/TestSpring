package com.opinta.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import static java.lang.String.join;

@Entity
@Table(name = "clients")
@DiscriminatorValue("INDIVIDUAL_CLIENT")
@Data
@NoArgsConstructor
@ToString(exclude = {"address", "counterparty"})
public class IndividualClient extends Client {
    private String firstName;
    private String middleName;
    private String lastName;
    
    public IndividualClient(Address address, Counterparty counterparty) {
        super(address, counterparty);
    }
    
    public IndividualClient(Address address, Phone phone, Counterparty counterparty) {
        super(address, phone, counterparty);
    }
    
    private void generateName() {
        String generatedName = join(" ",
                StringUtils.isEmpty(lastName) ? "" : lastName,
                StringUtils.isEmpty(firstName) ? "" : firstName,
                StringUtils.isEmpty(middleName) ? "" : middleName)
                // regular expression to replace possible multiple spaces with exactly one space
                .replaceAll("\\s+", " ");
        super.setName(generatedName);
    }
    
    public void setCredentials(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        generateName();
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
    
    @Override
    public void setName(String name) {
        // ignore passed name
        generateName();
    }
}
