# 🔐 Logger-Authentication-System

### Secure Authentication & Authorization with JWT, Google OAuth 2.0, Spring Security, Redis and Bucket4j

A backend authentication and security project developed using **Java 21, Spring Boot, Spring Security, JWT, Google OAuth 2.0, OpenID Connect, MySQL, Redis, and Bucket4j**.

Logger-Authentication-System demonstrates how to build a secure REST API with traditional email/password authentication, Google authentication, JWT-based authorization, role-based access control, login-attempt protection, and distributed API rate limiting.

The project integrates MySQL for persistent user management and Redis for storing distributed rate-limiting bucket states.

---

## 📌 About the Project

Logger-Authentication-System is a backend security project focused on implementing authentication, authorization, and rate limiting using Spring Boot and Spring Security.

The application supports two authentication methods:

- **Traditional Authentication:** Users can register and log in using their email and password.
- **Google OAuth 2.0 Authentication:** Users can authenticate using their Google accounts through OpenID Connect (OIDC).

After successful authentication through either method, the application generates its own JWT, which clients can use to access protected REST APIs.

The project also demonstrates password hashing using BCrypt, role-based access control, login-attempt restrictions, and distributed API rate limiting using Bucket4j and Redis.

**Project Type:** Backend Security & Authentication System

**Project Purpose:** Learning and implementing authentication, authorization, OAuth 2.0, JWT, and API rate-limiting concepts.

---

## 🎯 Project Objectives

- Understand authentication and authorization using Spring Security.
- Implement traditional email/password authentication.
- Integrate Google OAuth 2.0 and OpenID Connect.
- Implement JWT-based authentication.
- Generate application JWTs after successful Google login.
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

### 2. Traditional Login & Authentication

- Login using email and password.
- Password verification using BCrypt.
- JWT generation after successful authentication.
- Login-attempt restrictions using Bucket4j and Redis.
- Rejection of login requests after the configured attempt limit is exhausted.

### 3. Google OAuth 2.0 Authentication

The application supports Google authentication using Spring Security OAuth2 Client and OpenID Connect (OIDC).

**Authentication flow:**

1. The user initiates Google login through the application's OAuth authorization endpoint.
2. Spring Security redirects the user to Google's authentication page.
3. The user authenticates using their Google account.
4. Google redirects the user back to the application's OAuth callback endpoint.
5. Spring Security validates the OIDC authentication response and retrieves the user's information.
6. A custom `GoogleOidcUserService` checks the Google email and its verification status.
7. If the user does not exist, the application creates a new user record in MySQL.
8. The newly created account receives the default USER role.
9. After successful authentication, `GoogleLoginSuccessHandler` retrieves the application user from MySQL.
10. The application generates its own JWT using the existing `JwtService`.
11. The JWT and user information are returned as a JSON response.

**Google OAuth endpoint:**

```http
GET /oauth2/authorization/google
```

**OAuth callback endpoint:**

```http
GET /login/oauth2/code/google
```

**Example successful response:**

```json
{
  "message": "Google login successful",
  "email": "user@gmail.com",
  "username": "generated_username",
  "role": "USER",
  "tokenType": "Bearer",
  "token": "your_application_jwt"
}
```

**Important:** The application generates its own JWT after Google authentication. It does not use Google's ID token as the application's API authentication token.

### 4. JWT-Based Authentication

- JWT generation using JJWT.
- Signed JWT generation using HMAC.
- User email stored as the JWT subject.
- User role included as a JWT claim.
- Token expiration configuration.
- JWT validation through a custom authentication filter.
- User retrieval from MySQL during authentication.
- Bearer token authentication for protected APIs.

**Token expiration:** 1 hour.

### 5. Role-Based Access Control (RBAC)

The application supports two user roles:

| Role | Access |
|---|---|
| USER | Authenticated user endpoints |
| ADMIN | Administrator endpoints |

Spring Security checks the authenticated user's role before allowing access to protected administrator resources.

New users, including users registering through Google, are assigned the USER role by default.

