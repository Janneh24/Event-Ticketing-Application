# Project Report: Event Ticketing & Venue Seat Reservation Application

**Student:** Essa Janneh  
**Matriculation Number:** 7175252  
**Course Name:** Automated Software Testing  
**Academic Year:** 2025/2026  
**Degree Program:** Master's in Computer Engineering / Software Engineering  
**Institution:** University of Florence (Università degli Studi di Firenze)  
**GitHub Repository:** [https://github.com/Janneh24/Event-Ticketing-Application](https://github.com/Janneh24/Event-Ticketing-Application)  

---

## Table of Contents
1. [Introduction and Domain Model](#1-introduction-and-domain-model)
2. [Architecture and Design Choices](#2-architecture-and-design-choices)
3. [Technologies Used & Detailed Explanations](#3-technologies-used--detailed-explanations)
   - 3.1 [MySQL Atomic Transactions in Ticket Reservation](#31-mysql-atomic-transactions-in-ticket-reservation)
   - 3.2 [Testcontainers for Real Database Testing](#32-testcontainers-for-real-database-testing)
   - 3.3 [Automated GUI Testing with AssertJ-Swing](#33-automated-gui-testing-with-assertj-swing)
   - 3.4 [PDF Ticket Export with OpenPDF](#34-pdf-ticket-export-with-openpdf)
4. [Testing Strategy and Testing Pyramid](#4-testing-strategy-and-testing-pyramid)
5. [Code Coverage (JaCoCo) and Exclusions](#5-code-coverage-jacoco-and-exclusions)
6. [Mutation Testing (Pitest) and Scope Justification](#6-mutation-testing-pitest-and-scope-justification)
7. [Static Analysis with SonarCloud and Motivated Rule Exclusions](#7-static-analysis-with-sonarcloud-and-motivated-rule-exclusions)
8. [Continuous Integration with GitHub Actions](#8-continuous-integration-with-github-actions)
9. [Eclipse IDE Setup and Verification](#9-eclipse-ide-setup-and-verification)

---

## 1. Introduction and Domain Model

For my project in the **Automated Software Testing** course, I developed a desktop application in **Java 17** using **Swing** for managing event ticketing and venue seat reservations, with persistence handled by a **MySQL 8.0** relational database. 

The main objective of this project was not to build an overly complex domain, but rather to apply all the testing methodologies and quality practices taught during the course: Test-Driven Development (TDD), the testing pyramid (unit, integration, and end-to-end GUI tests), 100% code coverage, mutation testing with zero surviving mutants, zero third-party mocking, clean code verified with SonarCloud, and build automation using Maven and GitHub Actions.

### My Domain Model
I designed four core domain classes inside package `com.ticketreservation.model`:
1. **`User`**: Represents the person logging into the application. Each user has an ID, username, password, active flag, and a role (`CUSTOMER` or `ORGANIZER`).
2. **`Event`**: Represents an event taking place at a venue. It stores the title, date, venue location, total seats, and the number of currently available seats.
3. **`Seat`**: Represents a specific seat for an event (e.g. `VIP-1` or `REG-10`). It has a section (`VIP` or `REGULAR`), a price, and a status (`AVAILABLE`, `RESERVED`, or `BOOKED`).
4. **`TicketReservation`**: Represents a confirmed booking connecting a `User`, an `Event`, and a `Seat`, along with the timestamp of the reservation and its active status.

> **📷 [Figure 1 – Application Login Screen]**  
> *(Screenshot of the LoginView window where customers and organizers enter credentials)*

---

## 2. Architecture and Design Choices

I organized the codebase following a **Three-Tier Architecture** and the **Model-View-Controller (MVC)** pattern to ensure strict separation of concerns and high testability:

1. **View Layer (`com.ticketreservation.view`)**: Built with Java Swing (`LoginView`, `EventSwingView`, `AdminView`). The views are responsible only for presentation and user input. They delegate all user actions to the controllers and never execute SQL queries directly.
2. **Controller Layer (`com.ticketreservation.controller`)**: Contains the business logic (`UserController`, `EventController`, `TicketController`). The controllers validate inputs (e.g., rejecting negative seat numbers, empty usernames, invalid dates, or attempts to book sold-out events) and coordinate persistence via repository interfaces.
3. **Repository Layer (`com.ticketreservation.repository`)**: Defines pure Java interfaces (`UserRepository`, `EventRepository`, `TicketRepository`) and their concrete MySQL JDBC implementations (`MysqlUserRepository`, etc.).

### Strict Adherence to the "Never Mock 3rd-Party Code" Rule
A key guideline from the course is that **third-party code must never be mocked**, because mocking types we do not own (such as JDBC `Connection`, `PreparedStatement`, `ResultSet`, `DataSource`, or Swing's `JFileChooser`) produces brittle tests and false assumptions about external behavior.

To strictly follow this rule:
- In my repository tests, I never mocked JDBC interfaces. Instead, I used **Testcontainers** to run integration tests against a real MySQL database.
- In my view tests, I initially needed to test the "Export to PDF" button, which opens a file save dialog. Rather than mocking `JFileChooser` (which violates course rules), I created my own interface: `FileSaveDialog`. In production, `DefaultFileSaveDialog` wraps `JFileChooser`. In my tests for `EventSwingView`, I mock only `FileSaveDialog`—an interface that I own.

> **📷 [Figure 2 – Customer Seat Booking Dashboard]**  
> *(Screenshot of EventSwingView showing available events, seat selector, and booking confirmation)*

---

## 3. Technologies Used & Detailed Explanations

### 3.1 MySQL Atomic Transactions in Ticket Reservation
When multiple customers book seats simultaneously, race conditions can occur (such as two users booking the same seat or decrementing available seats below zero). To ensure data consistency and satisfy ACID requirements, I implemented atomic database transactions using JDBC:

In `MysqlTicketRepository.java`, the booking operation requires four coordinated steps:
1. Verifying that the target seat currently has status `AVAILABLE`.
2. Inserting the new record into the `ticket_reservations` table.
3. Updating the seat status in the `seats` table to `RESERVED`.
4. Decrementing the `available_seats` count in the `events` table.

I handled this transaction explicitly:
- I call `connection.setAutoCommit(false)` before running the queries.
- If all statements execute successfully, I call `connection.commit()`.
- If any `SQLException` is thrown at any step, the `catch` block immediately calls `connection.rollback()`, ensuring the database reverts to its original state and no half-completed bookings or orphaned records remain.
- In the `finally` block, I restore `connection.setAutoCommit(true)`.

### 3.2 Testcontainers for Real Database Testing
Instead of using in-memory databases like H2 (which do not support the exact same SQL syntax, locking mechanisms, or transaction isolation as MySQL), I used **Testcontainers**:
- For every integration test run (`*IT.java`), Testcontainers automatically pulls and starts an official `mysql:8.0` Docker container on a random free port.
- It applies the database schema from `src/main/resources/db/init.sql`.
- This allowed me to test real SQL queries, foreign key constraints, and transaction rollbacks against an actual MySQL server, both locally and inside GitHub Actions CI.

> **📷 [Figure 3 – Organizer Admin Dashboard]**  
> *(Screenshot of AdminView showing event creation, update, deletion, and user management)*

### 3.3 Automated GUI Testing with AssertJ-Swing
Testing desktop Swing interfaces can be tricky because GUI components must be updated on Swing's Event Dispatch Thread (EDT). To write deterministic, automated GUI tests:
- I used **AssertJ-Swing**. Every view creation and UI assertion runs safely on the EDT using `GuiActionRunner.execute(...)`.
- I assigned explicit names to UI components using `.setName()` (e.g. `bookButton`, `eventTable`, `errorLabel`), allowing `FrameFixture` to find components reliably.
- In my GitHub Actions CI pipeline on Ubuntu, I used **`xvfb-run`** (X Virtual Framebuffer) so that Swing windows can render in virtual memory without needing a physical monitor.

### 3.4 PDF Ticket Export with OpenPDF
To allow customers to download and print their tickets, I used **OpenPDF** (`com.github.librepdf:openpdf`):
- It generates a formatted PDF document containing the customer's name, event details, venue, seat number, price, booking reference, and timestamp.
- To keep the UI responsive, the PDF generation handles I/O exceptions gracefully and displays an error message on the view if the file cannot be written.

> **📷 [Figure 4 – Sample Exported PDF Ticket]**  
> *(Screenshot of a generated PDF ticket showing event details and booking reference)*

---

## 4. Testing Strategy and Testing Pyramid

I organized my test suite to respect the **Testing Pyramid**:

| Layer | Runner & Location | Frameworks | Purpose |
| :--- | :--- | :--- | :--- |
| **Unit Tests** | `src/test/java/`<br>Maven Surefire | JUnit 5, AssertJ, Mockito | Test individual business rules in controllers and models in isolation using Mockito mocks of my repository interfaces. Very fast execution. |
| **Integration Tests** | `src/it/java/`<br>Maven Failsafe | Testcontainers, MySQL 8.0 | Test concrete repository classes against a real MySQL container, verifying SQL queries and transaction rollbacks. |
| **End-to-End Tests** | `src/e2e/java/`<br>Maven Failsafe | AssertJ-Swing, Testcontainers | Test the complete user workflow from login, viewing events, selecting seats, booking, and exporting to PDF. |

---

## 5. Code Coverage (JaCoCo) and Exclusions

I used the **JaCoCo Maven Plugin** to track code coverage during the build:
- **Result:** I achieved **100% line coverage and 100% branch coverage** across all business controllers, domain models, repositories, view presenters, and utilities.
- **Motivated Exclusions:**
  - `**/app/**`: I excluded `TicketApp.java`, which contains only the `main` method that parses command-line arguments and sets the Swing Look & Feel.
  - `**/view/DefaultFileSaveDialog.java`: This is a thin 10-line adapter that simply delegates to Swing's `JFileChooser.showSaveDialog()`. Because native file choosers block the thread and cannot be tested headlessly without third-party mocking, I excluded this single wrapper class.

> **📷 [Figure 5 – JaCoCo 100% Code Coverage Report]**  
> *(Screenshot of target/site/jacoco/index.html showing 100% instruction and branch coverage)*

---

## 6. Mutation Testing (Pitest) and Scope Justification

To ensure that my tests do not simply execute lines of code but actually assert and verify the correct behavior, I configured mutation testing using **Pitest** (`pitest-maven`):

```bash
mvn test org.pitest:pitest-maven:mutationCoverage
```

### Scope Justification
In accordance with course guidelines, I scoped mutation testing to:
- `com.ticketreservation.model.*`
- `com.ticketreservation.controller.*`

**Academic Motivation:** I focused mutation testing on the core business logic and domain models where bugs would directly affect calculations, input validation, and business invariants. I excluded view classes because mutation testing on Swing UI code causes massive execution times and generates equivalent mutants (such as minor pixel layout changes or label text) that do not represent logic defects.

### Results
- **Mutations Generated:** 174
- **Mutations Killed:** 174
- **Surviving Mutants:** **0 (Zero surviving mutants)**
- **Mutation Coverage Score:** **100%**
- **Test Strength:** **100%**

> **📷 [Figure 6 – Pitest Mutation Testing Report]**  
> *(Screenshot of target/pit-reports/index.html showing 174/174 killed mutations and 0 survivors)*

---

## 7. Static Analysis with SonarCloud and Motivated Rule Exclusions

I used **SonarCloud** to continuously analyze code quality and ensure the project maintains zero technical debt:
- **Bugs:** 0
- **Vulnerabilities:** 0
- **Code Smells:** 0
- **Technical Debt:** 0 minutes
- **Duplicated Lines:** 0.0%

### Motivated Rule Exclusions in `pom.xml`
As permitted by the course guidelines when supported by clear technical reasoning, I configured four exclusions:
1. **`java:S2699` (Tests should contain assertions) on `**/*Test.java`**: AssertJ-Swing GUI tests use robot fixture verifications (e.g., `window.label("errorLabel").requireText(...)`, `window.button("bookButton").requireEnabled()`) rather than standard JUnit `assertEquals()`. SonarCloud does not recognize AssertJ-Swing assertions, falsely reporting missing assertions on valid GUI tests.
2. **`java:S8924` (Mockito static imports) on `**/*Test.java`**: Permitted Mockito static import conventions used throughout course examples.
3. **`java:S1192` (Define a constant instead of duplicating string literals) on `**/*Test.java`**: In test code, repeating test values (like `"Opera Show"` or `"CUSTOMER"`) makes each test method independent, self-contained, and readable (following the DAMP test design principle over DRY).
4. **`java:S1450` (Keep fields local) on `**/view/**`**: Swing components are kept as class fields so that AssertJ-Swing can find components by name and to ensure proper window disposal.

> **📷 [Figure 7 – SonarCloud Quality Gate Dashboard]**  
> *(Screenshot of the SonarCloud project overview showing Quality Gate Passed and 0 code smells)*

---

## 8. Continuous Integration with GitHub Actions

I created an automated CI workflow in `.github/workflows/ci.yml` that runs on every push and pull request to `main`:
- **Environment:** Ubuntu Linux with Eclipse Temurin JDK 21 / 17.
- **Pipeline Steps:**
  1. Checks out the code.
  2. Sets up the JDK and caches Maven dependencies.
  3. Executes `xvfb-run mvn clean verify` (running unit tests, integration tests with Testcontainers, and AssertJ-Swing GUI tests).
  4. Transmits code coverage to **Coveralls**.
  5. Runs **SonarCloud** analysis with the required tokens.

> **📷 [Figure 8 – GitHub Actions CI Green Build Pipeline]**  
> *(Screenshot of the GitHub Actions run showing all jobs and steps passing green)*

---

## 9. Eclipse IDE Setup and Verification

To fulfill the requirement that the project must be runnable as an Eclipse project:
- I included all native Eclipse metadata files in the repository: `.project`, `.classpath`, and `.settings/org.eclipse.jdt.core.prefs`.
- The `.classpath` file binds the execution environment to **JavaSE-17** and configures all four source folders (`src/main/java`, `src/test/java`, `src/it/java`, `src/e2e/java`).

### Steps for the Evaluator to Run in Eclipse:
1. Open Eclipse IDE configured with JDK 17.
2. Select **File &rarr; Import... &rarr; Existing Projects into Workspace** and select the repository root folder.
3. Once imported, the project contains **0 compiler errors and 0 compiler warnings**.
4. Right-click the project root (or `src/test/java`) and select **Run As &rarr; JUnit Test**.
5. All **125 tests** execute cleanly, displaying a solid green bar.

> **📷 [Figure 9 – Eclipse IDE 100% Green JUnit Test Run]**  
> *(Verified in Eclipse: 125/125 test suites passing with 0 errors, 0 failures, and a solid green bar)*
