# Architectural Plan: Logic-First Modularization

## 1. Vision
We are moving towards a structure where the **`core` module is the "Brain"** of the application, containing all business logic and domain definitions. The **`server` module is strictly a "Delivery Mechanism"** (Infrastructure), responsible only for HTTP endpoints, Ktor configuration, and persistence implementation.

This ensures that business logic is independent of the web framework and can be reused by the `client` or other modules.

---

## 2. Updated Module & Package Structure

### A. `:core` Module (The Brain)
Contains the core value of the application. Shared across all platforms.
- **`com.gattaca.domain`**: 
    - **Models**: Business entities (e.g., `User`, `Candidate`).
    - **Repositories**: Interface definitions (Outbound Ports).
    - **Exceptions**: Domain-specific errors.
- **`com.gattaca.service`**: 
    - **Business Logic**: Pure Kotlin services (e.g., `AuthService`, `ExerciseService`) that coordinate models and repositories.

### B. `:server` Module (The Delivery)
Infrastructure implementations for the JVM backend.
- **`com.gattaca.api`**: Ktor Routes and Controllers. Handles HTTP parsing, DTOs, and status codes.
- **`com.gattaca.persistence`**: Outbound adapters (e.g., JDBC implementations of `core` interfaces).
- **`com.gattaca.configs`**: Ktor plugins and setup (Serialization, Security, etc.).

---

## 3. Migration Tasks

1.  **Move Services to Core**: Relocate any existing logic from `server` into `core/src/commonMain/kotlin/com/gattaca/service`.
2.  **Consolidate Domain**: Group models and repository interfaces in `core/src/commonMain/kotlin/com/gattaca/domain`.
3.  **Refactor Server**:
    - Rename `infrastructure.web` -> `web`.
    - Rename `infrastructure.persistence` -> `persistence`.
    - Rename `infrastructure.plugins` -> `config`.
4.  **Decouple Controllers**: Controllers must only call Services. They should not interact with Repositories directly if a Service exists for that feature.

---

## 4. Key Architectural Rules
- **Core is King**: No framework-specific code (Ktor, JDBC, SQL) in `core`.
- **Server is Thin**: Controllers should be minimal. If there is an `if` statement or a calculation, it probably belongs in a Service in `core`.
- **Service-First**: Routes call Services; Services call Repositories.