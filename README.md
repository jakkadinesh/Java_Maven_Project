# 🗂️ Task Manager API

A **production-grade REST API** for task and project management built with Spring Boot 3.5, JDK 24, and MySQL. Features JWT authentication, role-based access control, pagination, filtering, and full Docker support.

[![Java](https://img.shields.io/badge/Java-24-orange?logo=openjdk)](https://jdk.java.net/24/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📑 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [API Endpoints](#-api-endpoints)
- [Authentication](#-authentication)
- [Environment Variables](#-environment-variables)
- [Docker](#-docker)
- [Health Check & Monitoring](#-health-check--monitoring)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)

---

## ✨ Features

- **JWT Authentication** — Secure stateless auth with access & refresh tokens (HS256)
- **Role-Based Access Control** — `ROLE_USER` and `ROLE_ADMIN` roles
- **CRUD Operations** — Full create, read, update, delete for Tasks and Projects
- **Pagination & Sorting** — Configurable page size, sort field, and direction
- **Advanced Filtering** — Filter tasks by status, priority, project, and search by title
- **Global Exception Handling** — Consistent error responses across all endpoints
- **Input Validation** — Jakarta Bean Validation on all request DTOs
- **API Documentation** — Interactive Swagger UI with OpenAPI 3.0
- **Health Checks** — Custom Spring Boot Actuator health indicator with DB connectivity checks
- **Metrics & Monitoring** — Prometheus-compatible metrics endpoint
- **Dockerized** — Multi-stage Docker build with Docker Compose orchestration
- **Production-Ready Profiles** — Separate `dev` and `prod` Spring profiles

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 24 |
| **Framework** | Spring Boot 3.5.13 |
| **Security** | Spring Security + JWT (JJWT 0.13.0) |
| **Database** | MySQL 8.0 |
| **ORM** | Spring Data JPA / Hibernate 6 |
| **Build Tool** | Apache Maven |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) |
| **Monitoring** | Spring Boot Actuator + Micrometer + Prometheus |
| **Containerization** | Docker + Docker Compose |
| **Code Generation** | Lombok |

---

## 🏗️ Architecture

The project follows a **layered architecture** pattern:

```
┌──────────────────────────────────────────┐
│              Controller Layer            │  ← REST endpoints
├──────────────────────────────────────────┤
│               Service Layer              │  ← Business logic
├──────────────────────────────────────────┤
│             Repository Layer             │  ← Data access (JPA)
├──────────────────────────────────────────┤
│                 MySQL DB                 │  ← Persistence
└──────────────────────────────────────────┘
```

**Cross-cutting concerns:**
- `Security` — JWT filter chain + Spring Security
- `Exception Handling` — Global `@ControllerAdvice`
- `Validation` — Jakarta Bean Validation
- `Mapper` — Entity ↔ DTO conversion

---

## 🚀 Getting Started

### Prerequisites

- **JDK 24** — [Download](https://jdk.java.net/24/)
- **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)
- **Docker & Docker Compose** — [Download](https://www.docker.com/products/docker-desktop/)

### Option 1: Run with Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/jakkadinesh/Java_Maven_Project.git
cd Java_Maven_Project

# Start both MySQL & the API
docker compose up -d

# Check health
curl http://localhost:8080/actuator/health
```

### Option 2: Run Locally

```bash
# 1. Start MySQL (via Docker or local install)
docker run -d --name mysql-local \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=taskmanager_db \
  -e MYSQL_USER=taskmanager \
  -e MYSQL_PASSWORD=taskmanager123 \
  -p 3306:3306 mysql:8.0

# 2. Build & run the application
mvn clean package -DskipTests
java -jar target/taskmanager-api-1.0.0.jar
```

### Access Points

| Service | URL |
|---|---|
| **API Base** | `http://localhost:8080` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI JSON** | `http://localhost:8080/api-docs` |
| **Health Check** | `http://localhost:8080/actuator/health` |
| **Metrics** | `http://localhost:8080/actuator/metrics` |
| **Prometheus** | `http://localhost:8080/actuator/prometheus` |

---

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a new user | ❌ |
| `POST` | `/api/auth/login` | Login and get JWT tokens | ❌ |
| `POST` | `/api/auth/refresh` | Refresh access token | ❌ |

### Tasks

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/tasks` | List tasks (paginated, filterable) | ✅ |
| `GET` | `/api/tasks/{id}` | Get task by ID | ✅ |
| `POST` | `/api/tasks` | Create a new task | ✅ |
| `PUT` | `/api/tasks/{id}` | Update an existing task | ✅ |
| `DELETE` | `/api/tasks/{id}` | Delete a task | ✅ |

**Task Query Parameters:**

| Parameter | Type | Default | Description |
|---|---|---|---|
| `page` | int | `0` | Page number (0-indexed) |
| `size` | int | `10` | Items per page |
| `sortBy` | string | `createdAt` | Sort field |
| `direction` | string | `desc` | Sort direction (`asc`/`desc`) |
| `status` | enum | — | Filter: `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`, `CANCELLED` |
| `priority` | enum | — | Filter: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| `projectId` | long | — | Filter by project |
| `search` | string | — | Search in task title |

### Projects

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/projects` | List projects (paginated) | ✅ |
| `GET` | `/api/projects/{id}` | Get project by ID | ✅ |
| `POST` | `/api/projects` | Create a new project | ✅ |
| `PUT` | `/api/projects/{id}` | Update a project | ✅ |
| `DELETE` | `/api/projects/{id}` | Delete a project | ✅ |

---

## 🔑 Authentication

The API uses **JWT (JSON Web Tokens)** for stateless authentication.

### 1. Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Dinesh Jakka",
    "email": "dinesh@example.com",
    "password": "Password123!"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dinesh@example.com",
    "password": "Password123!"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

### 3. Use Protected Endpoints

```bash
curl http://localhost:8080/api/tasks \
  -H "Authorization: Bearer <your-access-token>"
```

### 4. Refresh Token

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "<your-refresh-token>"}'
```

| Token | Expiration |
|---|---|
| Access Token | 15 minutes |
| Refresh Token | 7 days |

---

## ⚙️ Environment Variables

Create a `.env` file in the project root:

```env
# MySQL
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=taskmanager_db
MYSQL_USER=taskmanager
MYSQL_PASSWORD=taskmanager123

# JWT Secret (Base64-encoded, min 256 bits for HS256)
JWT_SECRET=bXktc3VwZXItc2VjcmV0LWtleS10aGF0LWlzLWF0LWxlYXN0LTI1Ni1iaXRzLWxvbmctZm9yLWhzMjU2LXNpZ25pbmc=
```

> ⚠️ **Important:** Never commit production secrets. Use a secrets manager in production environments.

---

## 🐳 Docker

### Docker Compose Services

| Service | Container Name | Port | Description |
|---|---|---|---|
| `db` | `taskmanager-mysql` | `3000:3306` | MySQL 8.0 database |
| `app` | `taskmanager-api` | `8080:8080` | Spring Boot application |

### Commands

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f app

# Check container health
docker compose ps

# Stop all services
docker compose down

# Stop and remove volumes (reset DB)
docker compose down -v

# Rebuild after code changes
docker compose up -d --build
```

### Multi-Stage Dockerfile

The Dockerfile uses a **multi-stage build** for optimized image size:

1. **Build stage** — `maven:3.9-eclipse-temurin-24` compiles and packages the JAR
2. **Run stage** — `eclipse-temurin:24-jre-alpine` runs the app with minimal footprint

Features:
- Non-root user execution (`appuser`)
- JVM container-aware tuning (`-XX:+UseContainerSupport`, `-XX:MaxRAMPercentage=75.0`)
- Dependency caching for faster rebuilds
- `curl` installed for Docker health checks

---

## 🏥 Health Check & Monitoring

### Health Check Endpoint

```bash
curl http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" },
    "taskManagerHealth": {
      "status": "UP",
      "details": {
        "service": "Task Manager API",
        "database": "MySQL — reachable",
        "database_product": "MySQL",
        "database_version": "8.0.45"
      }
    }
  }
}
```

### Docker Health Checks

Both services have Docker health checks configured:

- **MySQL** — `mysqladmin ping` every 10s
- **API** — `curl /actuator/health` every 15s (45s start period for JVM warmup)

The API container only starts after MySQL reports healthy (`service_healthy` condition).

### Prometheus Metrics

Scrape metrics at `http://localhost:8080/actuator/prometheus` for integration with Grafana or other monitoring tools.

---

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test -Dspring-boot.test.randomPort=true

# Skip tests during build
mvn clean package -DskipTests
```

### Test Coverage

| Test Suite | Tests | Description |
|---|---|---|
| `AuthControllerTest` | 5 | Registration, login, duplicate email, validation |
| `TaskControllerTest` | 5 | CRUD operations, pagination, authorization |
| `TaskServiceTest` | 7 | Unit tests for business logic |
| **Total** | **17** | All passing ✅ |

Tests use an **H2 in-memory database** to avoid requiring MySQL during testing.

---

## 📁 Project Structure

```
Java_Maven_Project/
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/api/
│   │   │   ├── TaskManagerApplication.java     # Application entry point
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java         # Spring Security & CORS
│   │   │   │   ├── OpenApiConfig.java          # Swagger/OpenAPI setup
│   │   │   │   └── HealthCheckConfig.java      # Custom health indicator
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java         # Auth endpoints
│   │   │   │   ├── TaskController.java         # Task CRUD endpoints
│   │   │   │   └── ProjectController.java      # Project CRUD endpoints
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java            # Auth business logic
│   │   │   │   ├── TaskService.java            # Task business logic
│   │   │   │   └── ProjectService.java         # Project business logic
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java         # User data access
│   │   │   │   ├── TaskRepository.java         # Task data access
│   │   │   │   └── ProjectRepository.java      # Project data access
│   │   │   ├── model/
│   │   │   │   ├── entity/
│   │   │   │   │   ├── User.java               # User entity
│   │   │   │   │   ├── Task.java               # Task entity
│   │   │   │   │   ├── Project.java            # Project entity
│   │   │   │   │   └── enums/
│   │   │   │   │       ├── Role.java           # ROLE_USER, ROLE_ADMIN
│   │   │   │   │       ├── TaskStatus.java     # TODO → DONE, CANCELLED
│   │   │   │   │       └── TaskPriority.java   # LOW → CRITICAL
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/                # Input validation DTOs
│   │   │   │   │   └── response/               # API response DTOs
│   │   │   │   └── mapper/                     # Entity ↔ DTO mappers
│   │   │   ├── security/
│   │   │   │   ├── JwtService.java             # JWT token generation/validation
│   │   │   │   ├── JwtAuthenticationFilter.java # HTTP filter for JWT
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java # @ControllerAdvice
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── DuplicateResourceException.java
│   │   │       └── UnauthorizedException.java
│   │   └── resources/
│   │       ├── application.yml                 # Shared configuration
│   │       ├── application-dev.yml             # Dev profile (debug logging)
│   │       ├── application-prod.yml            # Prod profile (optimized)
│   │       └── logback-spring.xml              # Logging configuration
│   └── test/                                   # Integration & unit tests
├── Dockerfile                                  # Multi-stage Docker build
├── docker-compose.yml                          # MySQL + App orchestration
├── pom.xml                                     # Maven dependencies & config
├── .env                                        # Environment variables
└── README.md
```

---

## 🤝 Contributing

1. **Fork** the repository
2. **Create** your feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

<p align="center">
  Built with ❤️ by <a href="https://github.com/jakkadinesh">Dinesh Jakka</a>
</p>
