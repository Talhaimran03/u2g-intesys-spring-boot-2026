# **Day 5 — Column Entity & JPA Relationships**

### **Focus:** `@OneToMany`, `FetchType`, JSON Recursion Problem, Nested Domain Modeling

**Goal of the Day:** Expand the domain model by introducing `Column` as a child entity of `Project`. Students will implement relational mapping using JPA, understand fetch strategies, solve JSON recursion issues, and expose CRUD endpoints for columns.

By the end of the day, the backend will:

* Persist `Column` entities
* Link `Column` → `Project`
* Implement CRUD endpoints for columns
* Prevent creation of columns if project does not exist
* Update OpenAPI contract
* Handle relational serialization properly

---

# **1. Domain Expansion — Project → Columns**

So far:

* User → Project (owner relationship)

Now:

> A Project contains multiple Columns.

This introduces a **parent-child relationship**.

---

# **2. JPA Relationship: `@OneToMany`**

## **2.1 Relationship Model**

* One Project → Many Columns
* One Column → One Project

Database:

```
columns.project_id → projects.id
```

---

## **2.2 Column Entity**

```java
@Entity
@Table(name = "columns")
public class Column {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

   @Column(nullable = false)
   private Integer position;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
```

---

## **2.3 Project Entity Update**

```java
@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
private List<Column> columns = new ArrayList<>();
```

* `mappedBy` indicates ownership is on `Column`
* `cascade = ALL` allows automatic persistence

---

# **3. FetchType — LAZY vs EAGER**

## **3.1 EAGER**

* Loads related entity immediately
* Can cause performance issues

## **3.2 LAZY**

* Loads relationship only when accessed
* Recommended for collections

Example:

```java
@OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
```

---

### Best Practice

* Use **LAZY** for collections
* Avoid loading entire object graphs unnecessarily

---

# **4. JSON Recursion Problem**

Problem:

Project → Columns
Column → Project

This creates infinite recursion during JSON serialization.

Example:

```
Project
  → Columns
      → Project
          → Columns
              → Project
                  ...
```

---

## Solution

Use DTOs.

Never expose entities directly.

This prevents:

* Infinite recursion
* Over-fetching
* Sensitive data leaks

---

# **5. Column DTOs**

### ColumnRequest

```java
private String title;
private Integer position;
private Long projectId;
```

### ColumnResponse

```java
private Long id;
private String title;
private Long projectId;
```

---

# **6. CRUD Endpoints**

We implement:

* `GET /column`
* `GET /column/{id}`
* `POST /column`
* `PUT /column/{id}`
* `DELETE /column/{id}`

---

## **6.1 GET All Columns**

```java
@GetMapping
public List<ColumnResponse> getAll() {
    return columnService.getAll();
}
```

---

## **6.2 GET Column by ID**

Return:

* 404 if not found

---

## **6.3 POST Column**

Steps:

1. Receive `ColumnRequest`
2. Verify project exists
3. Create Column
4. Link to Project
5. Save

If project does NOT exist:

* Return 404

---

## **6.4 UPDATE Column**

Steps:

1. Find column
2. Update title
3. Save
4. Return updated response

---

## **6.5 DELETE Column**

If:

* Column not found → 404
* Otherwise → delete

---

# **7. Validation Rules**

* Title must be required:

```java
@NotBlank
private String title;
```

* Project must exist before creation

Service-level check:

```java
Project project = projectRepository.findById(request.getProjectId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
```

---

# **8. Updating OpenAPI (Contract First)**

Add:

* `/column`
* `/column/{id}`
* `ColumnRequest`
* `ColumnResponse`
* Proper security (Bearer)

Then:

1. Save `openapi.yaml`
2. Regenerate code
3. Implement interfaces

Never modify generated classes.

---

# **9. In-Class (Backend PM)**

1. Create `Column` entity
2. Add relationship to `Project`
3. Update `Project` entity with `@OneToMany`
4. Create DTOs
5. Implement:

    * GET all
    * POST
    * DELETE
6. Prevent creation if project not found
7. Solve JSON recursion properly (via DTOs)
8. Update OpenAPI
9. Regenerate code

---
