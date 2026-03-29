# Production-Grade Task Manager REST API

Build a complete, production-grade **Task Manager REST API** using Spring Boot 3.5, JDK 24, and Maven — with JWT authentication, layered architecture, database persistence, and DevOps-ready containerization.

---

## User Review Required

> [!IMPORTANT]
> **Database Choice**: This plan uses **MySQL**. If you prefer PostgreSQL, let me know and I'll adjust. You'll need MySQL installed locally or running via Docker.

> [!IMPORTANT]
> **Use Case**: I've chosen a **Task Manager** application (projects, tasks, users). It covers all required patterns (CRUD, auth, relations, filtering) while being easy to understand. Confirm or suggest an alternative (e-commerce, stock tracker, etc.).

> [!WARNING]
> **Sections 3–6 from your image were cut off.** I've inferred DevOps requirements (Docker, CI/CD, monitoring). Please confirm if you had specific requirements for those sections.

---

## Tech Stack & Versions

| Component | Version | Notes |
|---|---|---|
| JDK | 24.0.2 | Already installed |
| Maven | 3.9.14 | Already installed |
| Spring Boot | 3.5.13 | Latest supported, JDK 24 compatible |
| Spring Security | 6.x | Bundled with Spring Boot 3.5 |
| JJWT | 0.13.0 | JWT token generation & validation |
| MySQL | 8.x | Relational database |
| springdoc-openapi | 2.8.16 | Swagger UI & OpenAPI 3 docs |
| JUnit 5 + Mockito | Latest | Bundled with spring-boot-starter-test |
| Docker | Latest | Containerization |
| Lombok | Latest | Boilerplate reduction |

---

## Project Folder Structure

```
Java Project/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── .env
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/api/
│   │   │   ├── TaskManagerApplication.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java          # Spring Security + JWT config
│   │   │   │   ├── OpenApiConfig.java            # Swagger/OpenAPI config
│   │   │   │   └── AppConfig.java                # General beans (PasswordEncoder, etc.)
│   │   │   │
│   │   │   ├── security/
│   │   │   │   ├── JwtService.java               # Token generation & validation
│   │   │   │   ├── JwtAuthenticationFilter.java   # OncePerRequestFilter
│   │   │   │   └── CustomUserDetailsService.java  # Load user from DB
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java           # Register, login, refresh
│   │   │   │   ├── TaskController.java           # Task CRUD + filtering
│   │   │   │   └── ProjectController.java        # Project CRUD
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── TaskService.java
│   │   │   │   └── ProjectService.java
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── TaskRepository.java
│   │   │   │   └── ProjectRepository.java
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── entity/
│   │   │   │   │   ├── User.java                 # JPA entity
│   │   │   │   │   ├── Task.java
│   │   │   │   │   ├── Project.java
│   │   │   │   │   └── enums/
│   │   │   │   │       ├── TaskStatus.java       # TODO, IN_PROGRESS, DONE
│   │   │   │   │       ├── TaskPriority.java     # LOW, MEDIUM, HIGH, CRITICAL
│   │   │   │   │       └── Role.java             # USER, ADMIN
│   │   │   │   │
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   │   ├── TaskRequest.java
│   │   │   │   │   │   └── ProjectRequest.java
│   │   │   │   │   │
│   │   │   │   │   └── response/
│   │   │   │   │       ├── AuthResponse.java     # JWT tokens
│   │   │   │   │       ├── TaskResponse.java
│   │   │   │   │       ├── ProjectResponse.java
│   │   │   │   │       ├── PagedResponse.java    # Generic paged wrapper
│   │   │   │   │       └── ApiErrorResponse.java # Standard error format
│   │   │   │   │
│   │   │   │   └── mapper/
│   │   │   │       ├── TaskMapper.java           # Entity ↔ DTO conversion
│   │   │   │       └── ProjectMapper.java
│   │   │   │
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java   # @RestControllerAdvice
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── DuplicateResourceException.java
│   │   │       └── UnauthorizedException.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml                   # Main config
│   │       ├── application-dev.yml               # Dev profile
│   │       ├── application-prod.yml              # Prod profile
│   │       └── logback-spring.xml                # Structured logging config
│   │
│   └── test/
│       └── java/com/taskmanager/api/
│           ├── controller/
│           │   ├── AuthControllerTest.java       # MockMvc integration tests
│           │   └── TaskControllerTest.java
│           ├── service/
│           │   ├── AuthServiceTest.java          # Unit tests with Mockito
│           │   └── TaskServiceTest.java
│           └── repository/
│               └── TaskRepositoryTest.java       # @DataJpaTest
```

