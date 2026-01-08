# 🚀 Volunteer Portal – Full Stack Web Application

A modern **Volunteer Management System** built using **React (Vite)** and **Spring Boot**, featuring secure authentication, role-based access control, and cloud deployment.

---

## 🌐 Live Deployment

- **Frontend (Vercel)**  
  👉 https://volunteer-portal-chi.vercel.app

- **Backend (Railway)**  
  👉 https://volunteerportal-production-54b6.up.railway.app

---

## ✨ Key Features

### 🔐 Authentication & Security
- JWT-based authentication
- Role-based authorization (Admin / User)
- BCrypt password hashing
- Stateless REST APIs

### 👤 Volunteer Module
- User registration & login
- Protected dashboard
- Event participation
- Certificate download

### 🛡 Admin Module
- Admin-only secured APIs
- Volunteer & certificate management
- Backend access control

### 🎨 Frontend
- React SPA with Vite
- Axios with environment-based API URLs
- Client-side routing
- Page refresh routing fixed (Vercel rewrite)

---

## 🛠 Tech Stack

### Frontend
- React (Vite)
- React Router
- Axios
- Deployed on **Vercel**

### Backend
- Spring Boot 3
- Spring Security 6
- JWT Authentication
- Hibernate / JPA
- Deployed on **Railway**

### Database
- MySQL (Railway managed)

---

## 🔐 CORS & Security Configuration

- Global CORS enabled using Spring Security
- Requests allowed only from production frontend
- All HTTP methods supported
- Preflight requests cached
- Credentials securely handled

---

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

## 🚀 Deployment Overview

### Frontend
- Deployed on Vercel
- SPA routing handled using `vercel.json`
- Auto redeploy on Git push

### Backend
- Docker-based Spring Boot service
- Environment-variable driven configuration
- Persistent MySQL database
- Hosted on Railway

---

## ✅ Production Status

✔ Backend running successfully  
✔ Database connected  
✔ CORS issues resolved  
✔ SPA refresh issue fixed  
✔ Secure API communication  

---

## 📘 Learning Outcomes

- Full-stack application development
- JWT authentication & Spring Security
- CORS debugging and deployment fixes
- Cloud deployment (Vercel & Railway)
- Environment-based configuration

---

## 📄 License

This project is developed for **educational and academic purposes**.

---

## ⭐ Support

If you found this project useful, please ⭐ the repository.

---

**Developed using React & Spring Boot**
