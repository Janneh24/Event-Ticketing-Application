# Contributing to Event Ticketing Application

Thank you for your interest in contributing to the Event Ticketing Application!

## Code Quality and Testing Standards

All pull requests must adhere to the following standards:
1. **100% Test Coverage:** All domain models, utilities, repositories, controllers, and views must be accompanied by comprehensive tests.
2. **Deterministic UI Tests:** Swing GUI tests must use AssertJ-Swing with explicit component names assigned via `setName()`.
3. **Continuous Integration:** Every commit must pass `mvn clean verify` on GitHub Actions without errors.
4. **Clean Code:** Adhere to standard Java conventions, meaningful naming, and SOLID principles.

## Workflow
1. Create a feature branch: `git checkout -b feature/my-feature`
2. Write unit and integration tests covering all execution paths.
3. Verify locally: `mvn clean verify`
4. Commit using conventional commit format: `feat:`, `fix:`, `test:`, `docs:`, or `refactor:`.
5. Push to origin and open a Pull Request against `main`.
