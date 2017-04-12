package com.opinta.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.cglib.core.Local;

import static java.time.LocalDateTime.now;

@Entity
@Data
@NoArgsConstructor
public class DiscountPerCounterparty {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID uuid;
    @ManyToOne
    private Discount discount;
    @OneToOne
    private Counterparty counterparty;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    
    public DiscountPerCounterparty(Counterparty counterparty, Discount discount) {
        this.counterparty = counterparty;
        this.discount = discount;
    }
    
    public boolean isDiscountTimeSpanValid() {
        // is DiscountPerCounterparty time span is valid in the associated Discount time span.
        return (discount.getFromDate().isBefore(this.fromDate) && discount.getToDate().isAfter(this.toDate));
    }
    
    public boolean isDiscountValidFor(LocalDateTime requestDate) {
        return (requestDate.isAfter(fromDate) && requestDate.isBefore(toDate));
    }
    
    public boolean isDiscountValidNow() {
        return isDiscountValidFor(now());
    }
}