---

## Proposed Changes — Implementation Phases

### Phase 1: Project Scaffolding & Database Setup

#### [NEW] pom.xml
Maven project configuration with all dependencies:
- `spring-boot-starter-web` — REST API
- `spring-boot-starter-data-jpa` — JPA/Hibernate
- `spring-boot-starter-security` — Spring Security
- `spring-boot-starter-validation` — Bean validation (Jakarta)
- `mysql-connector-j` — MySQL driver
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` — JWT support (v0.13.0)
- `springdoc-openapi-starter-webmvc-ui` — Swagger UI (v2.8.16)
- `lombok` — Boilerplate reduction
- `spring-boot-starter-test` — JUnit 5 + Mockito + MockMvc
- `h2` (test scope) — In-memory DB for tests
- Maven plugins: `spring-boot-maven-plugin`, `maven-surefire-plugin`

#### [NEW] application.yml / application-dev.yml / application-prod.yml
- Database connection (MySQL), JPA/Hibernate settings
- JWT secret key + expiration config
- Logging levels, server port
- Profile-specific overrides (H2 for dev/test, MySQL for prod)

#### [NEW] logback-spring.xml
- Structured JSON logging for production profile
- Console logging for dev profile
- Log rotation policies

---

### Phase 2: Entity Models & Repositories

#### [NEW] Entity classes (User, Task, Project, Enums)
- JPA annotations (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`)
- Relationships: `User` → many `Projects` → many `Tasks`
- Audit fields (`createdAt`, `updatedAt`) via `@CreationTimestamp` / `@UpdateTimestamp`
- `User` entity implements `UserDetails` for Spring Security integration
- Enum types: `TaskStatus`, `TaskPriority`, `Role`

#### [NEW] Repository interfaces
- Extend `JpaRepository` for built-in CRUD + pagination
- Custom query methods with `@Query` for filtering (by status, priority, assignee)
- `JpaSpecificationExecutor` on `TaskRepository` for dynamic filtering

---

### Phase 3: Security & JWT Authentication

#### [NEW] SecurityConfig.java
- `SecurityFilterChain` bean with:
  - CSRF disabled (stateless API)
  - CORS configured
  - Stateless session management
  - Permit `/api/auth/**` and Swagger endpoints
  - All other endpoints authenticated
  - JWT filter registered before `UsernamePasswordAuthenticationFilter`

#### [NEW] JwtService.java
- Token generation (access token: 15 min, refresh token: 7 days)
- Token validation & claims extraction
- HMAC-SHA256 signing with configurable secret

#### [NEW] JwtAuthenticationFilter.java
- `OncePerRequestFilter` implementation
- Extract Bearer token from `Authorization` header
- Validate and set `SecurityContext`

#### [NEW] CustomUserDetailsService.java
- Load `User` entity by email from database
- Convert to Spring Security `UserDetails`

#### [NEW] AuthController + AuthService
- `POST /api/auth/register` — Create user, return JWT
- `POST /api/auth/login` — Authenticate, return JWT
- `POST /api/auth/refresh` — Refresh access token
- Password encoding with BCrypt

---

### Phase 4: Business Logic (CRUD + Pagination + Filtering)

#### [NEW] DTOs (Request/Response classes)
- Request DTOs with Jakarta validation annotations (`@NotBlank`, `@Email`, `@Size`, etc.)
- Response DTOs (clean API contract, no entity leakage)
- `PagedResponse<T>` — generic wrapper for paginated results

#### [NEW] Mapper classes
- Static methods to convert Entity ↔ DTO
- Clean separation of internal and external representations

#### [NEW] TaskController + TaskService
- `GET /api/tasks` — List tasks with pagination & filtering (status, priority, search)
- `GET /api/tasks/{id}` — Get task by ID
- `POST /api/tasks` — Create task (with validation)
- `PUT /api/tasks/{id}` — Update task
- `DELETE /api/tasks/{id}` — Delete task
- Tasks scoped to authenticated user

#### [NEW] ProjectController + ProjectService
- Full CRUD for projects
- Projects owned by authenticated user
- Nested task listing per project

---

### Phase 5: Exception Handling & Validation

