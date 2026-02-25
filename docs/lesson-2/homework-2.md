
# **6. Practice / Homework (Backend PM)**

### **Objectives**

### Mandatory:

Homework for Dassi and Joel is to configure Docker

For the others is the following:
* In POST `/register` return:
    * 409 if username already exists

* Implement `DELETE /user/{id}` to delete user

---

### Expected Behaviors

* Duplicate username → 409
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
