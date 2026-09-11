package com.sports.payment_service.config;

import com.sports.payment_service.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter f;

    @Bean
    CorsConfigurationSource cors() {
        var c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "x-paystack-signature"));
        var s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }

    @Bean
    SecurityFilterChain chain(HttpSecurity h) throws Exception {
        h.cors(x -> x.configurationSource(cors())).csrf(x -> x.disable())
                .sessionManagement(
                        x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll().requestMatchers(HttpMethod.POST, "/api/payments/webhook")
                        .permitAll().requestMatchers(HttpMethod.POST, "/api/payments/initialize").hasRole("COACH")
                        .requestMatchers(HttpMethod.GET, "/api/payments/me", "/api/payments/subscription/me",
                                "/api/payments/verify/**")
                        .hasRole("COACH")
                        .requestMatchers(HttpMethod.GET, "/api/payments", "/api/payments/coach/**",
                                "/api/payments/subscription/coach/**")
                        .hasRole("ADMIN").anyRequest().authenticated())
                .addFilterBefore(f, UsernamePasswordAuthenticationFilter.class);
        return h.build();
    }
}