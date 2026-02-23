## **8. Practice / Homework (Backend PM)**

### **Objectives**

* Implement **POST /projects** to add a project to the in-memory list.
* Implement **GET /projects/{id}** to fetch a single project.
* Optional: practice DI with `UserService` and `GET /user/{id}`.
* Extend `Project` with a `description` field.
* Optional: add `Task` with nested endpoint `/projects/{id}/tasks`.

### **Example POST Request**

```json
{
  "title":"Fourth Project",
  "description":"Homework example"
}
```

### **Example GET Response**

```json
[
  {"id":1, "title":"First Project", "description":"Demo project 1"},
  {"id":2, "title":"Second Project", "description":"Demo project 2"},
  {"id":3, "title":"Third Project", "description":"Demo project 3"}
]
```

### **Testing**

* Use **Postman** or **IntelliJ HTTP client** to test endpoints
* Ensure GET returns the correct projects
* POST adds project to in-memory list

---
