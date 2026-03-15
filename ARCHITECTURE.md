# 🏥 OPD Hospital System — Architecture & Developer Guide

> **Scope**: This document is generated directly from the source code and reflects the
> system **as actually implemented**.  Where the original `README.md` describes a
> feature or endpoint that does **not yet exist in code**, it is explicitly marked
> **[PLANNED / NOT YET IMPLEMENTED]**.

---

## Table of Contents

1. [Repository Structure](#1-repository-structure)
2. [Architecture Overview](#2-architecture-overview)
3. [Service Catalogue](#3-service-catalogue)
   - 3.1 [Eureka Server](#31-eureka-server-opd-eureka-server)
   - 3.2 [API Gateway](#32-api-gateway-opd-api-gateway)
   - 3.3 [Auth Service](#33-auth-service-opd-auth-service)
   - 3.4 [Doctor Service](#34-doctor-service-opd-doctor-service)
   - 3.5 [Appointment Service](#35-appointment-service-opd-appointment-service)
   - 3.6 [Billing Service](#36-billing-service-opd-billing-service)
   - 3.7 [Notification Service](#37-notification-service-opd-notification-service)
4. [Inter-Service Communication](#4-inter-service-communication)
5. [Kafka Event Bus](#5-kafka-event-bus)
6. [Redis Slot Locking](#6-redis-slot-locking)
7. [Database Schemas](#7-database-schemas)
8. [End-to-End Workflows](#8-end-to-end-workflows)
9. [Running Locally](#9-running-locally)
10. [API Reference & curl Examples](#10-api-reference--curl-examples)
11. [Ports & Startup Order](#11-ports--startup-order)
12. [Known Issues & Planned Features](#12-known-issues--planned-features)

---

## 1. Repository Structure

```
opd-hospital-system-microservice/
│
├── docker-compose.yml          ← PRIMARY compose file (use this one locally)
├── init.sql                    ← PostgreSQL DB/user bootstrap script
├── infra/
│   ├── docker-compose.yml      ← Alternative compose; mirrors root compose
│   ├── .env                    ← Environment overrides for infra compose
│   └── README.md               ← Infra-specific quick-start
│
├── opd-eureka-server/          ← Service registry (port 8761)
├── opd-api-gateway/            ← Edge router + JWT validation (port 8080)
├── opd-auth-service/           ← Patient/user auth + profiles (port 8081)
├── opd-doctor-service/         ← Doctor profiles + schedules (port 8082)
├── opd-appointment-service/    ← Appointment booking (port 8083)
├── opd-billing-service/        ← Invoicing + payment (port 8084)
└── opd-notification-service/   ← Email/SMS notifications (port 8085)
```

Each service is an independent **Gradle** project using **Java 21 + Spring Boot 4**.

---

## 2. Architecture Overview

```
                          ┌───────────────────────────────────────────────┐
                          │              CLIENT (Browser / Postman)        │
                          └──────────────────────┬────────────────────────┘
                                                 │  HTTP
                                                 ▼
                          ┌───────────────────────────────────────────────┐
                          │         API GATEWAY  :8080                    │
                          │  • JWT validation (HMAC-SHA256)               │
                          │  • Injects X-User-ID, X-User-Email,           │
                          │    X-User-Role headers for downstream         │
                          │  • Routes:                                    │
                          │    /auth/**         → Auth Service            │
                          │    /doctors/**      → Doctor Service          │
                          │    /appointments/** → Appointment Service     │
                          │    /billing/**      → Billing Service         │
                          └──────────┬────────────────────────────────────┘
                                     │  lb:// (Eureka load-balanced)
          ┌──────────────────────────┼────────────────────────────────┐
          ▼                          ▼                                 ▼
 ┌─────────────────┐    ┌──────────────────────┐     ┌─────────────────────┐
 │  Auth Service   │    │   Doctor Service     │     │ Appointment Service │
 │     :8081       │    │      :8082           │     │      :8083          │
 │  PostgreSQL     │    │  PostgreSQL          │     │  PostgreSQL         │
 │  (auth_db)      │    │  (doctor_db)         │     │  (appointment_db)   │
 └─────────────────┘    └──────────────────────┘     │  Redis (slot lock)  │
          ▲                        ▲                  └──────┬──────────────┘
          │ Feign                  │ Feign                   │ Feign
          │                        └────────────────────────►│
          └────────────────────────────────────────────────► │
                                                             │
                                               Kafka: APPOINTMENT_CREATED
                                                             │
                                                             ▼
                                              ┌─────────────────────────┐
                                              │   Billing Service :8084 │
                                              │   MongoDB (billing_db)  │
                                              └──────────────┬──────────┘
                                                             │
                                               Kafka: PAYMENT_SUCCESS
                                                             │
                              ┌──────────────────────────────┤
                              │                              │
                              ▼                              ▼
              ┌───────────────────────┐     ┌───────────────────────────┐
              │  Appointment Service  │     │  Notification Service     │
              │  (confirms appt.)     │     │       :8085               │
              └───────────────────────┘     │  Kafka: APPOINTMENT_      │
                                            │  CREATED + CONFIRMED      │
                                            └───────────────────────────┘

 ┌────────────────────────────────┐
 │  Eureka Server  :8761          │   ← all services register here
 └────────────────────────────────┘
```

### Communication patterns

| Pattern | Used for |
|---------|----------|
| **Synchronous REST (Feign)** | Appointment→Auth, Appointment→Doctor, Appointment→Billing; Billing→Auth, Billing→Doctor, Billing→Appointment |
| **Async Kafka events** | `APPOINTMENT_CREATED`, `APPOINTMENT_CONFIRMED`, `PAYMENT_SUCCESS` |

---

## 3. Service Catalogue

### 3.1 Eureka Server (`opd-eureka-server`)

| Item | Value |
|------|-------|
| Port | **8761** |
| Dashboard | `http://localhost:8761` |
| Self-registration | Disabled (standalone mode) |

All other services register with Eureka at startup.  The API Gateway uses Eureka
for load-balanced routing (`lb://service-name`).

---

### 3.2 API Gateway (`opd-api-gateway`)

| Item | Value |
|------|-------|
| Port | **8080** |
| Framework | Spring Cloud Gateway (WebFlux reactive) |
| Auth | Spring Security OAuth2 Resource Server – HMAC-SHA256 JWT |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |

#### Route table

| Gateway prefix | Target service | Strip prefix | Example |
|---------------|----------------|--------------|---------|
| `/auth/**` | `opd-auth-service` | 1 | `/auth/login` → `/login` |
| `/doctors/**` | `opd-doctor-service` | 1 | `/doctors/api/doctors` → `/api/doctors` |
| `/appointments/**` | `opd-appointment-service` | 1 | `/appointments/appointments/create` → `/appointments/create` |
| `/billing/**` | `opd-billing-service` | 1 | `/billing/api/billing/invoice` → `/api/billing/invoice` |

#### Public (no JWT required) paths

```
/auth/**
/v3/api-docs/**
/swagger-ui/**
/actuator/**
```

#### JWT enrichment (`JwtHeaderEnrichmentFilter`)

After JWT validation the gateway **mutates** every downstream request to include:

| Header | JWT claim |
|--------|-----------|
| `X-User-ID` | `sub` (UUID of the authenticated user) |
| `X-User-Email` | `email` |
| `X-User-Role` | `role` |

These headers are trusted by downstream services; they **must not** be forwarded
directly by clients.

---

### 3.3 Auth Service (`opd-auth-service`)

| Item | Value |
|------|-------|
| Port | **8081** |
| Database | PostgreSQL `auth_db` |
| Security | Spring Security + HMAC-SHA256 JWT (`security.jwt.secret`, default expiry 900 s) |
| OAuth2 | Google OAuth2 (requires `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` env vars) |

#### REST Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/register/patient` | Public | Register new patient account |
| POST | `/register/users` | Public | Create user with explicit role (ADMIN/DOCTOR/PATIENT) |
| GET | `/admin/exists` | Public | Returns `true` if an ADMIN user exists |
| POST | `/login` | Public | Authenticate and receive JWT |
| GET | `/profile/` | JWT | Get own patient profile |
| PUT | `/profile/` | JWT | Update own patient profile |
| GET | `/internal/profile/{patientId}` | Internal | Fetch profile by ID (used by Appointment/Billing via Feign) |

Via API Gateway, prefix every path with `/auth`:
```
POST http://localhost:8080/auth/register/patient
POST http://localhost:8080/auth/login
GET  http://localhost:8080/auth/profile/
```

#### Entities

**`users` table**

| Column | Type | Notes |
|--------|------|-------|
| `id` | UUID (PK) | Auto-generated |
| `full_name` | VARCHAR | |
| `email` | VARCHAR | Unique |
| `password_hash` | VARCHAR | BCrypt |
| `phone` | VARCHAR | |
| `role` | ENUM | `ADMIN`, `PATIENT`, `DOCTOR` |
| `status` | ENUM | `ACTIVE`, `INACTIVE` |
| `created_at` | TIMESTAMP | |
| `updated_at` | TIMESTAMP | |

**`patient_profiles` table**

| Column | Type | Notes |
|--------|------|-------|
| `id` | UUID (PK) | |
| `user_id` | UUID (FK → users) | OneToOne |
| `date_of_birth` | DATE | |
| `gender` | ENUM | `MALE`, `FEMALE`, `OTHER` |
| `blood_group` | ENUM | `A_POSITIVE`, `B_NEGATIVE`, … |
| `address` | TEXT | |
| `created_at` | TIMESTAMP | |
| `updated_at` | TIMESTAMP | |

#### JWT payload claims

```json
{
  "sub":   "<user-uuid>",
  "email": "patient@example.com",
  "role":  "PATIENT",
  "exp":   <unix-epoch>,
  "iat":   <unix-epoch>
}
```

---

### 3.4 Doctor Service (`opd-doctor-service`)

| Item | Value |
|------|-------|
| Port | **8082** |
| Database | PostgreSQL `doctor_db` |
| Feign | Calls **Auth Service** to create credentials when registering a doctor |

#### REST Endpoints

**DoctorController** (`/api/doctors`)

| Method | Path | Headers required | Description |
|--------|------|------------------|-------------|
| POST | `/api/doctors/` | `X-User-Id`, `X-User-Role` | Create doctor profile |
| GET | `/api/doctors/` | — | List all doctors |
| GET | `/api/doctors/available` | — | Available doctors (`?date=dd/MM/yyyy&specialization=CARDIOLOGY`) |
| GET | `/api/doctors/{id}` | — | Get doctor by ID |
| PUT | `/api/doctors/{id}` | — | Update doctor |
| DELETE | `/api/doctors/{id}` | — | Delete doctor |

**ScheduleController** (`/api/doctors`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/doctors/{doctorId}/schedules` | Create schedule slot |
| GET | `/api/doctors/{doctorId}/schedules` | List doctor's schedules |
| GET | `/api/doctors/schedules/{scheduleId}` | Get schedule by ID |
| PUT | `/api/doctors/schedules/{scheduleId}` | Update schedule |
| DELETE | `/api/doctors/schedules/{scheduleId}` | Delete schedule |

**DoctorInternalController** (`/internal/doctors`)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/internal/doctors/{doctorId}/schedules/{scheduleId}/{serialNo}/availability?appointmentDate=` | Check if a slot has remaining capacity (returns `Boolean`) |

Via API Gateway prefix all public paths with `/doctors`:
```
GET  http://localhost:8080/doctors/api/doctors
POST http://localhost:8080/doctors/api/doctors/{id}/schedules
```

#### Entities

**`doctors` table**

| Column | Type | Notes |
|--------|------|-------|
| `id` | UUID (PK) | |
| `user_id` | UUID | Unique; links to Auth Service user |
| `doctor_name` | VARCHAR | |
| `degree` | VARCHAR | |
| `specialization` | ENUM | 94 values (CARDIOLOGY, NEUROLOGY, …) |
| `experience_years` | INT | |
| `license_number` | VARCHAR | Unique |
| `consultation_fee` | DECIMAL | |
| `status` | ENUM | `ACTIVE`, `INACTIVE`, `ON_LEAVE` |
| `bio` | VARCHAR(1000) | |
| `created_by` | UUID | |
| `created_at` | TIMESTAMP | |
| `updated_at` | TIMESTAMP | |

**`doctor_schedules` table**

| Column | Type | Notes |
|--------|------|-------|
| `id` | UUID (PK) | |
| `doctor_id` | UUID (FK) | ManyToOne |
| `day_of_week` | ENUM | `MONDAY`…`SUNDAY` |
| `start_time` | TIME | |
| `end_time` | TIME | |
| `max_patients` | INT | Capacity per slot |
| `appointed_patients` | INT | Running count |
| `created_at` | TIMESTAMP | |
| `updated_at` | TIMESTAMP | |
| Unique constraint | — | `(doctor_id, day_of_week, start_time, end_time)` |

#### Feign client (outbound)

Calls **Auth Service** during doctor creation:
- `POST /register/users` – creates a login account for the new doctor
- `GET /admin/exists` – used by `DataInitializer` to seed the first admin

---

### 3.5 Appointment Service (`opd-appointment-service`)

| Item | Value |
|------|-------|
| Port | **8083** |
| Database | PostgreSQL `appointment_db` |
| Cache | Redis `:6379` (slot-locking infrastructure ready; see §6) |
| Kafka producer | Topics: `APPOINTMENT_CREATED`, `APPOINTMENT_CONFIRMED` |
| Kafka consumer | Topic: `PAYMENT_SUCCESS` |
| Feign | Calls Auth, Doctor, Billing services |

#### REST Endpoints

| Method | Path | Headers | Description |
|--------|------|---------|-------------|
| POST | `/appointments/create` | `X-User-Id` (UUID), `X-User-Role` | Book an appointment |
| GET | `/appointments/countAppointments` | — | Count confirmed appointments (`?doctorId=&scheduleId=&date=&status=`) |

Via Gateway:
```
POST http://localhost:8080/appointments/appointments/create
GET  http://localhost:8080/appointments/appointments/countAppointments
```

#### Entity

**`appointments` table**

| Column | Type | Notes |
|--------|------|-------|
| `id` | UUID (PK) | Auto-generated |
| `patient_id` | UUID | From `X-User-Id` header |
| `doctor_id` | UUID | |
| `schedule_id` | UUID | |
| `appointment_date` | DATE | |
| `status` | ENUM | `PENDING_PAYMENT`, `CONFIRMED`, `CANCELLED`, `EXPIRED` |
| `serial_no` | INT | Assigned after payment confirmation |
| `created_at` | TIMESTAMP | |
| `updated_at` | TIMESTAMP | |
| Unique constraint | — | `(patient_id, doctor_id, schedule_id, appointment_date)` |

#### Feign clients (outbound)

| Client | Target | Endpoint called | When |
|--------|--------|-----------------|------|
| `AuthClient` | `opd-auth-service` | `GET /internal/profile/{patientId}` | Building Kafka event payload |
| `DoctorClient` | `opd-doctor-service` | `GET /api/doctors/{doctorId}` | Building event payload |
| `DoctorClient` | `opd-doctor-service` | `GET /api/doctors/schedules/{scheduleId}` | Building event payload |
| `DoctorClient` | `opd-doctor-service` | `GET /internal/doctors/{doctorId}/schedules/{scheduleId}/{serialNo}/availability` | Checking capacity before booking |
| `BillingClient` | `opd-billing-service` | `POST /api/billing/invoice` | Creating invoice immediately after saving appointment |

---

### 3.6 Billing Service (`opd-billing-service`)

| Item | Value |
|------|-------|
| Port | **8084** |
| Database | MongoDB `billing_db` |
| Kafka producer | Topic: `PAYMENT_SUCCESS` |
| Feign | Calls Auth, Doctor, Appointment services |

#### REST Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/billing/invoice` | Create invoice (called by Appointment Service via Feign) |
| POST | `/api/billing/pay/{invoiceId}` | Simulate payment |
| GET | `/api/billing/patient/{patientId}` | List all invoices for a patient |

Via Gateway:
```
POST http://localhost:8080/billing/api/billing/pay/{invoiceId}
GET  http://localhost:8080/billing/api/billing/patient/{patientId}
```

#### MongoDB Document (`invoices` collection)

```json
{
  "_id":             "<UUID>",
  "appointmentId":  "<UUID>",
  "patientUserId":  "<UUID>",
  "doctorId":       "<UUID>",
  "scheduleId":     "<UUID>",
  "appointmentDate":"<date>",
  "doctorName":     "Dr. Jane Smith",
  "patientName":    "Rahim Khan",
  "patientPhone":   "+8801XXXXXXXXX",
  "baseFee":        500.00,
  "tax":            25.00,
  "discount":       0.00,
  "totalAmount":    525.00,
  "status":         "UNPAID | PAID | EXPIRED",
  "payment": {
    "id":               "<UUID>",
    "paymentMethod":    "CARD | MOBILE_BANKING | CASH",
    "paymentReference": "<transaction-id>",
    "amount":           525.00,
    "paidAt":           "<instant>"
  },
  "createdAt": "<instant>"
}
```

**Tax calculation**: `baseFee × 5%`.  Discount is currently fixed at `0`.

#### Feign clients (outbound)

| Client | Target | Endpoint | When |
|--------|--------|----------|------|
| `AuthClient` | `opd-auth-service` | `GET /internal/profile/{patientId}` | Creating invoice |
| `DoctorClient` | `opd-doctor-service` | `GET /api/doctors/{doctorId}` | Fetching consultation fee |
| `DoctorClient` | `opd-doctor-service` | `GET /api/doctors/schedules/{scheduleId}` | Checking `maxPatients` on pay |
| `AppointmentClient` | `opd-appointment-service` | `GET /appointments/countAppointments` | Checking schedule capacity on pay |

---

### 3.7 Notification Service (`opd-notification-service`)

| Item | Value |
|------|-------|
| Port | **8085** |
| Database | None |
| Kafka consumer | Topics: `APPOINTMENT_CREATED`, `APPOINTMENT_CONFIRMED` |

#### Kafka listeners

| Topic | Handler | Action |
|-------|---------|--------|
| `APPOINTMENT_CREATED` | `AppointmentEventListener#onAppointmentCreated` | Formats "payment pending" email+SMS, calls `EmailClient`/`SmsClient` |
| `APPOINTMENT_CONFIRMED` | `AppointmentEventListener#onAppointmentConfirmed` | Formats "confirmed" email+SMS, calls `EmailClient`/`SmsClient` |

> **Note:** `EmailClient` and `SmsClient` are currently **mock implementations** that
> only log to console.  Integration with a real provider (SMTP, SendGrid, Twilio) is
> **[PLANNED / NOT YET IMPLEMENTED]**.

---

## 4. Inter-Service Communication

```
Appointment Service
   ├─[Feign]──► Auth Service          GET /internal/profile/{patientId}
   ├─[Feign]──► Doctor Service        GET /api/doctors/{doctorId}
   ├─[Feign]──► Doctor Service        GET /api/doctors/schedules/{scheduleId}
   ├─[Feign]──► Doctor Service        GET /internal/doctors/{..}/availability
   └─[Feign]──► Billing Service       POST /api/billing/invoice

Billing Service
   ├─[Feign]──► Auth Service          GET /internal/profile/{patientId}
   ├─[Feign]──► Doctor Service        GET /api/doctors/{doctorId}
   ├─[Feign]──► Doctor Service        GET /api/doctors/schedules/{scheduleId}
   └─[Feign]──► Appointment Service   GET /appointments/countAppointments

Doctor Service
   └─[Feign]──► Auth Service          POST /register/users
                                       GET /admin/exists
```

All Feign clients resolve service addresses via **Eureka** (`lb://service-name`).
Each client has a custom `ErrorDecoder` that translates HTTP error responses from
downstream services into typed exceptions (`AuthServiceException`,
`DoctorServiceException`, `BillingServiceException`, etc.) and propagates the
original HTTP status code to the caller.

---

## 5. Kafka Event Bus

Kafka runs in **KRaft mode** (no ZooKeeper) on port `9092`.

### Topics

| Topic | Producer | Consumers | Payload type |
|-------|----------|-----------|--------------|
| `APPOINTMENT_CREATED` | Appointment Service | Notification Service | `AppointmentCreatedEvent` |
| `APPOINTMENT_CONFIRMED` | Appointment Service | Notification Service | `AppointmentConfirmedEvent` |
| `PAYMENT_SUCCESS` | Billing Service | Appointment Service | `PaymentSuccessEvent` |

### Event schemas

#### `AppointmentCreatedEvent`
```json
{
  "eventId":         "<UUID>",
  "occurredAt":      "<Instant>",
  "appointmentId":   "<UUID>",
  "appointmentDate": "<LocalDate>",
  "doctorId":        "<UUID>",
  "doctorName":      "Dr. Jane Smith",
  "consultationFee": 500.00,
  "scheduleId":      "<UUID>",
  "startTime":       "09:00",
  "endTime":         "09:30",
  "patient": {
    "id":       "<UUID>",
    "email":    "patient@example.com",
    "phone":    "+8801XXXXXXXXX",
    "fullName": "Rahim Khan"
  },
  "paymentUrl": "localhost:8084/api/billing/pay/<invoiceId>"
}
```

#### `AppointmentConfirmedEvent`
```json
{
  "eventId":         "<UUID>",
  "occurredAt":      "<Instant>",
  "appointmentId":   "<UUID>",
  "appointmentDate": "<LocalDate>",
  "doctorId":        "<UUID>",
  "doctorName":      "Dr. Jane Smith",
  "consultationFee": 500.00,
  "scheduleId":      "<UUID>",
  "startTime":       "09:00",
  "endTime":         "09:30",
  "patient": { "id": "...", "email": "...", "phone": "...", "fullName": "..." },
  "serialNo":        3
}
```

#### `PaymentSuccessEvent`
```json
{
  "appointmentId": "<UUID>",
  "invoiceId":     "<UUID>",
  "amount":        525.00,
  "paidAt":        "<Instant>"
}
```

### Consumer groups

| Service | Group ID | ACK mode |
|---------|----------|----------|
| Appointment Service | `appointment-service` | Manual |
| Billing Service | `appointment-service` | Manual |
| Notification Service | `notification-service` | Record |

> **Note:** Billing Service's `group-id` is currently set to `appointment-service`
> (same as Appointment Service) in `application.properties`.  In production these
> should be distinct to prevent both consumers competing for the same messages.

---

## 6. Redis Slot Locking

**Infrastructure**: Redis 7, port `6379`.

**Implementation** (`SlotLockService`):

```java
// Key format
"lock:appointment:{doctorId}:{scheduleId}:{date}"

// Lock acquired with SETNX + TTL (8 seconds)
redisTemplate.opsForValue().setIfAbsent(key, "LOCKED", Duration.ofSeconds(8));

// Explicit release
redisTemplate.delete(key);
```

> ⚠️ **Currently the lock call is commented out** in `AppointmentService#bookAppointment`.
> The code and Redis infrastructure exist, but the actual locking is disabled.
> The duplicate-check is enforced instead via a database unique constraint on
> `(patient_id, doctor_id, schedule_id, appointment_date)`.
> Enabling the Redis lock is **[PLANNED]**.

---

## 7. Database Schemas

### PostgreSQL — `auth_db`

Managed by Hibernate `ddl-auto=update`.  Tables: `users`, `patient_profiles`.

### PostgreSQL — `doctor_db`

Managed by Hibernate `ddl-auto=update`.  Tables: `doctors`, `doctor_schedules`.

### PostgreSQL — `appointment_db`

Managed by Hibernate `ddl-auto=update`.  Tables: `appointments`.

### MongoDB — `billing_db`

Schema-less; collection: `invoices`.  Documents follow the `InvoiceDocument` structure
(see §3.6).

### Bootstrap script (`init.sql`)

The root `init.sql` is mounted into the PostgreSQL container and creates the three
databases plus per-database users (`auth_user`, `doctor_user`, `appointment_user`).

> **Note**: The service `application.properties` files connect using the superuser
> (`admin`/`admin`) created by the Docker `POSTGRES_USER`/`POSTGRES_PASSWORD`
> environment variables — not the per-service users defined in `init.sql`.  The
> per-service users are created for future use / principle of least privilege.

---

## 8. End-to-End Workflows

### 8.1 Patient Registration & Login

```
Client
  │
  ├─POST /auth/register/patient
  │       { fullName, email, password }
  │   → Auth Service creates UserEntity (role=PATIENT) + PatientProfileEntity
  │   ← { id, message }
  │
  └─POST /auth/login
          { email, password }
      → Auth Service validates credentials, generates HMAC-SHA256 JWT (TTL 900 s)
      ← { accessToken, tokenType, expiresInSeconds, email }
```

### 8.2 Doctor Registration (Admin workflow)

```
Admin Client (needs JWT with role=ADMIN)
  │
  ├─POST /doctors/api/doctors
  │       { doctorName, degree, specialization, consultationFee, … }
  │   → Doctor Service calls Auth Service via Feign:
  │       POST /register/users  { username, email, password, role=DOCTOR }
  │   → Doctor Service persists DoctorEntity
  │   ← DoctorResponse
  │
  └─POST /doctors/api/doctors/{doctorId}/schedules
          { dayOfWeek, startTime, endTime, maxPatients }
      → Doctor Service persists DoctorScheduleEntity
      ← DoctorScheduleResponse
```

### 8.3 Appointment Booking

```
Patient Client (needs JWT)
  │
  └─POST /appointments/appointments/create
          X-User-Id: <patientUUID>   ← injected by gateway JWT filter
          X-User-Role: PATIENT       ← injected by gateway JWT filter
          { doctorId, scheduleId, date }
          │
          ├─1. Duplicate check (DB unique constraint)
          ├─2. Fetch current serial count from appointment_db
          ├─3. Feign → Doctor Service: isScheduleAvailable?
          │         (capacity check: serialNo < maxPatients)
          ├─4. Save AppointmentEntity  (status=PENDING_PAYMENT)
          ├─5. Feign → Billing Service: POST /api/billing/invoice
          │         (creates InvoiceDocument in MongoDB, returns paymentLink)
          ├─6. Feign → Doctor Service: getDoctorDetails
          ├─7. Feign → Doctor Service: getScheduleDetails
          ├─8. Feign → Auth Service:   getPatientSummary
          ├─9. Kafka PUBLISH → "APPOINTMENT_CREATED"
          │     (Notification Service receives & sends email+SMS to patient)
          └─10. Return AppointmentResponse { appointmentId, status=PENDING_PAYMENT, paymentLink }
```

### 8.4 Payment & Confirmation

```
Patient Client (needs JWT)
  │
  └─POST /billing/api/billing/pay/{invoiceId}
          { paymentMethod: "CARD" }
          │
          ├─1. Load InvoiceDocument from MongoDB
          ├─2. Feign → Appointment Service: countConfirmedAppointments
          │         (re-validates capacity hasn't been exceeded)
          ├─3. Feign → Doctor Service: getScheduleDetails (maxPatients)
          ├─4. If schedule full → 409 CONFLICT
          ├─5. Persist PaymentDetails in InvoiceDocument, status=PAID
          └─6. Kafka PUBLISH → "PAYMENT_SUCCESS"

Appointment Service (Kafka consumer)
  └─CONSUME "PAYMENT_SUCCESS"
      ├─1. Set appointment status → CONFIRMED
      ├─2. Assign serialNo (next available)
      ├─3. Feign → Doctor / Auth services for event payload
      └─4. Kafka PUBLISH → "APPOINTMENT_CONFIRMED"

Notification Service (Kafka consumer)
  └─CONSUME "APPOINTMENT_CONFIRMED"
      └─ Send confirmation email + SMS to patient (currently mock / log only)
```

---

## 9. Running Locally

### Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ |
| Gradle | Bundled wrapper (`./gradlew`) |
| Docker & Docker Compose | Latest |

### Step 1 — Start infrastructure

Use the **root** `docker-compose.yml` (recommended):

```bash
cd opd-hospital-system-microservice
docker-compose up -d
```

Or use `infra/docker-compose.yml` (equivalent):

```bash
cd infra
docker-compose up -d
```

Both files start: **PostgreSQL**, **MongoDB**, **Redis**, **Kafka**, **Redpanda
Console**, and **pgAdmin**.

Wait ~15 seconds for all containers to be healthy.

### Step 2 — Configure JWT secret

The `JWT_SECRET` environment variable must be set **consistently** across the
API Gateway and Auth Service before starting them.

```bash
export JWT_SECRET="my-very-long-secret-key-at-least-32-chars"
```

> The API Gateway has a **default fallback** secret
> (`x9J3Q2vN8LpW6sY4DkF1hE2bR7uV5tM3qP9cT1zB8R6yK4J8N2L0U3W5X7Y9Z1A`)
> but the Auth Service requires `JWT_SECRET` to be explicitly set.

### Step 3 — Start services in order

**Order matters** — Eureka must be up before any other service, Auth must be up
before Doctor (because Doctor calls Auth at startup to seed the admin account).

```bash
# Terminal 1 — Eureka (wait until port 8761 is open)
cd opd-eureka-server
../gradlew bootRun

# Terminal 2 — API Gateway (after Eureka is up)
cd opd-api-gateway
JWT_SECRET=<secret> ../gradlew bootRun

# Terminal 3 — Auth Service (after Eureka is up)
cd opd-auth-service
JWT_SECRET=<secret> ../gradlew bootRun

# Terminal 4 — Doctor Service (after Auth is up)
cd opd-doctor-service
../gradlew bootRun

# Terminal 5 — Appointment Service (after Doctor + Billing are up)
cd opd-appointment-service
../gradlew bootRun

# Terminal 6 — Billing Service (after Doctor + Appointment are up)
cd opd-billing-service
../gradlew bootRun

# Terminal 7 — Notification Service (independent, just needs Kafka)
cd opd-notification-service
../gradlew bootRun
```

> All services auto-create their database tables (`ddl-auto=update`) on first startup.

### Management UIs

| UI | URL | Credentials |
|----|-----|-------------|
| Eureka dashboard | http://localhost:8761 | — |
| Swagger UI (aggregated) | http://localhost:8080/swagger-ui.html | — |
| pgAdmin | http://localhost:5050 | admin@admin.com / admin123 |
| Redpanda Console (Kafka UI) | http://localhost:8089 | — |

---

## 10. API Reference & curl Examples

> All requests go through the **API Gateway** on port `8080`.
> Replace `<JWT>` with the `accessToken` value from the login response.

### 10.1 Register patient

```bash
curl -s -X POST http://localhost:8080/auth/register/patient \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Rahim Khan",
    "email":    "rahim@example.com",
    "password": "securepass456"
  }'
```

Expected response:
```json
{ "id": "<uuid>", "message": "Patient registered successfully" }
```

### 10.2 Login

```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":    "rahim@example.com",
    "password": "securepass456"
  }'
```

Expected response:
```json
{
  "accessToken":      "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType":        "Bearer",
  "expiresInSeconds": 900,
  "email":            "rahim@example.com"
}
```

### 10.3 Get patient profile

```bash
curl -s http://localhost:8080/auth/profile/ \
  -H "Authorization: Bearer <JWT>"
```

### 10.4 List doctors

```bash
curl -s http://localhost:8080/doctors/api/doctors \
  -H "Authorization: Bearer <JWT>"
```

### 10.5 Find available doctors

```bash
curl -s "http://localhost:8080/doctors/api/doctors/available?date=15/03/2026&specialization=CARDIOLOGY" \
  -H "Authorization: Bearer <JWT>"
```

### 10.6 Create a doctor schedule (Admin/Doctor)

```bash
# First obtain the doctor's UUID from the list endpoint
curl -s -X POST http://localhost:8080/doctors/api/doctors/<DOCTOR_ID>/schedules \
  -H "Authorization: Bearer <ADMIN_JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek":   "MONDAY",
    "startTime":   "09:00:00",
    "endTime":     "09:30:00",
    "maxPatients": 10
  }'
```

### 10.7 Book an appointment

```bash
curl -s -X POST http://localhost:8080/appointments/appointments/create \
  -H "Authorization: Bearer <PATIENT_JWT>" \
  -H "Content-Type: application/json" \
  -d '{
    "doctorId":   "<DOCTOR_UUID>",
    "scheduleId": "<SCHEDULE_UUID>",
    "date":       "2026-03-20"
  }'
```

Expected response:
```json
{
  "appointmentId": "<uuid>",
  "status":        "PENDING_PAYMENT",
  "paymentLink":   "localhost:8084/api/billing/pay/<invoiceId>"
}
```

### 10.8 Pay invoice

```bash
curl -s -X POST http://localhost:8080/billing/api/billing/pay/<INVOICE_ID> \
  -H "Authorization: Bearer <PATIENT_JWT>" \
  -H "Content-Type: application/json" \
  -d '{ "paymentMethod": "CARD" }'
```

Expected response:
```json
{ "message": "Payment Successful" }
```

After payment:
- Appointment status updates to `CONFIRMED` (via Kafka `PAYMENT_SUCCESS`)
- Patient receives confirmation email+SMS (logged to console in dev mode)

### 10.9 Get patient invoices

```bash
curl -s http://localhost:8080/billing/api/billing/patient/<PATIENT_UUID> \
  -H "Authorization: Bearer <PATIENT_JWT>"
```

---

## 11. Ports & Startup Order

| Order | Service | Port | Depends on |
|-------|---------|------|------------|
| 1 | Eureka Server | **8761** | — |
| 2 | API Gateway | **8080** | Eureka |
| 3 | Auth Service | **8081** | Eureka, PostgreSQL |
| 4 | Doctor Service | **8082** | Eureka, PostgreSQL, Auth Service |
| 5 | Appointment Service | **8083** | Eureka, PostgreSQL, Redis, Kafka, Doctor + Billing |
| 6 | Billing Service | **8084** | Eureka, MongoDB, Kafka, Doctor + Appointment |
| 7 | Notification Service | **8085** | Eureka, Kafka |

**Infrastructure services**:

| Service | Port | Purpose |
|---------|------|---------|
| PostgreSQL | 5432 | Auth / Doctor / Appointment databases |
| MongoDB | 27017 | Billing database |
| Redis | 6379 | Slot locking |
| Kafka | 9092 | Event streaming (KRaft, no ZooKeeper) |
| Redpanda Console | 8089 | Kafka management UI |
| pgAdmin | 5050 | PostgreSQL management UI |

---

## 12. Known Issues & Planned Features

### Inaccuracies in original README.md

| README claim | Actual code |
|--------------|-------------|
| Auth DB: `identity_db` | Actual DB name: **`auth_db`** |
| `POST /api/auth/register` | Actual gateway path: **`POST /auth/register/patient`** |
| `POST /api/auth/login` | Actual gateway path: **`POST /auth/login`** |
| `POST /api/appointments/book` | Actual gateway path: **`POST /appointments/appointments/create`** |
| `DELETE /api/appointments/{id}/cancel` | **Not implemented** |
| `GET /api/appointments/my` | **Not implemented** |
| `GET /api/billing/my-invoices` | **Not implemented** (use `/billing/api/billing/patient/{patientId}`) |
| Mentions ZooKeeper | Kafka 4.0.0 runs in **KRaft mode — no ZooKeeper needed** |

### [PLANNED / NOT YET IMPLEMENTED]

- **Real email/SMS delivery** – `EmailClient` and `SmsClient` are mock stubs that
  log messages to console.  Integration with SMTP, SendGrid, or Twilio is pending.
- **Redis slot lock activation** – `SlotLockService` code exists but the `acquireBookingLock`
  call is commented out in `AppointmentService`.
- **OAuth2 Google login** – handlers exist but require `GOOGLE_CLIENT_ID` /
  `GOOGLE_CLIENT_SECRET` environment variables and a registered OAuth2 callback.
- **Appointment cancellation** – no controller endpoint exists yet.
- **Appointment listing** – no "my appointments" endpoint exists yet.
- **Swagger/OpenAPI per-service** – springdoc dependency is referenced in the
  gateway config but OpenAPI annotations are not yet applied to controllers.
- **JWT expiry configuration** – the default is 900 seconds (15 min); adjustable
  via `JWT_EXPIRATION` environment variable on Auth Service.
