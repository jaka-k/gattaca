# Gattaca

Gattaca is a Ktor-based multi-module project focused on clean architecture and modularity.

## Project Structure

- `server`: Ktor server implementation.
- `client`: Ktor client module (multiplatform).
- `core`: Shared models and utilities.

## Recent Changes & Cleanup

- **Renamed Project**: The project was renamed from `ktor-sample` to `gattaca`.
- **Package Reorganization**: All source files were moved from `com.example` to `com.gattaca`.
- **Interface-Driven Design**: 
    - Extracted `ICityService` interface from `CityService`.
    - Implemented `JdbcCityService` as the JDBC-backed implementation.
    - Moved the `City` model to the `core` module for shared access.
- **Agent Guidelines**: Established a `.instructions` folder with guidelines for future development, emphasizing:
    - Modularization
    - Interface-driven development
    - Dependency Injection
    - Type-safe configuration
    - Global error handling
- **Gradle Configuration**: Connected all modules and verified the Gradle wrapper.

## Guidelines for Agents

Please refer to [.instructions/AGENTS.md](.instructions/AGENTS.md) for development standards when working in this repository.