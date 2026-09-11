package com.sports.user_service.dto.response;

import com.sports.user_service.entity.AccountStatus;
import com.sports.user_service.entity.PaymentStatus;
import com.sports.user_service.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private AccountStatus status;
    private PaymentStatus paymentStatus;
    private Long teamId;
    private String profilePhotoUrl;

    private Integer heightCm;
    private Integer weightKg;

    private LocalDateTime createdAt;

}
