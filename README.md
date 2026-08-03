# Spring-Boot-ERP

Backend ERP API built with Spring Boot and PostgreSQL, covering production planning,
bill-of-materials, inventory, point-of-sale and revenue reporting.

## Repository Scope
This repository contains the **backend only**.

- Frontend repository: https://github.com/PLLV99/NextJS-ERP

## Tech Stack
- Java 17
- Spring Boot 3.4.5
- Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL 17
- JWT (`com.auth0:java-jwt`)
- BCrypt (`spring-security-crypto`)
- Maven (wrapper included)

## Actual Project Structure

```text
src/
├── main/
│   ├── java/com/app/my_project/
│   │   ├── annotation/
│   │   │   ├── Public.java            # endpoint reachable without a token
│   │   │   └── RequireRole.java       # endpoint restricted to given roles
│   │   ├── controller/
│   │   │   ├── FormulaApiController.java
│   │   │   ├── MaterialApiController.java
│   │   │   ├── ProductionApiController.java
│   │   │   ├── ProductionLogApiController.java
│   │   │   ├── ProductionLossApiController.java
│   │   │   ├── ReportApiController.java
│   │   │   ├── SaleTempApiController.java
│   │   │   ├── StoreApiController.java
│   │   │   ├── TransferStockApiController.java
│   │   │   └── UserApiController.java
│   │   ├── entity/
│   │   │   ├── BillSaleDetailEntity.java
│   │   │   ├── BillSaleEntity.java
│   │   │   ├── FormulaEntity.java
│   │   │   ├── MaterialEntity.java
│   │   │   ├── ProductionEntity.java
│   │   │   ├── ProductionLogEntity.java
│   │   │   ├── ProductionLossEntity.java
│   │   │   ├── SaleTempEntity.java
│   │   │   ├── StoreEntity.java
│   │   │   ├── StoreImportEntity.java
│   │   │   ├── TransferStockEntity.java
│   │   │   └── UserEntity.java
│   │   ├── interceptor/
│   │   │   └── JwtInterceptor.java    # authentication + role authorisation
│   │   ├── jpa/
│   │   │   └── EndSaleJpa.java
│   │   ├── repository/                # 12 Spring Data JPA repositories
│   │   ├── service/
│   │   │   ├── SaleTempService.java
│   │   │   └── UserService.java
│   │   ├── AdminUserSeeder.java       # creates the first admin on an empty DB
│   │   ├── ApiExceptionHandler.java   # maps exceptions to HTTP status codes
│   │   ├── MyProjectApplication.java
│   │   ├── ServletInitializer.java
│   │   └── WebConfig.java
│   └── resources/
│       └── application.properties
└── test/java/com/app/my_project/
    ├── ConfigurationSecurityTest.java
    └── MyProjectApplicationTests.java

Root files:
.mvn/wrapper, .env.example, .gitattributes, .gitignore, docker-compose.yml,
mvnw, mvnw.cmd, pom.xml, README.md
```

## Domain Model

12 tables, 13 foreign keys. The flow runs raw material → product → recipe →
production output → warehouse → sale → invoice → report.

| Entity | Purpose |
|---|---|
| `MaterialEntity` | Raw materials |
| `ProductionEntity` | Finished products (name, detail, price) |
| `FormulaEntity` | Bill of materials — associative entity carrying `qty` and `unit` |
| `ProductionLogEntity` / `ProductionLossEntity` | Output produced / scrapped per batch |
| `StoreEntity` / `StoreImportEntity` | Warehouses and stock intake |
| `TransferStockEntity` | Stock moved between warehouses |
| `SaleTempEntity` | Per-user shopping cart |
| `BillSaleEntity` / `BillSaleDetailEntity` | Invoice header and lines |
| `UserEntity` | Accounts and roles |

Invoice lines store the price **as it was at the time of sale** rather than joining
back to the product, so a later price change cannot rewrite past revenue. Cancelling
an invoice is a soft delete (`status = 'cancel'`); reports count only `paid` rows.

## Security

- **Authentication — deny by default.** `JwtInterceptor` requires a valid JWT on every
  endpoint unless the method is annotated `@Public`. Only `POST /api/users/admin-signin`
  is public, because you cannot sign in if signing in requires being signed in.
  Forgetting an annotation leaves an endpoint locked, not open.
