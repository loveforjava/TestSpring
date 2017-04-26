package ua.ukrpost.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