### 6. Login Attempt Protection

The application implements login-attempt limiting using Bucket4j and Redis.

| Configuration | Value |
|---|---|
| Bucket capacity | 5 attempts |
| Refill strategy | Greedy |
| Refill amount | 5 tokens |
| Refill duration | 2 minutes |
| Bucket identification | Email address |

**Working mechanism:**

- Each login request consumes one token before credential verification.
- Incorrect credentials consume the available tokens.
- Successful authentication refunds one token.
- When no tokens remain, additional login attempts are rejected.
- Login-attempt state is maintained in Redis.

### 7. API Rate Limiting

The application implements authenticated-user API rate limiting using Bucket4j and Redis.

| Configuration | Value |
|---|---|
| Bucket capacity | 5 requests |
| Refill strategy | Greedy |
| Refill amount | 5 tokens |
| Refill duration | 1 minute |
| Bucket identification | Authenticated user's email |

**Working mechanism:**

- An incoming request passes through JWT authentication.
- The authenticated user's email is retrieved from the Spring Security context.
- The rate-limit filter checks the user's Redis-backed bucket.
- If a token is available, the request proceeds.
- If the bucket is exhausted, the application returns HTTP 429 — Too Many Requests.

The rate-limit filter excludes authentication endpoints under the `/api/auth/` path.

### 8. Redis Integration

Redis acts as the distributed storage layer for Bucket4j.

The application maintains two independent categories of rate-limit buckets:

- Login-attempt buckets.
- Authenticated-user API rate-limit buckets.

Redis integration is implemented using Spring Data Redis, Lettuce, and Bucket4j's Redis integration.

### 9. Security Architecture

- JWT-based authentication for protected API requests.
- Google OAuth 2.0 and OIDC integration.
- Custom JWT authentication filter.
- Custom API rate-limit filter.
- BCrypt password hashing.
- Role-based authorization.
- Public authentication endpoints.
- Protected user and administrator endpoints.
- Session creation configured as IF_REQUIRED to support the OAuth login flow.

OAuth authentication requires temporary session support for the authorization process. After successful Google authentication, the application returns its own JWT for accessing protected APIs.

---

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.2 | Backend application framework |
| Spring Web MVC | REST API development |
| Spring Security | Authentication and authorization |
| Spring Security OAuth2 Client | Google OAuth integration |
| OpenID Connect (OIDC) | Google identity authentication |
| Spring Data JPA | Database interaction |
| Hibernate | Object-relational mapping |
| MySQL | Persistent user storage |
| Redis | Distributed rate-limit storage |
| Bucket4j 8.18.0 | Token Bucket rate limiting |
| Lettuce | Redis client |
| JJWT 0.13.0 | JWT generation and validation |
| BCrypt | Password hashing |
| Maven | Dependency and build management |
| Lombok | Boilerplate code reduction |

---

## 🏗️ System Architecture

The application follows a layered architecture with dedicated service classes, custom Spring Security filters, and OAuth authentication components.

### Traditional Authentication Flow

**Client → AuthController → AuthService → LoginAttemptService → UserRepository → BCrypt Verification → JwtService → JWT Response**

### Google OAuth Authentication Flow

**Client → Google OAuth Authorization → Google Authentication → OIDC User Service → UserRepository → Google Login Success Handler → JwtService → JSON Response with JWT**

### Protected API Request Flow

**Client → JWT Authentication Filter → Security Context → Rate Limit Filter → RateLimiterService → Bucket4j → Redis → Controller**

### Request Processing

1. The client sends an HTTP request to the application.
2. The JWT authentication filter checks the Authorization header.
3. The JWT is validated and the associated user is retrieved from MySQL.
4. Authentication information is stored in the Spring Security context.
5. The rate-limit filter checks whether the authenticated user can make another request.
6. Bucket4j retrieves and updates the bucket state through Redis.
7. If the request is allowed, it proceeds to the appropriate controller.
8. If the request limit is exhausted, the application returns HTTP 429.

---

