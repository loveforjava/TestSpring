package ua.ukrpost.entity.classifier;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CountrysidePostcode {
    @Id
    @GeneratedValue
    private long id;
    private String postcode;
}
