# Warehouse Management API - Spring Boot Backend

A RESTful API built with Spring Boot for managing warehouse inventory including categories, items, and user authentication. This backend serves as the API layer for a comprehensive warehouse management system.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [API Endpoints](#-api-endpoints)
- [Database Schema](#-database-schema)
- [Security](#-security)
- [Development](#-development)
- [Testing](#-testing)
- [Contributing](#-contributing)

## ✨ Features

- **User Management**: User registration, authentication, and role-based access control
- **Category Management**: CRUD operations for product categories
- **Item Management**: Complete inventory item management with category associations
- **JWT Authentication**: Secure token-based authentication system
- **Role-Based Access**: Different access levels for users and administrators
- **PostgreSQL Integration**: Robust database operations with JPA/Hibernate
- **RESTful Design**: Clean, standardized API endpoints
- **Global Exception Handling**: Centralized error handling and response formatting
- **Development Tools**: Hot reload with Spring DevTools for faster development

## 🛠 Tech Stack

- **Java 21** - Modern Java features and performance
- **Spring Boot 3.5.5** - Enterprise-grade framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database abstraction layer
- **PostgreSQL** - Production-ready database
- **JWT (JSON Web Tokens)** - Stateless authentication
- **Lombok** - Reduced boilerplate code
- **Maven** - Dependency management and build tool

## 📋 Prerequisites

Before running this application, make sure you have:

- **Java 21** or higher
- **Maven 3.6+** 
- **PostgreSQL 12+** database server
- **Git** (for cloning the repository)

## 🚀 Installation

1. **Clone the repository**
   ```bash
   git clone
   ```

2. **Install dependencies**
   ```bash
   ./mvnw clean install
   ```

## ⚙️ Configuration

1. **Database Setup**
   - Create a PostgreSQL database for the application
   - Note your database URL, username, and password

2. **Application Configuration**
   ```bash
   # Copy the example configuration file
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

3. **Update Configuration**
   Edit `src/main/resources/application.properties` with your values:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/warehouse_db
   spring.datasource.username=your_db_username
   spring.datasource.password=your_db_password
   
   # JWT Secret (Generate a secure secret key)
   jwt.secret=your_very_long_and_secure_jwt_secret_key_here
   
   # JPA Configuration
   spring.jpa.hibernate.ddl-auto=update
   logging.level.org.springframework.security=DEBUG
   ```

## 🏃‍♂️ Running the Application

### Development Mode
```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

### Production Mode
```bash
# Build the application
./mvnw clean package

# Run the JAR file
java -jar target/api-springboot-0.0.1-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`

## 🔗 API Endpoints

### Authentication
| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | User login | None |
| POST | `/api/auth/register` | User registration | None |

### Categories
| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|---------------|
| GET | `/api/categories` | Get all categories | JWT |
| GET | `/api/categories/{id}` | Get category by ID | JWT |
| POST | `/api/categories` | Create new category | JWT |
| PATCH | `/api/categories/{id}` | Update category | JWT |
| DELETE | `/api/categories/{id}` | Delete category | JWT |

### Items
| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|---------------|
| GET | `/api/items` | Get all items | JWT |
| GET | `/api/items/{id}` | Get item by ID | JWT |
| POST | `/api/items` | Create new item | JWT |
| PATCH | `/api/items/{id}` | Update item | JWT |
| DELETE | `/api/items/{id}` | Delete item | JWT |

### Users
| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|---------------|
| GET | `/api/users` | Get all users | JWT (Admin) |
| GET | `/api/users/{id}` | Get user by ID | JWT |
| POST | `/api/users` | Create new user | JWT (Admin) |
| PATCH | `/api/users/{id}` | Update user | JWT |
| DELETE | `/api/users/{id}` | Delete user | JWT (Admin) |

### Request/Response Examples

#### User Registration
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123",
  "email": "john@example.com"
}
```

#### Create Category
```bash
POST /api/categories
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

## 🗄️ Database Schema

The application uses the following main entities:

### Users Table
- `id` (BIGINT, Primary Key)
- `username` (VARCHAR, Unique, Not Null)
- `password` (VARCHAR, Not Null)
- `email` (VARCHAR, Unique, Not Null)
- `role` (VARCHAR, Not Null)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### Categories Table
- `id` (BIGINT, Primary Key)
- `name` (VARCHAR, Unique, Not Null)
- `description` (TEXT)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### Items Table
- `id` (BIGINT, Primary Key)
- `name` (VARCHAR, Not Null)
- `description` (TEXT)
- `category_id` (BIGINT, Foreign Key)
- `quantity` (INTEGER)
- `price` (DECIMAL)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

## 🔐 Security

- **JWT Authentication**: Stateless token-based authentication
- **Password Encryption**: BCrypt hashing for secure password storage
- **Role-Based Access Control**: Different permissions for users and administrators
- **CORS Configuration**: Configured for frontend integration
- **Input Validation**: Request validation and sanitization
- **Exception Handling**: Secure error responses without sensitive information exposure

## 🛠 Development

### Project Structure
```
src/
├── main/
│   ├── java/com/xw/api/
│   │   ├── common/          # Shared utilities and enums
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data transfer objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions and handlers
│   │   ├── filter/         # Security filters
│   │   ├── repository/     # Data repositories
│   │   ├── service/        # Business logic services
│   │   └── utils/          # Utility classes
│   └── resources/
│       ├── application.properties
│       └── application.properties.example
└── test/                   # Test files
```

### Code Style
- Follow Spring Boot best practices
- Use Lombok annotations to reduce boilerplate
- Implement proper exception handling
- Write meaningful commit messages
- Add comprehensive JavaDoc comments

### Adding New Features
1. Create entity classes in `entity/`
2. Add repository interfaces in `repository/`
3. Implement service classes in `service/`
4. Create DTOs in `dto/`
5. Add REST controllers in `controller/`
6. Update security configuration if needed

## 🧪 Testing

### Run Tests
```bash
# Run all tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report
```

### Test Structure
- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test API endpoints and database operations
- **Security Tests**: Verify authentication and authorization

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow existing code patterns and conventions
- Add tests for new functionality
- Update documentation for API changes
- Ensure all tests pass before submitting PR
- Use meaningful commit messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check existing issues for similar problems
- Refer to Spring Boot documentation

---

**Note**: This is the backend API component. Make sure to also set up the corresponding frontend application for a complete warehouse management system.