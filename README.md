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
aa:bb-ddpp.zz_nn-ss
```

### Key Elements
- **aa** - Area (installation location)
- **bb** - Specific area within location
- **dd** - Device function
- **pp** - Position number
- **zz** - Secondary position (for devices with horizontal/vertical positioning)
- **nn** - Append number (connection points)
- **ss** - Signal classification

For detailed syntax requirements and element descriptions, see the [Naming Convention](naming-convention.md) document.

## Features

- **Name Generation** - Create standardized device names following EIC conventions
- **Name Validation** - Verify device names comply with naming standards
- **Repository Access** - Utilize approved abbreviations and naming databases

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
