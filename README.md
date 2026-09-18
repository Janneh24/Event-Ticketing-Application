# Event Ticketing & Venue Seat Reservation Application

[![CI Build](https://github.com/Janneh24/Event-Ticketing-Application/actions/workflows/ci.yml/badge.svg)](https://github.com/Janneh24/Event-Ticketing-Application/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Event-Ticketing-Application&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Janneh24_Event-Ticketing-Application)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_Event-Ticketing-Application&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Janneh24_Event-Ticketing-Application)

A desktop software application built for managing events, venue seat maps, and ticket reservations with atomic MySQL database transactions.

Developed as part of the **Automated Software Testing** course at the **University of Florence**.

---

## 🚀 Technologies & Tools

- **Language:** Java 17
- **GUI Framework:** Java Swing
- **Build Tool:** Apache Maven
- **Database:** MySQL 8.0 (Docker / Testcontainers)
- **Testing Tools:**
  - **Unit Testing:** JUnit 5, AssertJ, Mockito
  - **Integration Testing:** Testcontainers (Real MySQL Docker Container)
  - **E2E Testing:** AssertJ-Swing (Automated Swing GUI testing)
  - **Mutation Testing:** Pitest
  - **Coverage:** JaCoCo & SonarCloud
- **PDF Export:** OpenPDF

---

## 📁 Project Directory Layout

```
event-ticketing-application/
├── src/main/java/             # Production Java source code
│   └── com/ticketreservation/
│       ├── app/               # Main application entry point
│       ├── controller/        # Business logic controllers
│       ├── model/             # Domain entities (User, Event, Seat, Reservation)
│       ├── repository/        # Data access interfaces & JDBC SQL transaction repositories
│       ├── util/              # PDF export & helper utilities
│       └── view/              # Swing GUI components
├── src/main/resources/
│   └── db/init.sql            # MySQL schema & initial data
├── src/test/java/             # Unit tests (*Test.java) - Maven Surefire
├── src/it/java/               # Integration tests (*IT.java) - Maven Failsafe
└── src/e2e/java/              # End-to-End GUI tests (*E2E.java) - Maven Failsafe
```

---

## Building and testing

Full build, including unit and integration tests (requires Docker running):

```
mvn clean verify
```

Generate a local code coverage report:

```
mvn clean verify -Pjacoco
```

Report at `target/site/jacoco/index.html`.

Run mutation testing (scoped to `model` and `controller` — see report for justification):

```
mvn test org.pitest:pitest-maven:mutationCoverage
```

Report at `target/pit-reports/index.html`.

### Run Application Locally

```bash
docker-compose up -d
java -cp target/classes com.ticketreservation.app.Main
```

---

## 📜 License
Developed for academic assessment at the University of Florence.

