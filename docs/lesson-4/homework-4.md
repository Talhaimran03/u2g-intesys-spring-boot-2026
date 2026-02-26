
# **9. Practice / Homework (Backend PM)**

## **Objectives**

### Mandatory

* Add validation:
  * Only owner can delete project

* Implement pagination for `GET /project`

  * Use `Pageable`
  * Return page metadata
* Add sorting by title
* Add `createdAt` timestamp

---

## **Expected Behavior**

* Create project → owner auto-assigned from JWT
* Delete by non-owner → 403
* Sort by title

---

## **Example POST Request**

```json
{
  "title": "New Project",
  "description": "Backend implementation"
}
```

---

## **Example Response**

```json
{
  "id": 1,
  "title": "New Project",
  "description": "Backend implementation",
  "ownerUsername": "john"
}
```

---