## 📁 Project Structure

```text
Logger-Authentication-System/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/SecureLoginDemo/
│   │   │       │
│   │   │       ├── Config/
│   │   │       │   ├── RedisConfig.java
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── PasswordConfig.java
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── UserController.java
│   │   │       │   └── AdminController.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── LoginRequest.java
│   │   │       │   ├── LoginResponse.java
│   │   │       │   └── RegisterRequest.java
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   └── User.java
│   │   │       │
│   │   │       ├── enumtype/
│   │   │       │   └── Role.java
│   │   │       │
│   │   │       ├── exception/
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   └── LoginLimitExceededException.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   └── UserRepository.java
│   │   │       │
│   │   │       ├── security/
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   ├── JwtService.java
│   │   │       │   ├── RateLimitFilter.java
│   │   │       │   └── GoogleLoginSuccessHandler.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── LoginAttemptService.java
│   │   │       │   ├── RateLimiterService.java
│   │   │       │   └── GoogleOidcUserService.java
│   │   │       │
│   │   │       ├── GenerateSecretKey.java
│   │   │       └── SecureLoginDemoApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
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
| `Config` | Configures Spring Security, Redis, and password encoding |
| `controller` | Handles incoming REST API requests |
| `dto` | Contains request and response objects |
| `entity` | Defines database entities |
| `enumtype` | Defines user roles |
| `exception` | Handles application exceptions |
| `repository` | Handles MySQL database operations |
| `security` | Contains JWT authentication, rate-limit filters, and OAuth success handling |
| `service` | Contains authentication, rate-limiting, and OIDC user-management logic |
| `resources` | Stores application configuration |
| `test` | Contains application test classes |

### 🔑 Important Components

- **AuthController:** Handles user registration and traditional login.
- **AuthService:** Manages registration and email/password authentication.
- **GoogleOidcUserService:** Processes Google OIDC user information, verifies the email claim, and creates a database user when needed.
- **GoogleLoginSuccessHandler:** Handles successful Google authentication and returns the application's JWT as JSON.
- **JwtService:** Generates and validates JWT tokens.
- **JwtAuthenticationFilter:** Validates JWT tokens for incoming requests.
- **RateLimitFilter:** Enforces API request limits.
- **LoginAttemptService:** Controls repeated login attempts.
- **RateLimiterService:** Manages authenticated-user rate limiting.
- **RedisConfig:** Configures Redis integration.
- **PasswordConfig:** Provides the shared BCrypt password encoder.
- **SecurityConfig:** Configures authentication, authorization, OAuth login, and security filters.
- **UserRepository:** Performs user-related database operations.

---

## 🔌 REST API Documentation

**Base URL:** `http://localhost:8080`

### Authentication APIs

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | Public |
| POST | `/api/auth/login` | Authenticate using email and password and generate JWT | Public |
| GET | `/oauth2/authorization/google` | Start Google OAuth login | Public |

**Google OAuth callback:** `/login/oauth2/code/google`

The callback is handled by Spring Security and is not a normal application login endpoint.

### User APIs

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| GET | `/api/user/profile` | Access the authenticated user profile endpoint | JWT required |

### Admin APIs

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| GET | `/api/admin/dashboard` | Access the admin dashboard | JWT + ADMIN role |

### Authorization Requirements

| Endpoint | Authentication | Required Role |
|---|---|---|
| Registration | Not required | Public |
| Traditional Login | Not required | Public |
| Google OAuth initiation | Not required | Public |
| User Profile | JWT required | Authenticated user |
| Admin Dashboard | JWT required | ADMIN |

New users are assigned the USER role automatically. An administrator account must be provisioned separately through a controlled process.

### HTTP Status Codes

| Status | Meaning |
|---|---|
| 200 | Successful request |
| 401 | Unauthorized request or invalid credentials |
| 403 | Access denied due to insufficient permissions |
| 429 | Login-attempt or API rate limit exceeded |

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

