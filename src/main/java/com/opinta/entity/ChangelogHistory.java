package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class ChangelogHistory {
    @Id
    @GeneratedValue
    private long id;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date changeDate;
    @Column(length = 36, nullable = false)
    @Enumerated(EnumType.STRING)
    private ChangelogEntity entity;
    @Column(name = "entity_uuid", nullable = false)
    private UUID entityUuid;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private ChangelogActionType actionType;
}
