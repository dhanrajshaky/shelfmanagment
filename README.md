# 📚 Library Shelf Management — Deployment & Project Overview

> This README presents the same information you provided, reorganized into a clean, easy-to-scan format. No facts or links were changed — only layout and formatting were improved.

---

## Live Project

- Frontend + Backend (Railway):
    - https://shelfmanagment-production.up.railway.app/

## Deployment Summary (PlanetScale + Railway)

This project uses PlanetScale as the production MySQL database and Railway to run the Spring Boot backend (which also serves the static frontend).

### Why PlanetScale?

- Zero-downtime deployments
- Free + scalable plan available
- MySQL-compatible
- Secure access via connection strings

### How Spring Boot is connected to PlanetScale

1. Create a database on PlanetScale.
2. Promote the branch to production.
3. Generate the Java (SSL) connection string in PlanetScale.
4. Add the connection settings to your application properties or Railway environment variables.

Example (application.properties / env values used in your project):

```properties
spring.datasource.url=jdbc:mysql://aws.connect.psdb.cloud/your-db-name?sslMode=VERIFY_IDENTITY
spring.datasource.username=your-username
spring.datasource.password=your-password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

Commit & push → Railway builds automatically and connects to PlanetScale when the environment variables are set.

---

## Architecture (high level)

```
+---------------------------+
|        PlanetScale        |
|     (MySQL Database)      |
+-------------+-------------+
                            |
                            | JDBC + SSL
                            |
+-------------v-------------+
|         Railway           |
|  Spring Boot Backend API  |
|  + Frontend (Static)      |
|     /index.html           |
+-------------+-------------+
                            |
                            | HTTPS
                            |
+-------------v-------------+
|        Users (Web)        |
+---------------------------+
```

### Final Deployment Status (as provided)

| Component | Service | Status |
|---|---:|:---:|
| Backend | Railway | ✔ Live |
| Frontend | Railway (served by Spring Boot) | ✔ Live |
| Database | PlanetScale MySQL | ✔ Connected |

---

## Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- REST APIs
- H2 (development)
- MySQL (PlanetScale production)

### Frontend

- HTML, CSS, JavaScript (Fetch API)
- Responsive UI served from `src/main/resources/static`

### Deployment

- PlanetScale (production database)
- Railway (deployment and hosting)

---

## Features

- Add, update, delete Shelves
- Add, update, delete Books
- Assign books to shelves
- Fetch shelves and books via REST APIs
- Clean & responsive UI
- H2 for local runs; PlanetScale MySQL for production

---

## Folder Structure (representative)

```
Library-Shelf-Management/
├── src/
│   ├── main/
│   │   ├── java/com/example/shelfmanagement/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── resources/
│   │   │   ├── static/   (HTML, CSS, JS frontend)
│   │   │   ├── application.properties
│   │   │   └── data.sql (optional)
├── pom.xml
└── README.md
```

---

## How to Run Locally

### Requirements

- Java 17
- Maven 3.8+

### Steps

```bash
git clone https://github.com/dhanrajshaky/shelfmanagment
cd shelfmanagment
mvn clean package
mvn spring-boot:run
```

The backend starts at: `http://localhost:8080`

Open the frontend (served by Spring Boot): `http://localhost:8080/index.html`

### Local Database (H2)

- No setup required. H2 will run by default in development.
- Console (if enabled): `http://localhost:8080/h2-console`

---

## Production Database — PlanetScale (MySQL)

Steps used during deployment (as provided):

1. Created a PlanetScale database.
2. Promoted the branch to production.
3. Generated the MySQL (Java/SSL) connection string.
4. Added connection settings to `application.properties` or Railway environment variables.

Example connection settings (repeat of above):

```properties
spring.datasource.url=jdbc:mysql://aws.connect.psdb.cloud/YOUR_DB_NAME?sslMode=VERIFY_IDENTITY
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

Add these environment variables to Railway and deploy — Railway will connect the backend to PlanetScale.

---

## Railway Deployment (Backend + Frontend)

You deployed the entire project as one Railway service. Key steps used:

1. Connected the GitHub repo to Railway (Spring Boot detected via `pom.xml`).
2. Added environment variables (for example):
     - `SPRING_DATASOURCE_URL`
     - `SPRING_DATASOURCE_USERNAME`
     - `SPRING_DATASOURCE_PASSWORD`
3. Railway built and deployed the backend; the frontend is served from `src/main/resources/static`.

No separate frontend hosting required.

---

## API Endpoints

### Shelves

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/shelves` | Get all shelves |
| POST | `/api/shelves` | Add a shelf |
| PUT | `/api/shelves/{id}` | Update a shelf |
| DELETE | `/api/shelves/{id}` | Delete a shelf |

### Books

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/books` | Get all books |
| POST | `/api/books` | Add a book |
| PUT | `/api/books/{id}` | Update a book |
| DELETE | `/api/books/{id}` | Delete a book |

---

## UI Screenshots

(Add your real screenshots to the `/screenshots` folder if you want them displayed here.)

- `/screenshots/home.png`
- `/screenshots/shelves.png`
- `/screenshots/books.png`
- `/screenshots/mobile-view.png`

---

## Future Enhancements (suggested)

- Add login/signup using Spring Security or JWT
- Add pagination for books
- Add file upload for book cover images
- Convert frontend to React or Vue
- Docker support
- Export data to PDF/Excel

---

## Author

**Dhanraj Shakya** — Full Stack Developer (Java | Spring Boot | JS)

GitHub: https://github.com/dhanrajshaky

---

