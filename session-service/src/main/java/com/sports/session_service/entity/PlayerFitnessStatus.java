package com.sports.session_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "player_fitness_status",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_fitness_team_student",
        columnNames = {"team_id", "student_id"}
    ),
    indexes = {
        @Index(name = "idx_fitness_team", columnList = "team_id"),
        @Index(name = "idx_fitness_student", columnList = "student_id"),
        @Index(name = "idx_fitness_coach", columnList = "coach_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerFitnessStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FitnessAvailability availability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FitnessWorkload workload;

    @Column(name = "condition_note", length = 500)
    private String conditionNote;

    @Column(name = "coach_note", length = 1500)
    private String coachNote;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (availability == null) availability = FitnessAvailability.FIT;
        if (workload == null) workload = FitnessWorkload.NORMAL;
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
