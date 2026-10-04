import os

gitattributes = """* text=auto eol=lf
*.sh text eol=lf
Dockerfile text eol=lf
*.yaml text eol=lf
*.yml text eol=lf
"""

gitignore = """target/
.idea/
*.iml
screenshots/
uploads/
.env
*.log
config.local.properties
db/*.mv.db
db/*.trace.db
.system_generated/
"""

dockerignore = """target/
.git/
.idea/
*.iml
screenshots/
uploads/
.env
*.log
config.local.properties
db/*.mv.db
db/*.trace.db
.system_generated/
"""

render_yaml = """services:
  - type: web
    name: college-complaint-system
    runtime: docker
    plan: free
    region: oregon
    healthCheckPath: /healthz
    autoDeploy: true
    envVars:
      - key: APP_ENV
        value: production
      - key: STORAGE_MODE
        value: db
      - key: SEED_DEMO_DATA
        value: "false"
      - key: DB_POOL_SIZE
        value: "5"
      - key: DB_URL
        sync: false
      - key: DB_USER
        sync: false
      - key: DB_PASSWORD
        sync: false
      - key: ADMIN_INITIAL_PASSWORD
        sync: false
      - key: ALLOWED_EMAIL_DOMAIN
        sync: false
"""

dockerfile = """# Multi-stage Docker build for College Complaint & Feedback System
# Stage 1: Build application using Maven & Java 21 LTS
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache Maven dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source
COPY src ./src

# Compile and build exploded WAR archive
RUN mvn clean compile war:exploded -DskipTests

# Stage 2: Hardened, minimal JRE runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root system user and group (UID/GID 10001)
RUN addgroup -g 10001 -S appgroup && \\
    adduser -u 10001 -S appuser -G appgroup

# Copy compiled classes, runtime libraries, and web resources
COPY --from=builder --chown=appuser:appgroup /app/target/classes ./target/classes
COPY --from=builder --chown=appuser:appgroup /app/target/complaint-system/WEB-INF/lib ./target/complaint-system/WEB-INF/lib
COPY --from=builder --chown=appuser:appgroup /app/src/main/webapp ./src/main/webapp
COPY --chown=appuser:appgroup db ./db

# Ensure app directories are owned by appuser
RUN mkdir -p /app/target/tomcat-embed /app/uploads && \\
    chown -R appuser:appgroup /app

# Switch to non-root execution
USER appuser

# JVM memory constraints tuned for 512 MB container (Render free tier)
ENV JAVA_OPTS="-XX:+UseContainerSupport -Xms128m -Xmx320m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Kolkata"

# Port will be dynamically injected by Render
EXPOSE 8080

# Use exec form so SIGTERM signals pass directly to JVM for graceful shutdown
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -cp 'target/classes:target/complaint-system/WEB-INF/lib/*' com.college.complaint.AppRunner"]
"""

files = {
    '.gitattributes': gitattributes,
    '.gitignore': gitignore,
    '.dockerignore': dockerignore,
    'render.yaml': render_yaml,
    'Dockerfile': dockerfile
}

for name, content in files.items():
    clean_lf = content.replace("\r\n", "\n").strip() + "\n"
    with open(name, "wb") as f:
        f.write(clean_lf.encode("utf-8"))
    print(f"Wrote {name} ({len(clean_lf.encode('utf-8'))} bytes, LF only, no BOM)")
