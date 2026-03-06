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

| Issue ID                     | Description                                                                               |
|------------------------------|-------------------------------------------------------------------------------------------|
| CREATE-PLAYER-CONTRACT       | Create player response does not match the expected API contract                           |
| CREATE-PLAYER-RESPONSE       | Create player response contains incorrect or inconsistent data                            |
| GET-PLAYER-STRING-ID         | Player can be retrieved using String id, but should be Integer                            |
| SPEC-AGE-TOP-BOUNDARY        | Player age boundary validation does not follow specification                              |
| DUPLICATE-SCREEN-NAME-PLAYER | Duplicate unique fields (login/screenName) are not properly handled                       |
| CREATE-PLAYER-PASSWORD       | Password validation rules are not enforced correctly                                      |
| CREATE-PLAYER-GENDER-VALUE   | Gender field accepts invalid values                                                       |
| GET-NON-EXISTING-404         | API does not return correct response for non-existing player                              |
| DELETE-NON-EXISTING-404      | API does not return correct response when deleting non-existing player                    |
| PLAYER-CAN-DELETE            | Role-based delete permissions are incorrect                                               |
| USER-CAN-DELETE-SELF         | User self-deletion permission logic is incorrect                                          |
| PERF-GET-PLAYER              | Response time for `/player/get` for user with `supervisor` role is too long (~10 seconds) |
| API-CREATE-METHOD            | HTTP method for `/player/create/{editor}` is incorrect (should be POST but is GET)        |
| API-GET-METHOD               | HTTP method for `/player/get` is incorrect (should be GET but is POST)                    |

---

## How Bugs Are Tracked

Bugs are tracked directly in test annotations:

```java
@Issue("CREATE-PLAYER-PASSWORD")
@Test(groups = {"bug"})
