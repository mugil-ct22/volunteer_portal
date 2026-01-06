# Volunteer Event & Community Service Management Portal - Backend

A complete Spring Boot backend for managing volunteer events, registrations, and community service activities with JWT authentication and role-based access control.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven**

## Database Design

### Tables

1. **users** - User information and authentication
   - id (PK), name, email (unique), password, role (USER/ADMIN), totalPoints, createdAt

2. **events** - Volunteer events
   - id (PK), title, description, eventDate, points, createdAt

3. **registrations** - Event registrations
   - id (PK), user_id (FK), event_id (FK), status (APPLIED/COMPLETED/REJECTED), registeredAt

4. **proofs** - Proof of completion
   - id (PK), user_id (FK), event_id (FK), proofUrl, status (PENDING/APPROVED/REJECTED), submittedAt, reviewedAt, pointsAwarded

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login (returns JWT token)

### User APIs
- `GET /api/user/dashboard` - Get user dashboard stats
- `GET /api/events` - Get all events
- `POST /api/events/register/{eventId}` - Register for an event
- `POST /api/proof/upload/{eventId}` - Upload proof of completion
- `GET /api/leaderboard` - Get leaderboard

### Admin APIs
- `GET /api/admin/dashboard` - Get admin dashboard stats
- `POST /api/admin/events` - Create new event
- `PUT /api/admin/events/{id}` - Update event
- `DELETE /api/admin/events/{id}` - Delete event
- `GET /api/admin/proofs` - Get all proofs for review
- `PUT /api/admin/proofs/{proofId}/approve` - Approve proof
- `PUT /api/admin/proofs/{proofId}/reject` - Reject proof

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

### Database Setup

1. **Create Database**
   ```sql
   CREATE DATABASE volunteer_portal;
   ```

2. **Update Application Properties**
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/volunteer_portal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

### Running the Application

1. **Build and Run**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

2. **Access the API**
   - Base URL: `http://localhost:5000`
   - API Endpoints: `http://localhost:5000/api/*`

### Sample Users (for testing)

The application comes with pre-configured users (password: `password`):

1. **User Account**
   - Email: `john@example.com`
   - Password: `password`
   - Role: USER

2. **Admin Account**
   - Email: `admin@example.com`
   - Password: `password`
   - Role: ADMIN

### Sample Events

The application includes sample events that are automatically created:

1. Beach Cleanup Drive (50 points)
2. Food Bank Volunteer (40 points)
3. Tree Planting Initiative (60 points)

## Security Features

- **JWT Authentication** with 24-hour expiration
- **BCrypt Password Hashing**
- **Role-based Access Control** (USER, ADMIN)
- **CORS Support** for React frontend (http://localhost:5173)
- **Input Validation** on all endpoints

## File Upload

- Proof files are stored in `uploads/` directory
- Supported file formats: Images, PDFs, Documents
- Maximum file size: 10MB

## Error Handling

Global exception handling provides consistent error responses:
- Validation errors (400)
- Authentication errors (401)
- Authorization errors (403)
- Not found errors (404)
- Server errors (500)

## Architecture

The application follows clean layered architecture:

- **Controller Layer** - REST API endpoints
- **Service Layer** - Business logic
- **Repository Layer** - Data access
- **Entity Layer** - Database models
- **DTO Layer** - Data transfer objects
- **Security Layer** - Authentication and authorization

## Testing with Frontend

The backend is designed to work seamlessly with the React frontend. Ensure:
1. Frontend runs on `http://localhost:5173`
2. Backend runs on `http://localhost:5000`
3. JWT token is sent in `Authorization: Bearer <token>` header

## Default Configuration

- Server Port: 5000
- JWT Secret: Configured in application.properties
- JWT Expiration: 24 hours
- Database: MySQL with auto-DDL enabled
