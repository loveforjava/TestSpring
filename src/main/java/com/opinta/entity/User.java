package com.opinta.entity;

import java.util.UUID;
import javax.persistence.*;

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
    @JoinColumn(name = "counterparty_id")
    private Counterparty counterparty;
    private UUID token;
}
