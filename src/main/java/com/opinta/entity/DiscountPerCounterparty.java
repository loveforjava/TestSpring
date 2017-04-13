package com.opinta.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
    private Counterparty counterparty;
    @ManyToOne
    private Discount discount;
    private Date fromDate;
    private Date toDate;
    
    public DiscountPerCounterparty(Counterparty counterparty, Discount discount, Date fromDate, Date toDate) {
        this.counterparty = counterparty;
        this.discount = discount;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
    
    public boolean isDiscountPerCounterpartyValidFor(Discount discount) {
        // is DiscountPerCounterparty time span is valid in the associated Discount time span.
        return (discount.getFromDate().before(fromDate) && discount.getToDate().after(toDate));
    }
    
    public boolean isDiscountPerCounterpartyValidFor(Date requestDate) {
        return (requestDate.after(fromDate) && requestDate.before(toDate));
    }
    
    public boolean isDiscountValidNow() {
        return isDiscountPerCounterpartyValidFor(new Date());
    }
}
