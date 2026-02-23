# **Day 1 — Java Foundations, Maven & Spring Boot Kickoff + Dependency Injection**

### **Focus:** Core Java, Maven, Spring Boot, REST APIs, and Dependency Injection

**Goal of the Day:** Build the foundation for backend development. Students will understand how Java works, how OOP models reality, and how a backend exposes data through REST APIs. By the end of the day, the backend will be able to serve a **hardcoded list of projects** and accept POST requests.

---

## **1. Java Theory Recap**

### **1.1 Compiled Language & Platform Independence**

Java is both **compiled** and **interpreted**.

* **Compilation**: Source code (`.java`) is compiled by `javac` into **bytecode** (`.class`).
* **Execution**: The **JVM (Java Virtual Machine)** translates bytecode into machine code for the specific operating system.

#### Key Benefit

**WORA — Write Once, Run Anywhere**

The same `.class` file can run on any system (Windows, macOS, Linux) as long as a compatible JVM is installed.


### **1.2 JDK, JRE, JVM**

* **JVM (Java Virtual Machine)** executes Java bytecode. Handles memory management, garbage collection, threads, platform independence.
* **JRE (Java Runtime Environment)**: JVM + core Java libraries. Allows running Java programs.
* **JDK (Java Development Kit)**: JRE + compiler (`javac`) + development tools. Needed to build Java apps.

### **1.3 Memory Management**

* **Stack:** fast, stores primitive types, method calls, local variables.
* **Heap:** stores objects and class instances. Managed by the Garbage Collector.
* Example:

```java
Project project = new Project(1L, "Demo", "Description");
```

Reference is on stack, object lives on heap.

### **1.4 Object-Oriented Programming (OOP)**

* **Encapsulation:** Hide internal state, expose controlled access via getters/setters.
* **Inheritance:** Extend a class to reuse behavior.
* **Polymorphism:** Same method signature can behave differently depending on object type.

### **1.5 SOLID Principles (SRP focus)**

* Each class should have **one reason to change**.
* Example:

```java
// Bad example
class Project {
    saveToDatabase();
    sendEmail();
    calculateStatistics();
}

// Good example
class Project { /* data only */ }
class ProjectService { /* business logic */ }
class ProjectRepository { /* data access */ }
```

---

## **2. How the Web Works**

* **Client-Server model:** Frontend (React) sends HTTP → Backend (Spring Boot) responds JSON.
* **Stateless:** Each request is independent.
* **HTTP Methods:** 
  - GET
  - POST
  - PUT
  - PATCH
  - DELETE

---

## **3. Maven Basics**

* `pom.xml`: defines dependencies, plugins, and project metadata.
* Maven lifecycle: `clean`, `compile`, `install`.
* Run Spring Boot app: `mvn spring-boot:run` or via IDE.

---

## **4. Spring Boot Overview**

Spring Boot simplifies the development of Spring applications by following the **"Convention over Configuration"** philosophy, allowing you to create stand-alone applications with minimal effort.

* **Rapid REST API Development:** Automatic configuration (defaults) lets you focus on business logic instead of boilerplate code.
* **Dependency Management:** Automatically handles libraries and versions through Maven/Gradle **Starters**.
* **Embedded Server:** Includes Tomcat internally; the app is an executable JAR file ready to run.
* **Structure:** Based on `@SpringBootApplication` (entry point) and `@RestController` (HTTP request handling).

---

### **IoC & Dependency Injection (DI)**

Spring acts as a "container" that manages the lifecycle of your objects (**Beans**).

* **Inversion of Control (IoC):** Spring creates and manages objects for you, rather than you instantiating them manually.
* **Dependency Injection (DI):** The mechanism by which Spring "delivers" dependencies to the classes that need them.
* **Constructor Injection:** The recommended method for injecting services into controllers.
* **`@Autowired` Annotation:** Tells Spring to automatically inject the required dependencies into the constructor.

> **Tip:** DI allows you to **swap implementations** easily and keeps classes focused on a **single responsibility**, significantly improving testability.

---

## **5. Our First Domain Models**

* **Project**

  ```java
  private Long id;
  private String title;
  private String description;
  ```
* **Column**

  ```java
  private Long id;
  private String title;
  ```
* **Card**

  ```java
  private Long id;
  private String title;
  private String description;
  private User assignedTo;
  ```

> Note: These are **POJOs** (Plain Old Java Objects).

---

## **6. Creating Our First REST Controller**

```java
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PostMapping
    public Project addProject(@RequestBody Project project) {
        return projectService.addProject(project);
    }
}
```

* `@RestController`: handles HTTP requests, returns JSON
* `@RequestMapping("/projects")`: base path
* DI: `ProjectService` injected into controller

---

## **7. In-Class (Backend PM)**

1. **Initialize project** via [Spring Initializr](https://start.spring.io/):
    * Java 21
    * Spring Boot 3.4
    * Dependencies: Spring Web
2. **Create in-memory models:** `User`, `Project`
3. **Expose simple endpoint**:

    * `GET /projects` returning `List.of(...)` with 3 hardcoded projects
4. **Demonstrate Dependency Injection**

    * Inject a `ProjectService` into `ProjectController`
5. Optional: `GET /user/{id}` endpoint

---

