# Spring Boot ERP

## Professional Overview
This project is a comprehensive Enterprise Resource Planning (ERP) system built using Spring Boot. It serves as a robust application suitable for real-world job applications, showcasing a rich feature set and a structured architecture.

## Tech Stack
- **Backend**: Spring Boot, JPA, Hibernate
- **Database**: MySQL
- **Frontend**: Thymeleaf, Bootstrap
- **Version Control**: Git
- **Build Tool**: Maven
- **APIs**: RESTful services

## Setup Instructions
1. **Clone the Repository**:  
   ```bash
   git clone https://github.com/PLLV99/Spring-Boot-ERP.git
   cd Spring-Boot-ERP
   ```  

2. **Configure the Database**:  
   - Create a MySQL database and update `application.properties` with your database credentials.

3. **Build the Application**:  
   ```bash  
   mvn clean install
   ```

4. **Run the Application**:  
   ```bash  
   mvn spring-boot:run
   ```

## Features
- User Authentication and Authorization
- Role-based Access Control
- Inventory Management
- Order Processing
- Reporting and Analytics
- Responsive UI with Bootstrap

## Architecture
The architecture follows the Model-View-Controller (MVC) design pattern, providing a clean separation of concerns:
- **Model**: Represents the data and business logic.
- **View**: The user interface that displays the data.
- **Controller**: Handles user input and interacts with the model to update the view.

This project demonstrates a scalable and maintainable architecture suitable for enterprise-level applications.