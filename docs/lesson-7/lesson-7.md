# **Day 7 — Testing**

### **Focus:** Unit Testing, Mockito, Service Layer Testing, Business Logic Verification

**Goal of the Day:**
Move from “working code” to **reliable, verifiable, and maintainable code**.
Students will understand how to test business logic properly, isolate dependencies using mocks, and validate both successful and failure scenarios.

---

# **1. Why Testing Matters**

Writing code is not enough.

Testing ensures:

* Bugs are caught early
* Business logic is protected
* Changes do not break existing features

---

# **2. Unit Test vs Integration Test**

## **2.1 Unit Test**

A unit test:

* Tests a single class in isolation
* Does not use a real database
* Does not start the Spring context
* Mocks dependencies

Example: Testing `ProjectService` with mocked `ProjectRepository`.

---

## **2.2 Integration Test**

An integration test:

* Starts the Spring context
* Uses real components
* May use a real database (or Testcontainers)
* Verifies component interaction

Today’s focus: **Unit tests for the Service layer**

---

# **3. The Test Pyramid**

The test pyramid suggests:

* Many unit tests
* Less integration tests
* Less end-to-end tests

Why?

* Unit tests are fast
* Easy to maintain
* Cheap to execute

Services are the ideal layer to test because:

* They contain business logic
* They orchestrate repositories
* They enforce rules

---

# **4. Deterministic vs Non-Deterministic Behavior**

A good unit test must be:

* Deterministic
* Repeatable
* Independent

Avoid:

* Real time (`LocalDateTime.now()` without control)
* Random values
* Real external services
* Real database

Mocks make behavior deterministic.

---

# **5. Testing Stack**

We use:

* JUnit 5
* Mockito

Dependencies in `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

# **6. Mockito Basics**

## **6.1 What is Mocking**

Mocking means:

Simulating the behavior of dependencies so we can test a class in isolation.

Example:

Instead of using a real `ProjectRepository`, we create a fake one that returns controlled data.

---

## **6.2 Core Annotations**

```java
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
```

* `@Mock` → fake dependency
* `@InjectMocks` → class under test

Example:

```java
@Mock
private ProjectRepository projectRepository;

@InjectMocks
private ProjectService projectService;
```

Mockito injects the mock into the service automatically.

---

## **6.3 Stubbing Behavior**

```java
when(projectRepository.findById(1L))
        .thenReturn(Optional.of(project));
```

This defines what the mock should return.

---

## **6.4 Verifying Interactions**

```java
verify(projectRepository).save(project);
```

Used to verify:

* Method was called
* Called with correct arguments
* Called specific number of times

---

# **7. Writing Unit Tests for Services**

We test:

* `UserService`
* `ProjectService`
* `CardService`

We mock:

* Repositories
* Security context (if needed)

We test:

* Successful execution
* Entity not found
* Unauthorized action
* Business rule violation

---

# **8. Example — Testing createProject()**

```java
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void shouldCreateProjectSuccessfully() {
        Project project = new Project(null, "Demo", "Description");

        when(projectRepository.save(any(Project.class)))
                .thenReturn(new Project(1L, "Demo", "Description"));

        Project result = projectService.createProject(project);

        assertNotNull(result.getId());
        verify(projectRepository).save(any(Project.class));
    }
}
```

---

# **9. Testing Exceptions**

Example: Entity not found.

```java
@Test
void shouldThrowWhenProjectNotFound() {

    when(projectRepository.findById(99L))
            .thenReturn(Optional.empty());

    assertThrows(ProjectNotFoundException.class,
            () -> projectService.getById(99L));
}
```

Important:

We test both:

* Success path
* Failure path

---

# **10. Testing moveCard() Thoroughly**

This method contains business logic and transaction boundaries.

We must cover:

1. Card exists → success
2. Card does not exist → exception
3. Column does not exist → exception
4. User not member → exception

---

## Example — Success Case

```java
@Test
void shouldMoveCardSuccessfully() {

    Card card = new Card(1L, "Title");
    Column target = new Column(2L, "Done");

    when(cardRepository.findById(1L))
            .thenReturn(Optional.of(card));

    when(columnRepository.findById(2L))
            .thenReturn(Optional.of(target));

   cardService.moveCard(1L, 2L);

    assertEquals(target, card.getColumn());
}
```

---

## Example — Card Not Found

```java
@Test
void shouldThrowWhenCardNotFound() {

    when(cardRepository.findById(1L))
            .thenReturn(Optional.empty());

    assertThrows(CardNotFoundException.class,
            () -> projectService.moveCard(1L, 2L));
}
```

---

# **11. In-Class (Backend PM)**

1. Add testing dependency if not present
2. Create:

   * `UserServiceTest`
   * `ProjectServiceTest`
   * `CardServiceTest`
3. Mock repositories
4. Write at least:

   * One success test per service
   * One failure test per service
5. Fully test `moveCard()`

---
