package com.sports.payment_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sports.payment_service.client.PaystackClient;
import com.sports.payment_service.dto.request.InitializePaymentRequest;
import com.sports.payment_service.dto.response.InitializePaymentResponse;
import com.sports.payment_service.dto.response.PaymentResponse;
import com.sports.payment_service.dto.response.SubscriptionStatusResponse;
import com.sports.payment_service.entity.Payment;
import com.sports.payment_service.entity.PaymentStatus;
import com.sports.payment_service.exception.BadRequestException;
import com.sports.payment_service.exception.ResourceNotFoundException;
import com.sports.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

        @Mock
        private PaymentRepository repo;
        @Mock
        private PaystackClient client;
        @Mock
        private WebhookSignatureService signatureService;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private PaymentService paymentService;

        @BeforeEach
        void setUp() {
                paymentService = new PaymentService(repo, client, objectMapper, signatureService);
                ReflectionTestUtils.setField(paymentService, "amount", 500000L);
                ReflectionTestUtils.setField(
                                paymentService, "defaultCb",
                                "http://localhost:5173/coach/dashboard?payment=callback");
        }

        @Test
        void initializeCreatesPendingPaymentAndReturnsPaystackUrl() throws Exception {
                when(repo.save(any(Payment.class))).thenAnswer(invocation -> {
                        Payment p = invocation.getArgument(0);
                        if (p.getId() == null)
                                p.setId(1L);
                        return p;
                });

                JsonNode paystackResponse = objectMapper.readTree("""
                                {
                                  "status": true,
                                  "data": {
                                    "authorization_url": "https://checkout.paystack.com/test",
                                    "access_code": "ACCESS_123"
                                  }
                                }
                                """);
                when(client.initialize(eq("coach@example.com"), eq(500000L), anyString(), anyString()))
                                .thenReturn(paystackResponse);

                InitializePaymentResponse response = paymentService.initialize(
                                3L, "coach@example.com", new InitializePaymentRequest(null));

                assertEquals(500000L, response.amount());
                assertEquals(PaymentStatus.PENDING, response.status());
                assertEquals("https://checkout.paystack.com/test", response.authorizationUrl());
                verify(client).initialize(eq("coach@example.com"), eq(500000L), anyString(), anyString());
        }

        @Test
        void initializeRejectsMissingCoachId() {
                assertThrows(BadRequestException.class,
                                () -> paymentService.initialize(null, "coach@example.com",
                                                new InitializePaymentRequest(null)));

                verifyNoInteractions(client);
        }

        @Test
        void verifyMarksSuccessfulPaymentAsSuccess() throws Exception {
                Payment payment = Payment.builder()
                                .id(1L).coachId(3L).email("coach@example.com")
                                .amount(500000L).reference("RWD-TEST")
                                .status(PaymentStatus.PENDING).build();

                when(repo.findByReference("RWD-TEST")).thenReturn(Optional.of(payment));
                when(repo.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

                JsonNode verification = objectMapper.readTree("""
                                {
                                  "status": true,
                                  "data": {
                                    "reference": "RWD-TEST",
                                    "amount": 500000,
                                    "status": "success"
                                  }
                                }
                                """);
                when(client.verify("RWD-TEST")).thenReturn(verification);

                PaymentResponse response = paymentService.verify(3L, "RWD-TEST");

                assertEquals(PaymentStatus.SUCCESS, response.status());
                assertNotNull(payment.getPaidAt());
                verify(repo).save(payment);
        }

        @Test
        void verifyRejectsPaymentBelongingToAnotherCoach() {
                Payment payment = Payment.builder()
                                .id(1L).coachId(3L).email("coach@example.com")
                                .amount(500000L).reference("RWD-TEST")
                                .status(PaymentStatus.PENDING).build();

                when(repo.findByReference("RWD-TEST")).thenReturn(Optional.of(payment));

                assertThrows(ResourceNotFoundException.class,
                                () -> paymentService.verify(99L, "RWD-TEST"));

                verifyNoInteractions(client);
        }

        @Test
        void subscriptionIsActiveAfterSuccessfulPayment() {
                Payment payment = Payment.builder()
                                .id(1L)
                                .coachId(3L)
                                .email("coach@example.com")
                                .amount(500000L)
                                .reference("RWD-PAID")
                                .status(PaymentStatus.SUCCESS)
                                .paidAt(LocalDateTime.now())
                                .build();

                when(repo.findFirstByCoachIdAndStatusOrderByPaidAtDesc(
                                3L,
                                PaymentStatus.SUCCESS)).thenReturn(Optional.of(payment));

                SubscriptionStatusResponse response = paymentService.sub(3L);

                assertTrue(response.active());
                assertEquals("ACTIVE", response.status());
                assertEquals("RWD-PAID", response.latestPaymentReference());
        }
}
