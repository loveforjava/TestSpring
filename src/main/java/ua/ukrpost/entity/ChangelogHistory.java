package ua.ukrpost.entity;

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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class ChangelogHistory {
    @Id
    @GeneratedValue
    private long id;
    @Column(nullable = false)
    private LocalDateTime changeDate;
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
