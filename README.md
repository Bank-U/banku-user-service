# user-service · BankU

This microservice handles **user registration, authentication, and profile management** for the BankU platform. It is the entry point for user onboarding and credential management.

## Tech Stack

- Java 17+
- Spring Boot
- Spring Web
- Spring Security
- Spring Data MongoDB
- JWT
- Lombok

## Endpoints (planned)

- `POST /auth/register` — Register a new user
- `POST /auth/login` — Authenticate and return JWT
- `PUT /user` — Update user data
- `DELETE /user` — Delete user account

## Setup

1. Clone the repository
2. Configure MongoDB connection in `application.yml`
3. Run the application with:
   ```bash
   ./mvnw spring-boot:run
