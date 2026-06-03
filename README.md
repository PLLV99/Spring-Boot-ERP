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

## Language Composition
- Java: 100%

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
│   │       │   ├── UserApiController.java
│   │       │   ├── BookController.java
│   │       │   ├── FileController.java
│   │       │   └── UserController.java
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
│   │       ├── MyProjectApplication.java
│   │       ├── ServletInitializer.java
│   │       └── WebConfig.java
│   └── resources/
│       └── application.properties
└── test/java/com/app/my_project/
    └── MyProjectApplicationTests.java

Root files:
.mvn/wrapper, .env.example, .gitattributes, .gitignore, application.properties,
mvnw, mvnw.cmd, pom.xml, README.md
```

## Main Components
- Controllers: ERP API endpoints
- Entities: Database mappings
- Repositories: Data access layer
- Services: Business logic
- Security: `RequireAuth` + `JwtInterceptor`
- Config: `WebConfig` + `application.properties`

## Getting Started

```bash
git clone https://github.com/PLLV99/Spring-Boot-ERP.git
cd Spring-Boot-ERP
cp .env.example .env
# Fill in DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET in .env
mvn spring-boot:run
```

You can optionally run `mvn clean install` after creating `.env`.

Default local URL: http://localhost:8080

## Testing

```bash
mvn test
```

## Notes
- This repository is backend-only.
- Frontend is maintained separately in `PLLV99/NextJS-ERP`.
- License section intentionally omitted until a LICENSE file is added.
