package ua.ukrpost.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import ua.ukrpost.constraint.RegexPattern;

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
    @Size(max = RegexPattern.CLIENT_NAME_LENGTH)
    private String name;
    @Size(max = RegexPattern.CLIENT_NAME_LENGTH)
    private String firstName;
    @Size(max = RegexPattern.CLIENT_NAME_LENGTH)
    private String middleName;
    @Size(max = RegexPattern.CLIENT_NAME_LENGTH)
    private String lastName;
    @Size(min = RegexPattern.POST_ID_LENGTH, max = RegexPattern.POST_ID_LENGTH)
    private String postId;
    @Size(max = RegexPattern.EXTERNAL_ID_LENGTH)
    private String externalId;
    @Size(max = RegexPattern.CLIENT_UNIQUE_REGISTRATION_NUMBER_LENGTH)
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
    @Size(min = RegexPattern.BANK_CODE_LENGTH, max = RegexPattern.BANK_CODE_LENGTH)
    private String bankCode;
    @Size(max = RegexPattern.BANK_ACCOUNT_LENGTH)
    private String bankAccount;

    private LocalDateTime created;
    private LocalDateTime lastModified;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne
    @JoinColumn(name = "lastModifier_id")
    private User lastModifier;

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
