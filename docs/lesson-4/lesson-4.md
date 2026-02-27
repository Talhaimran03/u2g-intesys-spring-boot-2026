# **Day 4 — Project Entity, Relationships & Full CRUD**

### **Focus:** JPA Relationships, Owner Concept, DTO Pattern, Full CRUD Operations, Authorization Rules

**Goal of the Day:** Move from user-only security to a real domain model. Students will implement a persistent `Project` entity, connect it to `User` through a relationship, build a complete CRUD API, and apply ownership-based authorization rules.

By the end of the day, the backend will:

* Persist `Project` entities in PostgreSQL
* Link each project to an owner (`User`)
* Expose full CRUD endpoints
* Prevent non-owners from deleting projects
* Update OpenAPI and regenerate code
* Apply validation rules

---

# **1. JPA Relationships & Domain Modeling**

## **1.1 Why Relationships Matter**

Real applications are not flat.

A project:

* Has an owner
* May have members
* May contain tasks

We start with the most important relation:

> A Project belongs to one User (owner)

---

## **1.2 `@ManyToOne` Relationship**

In JPA:

* Many projects → One user
* Database: foreign key column

Example:

```java
@ManyToOne
@JoinColumn(name = "owner_id", nullable = false)
private User owner;
```

This creates:

```
project.owner_id → users.id
```

---

## **1.3 Owner Concept**

The owner:

* Is the creator of the project
* Has deletion rights
* Controls project lifecycle

We extract the username from JWT and assign the owner automatically.

Users must NOT send owner ID manually.

---

# **2. DTO vs Entity**

## **2.1 Why Not Expose Entities?**

Entities:

* Represent database structure
* May contain sensitive data
* Can create circular references
* Should not define API contract

Instead, we use:

> DTO (Data Transfer Object)

---

## **2.2 Example**

### Bad Practice

```java
public Project create(Project project)
```

### Correct Practice

```java
public ProjectResponse create(ProjectRequest request)
```

---

## **2.3 Project DTOs**

### ProjectRequest

```java
private String title;
private String description;
```

### ProjectResponse

```java
private Long id;
private String title;
private String description;
private String ownerUsername;
```

---

# **3. Project Entity**

```java
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
```

---

# **4. Full CRUD Implementation**

We now implement:

* `GET /project`
* `GET /project/{id}`
* `POST /project`
* `PUT /project/{id}`
* `DELETE /project/{id}`

---

## **4.1 GET All Projects**

```java
@GetMapping
public List<ProjectResponse> getAll() {
    return projectService.getAll();
}
```

---

## **4.2 GET Project by ID**

```java
@GetMapping("/{id}")
public ProjectResponse getById(@PathVariable Long id) {
    return projectService.getById(id);
}
```

Return:

* 404 if not found

---

## **4.3 POST Project**

* Extract username from JWT
* Load User from database
* Assign as owner
* Save project

---

## **4.4 UPDATE Project**

Only owner can update.

Steps:

1. Load project
2. Check owner username == authenticated user
3. Update fields
4. Save

---

## **4.5 DELETE Project**

Only owner can delete.

If:

* Not owner → 403 Forbidden
* Not found → 404

---

# **5. Validation**

## **5.1 Title Mandatory**

Use Bean Validation:

```java
@NotBlank
private String title;
```

Controller:

```java
public ResponseEntity<?> create(@Valid @RequestBody ProjectRequest request)
```

If invalid:

* Return 400 Bad Request

---

# **6. Authorization Rule**

Inside service:

```java
if(!project.getOwner().getUsername().equals(authenticatedUsername)) {
    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
}
```

This enforces business-level authorization.

---

# **7. Updating OpenAPI (Contract First)**

Add:

* All `/project` endpoints
* `ProjectRequest` schema
* `ProjectResponse` schema
* Security requirement (Bearer)

Then:

1. Save `openapi.yaml`
2. Regenerate code
3. Implement interfaces only

Never modify generated files.

---

# **8. In-Class (Backend PM)**

1. Create `Project` entity
2. Add `@ManyToOne` relationship with `User`
3. Create `ProjectRepository`
4. Create DTOs
5. Implement:

    * GET all
    * GET by id
    * POST
    * PUT
    * DELETE
6. Enforce owner-based deletion
7. Update OpenAPI
8. Regenerate code

---
