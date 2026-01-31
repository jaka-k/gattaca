# Gattaca

Gattaca is a Ktor-based recruitment platform built with a focus on **clean architecture**, **observability**, and **type-safe modularity**. It is designed to scale from a simple prototype to a robust production system.

## Architectural Philosophy (The "Why")

### 1. Hexagonal Architecture (Ports and Adapters)
We use Hexagonal Architecture to decouple the core business logic from external technologies (like Databases or Web Frameworks).
- **Why?** This allows us to test the recruitment logic (Organizations, Exercises, Evaluations) without starting a server or a database. It also makes it trivial to swap a JDBC implementation for a NoSQL one, or add a gRPC adapter alongside the Ktor REST API.

### 2. Multi-Module Project Structure
- **`core`**: Contains pure domain models, repository interfaces, and shared logic.
    - **Why?** By keeping models here, both the `server` and potentially a Kotlin Multiplatform `client` can share the exact same data structures, ensuring type safety across the network boundary.
- **`server`**: The Ktor-based implementation of our ports (Adapters).
- **`client`**: A placeholder for future frontend integrations.

### 3. Unified Error Handling (`GattacaException`)
We implemented a centralized exception system with a `StatusPages` global handler.
- **Why?** Security and DX (Developer Experience). In **Development**, we leak stack traces and internal "developer details" to speed up debugging. In **Production**, we mask these details to prevent information disclosure, returning only polished, user-friendly error codes.

### 4. Observability with OpenTelemetry (Grafana Stack)
Tracing is integrated directly into the Ktor pipeline via OTel.
- **Why?** Distributed systems are hard to debug. By exporting OTLP traces to the Grafana stack (Tempo/Loki), we can visualize the entire lifecycle of a request—from a Dashboard POST request down to the JDBC query—allowing us to identify bottlenecks and failures instantly.

---

## Domain Model

The platform is built around the following recruitment entities:
- **Organization**: The tenant owning the data.
- **User**: Members of an organization (e.g., Recruiters) who manage the process.
- **Exercise**: The assessment tasks created by users.
- **Candidate**: Individuals applying for roles.
- **Evaluation**: The bridge between a Candidate and an Exercise, containing scores and feedback.

---

## Getting Started

### Prerequisites
- JDK 21+
- Docker & Docker Compose (for local infrastructure)

### Environment
The server behaves differently based on the `ktor.development` flag in `application.yaml`:
- `true`: Detailed JSON errors with stack traces.
- `false`: Minimalist, secure error responses.

### Monitoring
Tracing is enabled by default. Ensure your environment has the following set for Grafana/OTEL:
`OTEL_EXPORTER_OTLP_ENDPOINT=http://your-otel-collector:4317`

## Development Workflow

We use a `Makefile` to streamline the local development process.

### 1. Configuration
A `.env` file is required in the root directory to configure ports and versions for the infrastructure.
A default `.env` file has been generated with standard ports (Postgres: 5432, Grafana: 3000, etc.).

### 2. Start Infrastructure
To spin up Postgres, Kafka, and the full Observability stack (Grafana, Loki, Tempo, Prometheus, Otel Collector):
```bash
make up
```
*Note: Ensure Docker Desktop (or your Docker daemon) is running.*

### 3. Run Server
To run the Ktor server in development mode (hot-reload enabled where supported):
```bash
make dev
```
The server will start on port `8080`.

### 4. View Logs
To view logs from the infrastructure containers:
```bash
make logs
```

### 5. Stop Infrastructure
To stop and remove the infrastructure containers:
```bash
make down
```

## Documentation

The API documentation is available via Swagger UI.
Once the server is running (`make dev`), visit:
**[http://localhost:8080/swagger](http://localhost:8080/swagger)**

The raw OpenAPI spec is available at:
`http://localhost:8080/openapi`

---

## Agent Guidelines
Refer to [.instructions/agent_guidelines.md](.instructions/agent_guidelines.md) for strict development standards regarding modularization, interface-driven design, and testing.
