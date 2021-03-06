package ua.ukrpost.entity;

import ua.ukrpost.exception.PerformProcessFailedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
    private LocalDate fromDate;
    private LocalDate toDate;

    private LocalDateTime created;
    private LocalDateTime lastModified;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne
    @JoinColumn(name = "lastModifier_id")
    private User lastModifier;

    public DiscountPerCounterparty(Counterparty counterparty, Discount discount, LocalDate fromDate, LocalDate toDate) {
        this.counterparty = counterparty;
        this.discount = discount;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public void validate() throws PerformProcessFailedException {
        if ((discount == null) ||
                (!discount.getFromDate().isBefore(fromDate) && discount.getToDate().isAfter(toDate))) {
            throw new PerformProcessFailedException(
                    format("Discount per counterparty %s is not in the range of the discount %s", this, discount));
        }
    }
}
