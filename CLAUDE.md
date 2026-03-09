# microservice-baseline

## Purpose
A template/baseline project for building microservices in the Cantara style. Provides a pre-configured project structure with embedded Jetty, Jersey REST, health endpoints, and OAuth2 token handling as a starting point for new microservice development.

## Tech Stack
- Language: Java 8+
- Framework: Jersey 2.x, Jetty 9.x, Spring 5.x
- Build: Maven
- Key dependencies: Jersey, Jetty, Spring, Hystrix

## Architecture
Standalone microservice template with embedded Jetty server. Includes pre-configured health endpoint, token validation, and OAuth2 integration patterns. Designed to be cloned and customized for specific microservice implementations.

## Key Entry Points
- Health: `http://localhost:8086/microservice-baseline/health`
- Token: `http://localhost:8086/microservice-baseline/token`
- Verify: `http://localhost:8086/microservice-baseline/verify`

## Development
```bash
# Build
mvn clean install

# Run
java -jar target/microservice-baseline-*.jar

# Verify
curl http://localhost:8086/microservice-baseline/health
```

## Domain Context
Microservice project template. Starting point for new Cantara microservices, providing standard patterns for health checks, authentication, and REST API exposure.
