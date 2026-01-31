# Agent Guidelines for Gattaca

To ensure maintainability, testability, and clarity, all agents must adhere to the following architectural standards:

## 1. Logic-First Reusability
- **Business Logic belongs in `:core`**: All services, use cases, and domain logic must reside in the `core` module.
- **`:server` is Infrastructure**: The server module should only handle HTTP concerns (Ktor), Persistence (JDBC), and Configuration. Keep it thin.

## 2. Package Structure
- **Core**:
    - `com.gattaca.domain`: Models and Repository Interfaces (Ports).
    - `com.gattaca.service`: Business logic/Services.
- **Server**:
    - `com.gattaca.api`: Controllers and Routes.
    - `com.gattaca.persistence`: Database implementations.
    - `com.gattaca.config`: Ktor setup.

## 3. Service-Oriented Flow
- **Routes -> Services -> Repositories**: 
    - Controllers/Routes should delegate to Services in `core`.
    - Services should coordinate domain models and repository interfaces.
    - Controllers should **never** perform business logic (e.g., password hashing, complex validation).

## 4. Interface-Driven Development
- Define Repository interfaces in `core`.
- Implement them in `server` (or other infrastructure modules).
- Services must only depend on interfaces, never on implementations.

## 5. Testability
- Unit test business logic in `core` using mocks for repositories.
- Use integration tests in `server` to verify the full HTTP -> Service -> DB stack.

## 6. Error Handling
- Use domain-specific exceptions in `core`.
- Map these exceptions to HTTP status codes in `server` using Ktor's `StatusPages`.