| Bucket | Purpose | Capacity | Refill |
|---|---|---:|---|
| Login Attempt Bucket | Controls login attempts | 5 tokens | 5 tokens every 2 minutes |
| API Rate Limit Bucket | Controls authenticated requests | 5 tokens | 5 tokens every minute |

The refill strategy used in both cases is Greedy, which replenishes tokens gradually rather than waiting for the entire refill period to finish.

---

## 🗄️ Database & Storage

### MySQL

MySQL is used for persistent storage of user information.

The application maintains a `users` table containing:

| Column | Description |
|---|---|
| id | Primary key |
| username | Unique username |
| email | Unique email address |
| password | BCrypt-hashed password |
| role | USER or ADMIN |

For traditional registration, the user's submitted password is hashed using BCrypt.

Google-authenticated users do not provide an application password. The current implementation generates a random password value and stores its BCrypt hash to satisfy the existing non-null password column.

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
- Google Cloud account for Google OAuth configuration

Maven is optional because the project includes the Maven Wrapper.

### 2. Clone the Repository

Open your terminal and navigate to the directory where you want to store the project.

Clone the repository:

```bash
git clone https://github.com/YOUR_GITHUB_USERNAME/Logger-Authentication-System.git
```

Navigate into the project directory:

```bash
cd Logger-Authentication-System
```

Replace `YOUR_GITHUB_USERNAME` with your actual GitHub username.

### 3. Open the Project

Open IntelliJ IDEA.

- Select Open.
- Choose the cloned `Logger-Authentication-System` folder.
- Allow IntelliJ IDEA to import the Maven project.
- Wait for Maven to download all required dependencies.
- Make sure the project is configured to use JDK 21.

### 4. Configure MySQL

Start your MySQL server and create a database named:

```sql
CREATE DATABASE secure_login_db;
```

The application uses the following database settings:

| Setting | Value |
|---|---|
| Database | secure_login_db |
| Host | localhost |
| Port | 3306 |
| Username | root |
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

Expected response:

```text
PONG
```

The application uses Redis at `localhost:6379` by default.

### 6. Configure Google OAuth 2.0

Google OAuth requires a client ID and client secret.

Follow these steps:

1. Open the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a Google Cloud project or select an existing project.
3. Configure the OAuth consent screen.
4. Create an OAuth 2.0 Client ID.
5. Select Web application as the application type.
6. Add the following authorized redirect URI:

```text
http://localhost:8080/login/oauth2/code/google
```

7. Copy the generated Client ID and Client Secret.

If the OAuth consent screen is configured for testing, add your Google account as a test user.

**Important:** Never commit your Google Client Secret to GitHub.

### 7. Configure Environment Variables

The application requires the following environment variables:

| Variable | Description |
|---|---|
| `SQL_PASS` | MySQL database password |
| `JWT_SECRET` | Base64-encoded JWT signing secret |
| `GOOGLE_CLIENT_ID` | Google OAuth Client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth Client Secret |

Generate a suitable JWT signing secret using the project's `GenerateSecretKey` utility.

Configure all four environment variables in your IDE's Run Configuration or in your terminal before starting the application.

Example IntelliJ environment-variable configuration:

