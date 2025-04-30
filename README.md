# BankU - User Service

User service for BankU, implemented with Spring Boot and following Event Sourcing principles.

## Features

- User management (registration, update, deletion)
- JWT Authentication
- Event Sourcing with MongoDB as event store
- Events published to Kafka for inter-service communication
- Security with Spring Security
- Data validation
- Custom exception handling

## Technologies

- Java 17
- Spring Boot 3.4.4
- Spring Security
- Spring Data MongoDB
- Spring Kafka
- JWT
- Docker
- Docker Compose

## Configuration

### Required Environment Variables

The service requires the following environment variables to be set:

```bash
# JWT Configuration
jwt.secret=2a1cf8399b4951d738e9b62c63b11c867f7c4e471cb108c1e7b4a4377e5d7a4f

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://banku:secret@localhost:27017/banku-user?authSource=admin

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092

# OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=000000000000-00000000000000000000000000000000.apps.googleusercontent.com
spring.security.oauth2.client.registration.google.client-secret=GOCSPX-000000000000_0000-0000000000
spring.security.oauth2.client.registration.google.scope=email,profile
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/api/v1/auth/oauth2/callback/google

# Frontend Configuration
frontend.redirect.url=http://localhost:4200/auth/callback
```

### Local Development Setup

1. Create a `application-local.properties` file in the project root with the required environment variables
2. Start the required services using Docker Compose:
   ```bash
   docker-compose up -d
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The service will be available at `http://localhost:8081`

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── banku/
│               └── userservice/
│                   ├── aggregate/      # Aggregates (UserAggregate)
│                   ├── config/         # Configurations (Security, Kafka)
│                   ├── controller/     # REST Controllers
│                   ├── event/          # Domain Events
│                   ├── exception/      # Custom Exceptions
│                   ├── repository/     # Repositories (MongoDB, Kafka)
│                   ├── security/       # Security Configuration
│                   └── service/        # Application Services
└── test/                              # Unit and Integration Tests
```

## Event Sourcing

The service implements Event Sourcing to maintain user state. Events are stored in MongoDB and published to Kafka for inter-service communication.

### Events

- `UserCreatedEvent`: When a new user is created
- `UserUpdatedEvent`: When an existing user is updated
- `UserDeletedEvent`: When a user is deleted
- `UserLoginEvent`: When a user has logged in

### Event Store

Events are stored in the `user_events` collection in MongoDB, maintaining a complete history of changes.

## Kafka

The service uses Kafka to publish events that can be consumed by other services.

### Configuration

- Topic: `banku.user`
- Partitions: 1
- Replication Factor: 1

### Events in Kafka

Events are published to the `banku.user` topic with the following structure:
- Key: Aggregate ID (user)
- Value: Serialized event in JSON with type information

## API Documentation

The service provides Swagger UI for API documentation at:
- Swagger UI: `http://localhost:8081/api/v1/users/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/api/v1/users/v3/api-docs`

## Development

### Requirements

- Java 17
- Docker
- Docker Compose

### Local Execution

1. Clone the repository
2. Run `docker-compose up -d` to start MongoDB and Kafka
3. Run the application with `./mvnw spring-boot:run`

### Tests

Run tests with:
```bash
./mvnw test
```

## License

This project is private and confidential.
