# 🔐 Logger-Authetication-System

### Secure Authentication & Authorization with JWT, Spring Security, Redis and Bucket4j

A backend authentication and security project developed using **Java 21, Spring Boot, Spring Security, JWT, MySQL, Redis, and Bucket4j**.

Logger-Authetication-System demonstrates how to build a secure REST API with user registration, login authentication, JWT-based authorization, role-based access control, login-attempt protection, and distributed API rate limiting.

The project integrates MySQL for persistent user management and Redis for storing distributed rate-limiting bucket states.

---

## 📌 About the Project

Logger-Authetication-System is a practical backend learning project focused on understanding how authentication, authorization, and rate limiting work together in a Spring Boot application.

It demonstrates how to protect REST APIs against unauthorized access and excessive requests while maintaining a stateless authentication architecture.

The application uses JWT for user authentication, Spring Security for access control, BCrypt for password hashing, and Bucket4j with Redis for managing request limits.

**Project Type:** Backend Security & Authentication System

**Project Purpose:** Learning, implementation, and testing of authentication, authorization, and API rate-limiting concepts.

---

## 🎯 Project Objectives

- Understand authentication and authorization using Spring Security.
- Implement stateless authentication using JWT.
- Secure user passwords using BCrypt hashing.
- Implement role-based access control.
- Protect login endpoints against repeated failed attempts.
- Implement API rate limiting using Bucket4j.
- Integrate Redis for distributed rate-limit storage.
- Understand the Token Bucket Algorithm.
- Implement custom Spring Security filters.
- Integrate MySQL with Spring Boot using Spring Data JPA.

---

## ✨ Features

### 1. User Registration

- User registration using username, email, and password.
- Request validation.
- Duplicate email and username checking.
- Password hashing using BCrypt.
- Automatic assignment of the default USER role.
- Persistent user information storage using MySQL.

### 2. User Login & Authentication

- Login using email and password.
- Password verification using BCrypt.
- JWT generation after successful authentication.
- Login-attempt restrictions using Bucket4j and Redis.
- Rejection of login requests after the configured attempt limit is exhausted.

### 3. JWT-Based Authentication

- Stateless authentication using JSON Web Tokens.
- Signed JWT generation using HMAC.
- User email stored as the JWT subject.
- User role included as a JWT claim.
- Token expiration configuration.
- JWT validation through a custom authentication filter.
- User retrieval from MySQL during authentication.

**Token expiration:** 1 hour.

### 4. Role-Based Access Control (RBAC)

The application supports two user roles:

| Role  | Access                       |
| ----- | ---------------------------- |
| USER  | Authenticated user endpoints |
| ADMIN | Administrator endpoints      |

Spring Security checks the authenticated user's role before allowing access to protected administrator resources.

### 5. Login Attempt Protection

The application implements login-attempt limiting using Bucket4j and Redis.

| Configuration         | Value         |
| --------------------- | ------------- |
| Bucket capacity       | 5 attempts    |
| Refill strategy       | Greedy        |
| Refill amount         | 5 tokens      |
| Refill duration       | 2 minutes     |
| Bucket identification | Email address |

**Working mechanism:**

- Each login request consumes one token before credential verification.
- Incorrect credentials consume the available tokens.
- Successful authentication refunds one token.
- When no tokens remain, additional login attempts are rejected.
- Login-attempt state is maintained in Redis.

### 6. API Rate Limiting

The application implements authenticated-user API rate limiting using Bucket4j and Redis.

| Configuration         | Value                      |
| --------------------- | -------------------------- |
| Bucket capacity       | 5 requests                 |
| Refill strategy       | Greedy                     |
| Refill amount         | 5 tokens                   |
| Refill duration       | 1 minute                   |
| Bucket identification | Authenticated user's email |

**Working mechanism:**

