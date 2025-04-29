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

## Docker Compose

The project includes a Docker Compose configuration for local development:

```yaml
version: '3.8'

services:
  mongodb:
    image: mongo:6.0
    container_name: banku-mongodb
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: banku
      MONGO_INITDB_ROOT_PASSWORD: secret
      MONGO_INITDB_DATABASE: banku-user
    volumes:
      - mongo_data:/data/db

  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.0
    container_name: banku-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.3.0
    container_name: banku-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

volumes:
  mongo_data:
```

## Configuration

### MongoDB

```properties
spring.data.mongodb.uri=mongodb://banku:secret@localhost:27017/banku-user?authSource=admin
```

### Kafka

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=banku-user-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
```

## API Endpoints

### Authentication

- `POST /api/v1/auth/register`: Register new user
- `POST /api/v1/auth/login`: User login

### Users

- `GET /api/v1/users/me`: Get current user information
- `PUT /api/v1/users/me`: Update current user information
- `DELETE /api/v1/users/me`: Delete current user

## API Documentation

The service provides OpenAPI (Swagger) documentation that can be accessed through:

- Direct access: http://localhost:8081/api/v1/user/swagger-ui/index.html
- Through Gateway: http://localhost:8080/api/v1/user/swagger-ui/index.html

The API documentation includes detailed information about all endpoints, request/response schemas, and authentication requirements.

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
