# Food Delivery ( ReactJS + Vite + Java Spring Boot + MongoDB)

Foodies Delivery - Payment with Sepay

Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Run locally](#run-locally)
  - [Run with Docker](#run-with-docker)
- [API documentation](#api-documentation)
- [Database and Migrations](#database-and-migrations)
- [Payment (Sepay) integration](#payment-sepay-integration)
- [Testing](#testing)
- [Monitoring and Health](#monitoring-and-health)
- [Security considerations](#security-considerations)
- [CI / CD](#ci--cd)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

Overview
--------
FoodDelivery is a Spring Boot microservice/web application that powers a food ordering and delivery system, with integrated payment processing via Sepay. It provides REST APIs for customers, restaurants, orders, and payments.

Features
--------
- User registration, authentication (JWT)
- Restaurant listings and menu management
- Order creation, status tracking, and history
- Payment processing integrated with Sepay
- Admin dashboards and basic reporting
- Health checks and metrics (Spring Boot Actuator)
- API documentation (OpenAPI / Swagger)

Tech stack
----------
- Java (recommended: 17+ LTS)
- Spring Boot (3.x+ recommended)
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- PostgreSQL / MongoDB
- Maven as build tool
- Optional: Docker for containerization

Architecture
------------
- REST API backend (Spring Boot)
- Relational database for persistent storage
- Payment gateway integration (Sepay) for processing transactions
- JWT-based authentication for APIs
- Optional background workers (for notifications, webhook processing)

Getting Started
---------------
These instructions will get you a copy of the project up and running locally.

Prerequisites
- Java 17+ installed (JAVA_HOME configured)
- Maven 3.6+ (or use the included mvnw)
- PostgreSQL (or other supported RDBMS) / MongoDB
- Docker (optional, for containers)
- Sepay account and API credentials (merchant ID, secret key)

Configuration
-------------
The project reads configuration from Spring Boot properties / YAML and environment variables. Example (application.yml snippet):

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:fooddelivery}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true

sep:
  pay:
    merchant-id: ${SEPAY_MERCHANT_ID}
    secret-key: ${SEPAY_SECRET_KEY}
    api-base-url: ${SEPAY_API_BASE:https://api.sepay.example}

security:
  jwt:
    secret: ${JWT_SECRET}
    expiration-ms: 3600000
```

Environment variables
- DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD — database connection
- SEPAY_MERCHANT_ID — Sepay merchant identifier
- SEPAY_SECRET_KEY — Sepay secret/API key
- SEPAY_API_BASE — Sepay base API URL (optional, defaults to production or sandbox)
- JWT_SECRET — secret used to sign JWT tokens
- (Optional) S3_* credentials if using object storage for images

Run locally
-----------
1. Configure environment variables or update application.yml.
2. Build the project:
   - With Maven:
     - ./mvnw clean package
     - or mvn clean package
3. Run:
   - ./mvnw spring-boot:run
   - or java -jar target/fooddelivery-*.jar

Run with Docker
---------------
Example Docker commands (adjust as required):

Build:
docker build -t fooddelivery:latest .

Run:
docker run -d \
  -p 8080:8080 \
  -e DB_HOST=your-db-host \
  -e DB_PORT=5432 \
  -e DB_NAME=fooddelivery \
  -e DB_USER=postgres \
  -e DB_PASSWORD=secret \
  -e SEPAY_MERCHANT_ID=your_merchant_id \
  -e SEPAY_SECRET_KEY=your_secret \
  --name fooddelivery \
  fooddelivery:latest

API Documentation
-----------------
- Swagger / OpenAPI: /swagger-ui/index.html or /v3/api-docs
- Common endpoints:
  - POST /api/auth/login — log in and receive JWT
  - POST /api/users — register
  - GET /api/restaurants — list restaurants
  - GET /api/restaurants/{id}/menu — get menu
  - POST /api/orders — create order
  - GET /api/orders/{id} — get order details
  - POST /api/payments — initiate payment (Sepay)
  - Webhook endpoint for Sepay callbacks: /api/payments/webhook (configure in Sepay dashboard)

Database and Migrations
-----------------------
Use Flyway or Liquibase for schema migrations. Typical Flyway usage:
- Place migration scripts under src/main/resources/db/migration
- Flyway will run on application startup (check configuration)

If you prefer manual SQL for initial setup, create the DB and a user:
```sql
CREATE DATABASE fooddelivery;
CREATE USER food_user WITH PASSWORD 'change_me';
GRANT ALL PRIVILEGES ON DATABASE fooddelivery TO food_user;
```

Payment (Sepay) integration
---------------------------
This app integrates with Sepay to process card or wallet payments. High-level flow:
1. Client places an order and requests payment.
2. Backend creates a payment request and calls Sepay's create-payment API using SEPAY_MERCHANT_ID and SEPAY_SECRET_KEY.
3. Redirect or present a payment form per Sepay's integration method.
4. Sepay returns payment result asynchronously via webhook to /api/payments/webhook.
5. Backend verifies signature on webhook (use SEPAY_SECRET_KEY) and updates order status.

Important:
- Do NOT log full payment credentials or card details.
- Use HTTPS when communicating with Sepay.
- Implement replay protection and verify webhook signatures.

Sample pseudo-code (server-side call):
```java
// build request payload -> send to SEPAY API with merchant id + secret
// handle response and store transaction reference
```

Testing
-------
- Unit tests: mvn test
- Integration tests: configure test database and run mvn -DskipTests=false verify
- For payment flows, use Sepay sandbox/test credentials and test webhook simulator if provided.

Monitoring and Health
---------------------
- Spring Boot Actuator endpoints:
  - /actuator/health
  - /actuator/metrics
  - /actuator/prometheus (if configured)
- Expose only necessary actuator endpoints in production and protect them behind authentication.

Security considerations
-----------------------
- Store SEPAY_SECRET_KEY and JWT_SECRET in a secure secrets manager (HashiCorp Vault, AWS Secrets Manager, GitHub Secrets for CI).
- Rotate keys periodically.
- Enforce HTTPS and HSTS in production.
- Follow PCI-DSS guidelines when handling card data — prefer delegated or tokenized payment flows so card data never hits your servers.
- Rate limit public endpoints to reduce abuse.

CI / CD
-------
- Add GitHub Actions (or your CI) to build, test, and optionally publish Docker images.
- Example stages:
  - checkout, build, run tests
  - build Docker image and push to registry on tags
  - deploy to staging/production via Terraform/Helm/Ansible

Contributing
------------
Contributions are welcome. Please:
1. Fork the repository
2. Create a feature branch: git checkout -b feature/your-feature
3. Commit your changes and open a pull request with a clear description
4. Ensure tests are added/updated and build passes

License
-------
This project is released under the MIT License. See LICENSE for details.

Contact
-------
- Developer: Duc Nguyen (Ducnguyen-arch)
Troubleshooting
---------------
- Cannot connect to DB: verify DB_HOST, DB_PORT, DB_USER, DB_PASSWORD and network access.
- Payment errors: verify SEPAY_MERCHANT_ID and SEPAY_SECRET_KEY; check Sepay sandbox/production endpoints and webhook configuration.
- Application fails to start: check logs for missing configuration or required environment variables.

Acknowledgements
----------------
- Built with Spring Boot and many great open-source libraries.
- Sepay — payment gateway used for transaction processing.