- An incoming request first passes through JWT authentication.
- The authenticated user's email is retrieved from the Spring Security context.
- The rate-limit filter checks the user's Redis-backed bucket.
- If a token is available, the request proceeds.
- If the bucket is exhausted, the application returns HTTP 429 — Too Many Requests.

The rate-limit filter excludes authentication endpoints under the `/api/auth/` path.

### 7. Redis Integration

Redis acts as the distributed storage layer for Bucket4j.

The application maintains two independent categories of rate-limit buckets:

- Login-attempt buckets.
- Authenticated-user API rate-limit buckets.

Redis integration is implemented using Spring Data Redis, Lettuce, and Bucket4j's Redis integration.

### 8. Stateless Security Architecture

- HTTP session management is disabled.
- JWT is used for authentication.
- Custom JWT authentication filter.
- Custom API rate-limit filter.
- Public authentication endpoints.
- Protected user and administrator endpoints.
- Role-based authorization.

---

## 🛠️ Technology Stack

| Technology        | Purpose                          |
| ----------------- | -------------------------------- |
| Java 21           | Programming language             |
| Spring Boot 4.1.2 | Backend application framework    |
| Spring Web MVC    | REST API development             |
| Spring Security   | Authentication and authorization |
| Spring Data JPA   | Database interaction             |
| Hibernate         | Object-relational mapping        |
| MySQL             | Persistent user storage          |
| Redis             | Distributed rate-limit storage   |
| Bucket4j 8.18.0   | Token Bucket rate limiting       |
| Lettuce           | Redis client                     |
| JJWT 0.13.0       | JWT generation and validation    |
| BCrypt            | Password hashing                 |
| Maven             | Dependency and build management  |
| Lombok            | Boilerplate code reduction       |

---

## 🏗️ System Architecture

The application follows a layered architecture with custom Spring Security filters and dedicated service classes.

### Request Processing Architecture

**Client → JWT Authentication Filter → Security Context → Rate Limit Filter → RateLimiterService → Bucket4j → Redis → Controller**

The request processing sequence:

1. The client sends an HTTP request to the application.
2. The JWT authentication filter checks the Authorization header.
3. The JWT is validated and the associated user is retrieved from MySQL.
4. Authentication information is stored in the Spring Security context.
5. The rate-limit filter checks whether the authenticated user can make another request.
6. Bucket4j retrieves and updates the bucket state through Redis.
7. If the request is allowed, it proceeds to the appropriate controller.
8. If the request limit is exhausted, the application returns HTTP 429.

### Authentication Flow

**Client → AuthController → AuthService → LoginAttemptService → UserRepository → BCrypt Verification → JwtService → JWT Response**

The authentication process:

1. A user submits registration or login credentials.
2. The controller forwards the request to the authentication service.
3. During registration, the application checks for existing email and username values.
4. The password is hashed using BCrypt.
5. The user is stored in MySQL with the default USER role.
6. During login, the login-attempt bucket is checked.
7. The submitted password is verified against the stored password hash.
8. After successful authentication, the application generates a signed JWT.
9. The generated token is returned to the client.

---

## 📁 Project Structure

