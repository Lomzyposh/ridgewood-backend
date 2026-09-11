# session-service

Spring Boot 3.2.5 / Java 21 microservice for team training sessions and attendance.

## Database
Create PostgreSQL database:

```sql
CREATE DATABASE session_db;
```

Then set environment variables:

```bash
DB_PASSWORD=your_postgres_password
JWT_SECRET=the_same_base64_secret_used_by_team-service
```

Default server port: `8084`.

## JWT assumptions
The service expects the same Bearer JWT pattern as the other sports microservices, with:

- subject: user email
- `userId`: numeric user id
- `role`: `COACH`, `STUDENT`, or `ADMIN`

If team-service uses a different claim name or role spelling, update only the `security/` package.

## Endpoints

- `POST /api/sessions` — COACH
- `GET /api/sessions/team/{teamId}` — authenticated
- `PUT /api/sessions/{id}` — COACH
- `DELETE /api/sessions/{id}` — COACH
- `POST /api/attendance` — COACH
- `GET /api/attendance/session/{sessionId}` — COACH/ADMIN
- `GET /api/attendance/student/{studentId}` — matching STUDENT, COACH, or ADMIN
- `GET /api/attendance/team/{teamId}/rate` — COACH/ADMIN

## Example session payload

```json
{
  "teamId": 5,
  "title": "Evening Training",
  "day": "WEDNESDAY",
  "startTime": "17:00:00",
  "endTime": "19:00:00",
  "recurring": true
}
```

## Example bulk attendance payload

`date` is optional; when omitted, the server uses today's date.

```json
{
  "sessionId": 1,
  "date": "2026-09-01",
  "attendance": [
    { "studentId": 10, "status": "PRESENT" },
    { "studentId": 11, "status": "ABSENT" }
  ]
}
```

Submitting the same session/student/date again updates that attendance record rather than creating a duplicate.

## Attendance rate

`GET /api/attendance/team/{teamId}/rate` calculates:

`PRESENT attendance records / all attendance records * 100`

and rounds to two decimal places.
