# Ridgewood payment-service

Complete Spring Boot 3.2.5 / Java 21 Paystack Test Mode service.

Default port: 8087

Create DB: `CREATE DATABASE payment_db;`

Set these PowerShell environment variables before running:

```powershell
$env:DB_PASSWORD="YOUR_POSTGRES_PASSWORD"
$env:JWT_SECRET="THE_SAME_SECRET_USED_BY_USER_SERVICE"
$env:PAYSTACK_SECRET_KEY="sk_test_..."
```

Run with `mvn spring-boot:run`.

Coach endpoints: POST `/api/payments/initialize`, GET `/api/payments/verify/{reference}`, GET `/api/payments/me`, GET `/api/payments/subscription/me`.

Admin endpoints: GET `/api/payments`, GET `/api/payments/coach/{coachId}`, GET `/api/payments/subscription/coach/{coachId}`.

Webhook: POST `/api/payments/webhook` (public, but HMAC SHA512 signature checked).

Assumption: JWT has `userId`, `role`, and subject=email and uses the same JWT secret as user-service.
