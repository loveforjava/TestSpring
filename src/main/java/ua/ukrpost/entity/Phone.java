package ua.ukrpost.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.ukrpost.constraint.RegexPattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
public class Phone {
    @Id
    @GeneratedValue
    private long id;
    @Size(max = 25)
    private String phoneNumber;

    public Phone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Phone removeNonNumericalCharacters() {
        phoneNumber = phoneNumber.replaceAll(RegexPattern.DIGIT_REGEX, "");
        return this;
    }
}
