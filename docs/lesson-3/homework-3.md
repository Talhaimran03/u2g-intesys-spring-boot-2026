
# **8. Practice / Homework (Backend PM)**

## **Objectives**

### Mandatory

* Protect `GET /user/{id}`
* Implement `POST /logout`

## **Extra Challenge **

* Add token expiration handling
* Extract username from token and return it in `/me` endpoint


---

## **Example Login Response**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## **Using the Token**

Request header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## **Expected Behavior**

* Login → returns token
* Access protected endpoint without token → 401
* Access with invalid token → 401
* Access with valid token → 200

---
