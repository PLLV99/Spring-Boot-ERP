# Spring Boot ERP

## Overview
This project is a Spring Boot ERP application developed using Spring Boot v3.4.5 and Java 17. It utilizes PostgreSQL as its database and implements JWT authentication for secure API access.

## Technologies Used
- **Spring Boot**: v3.4.5
- **Java**: v17
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Token)

## Complete API Endpoints List
| HTTP Method | Endpoint                     | Description                    |
|-------------|------------------------------|--------------------------------|
| GET         | /api/users                   | Retrieve all users             |
| POST        | /api/users                   | Create a new user              |
| GET         | /api/users/{id}              | Retrieve user by ID            |
| PUT         | /api/users/{id}              | Update user by ID              |
| DELETE      | /api/users/{id}              | Delete user by ID              |
| ...         | ...                          | ...                            |

## Project Structure
```
Spring-Boot-ERP/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── erp/
│   │   │               ├── controller/
│   │   │               ├── model/
│   │   │               ├── repository/
│   │   │               └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   └── test/
└── pom.xml
```

## Setup Instructions
1. Clone this repository:
   ```bash
   git clone https://github.com/PLLV99/Spring-Boot-ERP.git
   cd Spring-Boot-ERP
   ```
2. Ensure you have Java 17 and PostgreSQL installed.
3. Configure your database settings in the `application.properties` file.
4. Build the application:
   ```bash
   mvn clean install
   ```

## How to Run with NextJS Frontend
1. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
2. Start the NextJS frontend.
3. Make sure the frontend API points to the backend endpoints.

## Development Guide
- Follow RESTful principles when creating new endpoints.
- Maintain clear and concise commit messages.
- Write unit tests for new features.

## Deployment Options
- Deploy on cloud platforms like AWS, Heroku, or DigitalOcean.
- Containerize with Docker for easy deployment.

## Testing Examples
- Use Postman or curl for testing API endpoints.
- Example of a GET request:
   ```bash
   curl -X GET http://localhost:8080/api/users
   ```

## License
This project is licensed under the MIT License.