# 🏥 OPD Hospital System Microservice

![OPD Hospital System Logo](opd-logo.png)

*A scalable microservices-based outpatient (OPD) hospital management system*

---

## 📌 Overview

The **OPD Hospital System** manages hospital outpatient operations using a modern **microservices architecture**. It covers the complete OPD flow including **patient authentication, doctor management, appointment booking, billing, and notifications**.

The system is built with **Java & Spring Boot**, uses **Eureka for service discovery**, **API Gateway for routing and security**, **Kafka for event-driven communication**, and **multiple databases** optimized per service.

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

- **Synchronous**: REST (via API Gateway)
- **Asynchronous**: Kafka events

---

## 🧩 Microservices Details

### 🔐 Auth Service

**Responsibilities**
- Patient registration and login
- JWT token generation and validation
- OAuth2 support (extensible)

**Connections**
- PostgreSQL (`identity_db`)
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
- MongoDB
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
| Backend | Java 21, Spring Boot 3.x, Spring Cloud |
| Databases | PostgreSQL, MongoDB |
| Messaging / Caching | Kafka, Zookeeper, Redis |
| Security | JWT, OAuth2, Spring Security |
| Tooling | Docker Compose, Maven, Lombok, JPA |

---

## 🚀 Installation & Setup

### Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven

### Setup Steps

```bash
# Clone repository
git clone https://github.com/KhairulBasharbd/opd-hospital-system-microservice.git

# Navigate to project
cd opd-hospital-system-microservice

# Start infrastructure (DBs, Kafka, Redis)
docker-compose up -d

# Build and run services (start Eureka first)
mvn clean install
mvn spring-boot:run
```

Update `application.yml / application.properties` if needed.

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
| POST | `/api/auth/register` | Register patient | No | `{ "fullName": "John Doe", "email": "john@example.com", "password": "pass123" }` |
| POST | `/api/auth/login` | Login (JWT) | No | `{ "email": "john@example.com", "password": "pass123" }` |
| GET | `/api/auth/me` | User profile | Yes | — |

---

### 🩺 Doctor Management

| Method | Endpoint | Description | Auth | Example |
|------|--------|-------------|------|--------|
| GET | `/api/doctors` | List doctors | Yes | `?specialization=Cardiology` |
| GET | `/api/doctors/{id}` | Doctor details | Yes | — |
| POST | `/api/doctors` | Create doctor | Yes (Admin) | `{ "name": "Dr. Sarah" }` |
| PUT | `/api/doctors/{id}/schedule` | Update schedule | Yes (Doctor) | `{ "day": "Monday", "startTime": "09:00" }` |
| GET | `/api/doctors/{id}/availability` | Available slots | Yes | `?date=2025-03-15` |

---

### 📅 Appointments

| Method | Endpoint | Description | Auth | Example |
|------|--------|-------------|------|--------|
| POST | `/api/appointments/book` | Book appointment | Yes (Patient) | `{ "doctorId": 5, "date": "2025-03-20" }` |
| POST | `/api/appointments/{id}/confirm` | Confirm appointment | Yes | — |
| GET | `/api/appointments/my` | My appointments | Yes (Patient) | `?status=CONFIRMED` |
| GET | `/api/appointments/{id}` | Appointment details | Yes | — |
| DELETE | `/api/appointments/{id}/cancel` | Cancel appointment | Yes | `{ "reason": "Emergency" }` |

---

### 💳 Billing

| Method | Endpoint | Description | Auth | Example |
|------|--------|-------------|------|--------|
| GET | `/api/billing/my-invoices` | List invoices | Yes | — |
| GET | `/api/billing/invoices/{id}` | Invoice details | Yes | — |
| POST | `/api/billing/invoices/{id}/pay` | Pay invoice | Yes | `{ "paymentMethod": "bkash" }` |
| GET | `/api/billing/invoices/{id}/status` | Payment status | Yes | — |

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
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"fullName":"Rahim Khan","email":"rahim@example.com","password":"securepass456"}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"rahim@example.com","password":"securepass456"}'
```

### Book Appointment

```bash
curl -X POST http://localhost:8080/api/appointments/book \
-H "Authorization: Bearer <JWT>" \
-H "Content-Type: application/json" \
-d '{"doctorId":3,"date":"2025-03-25"}'
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

