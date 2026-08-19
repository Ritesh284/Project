# Car Rental Management System (Spring Boot + MongoDB + HTML/CSS/JS)

A full-stack Car Rental Management System built with a **Spring Boot 3 (Java 25)** backend, **MongoDB** database, and dynamic **HTML/CSS/JavaScript** frontend with **100% UI preservation**.

---

## 🚀 Key Features

* **100% Preserved UI:** Responsive single-page website with dynamic car cards, filters, and ride booking.
* **MongoDB Database:** Dynamic documents for `users`, `cars`, `bookings`, `states`, `cities`, and `locations`.
* **Admin Control Panel:** In-page modal with real-time stats, ride approvals/rejections, and direct fleet car creation/management.
* **JWT Security:** Protected endpoints, BCrypt password hashing, and role-based permissions (`ROLE_ADMIN` vs `ROLE_USER`).
* **Conflict Prevention:** Checks date overlaps before approving rides.
* **Dynamic Location Hierarchy:** Multi-tier selection for Indian States, Cities, and Pick-up/Drop spots.

---

## 🔑 Default Credentials

| Role | Email ID | Mobile Number | Password |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `` | `` | `` |
| **USER** | `` | `` | `` |

---

## 🛠️ Tech Stack & Prerequisites

* **Backend:** Spring Boot 3.3.4, Spring Security 6, JJWT 0.12.6, Spring Data MongoDB
* **Database:** MongoDB (running on `localhost:27017`)
* **Java:** JDK 17+ (JDK 25 tested)
* **Frontend:** Semantic HTML5, CSS3, Vanilla JavaScript (Fetch API)

---

## 🏃 Running the Application

### 1. Ensure MongoDB is Running
Make sure the local MongoDB service is active on port `27017`.

### 2. Start the Backend Server
```powershell
cd backend
& "C:\NEWPROJECT\tools\maven\bin\mvn.cmd" spring-boot:run
```

### 3. Open the Website
Open your browser and visit:
👉 **http://localhost:8080**
