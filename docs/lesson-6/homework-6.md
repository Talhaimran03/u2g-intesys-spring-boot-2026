# **10. Practice / Homework (Backend PM)**

## **Objectives**

### Mandatory

1. Create:
    * `CustomConflictException`
2. Implement logs, exception handling in all the `Service` classes
3. Ensure:
    * Validation errors return 400
    * Missing entity returns 404
    * Unauthorized returns 401
4. Add `@Transactional` where necessary

---

## **Testing Checklist**

* Missing title → 400
* Invalid email → 400
* Project not found → 404
* Move card to invalid column → 404
* Business rule violation → 409 or 400
* DB rollback verified (test manually)

---
