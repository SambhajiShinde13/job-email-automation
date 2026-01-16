# Dockerfile for Job Email Automation System

FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Install MySQL client (for health checks)
RUN apt-get update && apt-get install -y mysql-client && rm -rf /var/lib/apt/lists/*

# Copy the JAR file
COPY target/job-email-automation-1.0.0.jar app.jar

# Copy resume
COPY resume/resume.pdf /app/resume/resume.pdf

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/contacts/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
