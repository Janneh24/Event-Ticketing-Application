# Event Ticketing & Venue Seat Reservation Application

[![CI Build](https://github.com/Janneh24/Event-Ticketing-Application/actions/workflows/ci.yml/badge.svg)](https://github.com/Janneh24/Event-Ticketing-Application/actions/workflows/ci.yml)
[![Coverage Status](https://coveralls.io/repos/github/Janneh24/Event-Ticketing-Application/badge.svg?branch=main&v=100)](https://coveralls.io/github/Janneh24/Event-Ticketing-Application?branch=main)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Event-Ticketing-Application&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Janneh24_Event-Ticketing-Application)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Event-Ticketing-Application&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Janneh24_Event-Ticketing-Application)
[![Bugs](https://img.shields.io/badge/bugs-0-brightgreen.svg?logo=sonarcloud)](https://sonarcloud.io/summary/new_code?id=Janneh24_Event-Ticketing-Application)
[![Vulnerabilities](https://img.shields.io/badge/vulnerabilities-0-brightgreen.svg?logo=sonarcloud)](https://sonarcloud.io/summary/new_code?id=Janneh24_Event-Ticketing-Application)
[![Code Smells](https://img.shields.io/badge/code%20smells-0-brightgreen.svg?logo=sonarcloud)](https://sonarcloud.io/summary/new_code?id=Janneh24_Event-Ticketing-Application)
[![Technical Debt](https://img.shields.io/badge/technical%20debt-0-brightgreen.svg?logo=sonarcloud)](https://sonarcloud.io/summary/new_code?id=Janneh24_Event-Ticketing-Application)
[![Duplicated Lines](https://img.shields.io/badge/duplicated%20lines-0%25-brightgreen.svg?logo=sonarcloud)](https://sonarcloud.io/summary/new_code?id=Janneh24_Event-Ticketing-Application)

A desktop Java application for managing events, venue seat maps, and ticket reservations, backed by a MySQL database with transaction management.

Developed by **Essa Janneh** (Matricola: 7175252) for the **Automated Software Testing** exam at the **University of Florence**.

---

## Technologies and Tools

- **Java Version:** Java 17 (LTS)
- **GUI:** Java Swing
- **Build System:** Apache Maven
- **Database:** MySQL 8.0 (Docker / Testcontainers)
- **Testing Libraries:**
  - Unit Testing: JUnit 5 (Jupiter), AssertJ, Mockito
  - Integration Testing: Testcontainers (official MySQL 8.0 Docker image)
  - End-to-End GUI Testing: AssertJ-Swing (with `xvfb-run` on Linux CI)
  - Mutation Testing: Pitest
  - Code Coverage: JaCoCo & SonarCloud
- **PDF Generation:** OpenPDF

---

## Project Structure

```
event-ticketing-application/
├── src/main/java/             # Production code
│   └── com/ticketreservation/
│       ├── app/               # Main application entry point (TicketApp)
│       ├── controller/        # Business logic controllers
│       ├── model/             # Domain entities (User, Event, Seat, Reservation)
│       ├── repository/        # Data access interfaces & JDBC SQL implementations
│       ├── util/              # PDF export & formatting helpers
│       └── view/              # Swing GUI frames and dialog interfaces
├── src/main/resources/
│   └── db/init.sql            # MySQL schema and initial setup
├── src/test/java/             # Unit tests (*Test.java) - Maven Surefire
├── src/it/java/               # Integration tests (*IT.java) - Maven Failsafe
└── src/e2e/java/              # End-to-End GUI tests (*E2E.java) - Maven Failsafe
```

---

## Building and Running Tests

### 1. Full Build and Verification
Runs all unit, integration, and end-to-end tests (requires Docker Desktop running for Testcontainers):

```bash
mvn clean verify
```

### 2. Code Coverage Report
To generate the local JaCoCo HTML report:

```bash
mvn clean verify -Pjacoco
```
The report is generated at `target/site/jacoco/index.html`.

### 3. Mutation Testing
Runs Pitest on the domain models and controllers:

```bash
mvn test org.pitest:pitest-maven:mutationCoverage
```
The report is generated at `target/pit-reports/index.html`.

### 4. Running the Application Locally
To launch the desktop application against the local Docker MySQL container:

```bash
docker-compose up -d
mvn compile exec:java -Dexec.mainClass="com.ticketreservation.app.TicketApp"
```

---

## License & Academic Notice
Developed for academic assessment in the Automated Software Testing course at the University of Florence.
