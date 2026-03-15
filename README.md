# 🏥 OPD Hospital System Microservice

![OPD Hospital System Logo](opd-logo.png)

*A scalable microservices-based outpatient (OPD) hospital management system*

---

## 📌 Overview

The **OPD Hospital System** manages hospital outpatient operations using a modern **microservices architecture**. It covers the complete OPD flow including **patient authentication, doctor management, appointment booking, billing, and notifications**.

The system is built with **Java & Spring Boot**, uses **Eureka for service discovery**, **API Gateway for routing and security**, **Kafka for event-driven communication**, and **multiple databases** optimized per service.

> 📖 **See [ARCHITECTURE.md](ARCHITECTURE.md) for a comprehensive code-accurate developer guide** — including per-service endpoint tables, database schemas, Kafka event schemas, end-to-end workflows, and correct local-run instructions.

---

## ✨ Key Features

- JWT-secured patient registration and login
- Doctor profile management and scheduling
- Appointment booking with **slot locking** to avoid double booking
- Billing and payment integration
- Event-driven Email / SMS notifications

---

## 🏗️ Architecture

The system follows a **microservices architecture**:

### Core Components

- **API Gateway**
  - Routes all client requests
  - Validates JWT tokens
  - Acts as a single entry point

- **Eureka Server**
  - Service registry and discovery

- **Microservices**
  - Auth Service
  - Doctor Service
  - Appointment Service
  - Billing Service
  - Notification Service

- **Messaging**
  - Kafka for asynchronous events (appointment booked, payment completed, etc.)

- **Databases**
  - PostgreSQL (multiple schemas)
  - MongoDB (billing data)

- **Caching**
  - Redis for appointment slot locking

- **Security**
  - Spring Security with JWT & OAuth2

### Communication Pattern

- **Synchronous**: REST via Feign clients (inter-service) and API Gateway (client-facing)
- **Asynchronous**: Kafka events (`APPOINTMENT_CREATED`, `APPOINTMENT_CONFIRMED`, `PAYMENT_SUCCESS`)

---

## 🧩 Microservices Details

### 🔐 Auth Service

**Responsibilities**
- Patient registration and login
- JWT token generation and validation
- OAuth2 support (extensible)

**Connections**
- PostgreSQL (`auth_db`)
- Eureka registered

---

### 🩺 Doctor Service

**Responsibilities**
- Doctor profile management
- Doctor schedules and availability

**Connections**
- PostgreSQL (`doctor_db`)
- Called synchronously by Appointment Service

---

### 📅 Appointment Service

**Responsibilities**
- Appointment booking and cancellation
- Serial number generation
- Slot locking using Redis

**Connections**
- PostgreSQL (`appointment_db`)
- Redis (slot locks)
- Kafka producer
- Sync communication with Doctor & Billing services

---

### 💳 Billing Service

**Responsibilities**
- Invoice generation
- Payment processing
- Payment status tracking

**Connections**
- MongoDB (`billing_db`)
- Kafka consumer

---

### 📣 Notification Service

**Responsibilities**
- Email and SMS notifications

**Connections**
- Kafka consumer
- JavaMail / Twilio (or mock integrations)

---

## 🌐 API Gateway & Eureka

- **API Gateway**
  - Centralized routing
  - Authentication & authorization

- **Eureka Server**
  - Registers and discovers all services

---

## 🧰 Technology Stack

| Category | Technologies |
|------|-------------|
| Backend | Java 21, Spring Boot 4.x, Spring Cloud |
| Databases | PostgreSQL (auth_db, doctor_db, appointment_db), MongoDB (billing_db) |
| Messaging / Caching | Kafka 4.0 (KRaft mode, no ZooKeeper), Redis 7 |
| Security | JWT (HMAC-SHA256), OAuth2, Spring Security |
| Tooling | Docker Compose, Gradle, Lombok, JPA/Hibernate |

---

## 🚀 Installation & Setup

### Prerequisites

- Java 21+
- Docker & Docker Compose
- Gradle

### Setup Steps

