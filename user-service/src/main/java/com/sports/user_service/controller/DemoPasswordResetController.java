package com.sports.user_service.controller;

import com.sports.user_service.dto.request.DemoFaceVerifyRequest;
import com.sports.user_service.dto.request.DemoResetIdentifyRequest;
import com.sports.user_service.dto.request.DemoResetPasswordRequest;
import com.sports.user_service.dto.response.DemoFaceVerifyResponse;
import com.sports.user_service.dto.response.DemoResetIdentifyResponse;
import com.sports.user_service.dto.response.DemoResetPasswordResponse;
import com.sports.user_service.service.DemoPasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/password-reset/demo")
@RequiredArgsConstructor
public class DemoPasswordResetController {

    private final DemoPasswordResetService service;

    @PostMapping("/identify")
    public ResponseEntity<DemoResetIdentifyResponse> identify(
            @Valid @RequestBody DemoResetIdentifyRequest request
    ) {
        return ResponseEntity.ok(service.identify(request.accountId()));
    }

    @PostMapping("/face-verify")
    public ResponseEntity<DemoFaceVerifyResponse> faceVerify(
            @Valid @RequestBody DemoFaceVerifyRequest request
    ) {
        return ResponseEntity.ok(service.verifyFace(request.resetSession()));
    }

    @PostMapping("/reset")
    public ResponseEntity<DemoResetPasswordResponse> reset(
            @Valid @RequestBody DemoResetPasswordRequest request
    ) {
        return ResponseEntity.ok(
                service.reset(
                        request.resetSession(),
                        request.newPassword(),
                        request.confirmPassword()
                )
        );
    }
}
