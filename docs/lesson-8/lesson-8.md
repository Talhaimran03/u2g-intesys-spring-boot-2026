# **Day 8 — Integration Testing with Spring Boot & H2**

### **Focus:** Spring Context, Real Database (H2 In-Memory), Controller Testing, End-to-End Application Flow

**Goal of the Day:**
Move from isolated unit tests (Mockito) to tests that verify:

* Real Spring context
* Real repositories
* Real database (H2 in-memory)
* Controller layer
* JSON serialization
* Validation and exception handling
* Basic security integration

By the end of the lesson, students will understand:

* The concrete difference between unit and integration tests
* When to use `@SpringBootTest`
* How to configure H2 for tests
* How to test HTTP endpoints using `MockMvc`

---

# **1. Unit vs Integration Tests**
---

## Integration Tests (Day 8)

* Spring context started
* Real repositories
* Real database (H2)
* Real dependency wiring
* Slower but more realistic

Here we test the system almost as a whole.

---

# **2. Why Integration Testing Matters**

Unit tests verify logic.

Integration tests verify:

* Correct JPA mappings
* Query correctness
* JSON serialization
* Bean validation
* Global exception handling
* Security integration

Typical bugs only integration tests catch:

* Wrong column name in JPA
* Incorrect relationship mapping (`@ManyToOne`, `@OneToMany`)
* DTO mapping errors
* Misconfigured validation
* Controller not returning expected HTTP status

---

# **3. H2 In-Memory Database**

For testing, we do NOT use PostgreSQL.

We use:

**H2 in-memory database**

Why?

* Fast
* No Docker required
* Automatically reset at each test run
* Perfect for automated pipelines

---

## 3.1 Maven Dependency

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 3.2 application-test.yml

Create:

```
src/test/resources/application-test.yml
```

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

Explanation:

* `create-drop` → schema created at startup and dropped at shutdown
* Database exists only in memory

---

# **4. Using @SpringBootTest**

For full integration testing:

```java
@SpringBootTest
@ActiveProfiles("test")
class ProjectIntegrationTest {
}
```

This:

* Starts the entire Spring context
* Uses H2
* Loads real repositories
* Loads real services

---

# **5. Testing the Repository Layer**

Example:

```java
@SpringBootTest
@ActiveProfiles("test")
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void shouldSaveAndFindProject() {

        Project project = new Project()
                .setTitle("Integration")
                .setDescription("Test");

        projectRepository.save(project);

        List<Project> projects = projectRepository.findAll();

        assertEquals(1, projects.size());
    }
}
```

Nothing is mocked here.

We are testing:

* Entity mapping
* Repository behavior
* H2 database

---

# **6. Testing Controllers with MockMvc**

We use `MockMvc` to test REST endpoints without starting a real HTTP server.

---

## 6.1 Setup

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
}
```

`@AutoConfigureMockMvc` allows us to simulate HTTP requests.

---

## 6.2 Testing POST /projects

```java
@Test
void shouldCreateProject() throws Exception {

    CreateProjectRequestApiDTO request =
            new CreateProjectRequestApiDTO()
                    .title("Integration Project")
                    .description("Test");

    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Integration Project"));
}
```

This verifies:

* Controller
* Service
* Repository
* Database
* JSON serialization

All layers together.

---

# **7. Testing Validation**

Example: Missing title should return 400.

```java
@Test
void shouldReturn400WhenTitleMissing() throws Exception {

    CreateProjectRequestApiDTO request =
            new CreateProjectRequestApiDTO()
                    .description("No title");

    mockMvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
}
```

This verifies:

* Bean Validation
* GlobalExceptionHandler
* ErrorResponse structure

---

# **8. Testing Security (Basic Concept)**

If using Spring Security:

```java
@WithMockUser(username = "testuser")
```

This allows simulation of an authenticated user.

You can test:

* 401 Unauthorized
* 403 Forbidden

---

# **9. Unit vs Integration Comparison**

| Aspect             | Unit Test | Integration Test |
| ------------------ | --------- | ---------------- |
| Spring Context     | No        | Yes              |
| Database           | Mocked    | Real (H2)        |
| Speed              | Very fast | Slower           |
| Business Logic     | Yes       | Yes              |
| JPA Mapping        | No        | Yes              |
| Controller Layer   | No        | Yes              |
| JSON Serialization | No        | Yes              |

---

# **10. In-Class Activities**

1. Configure H2 test profile
2. Create:

   * `ProjectRepositoryTest`
   * `ProjectControllerIntegrationTest`
3. Write integration tests for:

   * POST /projects
   * GET /projects
4. Write validation test (400 response)
5. Run tests and analyze SQL logs

---

