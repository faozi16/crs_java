# Car Reservation System — Development Guide

**Last Updated**: February 18, 2026  
**Version**: 0.0.1-SNAPSHOT

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Project Structure](#project-structure)
4. [Database Schema (DDL)](#database-schema-ddl)
5. [Entity Classes](#entity-classes)
6. [Component Architecture](#component-architecture)
7. [API Endpoints Summary](#api-endpoints-summary)
8. [Key Configurations](#key-configurations)
9. [Build & Run Instructions](#build--run-instructions)
10. [Testing Strategy](#testing-strategy)
11. [Development Notes](#development-notes)
12. [Enhancement Roadmap](#enhancement-roadmap)

---

## Project Overview

**Name**: Car Reservation System  
**Type**: Spring Boot 3 REST API (Monolithic Architecture)  
**Purpose**: Manage vehicle reservations, customers, drivers, payments, and feedback.

**Key Features**:
- Customer management (registration, profile, status)
- Vehicle catalog and inventory management
- Driver assignment and management
- Reservation booking workflow (pickup/dropoff, status tracking)
- Payment processing & Payment Method storage
- Reservation feedback and ratings
- Spring Security integration for authentication
- MapStruct-based DTO/Entity mapping
- Testcontainers-based integration testing

---

## Tech Stack

### Core Framework
- **Language**: Java 21
- **Framework**: Spring Boot 3.4.0
- **Build Tool**: Gradle (wrapper included)

### Libraries & Dependencies
| Component | Library | Version |
|-----------|---------|---------|
| REST API | Spring Boot Web | 3.4.0 |
| Reactive API | Spring Boot WebFlux | 3.4.0 |
| Persistence | Spring Data JPA | 3.4.0 |
| Security | Spring Security | 3.4.0 |
| Validation | Hibernate Validator | 8.0.2.Final |
| DTO Mapping | MapStruct | 1.6.3 |
| Boilerplate | Lombok | 1.18.24 |
| JSON | Jackson Databind | 2.15.2 |
| Database | MySQL Connector/J | Latest |
| Testing | JUnit 5 + Testcontainers | 3.4.0 |

### Runtime Requirements
- **Database**: MySQL 5.7+ (via Docker Compose or local instance)
- **JDK**: Java 21
- **Memory**: Minimum 2GB RAM

---

## Project Structure

```
car_reservation_system/
├── src/
│   ├── main/
│   │   ├── java/com/af/carrsvt/
│   │   │   ├── CarReservationApplication.java     # Main entry point
│   │   │   ├── audit/                             # Audit logging utilities
│   │   │   ├── constant/                          # Application constants
│   │   │   ├── controller/                        # REST controllers (7 entities)
│   │   │   ├── dto/                               # Data Transfer Objects
│   │   │   ├── entity/                            # JPA entities (7 entities)
│   │   │   ├── exception/                         # Custom exceptions
│   │   │   ├── mapper/                            # MapStruct mappers
│   │   │   ├── repository/                        # Spring Data repositories
│   │   │   ├── security/                          # Security configs & services
│   │   │   └── service/                           # Business logic layer
│   │   └── resources/
│   │       ├── application.properties             # Database & app config
│   │       ├── static/                            # Static assets
│   │       └── templates/                         # Thymeleaf templates (optional)
│   └── test/
│       └── java/com/af/carrsvt/
│           ├── integration/                       # Integration tests
│           ├── ControllerIntegrationTests.java
│           ├── ContainerConfiguration.java
│           └── IntegrationTests.java
├── build.gradle                                    # Gradle build configuration
├── settings.gradle                                 # Gradle settings
├── compose.yaml                                    # Docker Compose for MySQL
├── DESIGN.md                                       # Architecture & design doc
├── DEVELOPMENT.md                                  # This file
└── HELP.md                                         # Spring Boot helper
```

---

## Database Schema (DDL)

### MySQL Database: `car_rsvt`

#### Create Database
```sql
CREATE DATABASE IF NOT EXISTS car_rsvt;
USE car_rsvt;
```

#### CUSTOMER Table
```sql
CREATE TABLE CUSTOMER (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    status VARCHAR(20) DEFAULT 'A',
    payment_method_1 VARCHAR(100),
    payment_method_2 VARCHAR(100),
    detail_payment_method_1 VARCHAR(255),
    detail_payment_method_2 VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### VEHICLE Table
```sql
CREATE TABLE VEHICLE (
    vehicle_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    driver_id BIGINT,
    vehicle_type VARCHAR(50) NOT NULL,
    license_plate VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    FOREIGN KEY (driver_id) REFERENCES DRIVER(driver_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### DRIVER Table
```sql
CREATE TABLE DRIVER (
    driver_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    phone_number VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### RESERVATION Table
```sql
CREATE TABLE RESERVATION (
    reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    pickup_time DATETIME(6),
    pickup_location VARCHAR(255),
    dropoff_location VARCHAR(255),
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES VEHICLE(vehicle_id) ON DELETE CASCADE,
    INDEX idx_customer_reservation (customer_id),
    INDEX idx_vehicle_reservation (vehicle_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### PAYMENT Table
```sql
CREATE TABLE PAYMENT (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    method VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (reservation_id) REFERENCES RESERVATION(reservation_id) ON DELETE CASCADE,
    INDEX idx_reservation_payment (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### PAYMENT_METHOD Table
```sql
CREATE TABLE PAYMENT_METHOD (
    payment_method_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    method_type VARCHAR(50),
    details VARCHAR(255),
    primary_method BOOLEAN DEFAULT FALSE,
    created_at DATETIME(6),
    FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id) ON DELETE CASCADE,
    INDEX idx_customer_payment_method (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### FEEDBACK Table
```sql
CREATE TABLE FEEDBACK (
    feedback_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    FOREIGN KEY (reservation_id) REFERENCES RESERVATION(reservation_id) ON DELETE CASCADE,
    INDEX idx_reservation_feedback (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## Entity Classes

### 1. Customer
**File**: [src/main/java/com/af/carrsvt/entity/Customer.java](src/main/java/com/af/carrsvt/entity/Customer.java)

**Relationships**:
- One-to-Many: RESERVATION
- One-to-Many: PAYMENT_METHOD

**Key Fields**: customerId (PK), username, email, phoneNumber, status, paymentMethod1/2

### 2. Vehicle
**File**: [src/main/java/com/af/carrsvt/entity/Vehicle.java](src/main/java/com/af/carrsvt/entity/Vehicle.java)

**Relationships**:
- Many-to-One: DRIVER
- One-to-Many: RESERVATION

**Key Fields**: vehicleId (PK), driverId (FK), vehicleType, licensePlate, status

### 3. Driver
**File**: [src/main/java/com/af/carrsvt/entity/Driver.java](src/main/java/com/af/carrsvt/entity/Driver.java)

**Relationships**:
- One-to-Many: VEHICLE

**Key Fields**: driverId (PK), name, licenseNumber, status, phoneNumber

### 4. Reservation
**File**: [src/main/java/com/af/carrsvt/entity/Reservation.java](src/main/java/com/af/carrsvt/entity/Reservation.java)

**Relationships**:
- Many-to-One: CUSTOMER
- Many-to-One: VEHICLE
- One-to-Many: PAYMENT
- One-to-Many: FEEDBACK

**Key Fields**: reservationId (PK), customerId (FK), vehicleId (FK), pickupTime, status

### 5. Payment
**File**: [src/main/java/com/af/carrsvt/entity/Payment.java](src/main/java/com/af/carrsvt/entity/Payment.java)

**Relationships**:
- Many-to-One: RESERVATION

**Key Fields**: paymentId (PK), reservationId (FK), amount, method, status

### 6. PaymentMethod
**File**: [src/main/java/com/af/carrsvt/entity/PaymentMethod.java](src/main/java/com/af/carrsvt/entity/PaymentMethod.java)

**Relationships**:
- Many-to-One: CUSTOMER

**Key Fields**: paymentMethodId (PK), customerId (FK), methodType, details, primaryMethod, createdAt

### 7. Feedback
**File**: [src/main/java/com/af/carrsvt/entity/Feedback.java](src/main/java/com/af/carrsvt/entity/Feedback.java)

**Relationships**:
- Many-to-One: RESERVATION

**Key Fields**: feedbackId (PK), reservationId (FK), rating, comment

---

## Component Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│   Client (Web/Mobile/API)               │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│   API Layer (Controllers)                │
│   - CustomerController                   │
│   - VehicleController                    │
│   - DriverController                     │
│   - ReservationController                │
│   - PaymentController                    │
│   - PaymentMethodController (NEW)        │
│   - FeedbackController                   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│   Service Layer (Business Logic)         │
│   - *Service classes (transactional)     │
│   - MapStruct Mappers (DTO ↔ Entity)     │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│   Data Access Layer (Repositories)       │
│   - Spring Data JPA Repositories         │
│   - JpaRepository<Entity, Long>          │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│   Database (MySQL)                       │
│   - 7 entities with relationships        │
└─────────────────────────────────────────┘
```

### Package Breakdown

| Package | Purpose | Key Classes |
|---------|---------|-------------|
| `controller` | REST endpoints | 7 Controllers (one per entity) |
| `service` | Business logic & transactions | 7 Services |
| `repository` | Database access | 7 JpaRepository implementations |
| `entity` | JPA domain models | 7 Entity classes |
| `dto` | Data transfer objects | 7 DTO classes |
| `mapper` | Entity-DTO conversion | 7 MapStruct mappers |
| `security` | Auth & AuthN | CustomerUserDetails, CustomerUserDetailsService, SecurityConfig |
| `exception` | Custom exceptions | N/A (to be implemented) |
| `audit` | Audit logging | N/A |
| `constant` | App constants | N/A |

---

## API Endpoints Summary

### Base URL: `http://localhost:8080/api`

#### Customers
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/customers/create` | Create new customer |
| GET | `/customers/get` | Fetch all customers |
| GET | `/customers/{id}` | Get customer by ID |
| PUT | `/customers/{id}` | Update customer |
| DELETE | `/customers/{id}` | Delete customer |

#### Vehicles
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/vehicles/create` | Create new vehicle |
| GET | `/vehicles/get` | Fetch all vehicles |
| GET | `/vehicles/{id}` | Get vehicle by ID |
| PUT | `/vehicles/{id}` | Update vehicle |
| DELETE | `/vehicles/{id}` | Delete vehicle |

#### Drivers
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/drivers/create` | Create new driver |
| GET | `/drivers/get` | Fetch all drivers |
| GET | `/drivers/{id}` | Get driver by ID |
| PUT | `/drivers/{id}` | Update driver |
| DELETE | `/drivers/{id}` | Delete driver |

#### Reservations
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/reservations/create` | Book new reservation |
| GET | `/reservations/get` | Fetch all reservations |
| GET | `/reservations/{id}` | Get reservation by ID |
| PUT | `/reservations/{id}` | Update reservation |
| DELETE | `/reservations/{id}` | Cancel reservation |

#### Payments
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/payments/create` | Create payment record |
| GET | `/payments/get` | Fetch all payments |
| GET | `/payments/{id}` | Get payment by ID |
| PUT | `/payments/{id}` | Update payment |
| DELETE | `/payments/{id}` | Delete payment |

#### Payment Methods *(NEW - Feb 17, 2026)*
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/payment-methods/create` | Add payment method |
| GET | `/payment-methods/get` | Get all methods (optional: filter by customerId) |
| GET | `/payment-methods/{id}` | Get method by ID |
| PUT | `/payment-methods/{id}` | Update payment method |
| DELETE | `/payment-methods/{id}` | Remove payment method |

#### Feedback
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/feedback/create` | Submit feedback |
| GET | `/feedback/get` | Fetch all feedback |
| GET | `/feedback/{id}` | Get feedback by ID |
| PUT | `/feedback/{id}` | Update feedback |
| DELETE | `/feedback/{id}` | Delete feedback |

---

## Key Configurations

### Database Configuration
**File**: [src/main/resources/application.properties](src/main/resources/application.properties)

```properties
spring.application.name=Car Reservation

# Database Connection
spring.datasource.url=jdbc:mysql://localhost:3306/car_rsvt
spring.datasource.username=theuser
spring.datasource.password=thepassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA & Hibernate
# spring.jpa.hibernate.ddl-auto=update  # Uncomment to auto-create/update schema
spring.jpa.show-sql=true                # Log all SQL queries
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect

# Security (configured in SecurityConfig class)
# - Uses Spring Security with in-memory or database-backed authentication
# - Passwords hashed with BCrypt (to be implemented in SecurityConfig)
```

### Security Configuration
**File**: [src/main/java/com/af/carrsvt/security/SecurityConfig.java](src/main/java/com/af/carrsvt/security/SecurityConfig.java)

- Spring Security enabled
- CustomerUserDetailsService loads users from database
- Authentication and authorization rules defined
- HTTPS recommended for production

---

## Build & Run Instructions

### Prerequisites - Local Development
- Java 21
- Gradle 8.0+ (or use included gradle wrapper)
- MySQL 5.7+ (or Docker Desktop for compose)
- Optional: IDE (IntelliJ IDEA, VS Code with Spring Extension Pack)

### Prerequisites - Container (Recommended)
- Docker 20.10+
- Docker Compose 2.0+
- No Java/Gradle installation needed in container mode

---

## Option 1: Full Containerized Setup (Recommended) 🐳

### Complete End-to-End Docker Deployment

This approach containerizes **both the MySQL database and the Spring Boot application** with automatic schema initialization.

#### Files Included:
- `Dockerfile` - Multi-stage build for optimized Spring Boot image
- `compose.yaml` - Docker Compose with MySQL + App services
- `init.sql` - Automatic database initialization script

#### Quick Start (One Command!)
```bash
# Navigate to project root
cd car_reservation_system

# Build and start everything
docker compose up -d

# Watch container logs
docker compose logs -f app

# Wait for "Started CarReservationApplication" message
```

**That's it!** The application will be available at `http://localhost:8080`

#### Container Details
```
├── MySQL Container (car-reservation-mysql)
│   └── Port: 3306
│   └── Database: car_rsvt
│   └── User: theuser / thepassword
│   └── Auto-initializes with init.sql schema
│
└── Spring Boot Container (car-reservation-app)
    └── Port: 8080
    └── Depends on: MySQL (healthy)
    └── Auto-configured environment variables
    └── Ready when health check passes
```

#### Container Health & Status
```bash
# Check if containers are running
docker compose ps

# View application logs
docker compose logs -f app

# View MySQL logs
docker compose logs -f mysql

# Check health of services
docker compose exec app curl http://localhost:8080/actuator/health
```

#### Build & Push (for Production)
```bash
# Build the application image only
docker compose build

# Rebuild without cache
docker compose build --no-cache

# Build and push to registry (optional)
docker build -t your-registry/car-reservation:latest .
docker push your-registry/car-reservation:latest
```

#### Stop & Cleanup
```bash
# Stop containers (keep volumes & running in background)
docker compose stop

# Stop and remove containers (volumes preserved)
docker compose down

# Stop and remove everything including volumes (database data cleared)
docker compose down -v

# Graceful shutdown - stop all services cleanly
docker compose stop -t 30  # Wait 30 seconds before killing
```

---

## How to Stop All Services (Complete Guide)

### Quick Reference - Stop Commands (Docker Compose Plugin)

| Use Case | Command | Effect |
|----------|---------|--------|
| **Pause services temporarily** | `docker compose stop` | Stops containers, keeps them for restart |
| **Graceful shutdown** | `docker compose stop -t 30` | Waits 30s before force-stop |
| **Stop & cleanup** | `docker compose down` | Removes containers, preserves volumes/DB |
| **Complete reset** | `docker compose down -v` | Removes everything including database |
| **Just stop running services** | `docker compose kill` | Immediately kill containers |

### Scenario-Based Stop Instructions

#### Scenario 1: End of Day (Pause, Keep Data)
```bash
# Gracefully stop all services, keeping database intact
docker compose stop -t 30

# Later, restart them
docker compose start
```
**Result**: Services stopped, all data preserved, ready to restart later.

#### Scenario 2: Local Development (Remove & Start Fresh)
```bash
# Stop and remove containers, keep database
docker compose down

# Restart later with: docker compose up -d
```
**Result**: Containers removed, database preserved, volumes intact.

#### Scenario 3: Complete Reset (Clear Everything)
```bash
# CAUTION: This deletes the database!
docker compose down -v
```
**Result**: All containers, networks, and volumes removed. Complete clean slate.

#### Scenario 4: Force Kill (Emergency)
```bash
# Forcefully kill all containers
docker compose kill

# Then remove
docker compose down
```
**Result**: Immediate stop, may interrupt transactions.

### Using the Interactive CLI

The easiest way - use the `docker-cli.sh` script:

```bash
./docker-cli.sh
# Select option "2) Stop application"
```

### Complete Workflow Example

```bash
# During development
docker compose up -d

# ... do work ...

# End of session - graceful stop
docker compose stop -t 30

# Next day - restart
docker compose start

# Later - full cleanup
docker compose down

# Even later - full reset
docker compose down -v
docker compose up -d
```

### Stopping Individual Services

```bash
# Stop only MySQL (app keeps running)
docker compose stop mysql

# Stop only the app (MySQL keeps running)
docker compose stop app

# Stop MySQL and remove it (app stays)
docker compose down --remove-orphans
```

### Verify Services Are Stopped

```bash
# Check container status
docker compose ps

# Should show all containers as "Exited"

# View logs to confirm shutdown
docker compose logs app | tail -20
```

---

## Option 2: Local Development Setup

### Setup Database (Option 2A: Docker Compose MySQL Only)
```bash
# Navigate to project root
cd car_reservation_system

# Start only MySQL container (for local development)
docker run -d \
  --name car-mysql \
  -e MYSQL_ROOT_PASSWORD=verysecret \
  -e MYSQL_DATABASE=car_rsvt \
  -e MYSQL_USER=theuser \
  -e MYSQL_PASSWORD=thepassword \
  -p 3306:3306 \
  mysql:8.0

# Initialize database
docker exec -i car-mysql mysql  -h 127.0.0.1 -u theuser -pthepassword car_rsvt < init.sql

# Verify connection
mysql -h 127.0.0.1 -u theuser  -h 127.0.0.1 -pthepassword car_rsvt -e "SHOW TABLES;"
```

### Setup Database (Option 2B: Local MySQL)
```bash
# Create database
mysql -u root -p
> CREATE DATABASE car_rsvt;
> CREATE USER 'theuser'@'localhost' IDENTIFIED BY 'thepassword';
> GRANT ALL PRIVILEGES ON car_rsvt.* TO 'theuser'@'localhost';
> FLUSH PRIVILEGES;
> EXIT;

# Initialize schema
mysql -u theuser -pthepassword car_rsvt < init.sql
```

### Build Project (Local)
```bash
# First, install Java 21 and Gradle (see prerequisites)

# Clean and build
./gradlew clean build -x test

# Build with tests
./gradlew clean build
```

### Run Application (Local)
```bash
# Using Gradle bootRun
./gradlew bootRun

# Or using JAR directly (after build)
java -jar build/libs/car-reservation-0.0.1-SNAPSHOT.jar
```

**Application starts at**: `http://localhost:8080`

### Verify Application (Local)
```bash
# Health check
curl http://localhost:8080/actuator/health

# Sample API call
curl -X GET http://localhost:8080/api/customers/get
```

---

## Container Deployment Guide

### Architecture Overview

```
┌─────────────────────────────────────┐
│  Docker Compose                     │
└─────────────────────────────────────┘
         ↓                    ↓
    ┌─────────┐          ┌─────────┐
    │ MySQL   │          │ App     │
    │ 3306    │←─────────→│ 8080    │
    │ Bridge  │          │ Bridge  │
    └─────────┘          └─────────┘
         ↓                    ↓
  car-reservation-    car-reservation-
  mysql:8.0           app:latest
```

### Common Deployment Scenarios

#### Scenario 1: Development (Full Stack in Docker)
**Use When**: You want isolated development environment without local Java/Gradle

```bash
cd car_reservation_system
docker compose up -d
# Everything ready in ~1 minute
```

**Result**:
- MySQL container running with auto-initialized schema
- Spring Boot app container running and healthy
- Both services connected via bridge network
- Data persisted in named volume `mysql_data`

**Access**:
- API: http://localhost:8080
- MySQL: localhost:3306 (theuser/thepassword)
- Logs: `docker compose logs -f app`

#### Scenario 2: Development (Local App, Containerized DB)
**Use When**: You want to code locally with hot-reload but use containerized MySQL

```bash
# Start only MySQL container
docker run -d \
  --name car-mysql \
  -e MYSQL_ROOT_PASSWORD=verysecret \
  -e MYSQL_DATABASE=car_rsvt \
  -e MYSQL_USER=theuser \
  -e MYSQL_PASSWORD=thepassword \
  -p 3306:3306 \
  -v mysql_data:/var/lib/mysql \
  mysql:8.0

# Wait for MySQL to be ready
sleep 10

# Initialize schema
docker exec -i car-mysql mysql -u theuser -pthepassword car_rsvt < init.sql

# Run app locally
./gradlew bootRun
```

#### Scenario 3: Testing in Containers
**Use When**: You want to run integration tests in isolated environment

```bash
# Build test image
docker compose build app

# Run all tests
docker compose run --rm app ./gradlew test

# Run specific tests
docker compose run --rm app ./gradlew test --tests ControllerIntegrationTests
```

#### Scenario 4: Production-like Deployment
**Use When**: You need to simulate production with proper networking and health checks

```bash
# Build production image
docker build -t car-reservation:1.0.0 .

# Run with production settings
docker run -d \
  --name car-reservation-prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/car_rsvt \
  -e SPRING_DATASOURCE_USERNAME=theuser \
  -e SPRING_DATASOURCE_PASSWORD=thepassword \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=validate \
  -p 8080:8080 \
  --link car-mysql:mysql \
  car-reservation:1.0.0
```

### Dockerfile Explained

```dockerfile
# Stage 1: Builder
# - Compiles Java code using OpenJDK 17
# - Runs Gradle build
# - Creates JAR file

# Stage 2: Runtime
# - Starts with JRE only (smaller image)
# - Copies JAR from builder
# - Includes curl for health checks
# - Exposes port 8080
# - Runs health check every 30s
```

**Image Size**:
- Builder stage: ~800MB
- Final image: ~300MB (multi-stage optimization)

### Environment Variables in Container

When running in Docker, these variables are automatically set:

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/car_rsvt
SPRING_DATASOURCE_USERNAME=theuser
SPRING_DATASOURCE_PASSWORD=thepassword
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

To override, use:
```bash
docker compose run -e SPRING_JPA_HIBERNATE_DDL_AUTO=validate app
```

### Database Initialization (init.sql)

Automatically runs on first MySQL container start:
- Creates all 7 tables with constraints
- Creates indexes for performance
- Inserts sample data (3 customers, 3 drivers, 3 vehicles)

To add custom initialization:
```bash
# Add SQL to init.sql
# Rebuild container
docker compose build --no-cache
docker compose up -d
```

### Troubleshooting Container Issues

| Issue | Solution |
|-------|----------|
| Container won't start | `docker compose logs app` to see error |
| "Connection refused" to MySQL | Ensure MySQL is healthy: `docker compose ps` |
| Port 8080 already in use | Stop other service: `lsof -i :8080` or use different port |
| Database not initialized | Check: `docker exec car-mysql mysql -u root -pverysecret car_rsvt -e "SHOW TABLES;"` |
| Out of disk space | Clean up: `docker system prune -a` |
| Slow container startup | Increase Docker resources in settings |
| App container exits immediately | Check logs: `docker compose logs app` |

### Network Connectivity

```
Docker Bridge Network: car-reservation-network
├── MySQL: dns name is 'mysql' (port 3306)
├── App: dns name is 'app' (port 8080)
└── External: localhost:8080 and localhost:3306
```

**Important**: Inside containers, use service names (mysql, app).
From host machine, use localhost.

### Volume Management

```bash
# View volumes
docker volume ls

# Inspect volume
docker volume inspect car_reservation-system_mysql_data

# Backup MySQL data
docker run --rm -v car_reservation-system_mysql_data:/data -v $(pwd):/backup \
  busybox tar czf /backup/mysql_backup.tar.gz -C /data .

# Restore MySQL data
docker run --rm -v car_reservation-system_mysql_data:/data -v $(pwd):/backup \
  busybox tar xzf /backup/mysql_backup.tar.gz -C /data

# Completely remove volume (CAUTION: deletes all data)
docker volume rm car_reservation-system_mysql_data
```



### Test Structure
**Directory**: [src/test/java/com/af/carrsvt/](src/test/java/com/af/carrsvt/)

### Integration Tests
- **IntegrationTests.java**: Helper class for setting up test environment
- **ContainerConfiguration.java**: Testcontainers MySQL container setup
- **ControllerIntegrationTests.java**: End-to-end API tests

### Running Tests (Container-Based)
```bash
# Build image with tests
docker compose build app

# Run tests inside container
docker compose run --rm app ./gradlew test

# Run specific test
docker compose run --rm app ./gradlew test --tests ControllerIntegrationTests

# Run with coverage
docker compose run --rm app ./gradlew test jacocoTestReport

# Copy test reports from container to host
docker compose run --rm app cp -r build/reports ./
```

### Running Tests (Local)
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests ControllerIntegrationTests

# Run with coverage
./gradlew test jacocoTestReport
```

### View Test Results
```bash
# After tests complete, view HTML report
open build/reports/tests/test/index.html  # macOS
xdg-open build/reports/tests/test/index.html  # Linux
start build/reports/tests/test/index.html  # Windows
```

### Test Technologies
- JUnit 5 (Jupiter)
- Testcontainers (MySQL)
- Spring Boot Test
- MockMvc (for unit tests)

### Coverage Goals
- Controllers: 70%+ coverage
- Services: 80%+ coverage
- Entities: N/A (POJOs mostly)

---

## Development Notes

### Current Implementation Status

#### ✅ Completed (as of Feb 18, 2026)
- [x] 7 Entity models (Customer, Vehicle, Driver, Reservation, Payment, PaymentMethod, Feedback)
- [x] 7 Controllers with CRUD endpoints
- [x] 7 Services with business logic
- [x] 7 Repositories (Spring Data JPA)
- [x] 7 DTOs for API layer
- [x] 7 MapStruct mappers
- [x] Spring Security integration
- [x] Testcontainers setup
- [x] Database schema (MySQL)
- [x] Docker Compose for local MySQL

#### Recent Fixes (Feb 18, 2026)

- Lower-cased SQL identifiers in `init.sql` to avoid MySQL case-sensitivity issues on Linux.
- Added `src/test/resources/application.properties` to enable `spring.jpa.hibernate.ddl-auto=create` for tests so Testcontainers-managed test DB is DDL-initialized by Hibernate during tests.
- Fixed `PaymentMethod` JPA mapping so `customer_id` is insertable (ensures payment methods persist the FK value when created).
- Adjusted integration test setup order to delete child entities (payment methods, reservations, vehicles, drivers) before parent `customer` to avoid foreign-key constraint failures.
- Re-ran full test suite: all tests now pass locally (`./gradlew clean test`).

These changes ensure reliable test runs in CI and on Linux developer machines.

#### 🔄 In Progress / Recommended
- [ ] Global error handling (@ControllerAdvice)
- [ ] Input validation on DTOs (JSR-380 annotations)
- [ ] Custom exception handling
- [ ] Audit logging for all CRUD operations
- [ ] Pagination & filtering on list endpoints
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Authentication JWT tokens (vs. form-based)
- [ ] Password hashing (BCrypt in SecurityConfig)
- [ ] Unit tests for service layer
- [ ] Email notifications (for confirmations/updates)

### Code Style & Conventions

**Naming**:
- Classes: PascalCase (e.g., `CustomerService`, `PaymentMethodController`)
- Methods: camelCase (e.g., `createPaymentMethod()`, `getByCustomerId()`)
- Constants: UPPER_SNAKE_CASE
- Database columns: snake_case (e.g., `payment_method_id`)

**Annotations Used**:
- `@Entity`: JPA entities
- `@RestController`: REST endpoints
- `@Service`: Service layer beans
- `@Repository`: Data access beans
- `@Autowired`: Dependency injection
- `@Transactional`: Database transactions
- `@Valid`: Input validation
- `@Mapper` (MapStruct): Entity-DTO conversion
- Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@ToString`

**Code Organization**:
1. Package structure: domain → service → controller
2. Layered architecture: one class per layer per entity
3. Bean lifecycle: constructor injection > field injection

---

## Enhancement Roadmap

### Phase 1: Robustness (Priority: HIGH)
1. **Global Error Handling**
   - Create `@ControllerAdvice` for consistent error responses
   - Map custom exceptions to HTTP status codes
   - Return standardized error JSON (e.g., `{ "error": "...", "status": 400 }`)

2. **Input Validation**
   - Add JSR-380 annotations to all DTOs
   - Example: `@NotBlank`, `@Email`, `@Min`, `@Max`
   - Include `@Valid` on all controller endpoints

3. **Audit Logging**
   - Log CREATE, UPDATE, DELETE operations
   - Capture timestamp, user, operation type, entity changes
   - Store in database or external audit table

### Phase 2: Features (Priority: MEDIUM)
1. **Advanced Queries**
   - Add filtering (by status, date range, etc.)
   - Add pagination (limit, offset)
   - Add sorting (by date, rating, etc.)
   - Example: `GET /api/reservations/get?status=CONFIRMED&limit=10&offset=0`

2. **Business Logic Enhancements**
   - Validate reservation availability (vehicle + time)
   - Calculate rental pricing based on duration & vehicle type
   - Auto-transition reservation status (PENDING → CONFIRMED → COMPLETED)
   - Set driver availability based on vehicle assignment

3. **Notifications**
   - Email confirmations on reservation creation
   - SMS reminders before pickup
   - Payment receipt emails

### Phase 3: Documentation & DevOps (Priority: MEDIUM)
1. **API Documentation**
   - Integrate Springdoc OpenAPI (Swagger UI)
   - Auto-generate OpenAPI 3.0 spec
   - Accessible at `/swagger-ui.html`

2. **Production Deployment**
   - Create production `application-prod.properties`
   - Dockerfile for containerization
   - Kubernetes manifests (optional)
   - CI/CD pipeline (GitHub Actions / GitLab CI)

3. **Monitoring & Logging**
   - Spring Boot Actuator endpoints
   - Centralized logging (ELK stack or similar)
   - Application metrics (Micrometer)

### Phase 4: Scalability (Priority: LOW)
1. **Caching**
   - Redis for session/query caching
   - Cache invalidation strategy

2. **Microservices Migration**
   - Split into modules: Customer, Reservation, Billing, Catalog
   - Event-driven architecture (message queues)
   - API Gateway pattern

3. **Performance Optimization**
   - Database indexing review
   - Query optimization
   - Connection pooling tuning

---

## Useful Commands

### Docker Compose (Container-Based) - Using Plugin
```bash
docker compose up -d                # Start all services (MySQL + App)
docker compose down                 # Stop and remove containers
docker compose down -v              # Stop and remove with volumes (delete DB data)
docker compose ps                   # Show running containers
docker compose logs -f app          # Follow application logs
docker compose logs -f mysql        # Follow MySQL logs
docker compose build                # Build images without starting
docker compose build --no-cache     # Force rebuild without cache
docker compose restart              # Restart all services
docker compose stop                 # Stop all services (keep containers)
docker compose stop -t 30           # Graceful stop with 30s timeout
docker compose start                # Start stopped containers
docker compose exec app sh          # Open shell in app container
docker compose exec mysql bash      # Open shell in MySQL container
```

### Docker (Manual Container Management)
```bash
docker images                       # List local images
docker ps                          # Show running containers
docker ps -a                       # Show all containers
docker logs <container_id>         # View container logs
docker logs -f <container_id>      # Follow container logs
docker stop <container_id>         # Stop container
docker rm <container_id>           # Remove container
docker exec -it <container> sh     # Open shell in container
docker rmi <image_id>              # Remove image
```

### Gradle (Local Development)
```bash
./gradlew clean                     # Clean build directory
./gradlew build                     # Build project
./gradlew bootRun                   # Run application
./gradlew test                      # Run tests
./gradlew tasks                     # Show all available tasks
./gradlew dependencies              # Show dependency tree
```

### Git (when repo initialized)
```bash
git status                          # Check status
git add .                           # Stage changes
git commit -m "message"             # Commit changes
git log --oneline -10               # View recent commits
```

### MySQL (via Docker Compose)
```bash
docker compose exec mysql mysql -u theuser -pthepassword car_rsvt  # Connect to MySQL
mysql> SHOW TABLES;                 # List tables
mysql> DESC CUSTOMER;               # Show table structure
mysql> SELECT * FROM CUSTOMER;      # Query data
mysql> EXIT;                        # Disconnect
```

### MySQL (Local Connection)
```bash
mysql -u theuser -p car_rsvt          # Connect to database
SHOW TABLES;                        # List tables
DESC CUSTOMER;                      # Show table structure
SELECT * FROM CUSTOMER;             # Query data
EXIT;                               # Disconnect

# Via Docker Compose
docker compose exec mysql mysql -u theuser -pthepassword car_rsvt  # Connect via compose
```

---

## Contact & Support

**Project Owner**: theuser  
**Repository**: `/home/theuser/workspace/car_reservation_system`  
**Last Updated**: February 17, 2026

For questions about architecture, refer to [DESIGN.md](DESIGN.md).  
For architectural decisions and observations, review [DESIGN.md](DESIGN.md).

---

**End of Development Guide**
