# API QA Automation - Known Bugs

This document lists backend issues detected by the automated API test suite.

## Running Tests

Run the test suite and generate the Allure report:

```bash
.\gradlew test allureReport
```

## 📊 Allure Report (GitHub Pages)

The latest automated test report is published via **GitHub Pages** and can be accessed from the **Deployment** section.

🔗 **View the latest report:**  
https://zakerman110.github.io/rest-api-player-task

Tests marked with:

- `groups={"bug"}`
- `@Issue("<BUG-ID>")`

indicate confirmed defects in the system under test.

---

## Bugs Detected by Automation

| #  | Issue ID                     | Description                                                                               |
|----|------------------------------|-------------------------------------------------------------------------------------------|
| 1  | CREATE-PLAYER-CONTRACT       | Create player response does not match the expected API contract                           |
| 2  | CREATE-PLAYER-RESPONSE       | Create player response contains incorrect or inconsistent data                            |
| 3  | GET-PLAYER-STRING-ID         | Player can be retrieved using String id, but should be Integer                            |
| 4  | SPEC-AGE-TOP-BOUNDARY        | Player age boundary validation does not follow specification                              |
| 5  | DUPLICATE-SCREEN-NAME-PLAYER | Duplicate unique fields (login/screenName) are not properly handled                       |
| 6  | CREATE-PLAYER-PASSWORD       | Password validation rules are not enforced correctly                                      |
| 7  | CREATE-PLAYER-GENDER-VALUE   | Gender field accepts invalid values                                                       |
| 8  | GET-NON-EXISTING-404         | API does not return correct response for non-existing player                              |
| 9  | DELETE-NON-EXISTING-404      | API does not return correct response when deleting non-existing player                    |
| 10 | PLAYER-CAN-DELETE            | Role-based delete permissions are incorrect                                               |
| 11 | USER-CAN-DELETE-SELF         | User self-deletion permission logic is incorrect                                          |
| 12 | PERF-GET-PLAYER              | Response time for `/player/get` for user with `supervisor` role is too long (~10 seconds) |
| 13 | API-CREATE-METHOD            | HTTP method for `/player/create/{editor}` is incorrect (should be POST but is GET)        |
| 14 | API-GET-METHOD               | HTTP method for `/player/get` is incorrect (should be GET but is POST)                    |

---

## How Bugs Are Tracked

Bugs are tracked directly in test annotations:

```java
@Issue("CREATE-PLAYER-PASSWORD")
@Test(groups = {"bug"})
