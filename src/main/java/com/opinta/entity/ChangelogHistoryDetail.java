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
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class ChangelogHistoryDetail {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(name = "changelogHistory_id", nullable = false)
    private ChangelogHistory changelogHistory;
    @Column(length = 36, nullable = false)
    private String field;
    private String oldValue;
    private String newValue;
}
