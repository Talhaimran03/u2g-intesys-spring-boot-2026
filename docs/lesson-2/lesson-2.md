# **Day 2 — Database Integration, Authentication & OpenAPI Introduction**

### **Focus:** PostgreSQL, JPA, User Registration/Login, Docker Integration, OpenAPI Contract-First

**Goal of the Day:** Move from in-memory backend to a real database-backed application. Students will connect Spring Boot to PostgreSQL (auto-started via Docker), persist users, implement secure password hashing, and introduce OpenAPI as a contract-first API design approach.

By the end of the day, the backend will:

* Automatically start a PostgreSQL container
* Persist users in the database
* Implement `POST /register`
* Implement `POST /login`
* Hash passwords securely
* Define API contracts using OpenAPI

---

## **1. Persistence & Database Integration**

### **1.1 Why Persistence Matters**

In Day 1, our data was stored in memory.
When the application stopped, data was lost.

Real backend applications require:

* Persistent storage
* Reliable database systems
* Reproducible environments

---

### **1.2 Docker & Reproducible Environments**

Instead of manually installing PostgreSQL, we use **Docker containers**.

With Spring Boot 3+, the application can automatically start a `docker-compose.yml` file when the app runs.

This allows:

* No manual `docker run`
* No local database setup
* Consistent environment for every developer

---

### **1.3 Docker Compose Configuration**

Create a `docker-compose.yml` in the project root:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    container_name: teamboard-db
    environment:
      POSTGRES_DB: teamboard
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin
    ports:
      - "5432:5432"
```

---

### **1.4 Spring Boot Docker Compose Integration**

Add dependency:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-docker-compose</artifactId>
</dependency>
```

When the application starts:

* Spring Boot detects `docker-compose.yml`
* Automatically starts PostgreSQL
* Connects to it

---

### **1.5 Database Configuration**

`application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/teamboard
    username: admin
    password: admin
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

## **2. JPA & Entity Mapping**

### **2.1 JPA Overview**

Spring Data JPA simplifies database interaction.

Key annotations:

* `@Entity` → maps class to database table
* `@Id` → primary key
* `@GeneratedValue` → auto-generated ID
* `@Table` → custom table name

---

### **2.2 User Entity**

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String email;

    @Column(unique = true)
    private String username;

    private String password;
}
```

---

### **2.3 Repository Layer**

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

`JpaRepository` provides:

* save()
* findById()
* findAll()
* delete()

---

## **3. Authentication Basics**

### **3.1 Why We Never Store Plain Passwords**

Passwords must never be stored as plain text.

If a database is compromised:

* Plain passwords → security breach
* Hashed passwords → protected

---

### **3.2 Hashing with BCrypt**

Add dependency:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Define bean:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

### **3.3 Register Endpoint**

```java
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody User user) {

    if(userRepository.findByUsername(user.getUsername()).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);

    return ResponseEntity.ok().build();
}
```

---

### **3.4 Login Endpoint**

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {

    User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow();

    if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        return ResponseEntity.ok("Login successful");
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
}
```

---

## **4. OpenAPI — Contract First Development**

### **4.1 What is OpenAPI**

OpenAPI is a standard specification to describe REST APIs.

Instead of writing controllers first:
We define the API contract in YAML.

This enables:

* Backend and frontend alignment
* Code generation
* Clear documentation
* Professional development workflow

---

### **4.2 Basic OpenAPI File**

Create:

`src/main/resources/openapi.yaml`

```yaml
openapi: 3.0.3
info:
  title: TeamBoard API
  version: 1.0.0

paths:
  /register:
    post:
      summary: Register user

  /login:
    post:
      summary: Login user
```

---

### **4.3 Why Contract-First**

From now on:

> APIs are defined before implementation.

This approach:

* Prevents frontend/backend misalignment
* Makes APIs self-documented
* Improves scalability

---

## **5. In-Class (Backend PM)**

1. Add Docker Compose integration
2. Configure PostgreSQL connection
3. Create `User` entity
4. Create `UserRepository`
5. Implement:

   * `POST /register`
   * `POST /login`
6. Verify password hashing in database
7. Create initial `openapi.yaml`

---
