# Financial Transaction System

## Overview

This project is a **case study** designed to demonstrate how a **financial money transfer flow** can be implemented using modern backend engineering practices.

The main goal of the case is to design and implement a **safe, consistent, and scalable transfer operation** where:

* Money is transferred atomically between accounts
* Balance consistency is guaranteed under concurrent requests
* Business rules (validation, insufficient balance, idempotency) are enforced correctly
* The system is structured in a way that is extensible towards an event‑driven microservice architecture

This repository is intended for **technical evaluation purposes** and for developers who want to understand how a real‑world transfer system can be modeled step‑by‑step.

---

## Microservices (Implemented So Far)

The system is designed as a **microservice‑based architecture**. The following services have been implemented so far:

* **transfer-service**
  Responsible for executing money transfers between accounts.
  Handles validation, transactional balance updates, idempotency, and transfer state management.

> Other services (e.g. notification-service, user-service) are planned but not yet implemented.

---

## Local Run

1. Start infrastructure services:

   ```bash
   docker compose up -d
   ```

2. Run transfer-service:

   ```bash
   cd services/transfer-service
   ./mvnw spring-boot:run
   ```

3. Check actuator health:

   ```bash
   curl http://localhost:8081/actuator/health
   ```

4. Open Swagger UI for testing:

   ```text
   http://localhost:8081/swagger-ui/index.html
   ```
