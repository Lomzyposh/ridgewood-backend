package com.sports.session_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tactical_training_plans",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tactical_plan_session_tactic",
                columnNames = {"coach_id", "session_id", "tactic_code"}
        ),
        indexes = {
                @Index(name = "idx_tactical_plan_coach", columnList = "coach_id"),
                @Index(name = "idx_tactical_plan_team", columnList = "team_id"),
                @Index(name = "idx_tactical_plan_session", columnList = "session_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TacticalTrainingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(nullable = false, length = 40)
    private String sport;

    @Column(name = "tactic_code", nullable = false, length = 80)
    private String tacticCode;

    @Column(name = "tactic_name", nullable = false, length = 140)
    private String tacticName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
