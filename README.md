# DemoApp - Reactive MongoDB Spring Boot Application

A Spring Boot WebFlux application with reactive MongoDB integration, complete with CI/CD pipeline for Docker deployment.

## Technology Stack

- **Spring Boot 3.5.0** with WebFlux for reactive programming
- **MongoDB** for database (reactive driver)
- **JaCoCo** for code coverage (90% threshold for instruction, branch, and line coverage)
- **Docker** for containerization
- **GitHub Actions** for CI/CD

## CI/CD Pipeline

This project includes a complete CI/CD pipeline using GitHub Actions that:

1. **Builds and tests** the application with MongoDB
2. **Generates JaCoCo coverage reports**
3. **Builds a Docker image** and pushes it to Docker Hub
4. **Deploys** to your production server

### Required GitHub Secrets

To use the CI/CD pipeline, you need to add the following secrets to your GitHub repository:

- `DOCKERHUB_USERNAME`: Your Docker Hub username
- `DOCKERHUB_TOKEN`: Your Docker Hub access token
- `SERVER_HOST`: Your production server hostname or IP
- `SERVER_USERNAME`: SSH username for your production server
- `SERVER_SSH_KEY`: SSH private key for authentication

## Local Development

### Prerequisites

- JDK 21
- Maven
- Docker and Docker Compose

### Running Locally with Docker Compose

```bash
# Start the application with MongoDB and Mongo Express
docker-compose up -d

# Check logs
docker-compose logs -f

# Stop all services
docker-compose down
```

The application will be available at:
- Spring Boot App: http://localhost:8080
- Mongo Express (MongoDB UI): http://localhost:8081

### Running Tests

```bash
# Run tests with coverage
mvn clean test

# View JaCoCo report
open target/site/jacoco/index.html
```

## MongoDB Configuration

The application is configured to connect to MongoDB with the following settings:

- **Host**: localhost (in dev) or mongodb (in Docker)
- **Port**: 27017
- **Database**: demodb

## Deployment

The application is automatically deployed when changes are pushed to the main branch. The deployment:

1. Pulls the latest Docker image
2. Updates the running containers using docker-compose
3. Ensures zero-downtime deployment

## Manual Deployment

If you need to deploy manually:

```bash
# Pull the latest image
docker-compose pull

# Restart services
docker-compose up -d
```
