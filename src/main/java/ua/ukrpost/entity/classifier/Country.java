package ua.ukrpost.entity.classifier;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Country {
    @Id
    @Column(columnDefinition = "varchar(2)", unique = true)
    private String ISO3166;
    private String name;
}