```text
Logger-Authetication-System/
│
├── src/
│   │
│   ├── main/
│   │   │
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── SecureLoginDemo/
│   │   │               │
│   │   │               ├── Config/
│   │   │               │   ├── RedisConfig.java
│   │   │               │   └── SecurityConfig.java
│   │   │               │
│   │   │               ├── controller/
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── UserController.java
│   │   │               │   └── AdminController.java
│   │   │               │
│   │   │               ├── dto/
│   │   │               │   ├── LoginRequest.java
│   │   │               │   ├── LoginResponse.java
│   │   │               │   └── RegisterRequest.java
│   │   │               │
│   │   │               ├── entity/
│   │   │               │   └── User.java
│   │   │               │
│   │   │               ├── enumtype/
│   │   │               │   └── Role.java
│   │   │               │
│   │   │               ├── exception/
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   └── LoginLimitExceededException.java
│   │   │               │
│   │   │               ├── repository/
│   │   │               │   └── UserRepository.java
│   │   │               │
│   │   │               ├── security/
│   │   │               │   ├── JwtAuthenticationFilter.java
│   │   │               │   ├── JwtService.java
│   │   │               │   └── RateLimitFilter.java
│   │   │               │
│   │   │               ├── service/
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── LoginAttemptService.java
│   │   │               │   └── RateLimiterService.java
│   │   │               │
│   │   │               ├── GenerateSecretKey.java
│   │   │               └── SecureLoginDemoApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── SecureLoginDemo/
│                       └── SecureLoginDemoApplicationTests.java
│
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

### 📂 Package Responsibilities

| Package | Description |
|---|---|
| `Config` | Configures Spring Security and Redis |
| `controller` | Handles incoming REST API requests |
| `dto` | Contains request and response objects |
| `entity` | Defines database entities |
| `enumtype` | Defines user roles |
| `exception` | Handles application exceptions |
| `repository` | Handles MySQL database operations |
| `security` | Contains JWT authentication and rate-limit filters |
| `service` | Contains authentication and rate-limiting business logic |
| `resources` | Stores application configuration |
| `test` | Contains application test classes |

### 🔑 Important Components

- **AuthController:** Handles user registration and login.
- **AuthService:** Manages registration and authentication logic.
- **JwtService:** Generates and validates JWT tokens.
- **JwtAuthenticationFilter:** Validates JWT tokens for incoming requests.
- **RateLimitFilter:** Enforces API request limits.
- **LoginAttemptService:** Controls repeated login attempts.
- **RateLimiterService:** Manages authenticated-user rate limiting.
- **RedisConfig:** Configures Redis integration.
- **SecurityConfig:** Configures authentication, authorization, and security filters.
- **UserRepository:** Performs user-related database operations.

## 🔌 REST API Documentation

**Base URL:** `http://localhost:8080`

### Authentication APIs

| Method | Endpoint             | Description                          | Authentication |
| ------ | -------------------- | ------------------------------------ | -------------- |
| POST   | `/api/auth/register` | Register a new user                  | Public         |
| POST   | `/api/auth/login`    | Authenticate a user and generate JWT | Public         |

### User APIs

| Method | Endpoint            | Description                                    | Authentication |
| ------ | ------------------- | ---------------------------------------------- | -------------- |
| GET    | `/api/user/profile` | Access the authenticated user profile endpoint | JWT required   |

### Admin APIs

| Method | Endpoint               | Description                | Authentication   |
| ------ | ---------------------- | -------------------------- | ---------------- |
| GET    | `/api/admin/dashboard` | Access the admin dashboard | JWT + ADMIN role |

### Authorization Requirements

| Endpoint        | Authentication | Required Role                         |
| --------------- | -------------- | ------------------------------------- |
| Registration    | Not required   | Public                                |
| Login           | Not required   | Public                                |
| User Profile    | JWT required   | USER or authorized authenticated role |
| Admin Dashboard | JWT required   | ADMIN                                 |

New users are assigned the USER role automatically. An administrator account must be provisioned separately through a controlled process.

### HTTP Status Codes

| Status | Meaning                                       |
| ------ | --------------------------------------------- |
| 200    | Successful request                            |
| 401    | Unauthorized request or invalid credentials   |
| 403    | Access denied due to insufficient permissions |
| 429    | Login-attempt or API rate limit exceeded      |

---

## ⚙️ Understanding the Token Bucket Algorithm

Bucket4j implements the Token Bucket Algorithm for controlling incoming requests.

The algorithm works through a virtual bucket containing a predefined number of tokens.

- A bucket is initialized with a fixed capacity.
- Every incoming request consumes one token.
- Requests are accepted while tokens are available.
- Requests are rejected when the bucket becomes empty.
- Tokens are replenished according to the configured refill strategy.
- The bucket cannot exceed its maximum capacity.

This project uses two independent token buckets:

| Bucket                | Purpose                         | Capacity | Refill                   |
| --------------------- | ------------------------------- | -------: | ------------------------ |
| Login Attempt Bucket  | Controls login attempts         | 5 tokens | 5 tokens every 2 minutes |
| API Rate Limit Bucket | Controls authenticated requests | 5 tokens | 5 tokens every minute    |

The refill strategy used in both cases is Greedy, which replenishes tokens gradually rather than waiting for the entire refill period to finish.

---

## 🗄️ Database & Storage

### MySQL

MySQL is used for persistent storage of user information.

The application maintains a `users` table containing:

| Column   | Description            |
| -------- | ---------------------- |
| id       | Primary key            |
| username | Unique username        |
| email    | Unique email address   |
| password | BCrypt-hashed password |
| role     | USER or ADMIN          |

Database interaction is handled using Spring Data JPA and Hibernate.

### Redis

Redis stores the state of Bucket4j rate-limit buckets.

The application maintains separate logical namespaces for login attempts and authenticated API requests.

This allows the application to maintain rate-limit state outside the local application memory.

---

## 🚀 Getting Started — Clone & Run Locally

Follow these instructions to download the project from GitHub and run it on your local machine.

### 1. Prerequisites

Install the following software before starting:

- Java Development Kit (JDK) 21
- MySQL Server
- Redis Server
- Git
- IntelliJ IDEA or another Java IDE
- Postman for API testing

Maven is optional because the project includes the Maven Wrapper.

### 2. Clone the Repository

Open your terminal and navigate to the directory where you want to store the project.

Clone the repository:

```bash
git clone https://github.com/YOUR_GITHUB_USERNAME/Logger-Authetication-System.git
```

Navigate into the project directory:

```bash
cd Logger-Authetication-System
```

**Important:** Replace `YOUR_GITHUB_USERNAME` with your actual GitHub username. Make sure the repository name and URL match the repository you created.

### 3. Open the Project

Open IntelliJ IDEA.

- Select **Open**.
- Choose the cloned `Logger-Authetication-System` folder.
- Allow IntelliJ IDEA to import the Maven project.
- Wait for Maven to download all required dependencies.
- Make sure the project is configured to use JDK 21.

### 4. Configure MySQL

Start your MySQL server and create a database named:

`secure_login_db`

The application uses the following database settings:

| Setting  | Value                     |
| -------- | ------------------------- |
| Database | secure_login_db           |
| Host     | localhost                 |
| Port     | 3306                      |
| Username | root                      |
| Password | Provided through SQL_PASS |

Make sure your MySQL username and password match your local configuration.

### 5. Start Redis

Start your local Redis server.

If you installed Redis using Homebrew on macOS, you can start it as a background service:

```bash
brew services start redis
```

Verify the Redis connection:

```bash
redis-cli ping
```

Expected response: `PONG`

The application uses Redis at `localhost:6379` by default.

### 6. Configure Environment Variables

The application requires two environment variables:

| Variable     | Description                       |
| ------------ | --------------------------------- |
| `SQL_PASS`   | MySQL database password           |
| `JWT_SECRET` | Base64-encoded JWT signing secret |

Generate a suitable JWT signing secret using the project's `GenerateSecretKey` utility.

Configure both environment variables in your IDE's Run Configuration or in your terminal before starting the application.

**Security note:** Never commit your actual database password or JWT signing secret to GitHub.

### 7. Run the Application

You can start the Spring Boot application using either IntelliJ IDEA or the Maven Wrapper.

**Option A — IntelliJ IDEA**

1. Open the project.
2. Locate the main application class, `SecureLoginDemoApplication`.
3. Open the class.
4. Click the Run button.
5. Wait for Spring Boot to initialize.

**Option B — macOS / Linux Terminal**

From the project root directory, execute:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

**Option C — Windows Terminal**

From the project root directory, execute:

```bash
mvnw.cmd spring-boot:run
```

The Maven Wrapper automatically downloads the required Maven version and project dependencies.

