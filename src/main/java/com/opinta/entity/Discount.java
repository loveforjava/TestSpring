package com.opinta.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date from;
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;
    private float value;
    
    public Discount(String name, Date from, Date to, float value) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.value = value;
    }
    
    public boolean isDiscountValidFor(Date currentDate) {
        return (currentDate.after(from) && currentDate.before(to));
    }
    
    public boolean isDiscountValidNow() {
        // new Date() means now.
        return isDiscountValidFor(new Date());
    }
}