- **Authorisation — `@RequireRole`.** Read from the `role` claim by the same interceptor,
  on a method or a whole controller. No annotation means "any signed-in user", which is
  what the shared Inventory, Production and Sales modules need. A role mismatch answers
  **403**, not 401 — the token is valid, the account simply lacks the role.
- **Passwords.** BCrypt hashes with a per-password salt; sign-in looks up by username and
  compares hashes. `UserEntity.password` is `@JsonProperty(WRITE_ONLY)`, so a hash is
  accepted from a request body but never serialised into a response.
- **Credentials.** Loaded from a gitignored `.env` (or environment variables in
  deployment); the application refuses to start without `JWT_SECRET`.
  `ConfigurationSecurityTest` fails the build if a credential is hardcoded back into
  `application.properties`.
- **Errors.** `ApiExceptionHandler` returns a consistent `{status, error, message}` body
  with a meaningful status — 409 for a business-rule conflict, 404 for a missing record,
  400 for bad input, 401 for bad credentials. Stack traces are never included.

### Role matrix

| | `admin` | `employee` |
|---|---|---|
| Inventory, Production, Sales, own profile | ✅ | ✅ |
| Dashboard, Invoices, Reports (`/api/report/**`) | ✅ | ❌ |
| Set product prices (`/api/productions/updatePrice/**`) | ✅ | ❌ |
| List / create / edit / delete users | ✅ | ❌ |

## Getting Started

Requirements: Java 17+ and Docker Desktop (no local Maven or PostgreSQL needed —
the Maven wrapper and a Dockerized database are included).

```bash
git clone https://github.com/PLLV99/Spring-Boot-ERP.git
cd Spring-Boot-ERP
cp .env.example .env
# Fill in DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET in .env
# For local dev with the bundled Docker database use:
#   DB_URL=jdbc:postgresql://localhost:5435/erp

# Start the local PostgreSQL (host port 5435)
docker compose up -d

# Run the API (Windows: mvnw.cmd, macOS/Linux: ./mvnw)
./mvnw spring-boot:run
```

Default local URL: http://localhost:8080

### First login

On an empty database `AdminUserSeeder` creates a single admin account. Its password comes
from the `ADMIN_PASSWORD` environment variable, falling back to `admin1234` with a startup
warning. **Change it after the first login.**

```bash
curl -X POST http://localhost:8080/api/users/admin-signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin1234"}'
```

The response contains a JWT valid for 7 days. Send it on every other request:

```bash
curl http://localhost:8080/api/productions -H "Authorization: Bearer <token>"
```

## API Overview

| Prefix | Purpose | Access |
|---|---|---|
| `/api/users/admin-signin` | Sign in, returns `{token, role}` | public |
| `/api/users/admin-info`, `/api/users/edit-profile` | Own account | any signed-in user |
| `/api/users/**` (list, admin-create/update/delete) | User management | `admin` |
| `/api/materials`, `/api/formulas` | Raw materials and recipes | any signed-in user |
| `/api/productions` | Products | any signed-in user |
| `/api/productions/updatePrice/{id}` | Pricing | `admin` |
| `/api/production-logs`, `/api/production-loss` | Output and scrap | any signed-in user |
| `/api/store`, `/api/store/import`, `/api/transfer-stock` | Warehouses and stock | any signed-in user |
| `/api/SaleTemp`, `/api/SaleTemp/endSale` | Cart and checkout | any signed-in user |
| `/api/report/**` | Dashboard, invoices, monthly revenue | `admin` |

## Testing

```bash
./mvnw test
```

## Known Limitations

- `spring.jpa.hibernate.ddl-auto=update` bootstraps the schema; a production deployment
  should use versioned migrations (Flyway) instead.
- Money is stored as `Double`; `BigDecimal` with a `NUMERIC` column is the correct type.
- CORS currently allows all origins.
- No pagination on list endpoints.
- Stock on hand is derived by aggregating logs, losses, imports and transfers rather than
  kept as a running balance.

## Notes
- This repository is backend-only.
- Frontend is maintained separately in `PLLV99/NextJS-ERP`.
- License section intentionally omitted until a LICENSE file is added.
