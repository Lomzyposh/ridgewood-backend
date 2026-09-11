package com.sports.session_service.security;

public record AuthenticatedUser(Long userId, String email, String role) {}
