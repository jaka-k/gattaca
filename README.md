# Gattaca

Gattaca is a Ktor-based recruitment platform built with a focus on **clean architecture**, **observability**, and **type-safe modularity**. It is designed to scale from a simple prototype to a robust production system.

## Architectural Philosophy (The "Why")

### 1. Hexagonal & Logic-First Architecture
We strictly separate **Business Logic** from **Infrastructure**.
- **What?** All services, domain models, and repository interfaces reside in the `:core` module. The `:server` module is purely a delivery mechanism (Ktor routes and configuration).
- **Why?** This ensures that our recruitment engine is platform-agnostic. By keeping logic in `core/commonMain`, we can reuse 100% of our business rules in a mobile app, web frontend, or another backend service without rewriting a single line of logic. It also **forces decoupling**: the compiler prevents you from accidentally leaking database-specific code (SQL) into your business logic.

### 2. Multi-Module Project Structure
- **`:core` (The Brain)**: 
    - `commonMain`: Domain models, Ports (Interfaces), and Services.
    - `jvmMain`: Platform-specific implementations like JDBC persistence and hashing.
- **`:server` (The Delivery)**: Handles HTTP endpoints (`api`), Ktor plugins, and dependency wiring (`config`).
- **`:client` (The Communication)**: Provides pre-configured, instrumented HTTP clients for both **Public** (external APIs) and **Internal** (Inter-process communication) use.

### 3. Automated Infrastructure
- **What?** Centralized `DatabaseInitializer` in the core module.
- **Why?** Reliability. The system automatically ensures all required database tables exist at startup, reducing manual setup and preventing "table not found" errors during deployment or local development.

### 4. Observability with OpenTelemetry (Grafana Stack)
Tracing is integrated directly into both the Server and the Clients via OTel.
- **Why?** Distributed systems are hard to debug. Our architecture allows us to trace a request through the public client, into the server, and down to the specific JDBC query, providing total visibility into performance and failure points.

---

## Domain Model

The platform is built around the following recruitment entities:
- **Organization**: The tenant owning the data.
- **User**: Members of an organization (e.g., Recruiters).
- **Exercise**: Assessment tasks.
- **Candidate**: Individuals applying for roles.
- **Evaluation**: Scores and feedback for a candidate on an exercise.
- **Session**: Database-backed user sessions for secure persistence.

---

## Getting Started

### Prerequisites
- JDK 25+
- Docker & Docker Compose (for local infrastructure)

### Environment
The server behaves differently based on the `ktor.development` flag in `application.yaml`:
- `true`: Detailed JSON errors with stack traces.
- `false`: Minimalist, secure error responses.

---

## Development Workflow

We use a `Makefile` to streamline the local development process.

### 1. Start Infrastructure
To spin up Postgres, Kafka, and the full Observability stack:
```bash
make up
```

### 2. Run Server
```bash
make dev
```
The server will start on port `8080`. API docs are at `http://localhost:8080/swagger`.

---

## Agent Guidelines
Refer to [.instructions/AGENTS.md](.instructions/AGENTS.md) for strict development standards regarding modularization, interface-driven design, and the logic-first approach.