# 🏦 Online Banking System Backend

A robust, secure banking backend API built with Spring Boot 3.2.4 and Java 21, featuring comprehensive banking operations, JWT authentication, and email integration.

## 🌟 Features

### 🔐 **Authentication & Security**
- JWT-based authentication with refresh token support
- Role-based authorization (Customer/Admin)
- Password strength validation
- OTP verification for account activation
- Secure password hashing with BCrypt

### 💰 **Core Banking Operations**
- Account creation and management
- Money deposit and withdrawal
- Fund transfers between accounts
- Balance inquiries and transaction history
- Transaction limits management
- Real-time balance updates

### 📧 **Communication (Enable or Disable in property file)**
- Email notifications with HTML templates
- OTP generation and verification
- Welcome emails for new customers
- Password reset notifications

### 🛡️ **Security Features**
- Global exception handling
- Input validation
- Transaction logging
- Account suspension capabilities
- Secure API endpoints

## 🛠️ Tech Stack

### **Backend Framework**
- **Java 21** - Latest LTS version
- **Spring Boot 3.2.4** - Main framework
- **Spring Security 6.2.4** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework

### **Database**
- **MySQL 8.0** - Production database
- **H2** - In-memory database for testing

### **Security & Authentication**
- **JWT (jjwt) 0.12.5** - Token management
- **BCrypt** - Password hashing

### **Email & Templates**
- **Spring Boot Mail** - Email service
- **Thymeleaf** - HTML email templates

### **Build & Deployment**
- **Maven 3.10.1** - Build tool
- **Docker & Docker Compose** - Containerization
- **JUnit 5** - Testing framework

## 🚀 Getting Started

### **Prerequisites**
- Java 21 JDK
- Maven 3.10.1+
- MySQL 8.0 (or Docker)
- Git

### **Environment Setup**

1. **Clone the repository**
   ```bash
   git clone https://github.com/devhnry/online-banking-backend.git
   cd online-banking-backend
   ```

2. **Set up environment variables (Depending on 'dev or qa' environment)**
   ```bash
   # Database Configuration
   export DATABASE_NAME=your_db_name
   export DATABASE_USERNAME=your_db_username
   export DATABASE_PASSWORD=your_db_password
   
   # Email Configuration (Gmail SMTP)
   export EMAIL_HOST=smtp.gmail.com
   export EMAIL_PASSWORD=your_app_password
   export EMAIL_SENDER=your_email@gmail.com
   export EMAIL_PORT=587
   ```

3. **Database Setup**
   ```sql
   CREATE DATABASE onlinebanking;
   CREATE USER 'banking_user'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON onlinebanking.* TO 'banking_user'@'localhost';
   ```

### **Running the Application**

#### **Local Development**
```bash
# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

### **Accessing the Application**
- **API Base URL**: `http://localhost:6000`
- **H2 Console** (Development): `http://localhost:6000/h2-console`
- **Health Check**: `GET http://localhost:6000/actuator/health`

## 📋 API Documentation

### **Authentication Endpoints**

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/onboard` | Register new customer | ❌ |
| POST | `/auth/send-otp` | Send OTP for verification | ❌ |
| POST | `/auth/onboard/verify-otp` | Verify OTP and activate account | ❌ |
| POST | `/auth/login` | Customer login | ❌ |
| POST | `/auth/refresh-token` | Refresh access token | ❌ |
| POST | `/auth/login` | Customer login | ❌ |
| POST | `/auth/refresh-token` | Refresh access token | ❌ |

### **Account Management**

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/v1/account/get-details` | Get customer details | ✅ |
| GET | `/api/v1/account/view-balance` | Check account balance | ✅ |
| POST | `/api/v1/account/make-deposit` | Deposit money | ✅ |
| POST | `/api/v1/account/withdraw` | Withdraw money | ✅ |
| POST | `/api/v1/account/make-transfer` | Transfer funds | ✅ |
| POST | `/api/v1/account/get-transfer-summary` | Get transfer summary | ✅ |
| GET | `/api/v1/account/details` | Get account holder name | ✅ |
| GET | `/api/v1/account/view-bank-statement` | View transaction history | ✅ |
| PUT | `/api/v1/account/change-password` | Change password | ✅ |
| PATCH | `/api/v1/account/update-profile` | Update profile information | ✅ |
| PUT | `/api/v1/account/update-transaction-limit` | Update transaction limit | ✅ |

### **Currently Inactive Endpoints**

> ⚠️ **Note**: The following endpoints exist in the codebase but are not fully implemented:

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| PUT | `/api/v1/admin/suspend/{id}` | Suspend user account | 🚧 Returns empty response |
| GET | `/api/v1/account/send-otp` | Generate OTP | 🚧 Returns empty response |
| PUT | `/api/v1/account/forgot-password` | Reset password | 🚧 Returns empty response |

> **Kora Virtual Account endpoints** (`/api/v1/kora/**`) are commented out and not active.

### **Test Database**
The application uses H2 in-memory database for testing with automatic schema generation.

## 📁 Project Structure

```
src/
├── main/
│   ├── java/org/henry/bankingsystem/
│   │   ├── config/              # Security & application configuration
│   │   ├── controller/          # REST API endpoints
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── entity/              # JPA entities
│   │   ├── enums/               # Enumerations
│   │   ├── exceptions/          # Custom exceptions & handlers
│   │   ├── repository/          # Data access layer
│   │   ├── service/             # Business logic
│   │   └── utils/               # Utility classes
│   └── resources/
│       ├── templates/           # Email templates
│       └── application*.yml     # Configuration files
└── test/                        # Test files
```

## 🔧 Configuration

### **Application Profiles**
- **dev**: Development environment with local MySQL
- **qa**: Testing environment with cloud database

### **Key Configuration Properties**
```yaml
server:
  port: 6000

spring:
  profiles:
    active: dev
  
  datasource:
    url: jdbc:mysql://localhost:3306/${DATABASE_NAME}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

## 🔐 Security Implementation

### **JWT Configuration**
- Access token expiry: 24 hours
- Refresh token for seamless authentication
- Stateless session management

### **Password Security**
- BCrypt hashing with strength 12
- Password strength validation (uppercase, numbers, special characters)
- 4-digit PIN for transactions

### **API Security**
- All endpoints except auth require valid JWT
- Role-based access control
- Request/response logging for audit

## 👨‍💻 Author

**Henry**
- LinkedIn: https://www.linkedin.com/in/henry-taiwo-b60198313/
- X: https://x.com/h3nry0x
- Email: devthenry@gmail.com

---

## Postman Collection
- https://www.postman.com/spaceflight-explorer-98033499/online-banking-system/collection/0iru7yg/online-banking-system-api?action=share&creator=40620161

## 📞 Support

For support, email me at devthenry@gmail.com or create an issue in this repository.