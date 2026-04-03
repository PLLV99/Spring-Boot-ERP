# Spring Boot ERP

## Database Information
This application uses PostgreSQL as its database. Make sure to set up the PostgreSQL database and update the connection properties accordingly.

## JWT Authentication
The application employs JWT (JSON Web Token) for authentication. Ensure that you have the required configurations in place for secure access to the APIs.

## API Endpoints
- `/api/login`: To authenticate users and receive a JWT token.
- `/api/users`: To manage user data.
- `/api/products`: To manage product data.

## Frontend Integration
The frontend can be integrated with the Spring Boot backend using RESTful API calls. Ensure CORS is configured to allow requests from your frontend application.