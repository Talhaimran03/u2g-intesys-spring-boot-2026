# **10. Practice / Homework (Backend PM)**

## **Objectives**

### Mandatory

* Implement:

  * `GET /column/{id}`
  * `PUT /column/{id}`
* Prevent column creation if project does not exist
* Update OpenAPI with full column endpoints
* Regenerate and implement interfaces correctly

---

## **Expected Behavior**

* Create column with valid project → 201
* Create column with invalid project → 404
* Delete non-existing column → 404
* Missing title → 400
* Request without token → 401

---

## **Example POST Request**

```json
{
  "title": "To Do",
  "projectId": 1
}
```

---

## **Example Response**

```json
{
  "id": 1,
  "title": "To Do",
  "projectId": 1
}
```

---

# **Extra Challenge **

* Add ordering of columns inside project

  * Add `position` field
  * Return ordered list
* Add `createdAt` timestamp
* Add endpoint:

  * `GET /project/{id}/columns`
* Optimize query using `@Query` or `JOIN FETCH`

