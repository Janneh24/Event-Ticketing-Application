# Event Ticketing Application - Project Verification Report

## Course: Automated Software Testing (ASR)
## Institution: University of Florence

---

## 1. Executive Summary
The **Event Ticketing Application** is a robust, production-grade desktop ticket booking and event management system developed following strict Test-Driven Development (TDD) methodologies, achieving:
- **100% Instruction, Line, and Branch Coverage** via JaCoCo.
- **SonarCloud Quality Gate:** **PASSED (`OK`)** with 0 bugs, 0 vulnerabilities, 0 security hotspots, and 0 code smells.
- **Coveralls:** **100.0% Coverage** verified on the `main` branch.
- **Automated Testing Suite:** 94+ unit, GUI, integration, and E2E tests executing deterministically in CI.

---

## 2. Functional Capabilities

| Feature Area | Implementation Details |
| :--- | :--- |
| **Authentication & Roles** | Role-based navigation (`ORGANIZER` vs `CUSTOMER`) with input validation and security isolation. |
| **Customer Booking Portal** | Interactive seat grid, reservation management, dynamic ticket pricing in Euro (`€`), and PDF report generation. |
| **Admin Operations** | Event CRUD with automatic seat allocation, customer booking inspector, and user administration. |
| **Database Architecture** | Relational MySQL storage with transaction management, explicit constraints, and ID sequences initialized from 15. |
| **Reporting Engine** | Clean iText PDF summaries formatted with Euro currency (€) and itemized reservation auditing. |

---

## 3. Pull Request Architecture (20 PRs Total)

1. **PR #1**: `feature/domain-models` - Core domain entities (`Event`, `Seat`, `TicketReservation`, `User`)
2. **PR #2**: `feature/database-repositories` - Repository interfaces and JDBC implementations
3. **PR #3**: `feature/testcontainers-it` - Integration tests using Testcontainers MySQL
4. **PR #4**: `feature/controllers` - Business controllers with Mockito unit tests
5. **PR #5**: `feature/gui-and-reporting` - Customer GUI view and iText PDF exporter
6. **PR #6**: `feature/e2e-and-app` - Main application launcher and AssertJ-Swing E2E test
7. **PR #7**: `ci/fix-sonar-fallback` - CI pipeline resilience for SonarCloud analysis
8. **PR #8**: `docs/architecture-design` - System architecture, MVC design, and ER diagram
9. **PR #9**: `feat/currency-formatter-util` - Euro currency formatting and parsing utility
10. **PR #10**: `feat/input-validation-helpers` - Input validation helper utility
11. **PR #11**: `chore/docker-compose-healthcheck` - MySQL container healthcheck and restart policies
12. **PR #12**: `test/boundary-model-tests` - Domain model boundary conditions and equality contracts
13. **PR #13**: `feat/date-time-util` - European date format conversion utility
14. **PR #14**: `feat/event-capacity-projection` - Event capacity, occupancy, and projected revenue utility
15. **PR #15**: `refactor/sql-query-constants` - Centralized repository SQL query constants
16. **PR #16**: `docs/testing-matrix` - Comprehensive test matrix mapping all test levels
17. **PR #17**: `chore/github-templates` - Issue and pull request markdown templates
18. **PR #18**: `docs/contributing-guidelines` - Contributing and Code of Conduct guides
19. **PR #19**: `test/view-accessibility-names` - View component naming test suite
20. **PR #20**: `docs/project-verification-report` - Final verification and exam submission report

---

## 4. How to Run the Application

```bash
# Start MySQL via Docker
docker-compose up -d

# Run complete verification
mvn clean verify

# Run desktop application
mvn compile exec:java
```
