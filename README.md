# NameOps

A naming convention system for EIC (Electron-Ion Collider) device nomenclature.

## Overview

This repository provides services to create, generate, and validate standardized device names for EIC (Electron-Ion Collider) components. The system helps ensure consistent naming across all EIC devices and infrastructure.

## Contents

- **[Naming Convention](naming-convention.md)** - Detailed specification for device naming syntax and requirements
- **Resources** - Supporting documents and reference materials
  - EIC-SEG-RSI-009 REV1 specification document
  - ES SWN lattice data
  - Naming examples
  - SWN abbreviation database

## Quick Reference

The nomenclature syntax for EIC devices is:

```
aa:bb-ddpp:zz:nn-cc:ss
```

### Key Elements
- **aa** - Area (installation location)
- **bb** - Specific area within location
- **dd** - Device function
- **pp** - Position number
- **zz** - Secondary position (for devices with horizontal/vertical positioning)
- **nn** - Append number (connection points)
- **cc** - Controller device
- **ss** - Signal classification

For detailed syntax requirements and element descriptions, see the [Naming Convention](naming-convention.md) document.

## Features

- **Name Generation** - Create standardized device names following EIC conventions
- **Name Validation** - Verify device names comply with naming standards
- **Repository Access** - Utilize approved abbreviations and naming databases
- **Configuration-Driven** - Valid naming elements defined in JSON configuration
- **REST API** - RESTful web service with OpenAPI/Swagger documentation

## Getting Started

### Running the Application

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/nameops-1.0.0-SNAPSHOT.jar
```

The service will start on `http://localhost:8080`

### API Documentation

Once the application is running, you can access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

The Swagger UI provides an interactive interface to:
- Explore all available endpoints
- View request/response schemas
- Test API calls directly from the browser
- See example requests and responses

### REST API Endpoints

All endpoints are prefixed with `/api/v1/nameops`:

- `GET /health` - Service health check
- `POST /generate` - Generate a device name
- `POST /validate` - Validate a device name
- `GET /convention` - Get naming convention details
- `GET /repository` - Get all naming repository elements
- `GET /repository/areas` - Get valid areas
- `GET /repository/devices` - Get valid devices
- `GET /repository/signals` - Get valid signals
- `GET /repository/controllers` - Get valid controllers

### Example API Calls

**Generate a device name:**
```bash
curl -X POST http://localhost:8080/api/v1/nameops/generate \
  -H "Content-Type: application/json" \
  -d '{"area":"TB","specificArea":"01","device":"PS","position":"10"}'
```

**Response:**
```json
{
  "status": "success",
  "generatedName": "TB:01-PS10",
  "parameters": {
    "area": "TB",
    "specificArea": "01",
    "device": "PS",
    "position": "10"
  },
  "message": "Device name generated successfully"
}
```

## Naming Repository

The system maintains a repository of valid naming elements in `src/main/resources/naming-repository.json`. This configuration file contains:

- **Areas (aa)**: Valid installation locations (ES, IS, TB, IR, HALL)
- **Devices (dd)**: Device function types with full names and lattice keywords
- **Controllers (cc)**: Controller device types for lattice devices
- **Signals (ss)**: Signal classification types (RB, SP, RD, WR, etc.)
- **Specific Areas (bb)**: Sub-locations within main areas

Each element includes:
- Abbreviation (used in device names)
- Full name (descriptive name)
- Description (purpose and usage)
- Lattice keyword (for beamline devices, when applicable)

The repository is validated at runtime when generating device names to ensure all components are valid and approved.

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080`

### API Endpoints

- **Health Check**: `GET /api/v1/nameops/health`
- **Generate Name**: `POST /api/v1/nameops/generate`
- **Validate Name**: `POST /api/v1/nameops/validate`
- **Get Convention**: `GET /api/v1/nameops/convention`
- **Get Repository**: `GET /api/v1/nameops/repository`
- **Get Areas**: `GET /api/v1/nameops/repository/areas`
- **Get Devices**: `GET /api/v1/nameops/repository/devices`
- **Get Signals**: `GET /api/v1/nameops/repository/signals`
- **Get Controllers**: `GET /api/v1/nameops/repository/controllers`

### API Documentation

Once the application is running, access the interactive API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Resources

All reference documents and databases are located in the `resources/` directory.

## Documentation

- [Naming Convention Specification](naming-convention.md) - Complete syntax requirements and guidelines

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.3** (with BOM dependency management)
- **Spring Web** - REST API framework
- **Spring Boot Actuator** - Production-ready monitoring
- **Jackson 2.16.1** - JSON processing
- **JUnit 5** - Testing framework
- **SLF4J** - Logging abstraction

## Build Configuration

The project uses Maven with:
- Spring Boot BOM for dependency management
- Explicit Jackson version (2.16.1)
- Log4j to SLF4J adapter
- Executable JAR profile for deployment

## Date

Last updated: March 20, 2026