```bash
# Clone repository
git clone https://github.com/KhairulBasharbd/opd-hospital-system-microservice.git

# Navigate to project
cd opd-hospital-system-microservice

# Start infrastructure (PostgreSQL, MongoDB, Kafka, Redis, pgAdmin, Redpanda Console)
docker-compose up -d

# Set the JWT secret (must be the same for API Gateway and Auth Service)
export JWT_SECRET="your-secret-key-at-least-32-chars"

# Start services in order (each in its own terminal):
# 1. Eureka Server
cd opd-eureka-server && ../gradlew bootRun

# 2. API Gateway
cd opd-api-gateway && JWT_SECRET=$JWT_SECRET ../gradlew bootRun

# 3. Auth Service
cd opd-auth-service && JWT_SECRET=$JWT_SECRET ../gradlew bootRun

# 4. Doctor Service (after Auth is up)
cd opd-doctor-service && ../gradlew bootRun

# 5. Appointment + Billing + Notification Services
cd opd-appointment-service && ../gradlew bootRun
cd opd-billing-service     && ../gradlew bootRun
cd opd-notification-service && ../gradlew bootRun
```

> 📖 See **[ARCHITECTURE.md](ARCHITECTURE.md)** for the complete startup guide, port list, and management UIs.

---

## ▶️ Usage

- Access system via **API Gateway**: `http://localhost:8080`
- Use **Postman** or **Swagger UI**
- Secure endpoints require **Bearer JWT token**

---

## 📡 API Endpoint Examples

> All requests go through API Gateway (`http://localhost:8080`)

### 🔐 Authentication

| Method | Endpoint | Description | Auth | Example Body |
|------|--------|-------------|------|--------------|
| POST | `/auth/register/patient` | Register patient | No | `{ "fullName": "John Doe", "email": "john@example.com", "password": "pass123" }` |
| POST | `/auth/login` | Login (JWT) | No | `{ "email": "john@example.com", "password": "pass123" }` |
| GET | `/auth/profile/` | User profile | Yes (JWT) | — |

---

### 🩺 Doctor Management

| Method | Endpoint | Description | Auth |
|------|--------|-------------|------|
| GET | `/doctors/api/doctors` | List all doctors | Yes |
| GET | `/doctors/api/doctors/{id}` | Doctor details | Yes |
| POST | `/doctors/api/doctors/` | Create doctor | Yes (Admin) |
| POST | `/doctors/api/doctors/{id}/schedules` | Create schedule | Yes (Admin) |
| GET | `/doctors/api/doctors/available` | Available doctors (`?date=dd/MM/yyyy&specialization=CARDIOLOGY`) | Yes |

---

### 📅 Appointments

| Method | Endpoint | Description | Auth |
|------|--------|-------------|------|
| POST | `/appointments/appointments/create` | Book appointment | Yes (Patient) |

---

### 💳 Billing

| Method | Endpoint | Description | Auth |
|------|--------|-------------|------|
| POST | `/billing/api/billing/pay/{invoiceId}` | Pay invoice | Yes |
| GET | `/billing/api/billing/patient/{patientId}` | Patient invoice history | Yes |

---

## 📣 Notifications

- Fully **event-driven**
- No direct REST endpoints
- Triggered automatically on:
  - Appointment booking
  - Appointment confirmation
  - Payment completion

---

## 🧪 Testing Examples (cURL)

### Register

```bash
curl -X POST http://localhost:8080/auth/register/patient \
-H "Content-Type: application/json" \
-d '{"fullName":"Rahim Khan","email":"rahim@example.com","password":"securepass456"}'
```

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"rahim@example.com","password":"securepass456"}'
```

### Book Appointment

```bash
curl -X POST http://localhost:8080/appointments/appointments/create \
-H "Authorization: Bearer <JWT>" \
-H "Content-Type: application/json" \
-d '{"doctorId":"<doctor-uuid>","scheduleId":"<schedule-uuid>","date":"2026-03-25"}'
```

### Pay Invoice

```bash
curl -X POST http://localhost:8080/billing/api/billing/pay/<invoice-uuid> \
-H "Authorization: Bearer <JWT>" \
-H "Content-Type: application/json" \
-d '{"paymentMethod":"CARD"}'
```

---

## 📝 Notes

- Swagger / OpenAPI recommended for API documentation
- Standard error format:

```json
{ "status": 400, "message": "Error" }
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes with clear messages
4. Submit a Pull Request

Follow clean code practices and include tests.

---

## 📄 License

MIT License – see the `LICENSE` file for details.

---

## 👨‍💻 Author

**Khairul Bashar**  
Backend Engineer | Microservices & Distributed Systems Enthusiast

GitHub: https://github.com/KhairulBasharbd

---

⭐ *Built as a real-world, production-thinking OPD hospital microservice system.*

