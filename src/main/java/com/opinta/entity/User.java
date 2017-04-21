package com.opinta.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

import lombok.Data;
import lombok.NoArgsConstructor;

import static javax.persistence.TemporalType.TIMESTAMP;

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

    @Temporal(TIMESTAMP)
    private Date created;
    @Temporal(TIMESTAMP)
    private Date lastModified;

    public User(String username, Counterparty counterparty, UUID token) {
        this.username = username;
        this.counterparty = counterparty;
        this.token = token;
    }
}
