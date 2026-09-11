# facility-service

Spring Boot 3.2.5 / Java 21 microservice for shared sports facilities and booking slots.

## Database
Create PostgreSQL database:

```sql
CREATE DATABASE facility_db;
```

Environment variables:
- `DB_URL` (default `jdbc:postgresql://localhost:5432/facility_db`)
- `DB_USERNAME` (default `postgres`)
- `DB_PASSWORD` (default `postgres`)
- `JWT_SECRET` — must match the service that issues JWTs

## JWT
Expected Bearer JWT claim:

```json
{
  "sub": "user@email.com",
  "role": "COACH"
}
```

Roles supported by authorization rules: `COACH`, `ADMIN`.

## Endpoints
- `GET /api/facilities` — authenticated users
- `POST /api/facilities` — ADMIN only
- `GET /api/facilities/{id}/bookings` — authenticated users
- `POST /api/bookings` — COACH or ADMIN
- `DELETE /api/bookings/{id}` — COACH or ADMIN

## Booking example

```json
{
  "facilityId": 1,
  "teamId": 15,
  "date": "2026-09-01",
  "startTime": "14:00:00",
  "endTime": "16:00:00"
}
```

`day` is derived from `date` on the server to avoid inconsistent values.

## Conflict rule
A requested interval `[start, end)` conflicts with an existing booking when:

```text
existing.startTime < requested.endTime
AND
existing.endTime > requested.startTime
```

Examples:
- Existing 10:00–12:00, requested 11:00–13:00 => conflict (409)
- Existing 10:00–12:00, requested 09:00–10:30 => conflict (409)
- Existing 10:00–12:00, requested 10:30–11:30 => conflict (409)
- Existing 10:00–12:00, requested 12:00–13:00 => allowed

This prevents partial/contained overlaps while allowing adjacent bookings.
