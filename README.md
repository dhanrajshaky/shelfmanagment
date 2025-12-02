☁️ Deployment Details (PlanetScale + Railway)

Your Library Shelf Management project is fully deployed using PlanetScale for database and Railway for backend + frontend.

🌐 Live Project

🔗 Frontend + Backend (Railway)
👉 https://shelfmanagment-production.up.railway.app/

🗄️ Database Hosting – PlanetScale (MySQL)

This project uses PlanetScale as a cloud MySQL database for production.

✔ Why PlanetScale?

Zero-downtime deployments

Free + scalable

MySQL-compatible

Secure access with connection strings

🔧 How You Connected Spring Boot with PlanetScale

Created a new database on PlanetScale

Promoted branch to production

Generated connection string using:

MySQL → Java (with SSL)


Added credentials in application.properties:

spring.datasource.url=jdbc:mysql://aws.connect.psdb.cloud/your-db-name?sslMode=VERIFY_IDENTITY
spring.datasource.username=your-username
spring.datasource.password=your-password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect


Commit & push → Railway builds automatically.

PlanetScale DB is now fully connected.

🚆 Backend Deployment – Railway

Your backend (Spring Boot) is deployed on Railway.

✔ Steps you used:

Connected GitHub repo to Railway

Selected Java / Spring Boot template

Railway auto-detected pom.xml

Set environment variables (DB credentials from PlanetScale)

Railway built + deployed the backend

Railway assigned a public URL

Added this URL into your frontend JS (if required for API base URL)

Backend now runs at:

https://shelfmanagment-production.up.railway.app/

🎨 Frontend Deployment – Railway (Static Hosting)

Your frontend (HTML/CSS/JS) lives inside:

src/main/resources/static


Because Railway runs Spring Boot:

Spring Boot serves the frontend automatically

No separate hosting is needed

index.html loads directly from the backend server

👉 You only deployed ONE Railway service (both frontend + backend).

🧩 Deployment Architecture

Here is the architecture in clean form:

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

🔥 Final Deployment Summary
Component	Service	Status
Backend	Railway	✔ Live
Frontend	Railway (served by Spring Boot)	✔ Live
Database	PlanetScale MySQL	✔ Connected

If you want, I can also:

✅ Add this to your PPT
✅ Create architecture diagrams
✅ Make a better README with badges, icons & visuals
✅ Write a professional project description for LinkedIn

Just tell me!

You said:
now give me all readme file 
ChatGPT said:

Here is your complete, polished, professional README.md — fully ready to copy-paste into your GitHub repo.

It includes:

✔ Project intro
✔ Live demo
✔ Tech stack
✔ Features
✔ Folder structure
✔ How to run locally
✔ PlanetScale DB configuration
✔ Railway deployment (Backend + Frontend)
✔ API endpoints
✔ Screenshots section
✔ Author + enhancement options

📚 Library Shelf Management – Full Stack Project

A clean and simple Library Shelf Management System built using:

Java 17

Spring Boot

Spring Data JPA

H2 / MySQL (PlanetScale)

HTML, CSS, JavaScript

This project allows you to manage Shelves and Books, with a fully working frontend + backend.

🌐 Live Demo

🔗 Production Link (Railway)
👉 https://shelfmanagment-production.up.railway.app/

(If Railway is sleeping, first request may take 20–30 seconds.)

☁️ Deployment Details

This project is deployed using:

✔ PlanetScale – Cloud MySQL database
✔ Railway – Backend server + static frontend hosting

Your entire project is live with one Railway service that handles both frontend & backend.

🧠 Tech Stack
👉 Backend

Java 17

Spring Boot

Spring Data JPA

REST APIs

H2 (development)

MySQL (PlanetScale production)

👉 Frontend

HTML

CSS

JavaScript (Fetch API)

Responsive UI

👉 Deployment

PlanetScale (Production database)

Railway (Deployment and hosting)

⭐ Features

Add, update, delete Shelves

Add, update, delete Books

Assign books to any shelf

Fetch shelves + books with REST APIs

Clean & responsive UI

In-memory database for instant local runs

PlanetScale MySQL for production

Simple & fast architecture

🗂 Folder Structure
Library-Shelf-Management/
 ├── src/
 │   ├── main/
 │   │   ├── java/com/example/shelfmanagement/
 │   │   │   ├── controller/
 │   │   │   ├── model/
 │   │   │   ├── repository/
 │   │   │   └── service/
 │   │   ├── resources/
 │   │   │   ├── static/ (HTML, CSS, JS frontend)
 │   │   │   ├── application.properties
 │   │   │   └── data.sql (optional)
 ├── pom.xml
 └── README.md

🏃‍♂️ How to Run Locally
1️⃣ Requirements

Java 17

Maven 3.8+

2️⃣ Clone the Repo
git clone https://github.com/dhanrajshaky/shelfmanagment
cd shelfmanagment

3️⃣ Run Backend
mvn clean package
mvn spring-boot:run


Backend starts at:
👉 http://localhost:8080

4️⃣ Open Frontend

Frontend auto-served by Spring Boot:

👉 http://localhost:8080/index.html

🗄 Local Database – H2 (Default)

No setup needed.
H2 auto-loads when you run the project.

Console (if enabled):
👉 http://localhost:8080/h2-console

🗄 Production Database – PlanetScale (MySQL)

This project uses PlanetScale for production.

✔ Steps Used During Deployment

Created a PlanetScale database

Promoted the branch to production

Generated MySQL connection string (Java SSL)

Added connection settings in application.properties:

spring.datasource.url=jdbc:mysql://aws.connect.psdb.cloud/YOUR_DB_NAME?sslMode=VERIFY_IDENTITY
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect


Added these variables to Railway environment

Deployed backend → auto-connected to PlanetScale

🚆 Railway Deployment (Backend + Frontend)

Your project is deployed on Railway in one service.

✔ HOW IT WAS DEPLOYED
1️⃣ Backend Deployment

Connected GitHub repo → Railway

Selected Spring Boot

Railway auto detected pom.xml

Added environment variables:

SPRING_DATASOURCE_URL

SPRING_DATASOURCE_USERNAME

SPRING_DATASOURCE_PASSWORD

2️⃣ Frontend Deployment

No separate hosting needed

Frontend lives inside src/main/resources/static/

Spring Boot serves index.html automatically

🎉 Railway now serves both backend + frontend.

📡 API Endpoints
Shelves
Method	Endpoint	Description
GET	/api/shelves	Get all shelves
POST	/api/shelves	Add a shelf
PUT	/api/shelves/{id}	Update a shelf
DELETE	/api/shelves/{id}	Delete a shelf
Books
Method	Endpoint	Description
GET	/api/books	Get all books
POST	/api/books	Add a book
PUT	/api/books/{id}	Update a book
DELETE	/api/books/{id}	Delete a book
🖼 UI Screenshots

(Add your real screenshots here)

📷 /screenshots/home.png
📷 /screenshots/shelves.png
📷 /screenshots/books.png
📷 /screenshots/mobile-view.png

🔥 Future Enhancements

Add login/signup using Spring Security or JWT

Add pagination for books

Add file upload for book cover images

Convert frontend to React/Vue

Docker support

Export data to PDF/Excel

👤 Author

Dhanraj Shakya
Full Stack Developer (Java | Spring Boot | JS)
🔗 GitHub: https://github.com/dhanrajshaky