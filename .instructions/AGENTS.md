# Agent Guidelines for Gattaca

To ensure maintainability, testability, and clarity in this repository, all agents must adhere to the following development standards:

## 1. Modularization
- **Everything must be modular.** Avoid monolithic files or functions.
- Break down complex logic into small, single-purpose modules or classes.
- Group related functionality into logical packages or directories.

## 2. Interface-Driven Development
- **Define interfaces for all services and external dependencies.**
- Implementations should be decoupled from their usage via interfaces.
- This is mandatory to facilitate the creation of stubs and mocks for unit and integration testing.

## 3. Testability
- Every new feature or service must be accompanied by tests.
- Use the defined interfaces to mock dependencies in tests.
- Aim for high test coverage and isolation of components.

## 4. Dependency Injection
- Use a Dependency Injection (DI) framework (e.g., Koin) to manage service lifetimes and dependencies.
- Avoid manual instantiation of services within Ktor configuration functions.

## 5. Configuration Management
- Prefer type-safe configuration classes over raw environment property lookups.
- Group related configuration properties into data classes.

## 6. Error Handling
- Use Ktor's `StatusPages` plugin to define a global exception handling strategy.
- Ensure consistent error response formats across all APIs.

## 7. Logging
- Standardize logging using a consistent SLF4J logger across all modules.
- Include correlation IDs in logs to facilitate tracing across services.

## 8. Coding Standards
- Follow the established patterns found in the existing codebase.
- Maintain consistent naming conventions and project structure.
