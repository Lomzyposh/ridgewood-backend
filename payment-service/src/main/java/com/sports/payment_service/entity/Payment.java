package com.sports.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(name = "uk_payment_reference", columnNames = "reference"), indexes = {
        @Index(name = "idx_payment_coach", columnList = "coach_id"),
        @Index(name = "idx_payment_status", columnList = "status") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "coach_id", nullable = false)
    private Long coachId;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private Long amount;
    @Column(nullable = false, length = 120)
    private String reference;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;
    @Column(length = 1000)
    private String authorizationUrl;
    private String accessCode;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
        if (status == null)
            status = PaymentStatus.PENDING;
    }
}