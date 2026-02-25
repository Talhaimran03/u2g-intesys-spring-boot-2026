# **Day 3 — JWT, Spring Security & OpenAPI Code Generation**

### **Focus:** Authentication vs Authorization, JWT, Spring Security Filter Chain, Stateless APIs, OpenAPI Security

**Goal of the Day:** Transform the application into a secure, stateless backend. Students will implement JWT-based authentication, protect endpoints with Spring Security, and integrate JWT into the OpenAPI contract using a contract-first approach.

By the end of the day, the backend will:

* Return a JWT token on login
* Protect all routes except `/register` and `/login`
* Validate JWT via Security Filter Chain
* Declare Bearer authentication in OpenAPI
* Regenerate code from OpenAPI contract

---

# **1. Security Foundations**

## **1.1 Authentication vs Authorization**

### Authentication

> “Who are you?”

Process of verifying identity (username + password).

### Authorization

> “What are you allowed to do?”

Determines access rights after authentication.

Example:

* Login → Authentication
* Access `/admin` → Authorization

---

## **1.2 Stateless APIs**

REST APIs are typically **stateless**:

* Server does NOT store sessions
* Each request must contain authentication data
* No HTTP Session

JWT enables stateless authentication.

---

## **1.3 What is JWT (JSON Web Token)**

JWT is a compact, URL-safe token used for authentication.

A JWT consists of:

```
HEADER.PAYLOAD.SIGNATURE
```

### Header

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Payload

```json
{
  "sub": "john",
  "iat": 1710000000,
  "exp": 1710003600
}
```

### Signature

Generated using:

```
HMACSHA256(base64Url(header) + "." + base64Url(payload), secret)
```

---

### Why JWT?

* Stateless
* Scalable
* No server session storage
* Standard across modern architectures

---

# **2. Spring Security Architecture**

## **2.1 Spring Security Filter Chain**

Spring Security works through a **Filter Chain**.

Every HTTP request passes through filters:

```
Request → Security Filter Chain → Controller
```

We will add a custom filter:

```
JwtAuthenticationFilter
```

Responsibilities:

* Read Authorization header
* Extract token
* Validate token
* Set authentication in SecurityContext

---

## **2.2 Security Configuration**

We disable default session-based security and configure:

* Stateless mode
* Permit:

   * `/register`
   * `/login`
* Protect all other endpoints

---

# **3. Implementing JWT**

## **3.1 Add Dependencies**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
</dependency>
```

---

## **3.2 JwtService**

Responsibilities:

* Generate token
* Extract username
* Validate token

Example structure:

```java
@Service
public class JwtService {

    private final String SECRET = "super-secret-key";

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }
}
```

---

## **3.3 Login Now Returns Token**

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {

    User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow();

    if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {

        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(token);
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
}
```

---

# **4. Protecting Endpoints**

## **4.1 SecurityConfig**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/login").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
```

---

## **4.2 JWT Filter Concept**

Pseudo-flow:

```java
String authHeader = request.getHeader("Authorization");

if(authHeader starts with "Bearer ") {
    extract token
    validate token
    set Authentication in SecurityContext
}
```

After that, the request proceeds normally.

---

# **5. OpenAPI — Adding Security (Contract First)**

We extend our `openapi.yaml`.

---

## **5.1 Define Security Scheme**

```yaml
components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```

---

## **5.2 Apply Security Globally**

```yaml
security:
  - bearerAuth: []
```

---

## **5.3 Exclude Public Endpoints**

For `/login` and `/register`:

```yaml
security: []
```

This overrides global security.

---

## **6. Code Generation Flow**

Now the correct professional workflow becomes:

1. Modify `openapi.yaml`
2. Generate code (DTOs + Interfaces)
3. Implement generated interfaces
4. Do NOT manually change generated code

This reinforces contract-first development.

---

# **7. In-Class (Backend PM)**

1. Add Spring Security dependency
2. Create `JwtService`
3. Modify login to return token
4. Configure `SecurityFilterChain`
5. Permit only:

* `/register`
* `/login`
6. Protect:

* `GET /user/{id}`
7. Add Bearer security scheme to OpenAPI
8. Regenerate code

---
