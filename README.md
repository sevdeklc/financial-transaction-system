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

## Flyway Database Migrations

### Troubleshooting: Flyway Not Running

**Symptoms:**
- No Flyway logs in application startup (no "Migrating schema" or "Successfully applied" messages)
- Database tables don't exist: `psql -h localhost -p 55432 -U transfer_user transfer_db -c "\dt"` shows no relations
- Application errors: `ERROR: relation "transfers" does not exist`
- Error: `Unsupported Database: PostgreSQL 16.11` and `Error creating bean 'flywayInitializer'`

**Root Causes:**
1. **Missing Starter Dependency:** Using `org.flywaydb:flyway-core` directly instead of `spring-boot-starter-flyway` prevents Spring Boot's `FlywayAutoConfiguration` from being activated.
2. **PostgreSQL 16.11 Compatibility:** Spring Boot 4.0.1 includes Flyway 11.14.1 by default, which doesn't support PostgreSQL 16.11. The transitive `flyway-database-postgresql` dependency uses the older version.

**Fix:**
1. Use `spring-boot-starter-flyway` for auto-configuration
2. Set `flyway.version=11.18.2` in `pom.xml` properties
3. Explicitly add `flyway-core` and `flyway-database-postgresql` dependencies with `${flyway.version}` to override Spring Boot's managed versions

**Verification:**
After the fix, you should see:
- Flyway migration logs at startup: `Flyway Community Edition 11.18.2`, `Migrating schema`, `Successfully applied 1 migration`
- Database tables created: `psql ... -c "\dt"` shows `flyway_schema_history`, `accounts`, `transfers`
- Application runs without "relation does not exist" or "Unsupported Database" errors
- `POST /api/v1/transfers` returns 201 status

