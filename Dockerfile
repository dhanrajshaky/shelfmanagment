# Use Eclipse Temurin JDK 21 as build and runtime image

# --- Builder stage: ensure Maven is available ---
FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

# Install Maven if not present (for environments like Render)
RUN apt-get update && apt-get install -y maven

COPY . .

# Use Maven Wrapper if present, else fallback to system Maven
RUN if [ -f ./mvnw ]; then chmod +x ./mvnw && ./mvnw -B -DskipTests package; else mvn -B -DskipTests package; fi

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/library-shelf-1.0.0.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=mysql"]
