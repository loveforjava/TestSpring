package com.opinta.entity;

import com.opinta.exception.PerformProcessFailedException;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import static java.lang.String.format;

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

    public void validate() throws PerformProcessFailedException {
        if ((discount == null) || (!discount.getFromDate().before(fromDate) && discount.getToDate().after(toDate))) {
            throw new PerformProcessFailedException(
                    format("Discount per counterparty %s is not in the range of the discount %s", this, discount));
        }
    }
}
