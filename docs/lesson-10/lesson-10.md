# **Day 10 — Build & Deployment Basics**

Introduction to **application packaging and deployment**.

Topics:

* What is a **build artifact**
* Maven build lifecycle (`clean`, `compile`, `package`)
* What is a **JAR file**
* Running a Spring Boot application from a JAR
* Basic concepts of **deployment**
* Overview of **Docker deployment workflow**

Goal: understand how a backend moves **from code → running application**.

---

### 1. **Build the application**

```bash
mvn clean package
```

### 2. **Locate the generated JAR**

```
target/teamboard-api.jar
```

### 3. **Run the application**

```bash
java -jar target/teamboard-api.jar
```

### 4. **Verify the API is running**

Test endpoints using:

* Browser
* Postman
* Swagger / OpenAPI UI


### **5. Optional: Run with Docker**
1. **Create a simple Dockerfile**

Example:

```dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/teamboard-api.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
```

2. **Build the Docker image**

```bash
docker build -t teamboard-api .
```

3. **Run the container**

```bash
docker run -p 8080:8080 teamboard-api
```

4. **Test the API**

Open the browser or Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

Docker allows us to:

* run the application **in a portable environment**
* ensure **consistent runtime configuration**
* simplify **deployment to servers or cloud platforms**

---
