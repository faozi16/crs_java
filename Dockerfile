# Multi-stage build for optimal image size
# Stage 1: Build Stage
FROM eclipse-temurin:21-jdk-jammy as builder

WORKDIR /app

# Copy gradle files
COPY gradle gradle/
COPY src/gradlew . 
COPY settings.gradle .
COPY build.gradle .

# Copy source code
COPY src src/

# Build the application
RUN chmod +x ./gradlew && \
    ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime Stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/carrsvt-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
