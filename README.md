# 🚀 Volunteer Portal – Full Stack Web Application

A modern **Volunteer Management System** developed using **React (Vite)** and **Spring Boot**, designed with secure authentication, role-based access control, and cloud-native deployment.



## 🌐 Live Deployment

### 🔹 Frontend (Vercel)
👉 https://volunteer-portal-chi.vercel.app

### 🔹 Backend (Railway)
👉 https://volunteerportal-production-54b6.up.railway.app



## ✨ Features

### 🔐 Authentication & Security
- JWT-based authentication
- Role-based access control (Admin / User)
- BCrypt password encryption
- Stateless REST APIs using Spring Security

### 👤 Volunteer Module
- User registration and login
- Secure volunteer dashboard
- Event participation
- Certificate generation and download

### 🛡 Admin Module
- Admin-restricted APIs
- Volunteer and certificate management
- Secure backend access

### 🎨 Frontend Capabilities
- React SPA built with Vite
- Axios configured using environment variables
- Client-side routing with React Router
- Page refresh routing fixed using Vercel rewrites



## 🛠 Technology Stack

### Frontend
- React (Vite)
- React Router
- Axios
- Deployed on **Vercel**

### Backend
- Spring Boot 3
- Spring Security 6
- JWT Authentication
- Hibernate & JPA
- Dockerized deployment on **Railway**

### Database
- MySQL (Railway managed)



## 🔐 CORS & Security Configuration

- Global CORS configuration using Spring Security
- Requests restricted to production frontend URL
- Supports all HTTP methods
- Preflight requests cached for performance
- Secure credential handling



## 📁 Project Structure

```text
volunteer_portal/
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── App.jsx
│   │   └── main.jsx
│   │
│   ├── vercel.json
│   └── package.json
│
├── backend/
│   ├── src/main/java/com/volunteer/portal/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── security/
│   │   └── config/
│   │
│   ├── src/main/resources/
│   │   └── application.properties
│   │
│   └── Dockerfile
│
└── README.md
