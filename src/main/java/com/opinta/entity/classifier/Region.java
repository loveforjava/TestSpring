package com.opinta.entity.classifier;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Region {
    @Id
    @GeneratedValue
    private long id;
    private String name;
}
