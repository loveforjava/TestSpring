package com.opinta.entity;

import java.util.Date;
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
    private Date fromDate;
    private Date toDate;
    private float value;
    
    public Discount(String name, Date from, Date to, float value) {
        this.name = name;
        this.fromDate = from;
        this.toDate = to;
        this.value = value;
    }
    
    public boolean isDiscountValidFor(Date currentDate) {
        return (currentDate.after(fromDate) && currentDate.before(toDate));
    }
    
    public boolean isDiscountValidNow() {
        return isDiscountValidFor(new Date());
    }
}
