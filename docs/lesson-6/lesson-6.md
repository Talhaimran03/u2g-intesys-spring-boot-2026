# **Day 6 — Validation, Exceptions & API Contract Alignment + @Transactional**

### **Focus:** Robust APIs, Bean Validation, Global Exception Handling, Transaction Management, OpenAPI Consistency

**Goal of the Day:**
Introducing validation, standardized error handling, proper transaction management, and strict alignment between:

> OpenAPI ↔ Controllers ↔ DTOs ↔ Entities

By the end of the day, the backend will:

* Validate all incoming requests
* Return standardized error responses
* Use `@Transactional` correctly
* Throw custom domain exceptions
* Align 100% with the OpenAPI contract

---

# **1. Why Robustness Matters**

So far, the application works.

But:

* What happens if a required field is missing?
* What if an entity does not exist?
* What if business logic fails mid-operation?

An API must:

* Validate inputs
* Fail predictably
* Return consistent error responses
* Avoid partial database writes

---

# **2. DTO-Level vs Domain-Level Validation**

## **2.1 DTO-Level Validation**

Applied to incoming requests.

Example:

```java
public class ProjectCreateRequest {

    @NotBlank
    private String title;

    @Size(max = 255)
    private String description;
}
```

Purpose:

* Validate user input early
* Avoid invalid data reaching service layer

---

## **2.2 Domain-Level Validation**

Business rules inside services.

Example:

* Only owner can delete project
* Cannot move card to non-existing column
* Cannot delete column if cards exist

These validations throw:

* `IllegalStateException`
* Custom exceptions

---

# **3. Bean Validation — `@Valid`**

To activate validation:

```java
@PostMapping
public ResponseEntity<ProjectResponse> create(
        @Valid @RequestBody ProjectCreateRequest request) {
    return ResponseEntity.ok(projectService.create(request));
}
```

Spring automatically throws:

```
MethodArgumentNotValidException
```

if validation fails.

---

# **4. Standard Constraints**

Common annotations:

* `@NotNull`
* `@NotBlank`
* `@Size(min, max)`
* `@Email`
* `@Positive`
* `@Min`, `@Max`

Example:

```java
public class RegisterRequest {

    @NotBlank
    private String username;

    @Email
    private String email;

    @Size(min = 6)
    private String password;
}
```

---

# **5. @Transactional — Why and When**

Without transactions:

* Multiple DB operations may partially succeed
* Data inconsistency risk

With `@Transactional`:

* All operations succeed OR all rollback

Example (Move Card):

```java
@Transactional
public void moveCard(MoveCardRequest request) {
    Card card = cardRepository.findById(request.getCardId())
            .orElseThrow(CardNotFoundException::new);

    Column target = columnRepository.findById(request.getTargetColumnId())
            .orElseThrow(ColumnNotFoundException::new);

    card.setColumn(target);
}
```

If any exception occurs → rollback automatically.

---

# **6. Global Exception Handling**

## **6.1 Why Not Handle Exceptions in Controllers?**

Because:

* Repetition
* Hard to maintain
* Inconsistent responses

Solution:

> `@RestControllerAdvice`

---

## **6.2 Standard Error Response Model**

Create:

```java
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path;
}
```

---

## **6.3 Implement Global Handler**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Validation Error",
                message,
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }
}
```

---

## **6.4 Handle Custom Exceptions**

Example:

```java
@ExceptionHandler(ProjectNotFoundException.class)
public ResponseEntity<ErrorResponse> handleProjectNotFound(...) {
    ...
}
```

---

# **7. Custom Domain Exceptions**

Create:

* `ProjectNotFoundException`
* `CardNotFoundException`
* `ColumnNotFoundException`

Example:

```java
public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(Long id) {
        super("Project not found with id: " + id);
    }
}
```

Service usage:

```java
projectRepository.findById(id)
        .orElseThrow(() -> new ProjectNotFoundException(id));
```

---

# **8. Aligning OpenAPI with Implementation**

Critical rule:

> The contract is the source of truth.

Update OpenAPI:

* Add 400 response
* Add 403 response
* Add 404 response
* Define ErrorResponse schema

Example:

```yaml
responses:
  '400':
    description: Validation Error
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/ErrorResponse'
```

Then:

1. Regenerate code
2. Implement new interfaces
3. Ensure controllers return documented responses

---

# **9. In-Class (Backend PM)**

1. Add validation annotations to:

    * RegisterRequest
    * ProjectCreateRequest
    * CardCreateRequest
    * MoveCardRequest
2. Add `@Valid` in controllers
3. Implement GlobalExceptionHandler
4. Create ErrorResponse model
5. Implement custom exceptions
6. Add `@Transactional` to moveCard logic
7. Update OpenAPI with error responses
8. Regenerate and align

---
