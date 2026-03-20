# Quick Start Guide

## Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd NameOps
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The service will start on http://localhost:8080

## Testing the API

### Health Check
```bash
curl http://localhost:8080/api/v1/nameops/health
```

### Get Naming Convention
```bash
curl http://localhost:8080/api/v1/nameops/convention
```

### Generate a Device Name
```bash
curl -X POST http://localhost:8080/api/v1/nameops/generate \
  -H "Content-Type: application/json" \
  -d '{
    "area": "TB",
    "specificArea": "01",
    "device": "PS",
    "position": "10"
  }'
```

### Validate a Device Name
```bash
curl -X POST http://localhost:8080/api/v1/nameops/validate \
  -H "Content-Type: application/json" \
  -d '{
    "name": "TB:01-PS10.01_01-RB"
  }'
```

## API Documentation

Access the interactive Swagger UI documentation:
```
http://localhost:8080/swagger-ui.html
```

## Development Tools

### H2 Database Console
Access the H2 console for database inspection:
```
http://localhost:8080/h2-console
```

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:nameops`
- Username: `sa`
- Password: (leave empty)

## Project Structure

```
NameOps/
â”œâ”€â”€ src/
â”‚   â”œâ”€â”€ main/
â”‚   â”‚   â”œâ”€â”€ java/
â”‚   â”‚   â”‚   â””â”€â”€ gov/bnl/eic/nameops/
â”‚   â”‚   â”‚       â”œâ”€â”€ NameOpsApplication.java
â”‚   â”‚   â”‚       â””â”€â”€ controller/
â”‚   â”‚   â”‚           â””â”€â”€ NameController.java
â”‚   â”‚   â””â”€â”€ resources/
â”‚   â”‚       â””â”€â”€ application.properties
â”‚   â””â”€â”€ test/
â”‚       â””â”€â”€ java/
â”‚           â””â”€â”€ gov/bnl/eic/nameops/
â”‚               â””â”€â”€ NameOpsApplicationTests.java
â”œâ”€â”€ resources/
â”‚   â”œâ”€â”€ SWN_abbreviation_database(swn abv editable for EIC).csv
â”‚   â”œâ”€â”€ es swn lattice(es_swn_lattice).csv
â”‚   â””â”€â”€ naming examples(Sheet1).csv
â”œâ”€â”€ pom.xml
â”œâ”€â”€ README.md
â”œâ”€â”€ naming-convention.md
â””â”€â”€ .gitignore
```

## Next Steps

1. Implement name generation logic in `NameController.generateName()`
2. Implement validation logic in `NameController.validateName()`
3. Load CSV data from resources directory
4. Create service layer for business logic
5. Add comprehensive unit and integration tests

## Troubleshooting

### Port Already in Use
If port 8080 is already in use, you can change it in `application.properties`:
```properties
server.port=8081
```

### Maven Build Fails
Ensure you have Java 17 or higher:
```bash
java -version
```

### Application Won't Start
Check the logs for detailed error messages:
```bash
tail -f logs/spring-boot.log
```
