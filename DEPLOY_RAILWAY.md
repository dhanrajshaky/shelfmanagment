# Deploying to Railway (Backend + frontend served by backend)

This guide walks through deploying this Spring Boot app to Railway. The UI (frontend) is served from `src/main/resources/static`, so the same backend serves both frontend and API.

1) Sign in to Railway
- Go to: https://railway.app and sign in with GitHub.

2) Create project from GitHub
- Click `New Project` → `Deploy from GitHub` and select this repository and branch.
- Railway usually detects Java / Maven. If prompted, use these settings:
  - Build command: `./mvnw -B package -DskipTests` (or `mvn -B package -DskipTests` if you don't have the wrapper)
  - Start command: `java -jar target/*.jar`

3) Add MySQL plugin
- In your Railway project, click `Add Plugin` → choose `MySQL` (free tier). Railway will provision a MySQL instance and show connection info: host, port, database, user, password (or a `DATABASE_URL`).

4) Set environment variables
- In Railway, open your Project → Deployments → Environment (Variables) and add the connection variables using the values from the plugin.

Preferred (Railway plugin values):
- `SPRING_DATASOURCE_URL` = `jdbc:mysql://<HOST>:<PORT>/<DATABASE>?useSSL=false&allowPublicKeyRetrieval=true`
- `SPRING_DATASOURCE_USERNAME` = `<USER>`
- `SPRING_DATASOURCE_PASSWORD` = `<PASSWORD>`

Alternative (project-specific vars supported too):
- `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DB`, `MYSQL_USER`, `MYSQL_PASSWORD` — supported by the project, but using `SPRING_DATASOURCE_URL` is the simplest when Railway provides a single `DATABASE_URL`.

Also set:
- `SPRING_PROFILES_ACTIVE` = `mysql`

5) Redeploy
- Trigger a redeploy (Redeploy/Rebuild). Watch logs:
  - Maven build step runs
  - Spring Boot starts
  - You should see a log line: `Initializing MySQL DataSource, jdbcUrl=...` showing the JDBC URL being used
  - If DB connects successfully you'll see Hikari messages and the app will start. If connection fails, logs will show clear errors.

6) Access the app
- Railway provides a public URL like `https://your-app.up.railway.app`.
- The UI is available at `/` (static assets in `src/main/resources/static`).
- API endpoints are under `/api/*` (for example `GET /api/books`).

Troubleshooting
- If startup fails with `Configuration error: MYSQL_HOST is not configured (using default 'localhost')...` it means you are running with the `mysql` profile but didn't provide platform vars; set `SPRING_DATASOURCE_URL` or the `MYSQL_*` vars.
- If you see `Communications link failure` or `Connection refused`, double-check host/port and that Railway plugin is healthy.
- If you want to manage schema with migrations instead of `spring.jpa.hibernate.ddl-auto=update`, consider adding Flyway and running migrations during deploy.

Notes
- The project supports both `SPRING_DATASOURCE_URL` (recommended with Railway) and the `MYSQL_*` env vars. It fails fast when the mysql profile is active and no valid DB connection details are supplied.
- The container will bind to the port Railway provides via `PORT` env var. No changes needed on Railway for port binding.

If you want, I can:
- (A) Walk you step-by-step in the Railway UI while you grant access, or
- (B) Run a local test against the Railway-provided connection info (if you paste it here), or
- (C) Add Flyway for controlled migrations before deploy.
