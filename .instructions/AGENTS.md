# Agent Guidelines for Gattaca

To ensure maintainability, testability, and clarity, all agents must adhere to the following architectural standards:

## 1. Logic-First Core
- **Business Logic belongs in `:core/commonMain`**: All services, use cases, and business logic must reside in the `core` module's common source set. This ensures logic is platform-agnostic and reusable.
- **`:server` is purely Delivery**: The server module should only contain Ktor Routes (`api` package) and Dependency Wiring (`config` package).
- **`:client` is for Communication**: Use the provided Public and Internal client configurations for any outgoing HTTP requests to ensure consistent telemetry and serialization.

## 2. Package Structure
- **Core (`commonMain`)**:
    - `com.gattaca.domain`: Pure models, Repository interfaces (Ports), and Exceptions.
    - `com.gattaca.service`: Business logic services that coordinate domain models.
- **Core (`jvmMain`)**:
    - `com.gattaca.infrastructure`: JDBC repository implementations and `DatabaseInitializer`.
- **Server**:
    - `com.gattaca.api`: Ktor Routes and Controllers.
    - `com.gattaca.config`: Ktor setup and `AppDependencies` wiring.
    - `com.gattaca.service`: Server-specific services (e.g., Auth/Security logic that requires JVM-specific libraries).

## 3. Service-Oriented Flow
- **Routes -> Services -> Repositories**: 
    - Controllers must delegate all logic to Services.
    - Services coordinate domain models and repository interfaces.
    - **Never** perform business logic or database queries directly in Controllers.

## 4. Automated Database Initialization
- All table creation logic must reside in `com.gattaca.infrastructure.DatabaseInitializer`.
- Tables must be created using `IF NOT EXISTS` to ensure safe startup.

## 5. Interface-Driven Development
- Define all Repository and Utility interfaces in `core/commonMain`.
- Services must only depend on these interfaces, never on concrete implementations (like JDBC).

## 6. Testability
- **Unit Tests**: Test business logic in `core` using mocks for repositories.
- **Integration Tests**: Verify the full stack in `server` (HTTP -> Service -> Database).