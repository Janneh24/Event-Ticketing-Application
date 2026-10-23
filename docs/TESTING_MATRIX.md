# Automated Software Testing Matrix

## University of Florence - Automated Software Testing

This testing matrix outlines all testing levels, frameworks, and verification artifacts implemented in the **Event Ticketing Application**.

---

## 1. Testing Pyramid Overview

| Level | Scope | Frameworks & Tools | Target Classes / Features |
| :--- | :--- | :--- | :--- |
| **Unit Testing** | Domain Models & Utilities | JUnit 5, AssertJ | `Event`, `Seat`, `TicketReservation`, `User`, `CurrencyFormatter`, `InputValidator`, `DateTimeUtil`, `EventCapacityUtil`, `SqlQueries` |
| **Unit Testing** | Business Logic & Controllers | JUnit 5, Mockito | `EventController`, `SeatController`, `UserController` |
| **GUI Testing** | Desktop Presentation | AssertJ-Swing, Xvfb | `LoginSwingView`, `EventSwingView`, `AdminView` |
| **Integration Testing** | Database & Persistence | Testcontainers MySQL | `MysqlEventRepositoryIT`, `MysqlSeatRepositoryIT`, `MysqlReservationRepositoryIT`, `MysqlUserRepositoryIT` |
| **End-to-End Testing** | Full System Workflows | AssertJ-Swing + Testcontainers | `TicketReservationAppE2E` |
| **Mutation Testing** | Fault Injection & Assertion Quality | Pitest (PIT) | Model and Controller mutation coverage |
| **Coverage & Static Analysis** | Code Hygiene & Quality Gates | JaCoCo, Coveralls, SonarCloud | Entire repository (100% line & branch coverage) |

---

## 2. Test Execution Commands

```bash
# Run unit tests only
mvn test

# Run integration tests (IT) and E2E tests
mvn verify

# Run mutation testing with Pitest
mvn test-compile org.pitest:pitest-maven:mutationCoverage

# Launch application
mvn compile exec:java
```

---

## 3. Coverage Metrics
- **Line Coverage:** 100.0%
- **Branch Coverage:** 100.0%
- **Instruction Coverage:** 100.0%
- **SonarCloud Quality Gate:** OK (0 bugs, 0 vulnerabilities, 0 code smells)