```text
SQL_PASS=your_mysql_password
JWT_SECRET=your_base64_encoded_jwt_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

The Google OAuth client configuration in `application.properties` should use environment variables:

```properties
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=openid,profile,email
```

**Security note:** Never commit actual database passwords, JWT signing secrets, or Google OAuth client secrets to GitHub.

### 8. Run the Application

You can start the Spring Boot application using either IntelliJ IDEA or the Maven Wrapper.

**Option A — IntelliJ IDEA**

1. Open the project.
2. Locate `SecureLoginDemoApplication`.
3. Open the class.
4. Click the Run button.
5. Wait for Spring Boot to initialize.

**Option B — macOS / Linux Terminal**

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

**Option C — Windows Terminal**

```bash
mvnw.cmd spring-boot:run
```

The Maven Wrapper automatically downloads the required Maven version and project dependencies.

### 9. Verify the Application

Once the application starts successfully, it will be available at:

```text
http://localhost:8080
```

Before testing the APIs, ensure that:

- MySQL is running.
- Redis is running.
- The database exists.
- All environment variables are configured correctly.
- Google OAuth credentials and redirect URI are configured.
- The application has started without errors.

### 10. Test Traditional Authentication Using Postman

1. Register a new user through `/api/auth/register`.
2. Log in using the registered email and password through `/api/auth/login`.
3. Copy the JWT returned by the login endpoint.
4. Add the JWT as a Bearer token in the Authorization header.
5. Access the protected user profile endpoint.
6. Send repeated requests to test API rate limiting.
7. Submit repeated invalid login attempts to test login-attempt protection.
8. Test the administrator endpoint using an account with the ADMIN role.

### 11. Test Google OAuth Authentication

1. Start the Spring Boot application.
2. Open the following URL in your browser:

```text
http://localhost:8080/oauth2/authorization/google
```

3. Select your Google account.
4. Complete the Google authentication process.
5. After successful authentication, the application creates a user record in MySQL if one does not already exist.
6. The application generates its own JWT.
7. The browser displays a JSON response containing the user's email, username, role, and JWT.
8. Copy the application JWT.
9. Open Postman.
10. Access a protected API using Authorization → Bearer Token.
11. Verify that the Google-generated JWT can access protected endpoints according to the user's role.

---

## 🧪 Testing Scenarios

| Test Scenario | Expected Result |
|---|---|
| Register a new user | User registered successfully |
| Register with an existing email | Registration rejected |
| Register with an existing username | Registration rejected |
| Login with correct credentials | JWT generated |
| Login with incorrect credentials | Authentication rejected |
| Login using Google account | Google authentication successful |
| First Google login | User created in MySQL |
| Subsequent Google login | Existing user retrieved |
| Google login with verified email | Application JWT generated |
| Exceed login-attempt limit | HTTP 429 |
| Access protected endpoint without JWT | HTTP 401 |
| Access protected endpoint with valid JWT | Request allowed if within rate limit |
| Access admin endpoint as USER | HTTP 403 |
| Access admin endpoint as ADMIN | Request allowed if within rate limit |
| Exceed API request limit | HTTP 429 |

**Note:** Newly registered users receive the USER role. An ADMIN account must be provisioned separately before testing administrator access.

---

## 📚 Key Backend Concepts Demonstrated

This project provides practical experience with:

- Spring Boot application architecture.
- RESTful API development.
- Spring Security filter chain.
- Traditional authentication.
- OAuth 2.0.
- OpenID Connect (OIDC).
- Google authentication integration.
- JWT authentication and validation.
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
- Authentication success handlers.
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
- Explicit and secure OAuth account-linking workflow.
- Persistent OAuth provider identity mapping.
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
- Google OAuth uses OpenID Connect and checks the email verification claim.
- The current Google user service identifies existing accounts by email. A production implementation should use an explicit, carefully verified account-linking policy and persistent provider identity mapping.
- Google-created accounts receive a random application password hash because the current database entity requires a non-null password.
- OAuth uses `IF_REQUIRED` session management to support the authorization flow. Session and JWT behavior should be reviewed carefully for the intended deployment architecture.
- The application disables CSRF. This configuration should be reviewed before connecting a browser-based frontend.
- The global exception handler maps general runtime exceptions to HTTP 401, which may not be appropriate for every error scenario.
- The existing automated test class checks application context loading; comprehensive security and rate-limiting tests are still required.
- A complete account-lockout policy, refresh-token workflow, and IP-based abuse prevention are not currently implemented.
- Production deployments should use HTTPS and secure secret management.

---

## 👨‍💻 Author

**Alok Kumar Sah**

---

## ⭐ Support

If this project helps you understand Spring Security, JWT authentication, Google OAuth 2.0, Redis, or Bucket4j-based rate limiting, consider giving the repository a ⭐ on GitHub.

Feel free to explore, experiment, and improve the implementation.
