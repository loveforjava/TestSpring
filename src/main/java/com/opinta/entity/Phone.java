package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import static com.opinta.constraint.RegexPattern.DIGIT_REGEX;

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
        this.phoneNumber = this.phoneNumber.replaceAll(DIGIT_REGEX, "");
        return this;
    }
}
