$content = @'
# Client Manager

A Spring Boot application for managing clients, invoices, and payments for freelancers.

## Overview

ClientManager is a web-based application built with Spring Boot 3.5.4 that helps freelancers manage their client relationships, track invoices, and process payments. The application provides a secure, user-friendly interface for handling all aspects of client management.

## Tech Stack

- **Java**: 17 (being upgraded to 21 LTS)
- **Spring Boot**: 3.5.4
- **Spring Security**: OAuth2 Client + Spring Security 6
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate 6.6.22
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven 3.9+
- **Testing**: JUnit 5 with Spring Boot Test
- **Additional Libraries**:
  - Lombok 1.18.30
  - PostgreSQL JDBC Driver 42.7.8
  - Spring Boot Actuator
  - Spring Validation

## Prerequisites

- **Java**: JDK 17 or higher (Java 21 recommended)
- **PostgreSQL**: 10.x or higher
- **Maven**: 3.9.x or higher
- **Git**: For version control

## Project Structure

Core Components:
- `Application.java` - Spring Boot application entry point
- `config/` - PostgreSQL and Security configurations
- `controller/` - REST API endpoints for Clients and Invoices
- `model/` - Entity classes (Client, Invoice, Payment, User, Status)
- `repository/` - Data Access Objects (DAO)
- `service/` - Business logic layer

Resources:
- `application.properties` - Application configuration
- `static/` - Static assets (CSS, JS, images)
- `templates/` - Thymeleaf HTML templates

## Installation & Setup

### 1. Clone the Repository

\`\`\`bash
git clone <repository-url>
cd clientManager
\`\`\`

### 2. Configure PostgreSQL

Update your PostgreSQL connection details in `application.properties`:

\`\`\`properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clientmanager
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
\`\`\`

### 3. Build the Project

Using Maven wrapper:

\`\`\`bash
./mvnw clean install
\`\`\`

## Configuration

The application uses Spring Security 6 with OAuth2 Client support. Key configuration options:

- `server.port`: Application port (default: 8080)
- `spring.datasource.url`: PostgreSQL database URL
- `spring.jpa.show-sql`: Show SQL queries (default: false)
- `spring.security.oauth2.client.*`: OAuth2 provider configuration

## Running the Application

### Development Mode

\`\`\`bash
./mvnw spring-boot:run
\`\`\`

### Production Build

\`\`\`bash
./mvnw clean package
java -jar target/client.manager-0.0.1-SNAPSHOT.jar
\`\`\`

The application will start on `http://localhost:8080`

## API Endpoints

### Client Management
- `GET /clients` - List all clients
- `POST /clients` - Create new client
- `GET /clients/{id}` - Get client details
- `PUT /clients/{id}` - Update client
- `DELETE /clients/{id}` - Delete client

### Invoice Management
- `GET /invoices` - List all invoices
- `POST /invoices` - Create new invoice
- `GET /invoices/{id}` - Get invoice details
- `PUT /invoices/{id}` - Update invoice
- `DELETE /invoices/{id}` - Delete invoice

## Features

- ✅ Client relationship management
- ✅ Invoice creation and tracking
- ✅ Payment processing
- ✅ User authentication with OAuth2
- ✅ PostgreSQL database integration
- ✅ RESTful API endpoints
- ✅ Responsive web interface with Thymeleaf
- ✅ Spring Boot Actuator monitoring endpoints

## Testing

Run tests with:

\`\`\`bash
./mvnw test
\`\`\`

Current test status: All tests passing with Spring Boot 3.5.4

## Java 21 Upgrade

This project is currently being upgraded from Java 17 to Java 21 (LTS).

Benefits of Java 21:
- Long-term support until September 2028
- Performance improvements
- Virtual Threads (Project Loom) support
- Enhanced pattern matching
- Record improvements

## Build & Deployment

### Maven Lifecycle
- `clean` - Remove build artifacts
- `compile` - Compile source code
- `test` - Run unit tests
- `package` - Create JAR file
- `install` - Install in local repository

### Spring Boot Maven Plugin

The project uses Spring Boot Maven Plugin for:
- Creating executable JAR with embedded Tomcat
- Running Spring Boot applications
- Packaging final application

## Troubleshooting

### Database Connection Issues
Ensure PostgreSQL is running and credentials in `application.properties` are correct.

### Missing Templates
If you see warnings about missing templates, ensure `src/main/resources/templates/` directory exists.

### Port Already in Use
Change the server port in `application.properties`:
\`\`\`properties
server.port=8081
\`\`\`

## Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -am 'Add feature'`
3. Push to branch: `git push origin feature/your-feature`
4. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues or questions, please open an issue in the repository.
'@; $content