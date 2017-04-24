package com.opinta.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@NoArgsConstructor
public class Discount {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID uuid;
    @Size(max = 255)
    private String name;
    private LocalDate fromDate;
    private LocalDate toDate;
    private float value;

    private LocalDateTime created;
    private LocalDateTime lastModified;
    
    public Discount(String name, LocalDate from, LocalDate to, float value) {
        this.name = name;
        this.fromDate = from;
        this.toDate = to;
        this.value = value;
    }
}
