# Gattaca

Gattaca is a Ktor-based multi-module project focused on clean architecture and modularity.

## Project Structure

- `server`: Ktor server implementation.
- `client`: Ktor client module (multiplatform).
- `core`: Shared models and utilities.

## Recent Changes & Cleanup

- **Domain Refactoring**: Removed `City` model and replaced it with a recruitment domain:
    - `Organization`: Companies using the platform.
    - `User`: Members of organizations who create exercises.
    - `Exercise`: Assessment tasks for candidates.
    - `Candidate`: Individuals being evaluated.
    - `Evaluation`: Results and feedback for a candidate on a specific exercise.
- **Hexagonal Architecture**:
    - **Core**: Contains domain models and repository interfaces (Ports).
    - **Server**: Contains JDBC implementations (Adapters) and Ktor routing.
- **Repository Pattern**: Implemented for all domain models with JDBC-backed storage.
- **Multi-Client Support**: Dedicated controllers for `Landing` and `Dashboard` clients.

## Guidelines for Agents

Please refer to [.instructions/AGENTS.md](.instructions/AGENTS.md) for development standards when working in this repository.