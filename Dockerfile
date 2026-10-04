# Multi-stage Docker build for College Complaint & Feedback System
# Stage 1: Build application using Maven & Java 21 LTS
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy Maven settings with Google Cloud CDN mirror (prevents Maven Central 429 Too Many Requests errors)
COPY settings.xml /root/.m2/settings.xml

# Copy application source & POM
COPY pom.xml .
COPY src ./src

# Compile and build exploded WAR archive directly (avoids dependency:go-offline burst requests)
RUN mvn clean compile war:exploded -DskipTests -B -s /root/.m2/settings.xml

# Stage 2: Hardened, minimal JRE runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root system user and group (UID/GID 10001)
RUN addgroup -g 10001 -S appgroup && \
    adduser -u 10001 -S appuser -G appgroup

# Copy compiled classes, runtime libraries, and web resources
COPY --from=builder --chown=appuser:appgroup /app/target/classes ./target/classes
COPY --from=builder --chown=appuser:appgroup /app/target/complaint-system/WEB-INF/lib ./target/complaint-system/WEB-INF/lib
COPY --from=builder --chown=appuser:appgroup /app/src/main/webapp ./src/main/webapp
COPY --chown=appuser:appgroup db ./db

# Ensure app directories are owned by appuser
RUN mkdir -p /app/target/tomcat-embed /app/uploads && \
    chown -R appuser:appgroup /app

# Switch to non-root execution
USER appuser

# JVM memory constraints tuned for 512 MB container (Render free tier)
ENV JAVA_OPTS="-XX:+UseContainerSupport -Xms128m -Xmx320m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Kolkata"

# Port will be dynamically injected by Render
EXPOSE 8080

# Use exec form so SIGTERM signals pass directly to JVM for graceful shutdown
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -cp 'target/classes:target/complaint-system/WEB-INF/lib/*' com.college.complaint.AppRunner"]
