# Deploying to Render.com with PlanetScale (TiDB)

This document explains how to configure Render and the app so your data is stored in your PlanetScale/TiDB database.

Prerequisites
- You have a PlanetScale database and a write-enabled branch.
- You have the connection parameters (host, port, database, user, password). You provided these already.
- Your code is pushed to a Git repo connected to Render (GitHub/GitLab).

Render service setup (Web Service)
1. In the Render dashboard, create a new **Web Service** and connect your repository/branch.
2. Build Command: `mvn -DskipTests package`
3. Start Command: `java -jar target/library-shelf-1.0.0.jar`
4. Instance Type: choose a plan that fits your needs.

Environment variables (set these in Render's service -> Environment -> Environment Variables)
- `MYSQL_HOST` = gateway01.ap-southeast-1.prod.aws.tidbcloud.com
- `MYSQL_PORT` = 4000
- `MYSQL_DB` = shelfmanagment
- `MYSQL_USER` = 28roJeAi16PthQY.root
- `MYSQL_PASSWORD` = ySnv54FOHO8BQYe4
- `SPRING_PROFILES_ACTIVE` = mysql
- Optional: `SERVER_PORT` = 8080

Render manifest (recommended)
- This repository includes a `render.yaml` manifest with a sample web service configuration. It contains placeholders for secrets and sets `SPRING_PROFILES_ACTIVE` to `mysql`.
- Edit `render.yaml` before committing to fill non-secret values. For credentials and secrets, prefer setting them in the Render service UI (Service → Environment → Environment Variables) or in Render's dashboard as encrypted secrets — do NOT commit plaintext secrets to the repo.

Health check
- Configure Render to use `/actuator/health` as the health check path (the app exposes Spring Boot Actuator endpoints when the profile is active). This helps Render know when the app is healthy.

Important PlanetScale notes
- PlanetScale does not allow `CREATE DATABASE` on managed branches. Make sure the database (`MYSQL_DB`) exists.
- SSL/TLS is required. `DataSourceConfig` has been updated to use `sslMode=REQUIRED` for cloud hosts.
- Use the write-enabled branch/connection string for writes.

Verifying after deploy
1. Watch the Render deploy logs for a successful Spring Boot startup and the message that the app is listening on the configured port.
2. Use the app's API (or Render's external URL) to create a resource:
   - POST /api/books with JSON body. If it returns an `id`, the write succeeded.
3. Restart the Render service and check GET /api/books to ensure data persisted.

Troubleshooting
- If the app fails to start, check Render logs for the datasource error. The datasource now fails fast on DB connection errors (the app will not silently fall back to H2).
- If you get SSL handshake errors, confirm `sslMode=REQUIRED` is acceptable for your PlanetScale setup. We can switch to `sslMode=VERIFY_IDENTITY` if you provide the CA or trust store.
- If writes silently fail or no data persists, confirm you're connected to a write-enabled PlanetScale branch.

Next steps I can do for you
- Attempt a local connection/test using your provided credentials to confirm connectivity and demonstrate a successful deploy flow.
- Help you set the Render service environment variables and walk through the first deploy from your Git repo.
