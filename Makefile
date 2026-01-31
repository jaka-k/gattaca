.PHONY: up down restart logs dev build-server test clean

# Start infrastructure (Postgres, Kafka, Observability)
up:
	docker-compose up -d

# Stop infrastructure
down:
	docker-compose down

# Restart infrastructure
restart: down up

# View logs from infrastructure
logs:
	docker-compose logs -f

# Run the server in development mode
dev:
	./gradlew :server:run

# Build the server
build-server:
	./gradlew :server:build

# Run tests
test:
	./gradlew test

# Clean build artifacts
clean:
	./gradlew clean
