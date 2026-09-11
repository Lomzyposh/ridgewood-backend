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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository repo;
    private final PaystackClient client;
    private final ObjectMapper om;
    private final WebhookSignatureService sig;

    @Value("${ridgewood.subscription.amount-kobo:500000}")
    Long amount;

    @Value("${ridgewood.frontend.payment-callback-url:http://localhost:5173/coach/dashboard?payment=callback}")
    String defaultCb;

    @Transactional
    public InitializePaymentResponse initialize(Long coachId, String email, InitializePaymentRequest req) {
        if (coachId == null) throw new BadRequestException("Authenticated coach id is missing from token");
        if (email == null || email.isBlank()) throw new BadRequestException("Authenticated coach email is missing from token");

        String ref = "RWD-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String cb = req != null && req.callbackUrl() != null && !req.callbackUrl().isBlank()
                ? req.callbackUrl()
                : defaultCb;

        Payment p = repo.save(Payment.builder()
                .coachId(coachId)
                .email(email)
                .amount(amount)
                .reference(ref)
                .status(PaymentStatus.PENDING)
                .build());

        JsonNode r = client.initialize(email, amount, ref, cb);
        if (!r.path("status").asBoolean(false)) {
            p.setStatus(PaymentStatus.FAILED);
            repo.save(p);
            throw new BadRequestException("Paystack could not initialize transaction");
        }

        JsonNode d = r.path("data");
        p.setAuthorizationUrl(d.path("authorization_url").asText(null));
        p.setAccessCode(d.path("access_code").asText(null));
        repo.save(p);

        return new InitializePaymentResponse(
                p.getId(),
                ref,
                amount,
                p.getStatus(),
                p.getAuthorizationUrl(),
                p.getAccessCode()
        );
    }

    @Transactional
    public PaymentResponse verify(Long coachId, String ref) {
        Payment p = find(ref);
        if (!p.getCoachId().equals(coachId)) throw new ResourceNotFoundException("Payment not found");
        if (p.getStatus() == PaymentStatus.SUCCESS) return dto(p);

        JsonNode response = client.verify(ref);
        if (!response.path("status").asBoolean(false)) {
            throw new BadRequestException("Paystack verification was unsuccessful");
        }

        JsonNode d = response.path("data");
        if (!ref.equals(d.path("reference").asText())) throw new BadRequestException("Paystack reference mismatch");
        if (!p.getAmount().equals(d.path("amount").asLong(-1))) throw new BadRequestException("Paystack amount mismatch");

        String st = d.path("status").asText();
        if ("success".equalsIgnoreCase(st)) {
            markSuccess(p);
        } else if (List.of("failed", "abandoned", "reversed").contains(st.toLowerCase())) {
            p.setStatus(PaymentStatus.FAILED);
            repo.save(p);
        } else {
            throw new BadRequestException("Payment has not been completed yet");
        }

        return dto(p);
    }

    public List<PaymentResponse> mine(Long id) {
        return repo.findByCoachIdOrderByCreatedAtDesc(id).stream().map(this::dto).toList();
    }

    public List<PaymentResponse> all(PaymentStatus s, Long c) {
        List<Payment> x = c != null && s != null
                ? repo.findByCoachIdAndStatusOrderByCreatedAtDesc(c, s)
                : c != null
                ? repo.findByCoachIdOrderByCreatedAtDesc(c)
                : s != null
                ? repo.findByStatusOrderByCreatedAtDesc(s)
                : repo.findAllByOrderByCreatedAtDesc();

        return x.stream().map(this::dto).toList();
    }

    public SubscriptionStatusResponse sub(Long id) {
        return repo.findFirstByCoachIdAndStatusOrderByPaidAtDesc(id, PaymentStatus.SUCCESS)
                .map(p -> new SubscriptionStatusResponse(id, "ACTIVE", p.getReference(), p.getAmount(), p.getPaidAt(), true))
                .orElse(new SubscriptionStatusResponse(id, "INACTIVE", null, null, null, false));
    }

    @Transactional
    public void webhook(String body, String signature) {
        if (!sig.isValid(body, signature)) throw new BadRequestException("Invalid Paystack webhook signature");

        try {
            JsonNode r = om.readTree(body);
            if (!"charge.success".equals(r.path("event").asText())) return;

            JsonNode d = r.path("data");
            String ref = d.path("reference").asText();
            Payment p = repo.findByReference(ref).orElse(null);
            if (p == null || p.getStatus() == PaymentStatus.SUCCESS) return;
            if (!p.getAmount().equals(d.path("amount").asLong(-1))) throw new BadRequestException("Webhook amount mismatch");
            if (!ref.equals(d.path("reference").asText())) throw new BadRequestException("Webhook reference mismatch");

            markSuccess(p);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Invalid webhook payload");
        }
    }

    private Payment find(String r) {
        return repo.findByReference(r).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    private void markSuccess(Payment p) {
        if (p.getStatus() == PaymentStatus.SUCCESS) return;
        p.setStatus(PaymentStatus.SUCCESS);
        p.setPaidAt(LocalDateTime.now());
        repo.save(p);
    }

    private PaymentResponse dto(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getCoachId(),
                p.getEmail(),
                p.getAmount(),
                p.getReference(),
                p.getStatus(),
                p.getAuthorizationUrl(),
                p.getCreatedAt(),
                p.getPaidAt()
        );
    }
}
