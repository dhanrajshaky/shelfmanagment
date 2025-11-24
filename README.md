Library Shelf Management - Full Stack (Java 17, Spring Boot, JPA, H2) - No Security
===============================================================================

What you get
------------
- Backend: Spring Boot (Java 17), Spring Data JPA
- Database: H2 in-memory (default). MySQL driver included (configure application.properties to use MySQL).
- Frontend: static HTML/CSS/JS (placed in src/main/resources/static)
- No Spring Security (per request)

How to run (quick)
------------------
1. Make sure you have Java 17 and Maven installed.
2. Unzip the project and from the project root run:
   mvn clean package
   mvn spring-boot:run

3. Open frontend:
   http://localhost:8080/index.html

Notes
-----
- The application uses H2 in-memory DB by default so it's ready to run without extra setup.
- To use MySQL: create a database, update application.properties with credentials and change the datasource URL and dialect.
- Sample SQL for MySQL is included in sql/sample-data.sql

Enjoy! If you want authentication or a production-ready setup, ask me and I can add it.