#### [NEW] GlobalExceptionHandler.java (`@RestControllerAdvice`)
- Handle `ResourceNotFoundException` → 404
- Handle `DuplicateResourceException` → 409
- Handle `MethodArgumentNotValidException` → 400 (field-level errors)
- Handle `AccessDeniedException` → 403
- Handle `AuthenticationException` → 401
- Handle generic `Exception` → 500
- Consistent `ApiErrorResponse` format with timestamp, status, message, details

#### [NEW] Custom exception classes
- `ResourceNotFoundException`
- `DuplicateResourceException`
- `UnauthorizedException`

---

### Phase 6: API Documentation (Swagger/OpenAPI)

#### [NEW] OpenApiConfig.java
- OpenAPI 3.0 metadata (title, description, version, contact)
- Security scheme: Bearer JWT
- Group APIs by tag (Auth, Tasks, Projects)
- Controller methods annotated with `@Operation`, `@ApiResponse`

**Access**: Swagger UI at `http://localhost:8080/swagger-ui.html`

---

### Phase 7: Testing

#### [NEW] Unit Tests (Service Layer)
- `TaskServiceTest` — Mockito mocks for repository, test all CRUD + edge cases
- `AuthServiceTest` — Test registration, login, token generation

#### [NEW] Integration Tests (Controller Layer)
- `TaskControllerTest` — `@SpringBootTest` + `MockMvc`, test endpoints end-to-end
- `AuthControllerTest` — Test auth flow with actual HTTP requests
- Use `@ActiveProfiles("test")` with H2 in-memory database

#### [NEW] Repository Tests
- `TaskRepositoryTest` — `@DataJpaTest`, test custom queries

---

### Phase 8: Docker & Deployment

#### [NEW] Dockerfile
```dockerfile
# Multi-stage build
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### [NEW] docker-compose.yml
- `app` service: Spring Boot container
- `db` service: MySQL 8 container with volume persistence
- Network linking, environment variables, health checks

#### [NEW] .env
- Database credentials, JWT secret (template, not committed)

---

## API Endpoints Summary

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | ❌ |
| POST | `/api/auth/login` | Login, get JWT | ❌ |
| POST | `/api/auth/refresh` | Refresh access token | ❌ |
| GET | `/api/tasks` | List tasks (paginated, filtered) | ✅ |
| GET | `/api/tasks/{id}` | Get task by ID | ✅ |
| POST | `/api/tasks` | Create task | ✅ |
| PUT | `/api/tasks/{id}` | Update task | ✅ |
| DELETE | `/api/tasks/{id}` | Delete task | ✅ |
| GET | `/api/projects` | List user's projects | ✅ |
| GET | `/api/projects/{id}` | Get project by ID | ✅ |
| POST | `/api/projects` | Create project | ✅ |
| PUT | `/api/projects/{id}` | Update project | ✅ |
| DELETE | `/api/projects/{id}` | Delete project | ✅ |

---

## Open Questions

> [!IMPORTANT]
> 1. **Database**: MySQL or PostgreSQL? (Plan defaults to MySQL)
> 2. **Use case**: Task Manager OK, or do you prefer e-commerce / stock tracking?
> 3. **Sections 3–6** from your requirements image were cut off — did they include CI/CD (GitHub Actions/Jenkins), monitoring (Prometheus/Grafana), or cloud deployment (AWS/Azure)? If so, I'll add those phases.
> 4. **Do you have MySQL installed locally**, or should we run it via Docker only?

---

## Verification Plan

### Automated Tests
```bash
# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report
```

### Manual Verification
1. Start app with `mvn spring-boot:run`
2. Open Swagger UI at `http://localhost:8080/swagger-ui.html`
3. Register a user → Login → Use JWT to access protected endpoints
4. Test pagination: `GET /api/tasks?page=0&size=10&status=TODO`
5. Verify error responses for invalid input (400), missing resources (404), unauthorized access (401)

### Docker Verification
```bash
docker-compose up --build
# App accessible at http://localhost:8080
# MySQL at localhost:3306
```

---

## Commands Quick Reference

| Action | Command |
|--------|---------|
| **Build** | `mvn clean package` |
| **Run locally** | `mvn spring-boot:run -Dspring-boot.run.profiles=dev` |
| **Run tests** | `mvn test` |
| **Run specific test** | `mvn test -Dtest=TaskServiceTest` |
| **Docker build** | `docker build -t taskmanager-api .` |
| **Docker Compose up** | `docker-compose up -d` |
| **Docker Compose down** | `docker-compose down` |
| **View logs** | `docker-compose logs -f app` |
| **Prod JAR** | `java -jar target/taskmanager-api-1.0.0.jar --spring.profiles.active=prod` |
