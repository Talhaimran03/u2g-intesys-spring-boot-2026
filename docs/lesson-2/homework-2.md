
# **6. Practice / Homework (Backend PM)**

### **Objectives**

### Mandatory:

* In POST `/register` return:
    * 409 if username already exists

* Extend `openapi.yaml` to include:
    * PUT `/user/{id}` to update user
    * DELETE `/user/{id}` to delete user

* Implement `PUT /user/{id}`
* Implement `DELETE /user/{id}`

---

### Expected Behaviors

* Duplicate username → 409
* Login with wrong password → 401
* You can update the user data and delete it (to do that, the request must be authenticated with the correct credentials)

---

### Testing

* Use Postman or IntelliJ HTTP Client
* Verify:

    * Container starts automatically
    * Database table created
    * Password is hashed
    * Correct HTTP status codes

---
