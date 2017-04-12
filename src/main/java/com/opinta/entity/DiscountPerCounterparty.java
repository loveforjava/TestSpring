package com.opinta.entity;

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
    @Temporal(TemporalType.TIMESTAMP)
    private Date from;
    @Temporal(TemporalType.TIMESTAMP)
    private Date to;
    
    public DiscountPerCounterparty(Counterparty counterparty, Discount discount) {
        this.counterparty = counterparty;
        this.discount = discount;
    }
    
    public boolean isDiscountTimeSpanValid() {
        // is DiscountPerCounterparty time span is valid in the associated Discount time span.
        return (discount.getFrom().before(this.from) && discount.getTo().after(this.to));
    }
    
    public boolean isDiscountValidFor(Date requestDate) {
        return (requestDate.after(from) && requestDate.before(to));
    }
    
    public boolean isDiscountValidNow() {
        // new Date() means now.
        return isDiscountValidFor(new Date());
    }
}
