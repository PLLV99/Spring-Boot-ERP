# Spring-Boot-ERP

Backend ERP API built with Spring Boot and PostgreSQL.

## Repository Scope
This repository contains the **backend only**.

- Frontend repository: https://github.com/PLLV99/NextJS-ERP

## Tech Stack
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL
- JWT
- Maven

## Actual Project Structure

```text
src/
├── main/
│   ├── java/com/app/
│   │   ├── models/
│   │   │   ├── BookModel.java
│   │   │   ├── TokenRequest.java
│   │   │   └── UserModel.java
│   │   └── my_project/
│   │       ├── annotation/
│   │       │   └── RequireAuth.java
│   │       ├── controller/
│   │       │   ├── FormulaApiController.java
│   │       │   ├── MaterialApiController.java
│   │       │   ├── ProductionApiController.java
│   │       │   ├── ProductionLogApiController.java
│   │       │   ├── ProductionLossApiController.java
│   │       │   ├── ReportApiController.java
│   │       │   ├── SaleTempApiController.java
│   │       │   ├── StoreApiController.java
│   │       │   ├── TransferStockApiController.java
│   │       │   └── UserApiController.java
│   │       ├── entity/
│   │       │   ├── BillSaleDetailEntity.java
│   │       │   ├── BillSaleEntity.java
│   │       │   ├── FormulaEntity.java
│   │       │   ├── MaterialEntity.java
│   │       │   ├── ProductionEntity.java
│   │       │   ├── ProductionLogEntity.java
│   │       │   ├── ProductionLossEntity.java
│   │       │   ├── SaleTempEntity.java
│   │       │   ├── StoreEntity.java
│   │       │   ├── StoreImportEntity.java
│   │       │   ├── TransferStockEntity.java
│   │       │   └── UserEntity.java
│   │       ├── interceptor/
│   │       │   └── JwtInterceptor.java
│   │       ├── jpa/
│   │       │   └── EndSaleJpa.java
│   │       ├── repository/
│   │       │   ├── BillSaleDetailRepository.java
│   │       │   ├── BillSaleRepository.java
│   │       │   ├── FormulaRepository.java
│   │       │   ├── MaterialRepository.java
│   │       │   ├── ProductionLogRepository.java
│   │       │   ├── ProductionLossRepository.java
│   │       │   ├── ProductionRepository.java
│   │       │   ├── SaleTempRepository.java
│   │       │   ├── StoreImportRepository.java
│   │       │   ├── StoreRepository.java
│   │       │   ├── TransferStockRepository.java
│   │       │   └── UserRepository.java
│   │       ├── service/
│   │       │   ├── SaleTempService.java
│   │       │   └── UserService.java
│   │       ├── BookController.java
│   │       ├── FileController.java
│   │       ├── UserController.java
│   │       ├── MyProjectApplication.java
│   │       ├── ServletInitializer.java
│   │       └── WebConfig.java
│   └── resources/
│       └── application.properties
└── test/java/com/app/my_project/
    ├── ConfigurationSecurityTest.java
    └── MyProjectApplicationTests.java

Root files:
.mvn/wrapper, .env.example, .gitattributes, .gitignore, application.properties,
docker-compose.yml, mvnw, mvnw.cmd, pom.xml, README.md
```

## Main Components
- Controllers: ERP API endpoints
- Entities: Database mappings
- Repositories: Data access layer
- Services: Business logic
- Security: `RequireAuth` + `JwtInterceptor`
- Config: `WebConfig` + `application.properties`

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

## Testing

```bash
./mvnw test
```

## Notes
- This repository is backend-only.
- Frontend is maintained separately in `PLLV99/NextJS-ERP`.
- License section intentionally omitted until a LICENSE file is added.
