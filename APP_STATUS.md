# Application Status Document

## Project Overview

clientManager is a Spring Boot 3.5.4 application for managing clients and invoices for a freelancer or small-business
workflow. The current implementation focuses on a secure authentication flow, role-based access, and a lightweight
static frontend.

## Current Tech Stack

- Java 17
- Spring Boot 3.5.4
- Spring Security with JWT authentication
- Spring Data JPA
- H2 database for local development
- Thymeleaf and static HTML/JavaScript frontend assets

## Current Implementation Status

### ✅ Implemented

- Spring Security configuration with role-based authorization for:
    - `/user/**` → `USER`
    - `/client/**` → `CLIENT`
    - `/admin/**` → `ADMIN`
- JWT-based authentication flow
- Login and registration endpoints under `/auth/login` and `/auth/register`
- Password hashing with BCrypt
- User entity with role-based authorities
- Static login/register/dashboard pages under `src/main/resources/static/`
- Local development database fallback using H2

### 🔧 Key Backend Components

- Security config: `src/main/java/com/project/client/manager/config/SecurityConfig.java`
- Auth controller: `src/main/java/com/project/client/manager/controller/AuthController.java`
- JWT service: `src/main/java/com/project/client/manager/security/JwtService.java`
- JWT filter: `src/main/java/com/project/client/manager/security/JwtAuthenticationFilter.java`
- User service: `src/main/java/com/project/client/manager/service/UserService.java`
- User model: `src/main/java/com/project/client/manager/model/User.java`

## Current Runtime Notes

The project compiles successfully, and the authentication-related code is in place. The main remaining limitation during
local execution is environment-related port binding issues when launching the embedded Tomcat server from this workspace
environment.

## Local Run Command

From the project root:

```bash
./mvnw spring-boot:run
```

Then open:

- `http://localhost:8080/`
- `http://localhost:8080/login.html`
- `http://localhost:8080/register.html`

## Known Focus Areas

- Verify the full login/register flow end to end in a local browser session
- Add or refine role-specific dashboard behavior for `USER`, `CLIENT`, and `ADMIN`
- Optionally switch from H2 to a persistent PostgreSQL setup for staging or production

## Verification Summary

- Backend compile check completed successfully
- Auth-related regression test path was exercised successfully
