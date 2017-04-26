package ua.ukrpost.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user_detail")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private long id;
    private String username;
    private String password;
    @ManyToOne
    @JoinColumn(name = "counterparty_uuid")
    private Counterparty counterparty;
    private UUID token;

    private LocalDateTime created;
    private LocalDateTime lastModified;

    public User(String username, Counterparty counterparty, UUID token) {
        this.username = username;
        this.counterparty = counterparty;
        this.token = token;
    }
}
