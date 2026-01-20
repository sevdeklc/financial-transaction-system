# Financial Transaction System

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