### 8. Verify the Application

Once the application starts successfully, it will be available at:

`http://localhost:8080`

Before testing the APIs, ensure that:

- MySQL is running.
- Redis is running.
- The database exists.
- The environment variables are configured correctly.
- The application has started without errors.

### 9. Test the APIs Using Postman

Use Postman to test the following sequence:

1. Register a new user through the registration endpoint.
2. Log in using the registered email and password.
3. Copy the JWT returned by the login endpoint.
4. Add the JWT as a Bearer token in the Authorization header.
5. Access the protected user profile endpoint.
6. Send repeated requests to test API rate limiting.
7. Submit repeated invalid login attempts to test login-attempt protection.
8. Test the administrator endpoint using an account with the ADMIN role.

---

## 🧪 Testing Scenarios

| Test Scenario                            | Expected Result                      |
| ---------------------------------------- | ------------------------------------ |
| Register a new user                      | User registered successfully         |
| Register with an existing email          | Registration rejected                |
| Register with an existing username       | Registration rejected                |
| Login with correct credentials           | JWT generated                        |
| Login with incorrect credentials         | Authentication rejected              |
| Exceed login-attempt limit               | HTTP 429                             |
| Access protected endpoint without JWT    | HTTP 401                             |
| Access protected endpoint with valid JWT | Request allowed if within rate limit |
| Access admin endpoint as USER            | HTTP 403                             |
| Access admin endpoint as ADMIN           | Request allowed if within rate limit |
| Exceed API request limit                 | HTTP 429                             |

**Note:** Newly registered users receive the USER role. An ADMIN account must be provisioned separately before testing administrator access.

---

## 📚 Key Backend Concepts Demonstrated

This project provides practical experience with:

- Spring Boot application architecture.
- RESTful API development.
- Spring Security filter chain.
- JWT authentication.
- Stateless authentication.
- Role-based access control.
- BCrypt password hashing.
- Spring Data JPA.
- Hibernate ORM.
- MySQL database integration.
- Redis integration.
- Distributed rate limiting.
- Bucket4j.
- Token Bucket Algorithm.
- Custom servlet filters.
- Exception handling.
- Environment-based configuration.
- Maven dependency management.

---

## 🚧 Future Enhancements

The following improvements can be implemented to extend the project:

- IP-based login-attempt protection.
- Account lockout and configurable cooldown policies.
- Refresh-token implementation.
- JWT revocation and logout functionality.
- Structured JSON error responses.
- Retry-After response headers.
- More comprehensive request validation.
- Configurable rate-limit policies.
- Controlled administrator account provisioning.
- Docker and Docker Compose integration.
- Centralized logging and monitoring.
- Swagger/OpenAPI documentation.
- Comprehensive unit and integration testing.
- Concurrency and load testing.
- Improved production secret management.
- HTTPS support.

---

## ⚠️ Security Considerations

This is an educational demonstration project and should not be considered a production-ready authentication system.

Important implementation considerations:

- Login-attempt protection currently identifies buckets using email addresses rather than client IP addresses.
- Authenticated API rate limiting uses the user's email as the bucket key. Protected endpoints share that user's configured bucket.
- JWT authentication retrieves user details and roles from MySQL.
- The application uses stateless session management and disables CSRF. This configuration should be reviewed for the intended deployment architecture.
- The global exception handler maps general runtime exceptions to HTTP 401, which may not be appropriate for every error scenario.
- The existing automated test class checks application context loading; comprehensive security and rate-limiting tests are still required.
- A complete account-lockout policy, refresh-token workflow, and IP-based abuse prevention are not currently implemented.

---

## 👨‍💻 Author

**Alok Kumar Sah**

---

## ⭐ Support

If this project helps you understand Spring Security, JWT authentication, Redis, or Bucket4j-based rate limiting, consider giving the repository a ⭐ on GitHub.

Feel free to explore, experiment, and improve the implementation.
