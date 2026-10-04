# College Complaint & Feedback Management System

An Advanced Java (Jakarta Servlet 6, JSP, HikariCP, JDBC) full-stack web application designed with an authentic **"Campus Notice Board, Digitised"** tactile neo-brutalist paper aesthetic, featuring **isolated dual-portal architecture (Student vs. Admin)** and **device-aware adaptive interfaces**.

---

## 📌 Project Overview

The **College Complaint & Feedback Management System** provides college students and staff administrators with dedicated, tailored portals:
- **Student Portal (`/student/*`)**: Device-aware experience adapting across phones, tablets, and laptops. On phones, students get a thumb-friendly bottom nav bar, a 4-step wizard with client-side Canvas photo compression and draft recovery, camera capture, and PWA offline support. On laptops, students get a master-detail grievance desk with keyboard shortcuts (`N`, `/`). Absolute anonymity is cryptographically and logically safeguarded.
- **Admin Staff Desk (`/admin/*`)**: Strictly desktop/laptop-only. Enforced by both server-side HTTP filter (`AdminDeviceGuardFilter`) and responsive CSS overlay. Mobile attempts are greeted with a full-screen mahogany desk notice: *"STAFF DESK IS FOR LAPTOPS"*.

### Key Highlights
- **100% Pure Java Backend**: Jakarta Servlet 6.0 + JSP 3.1 + JSTL 3.0 running on embedded or standalone Apache Tomcat 10.1.
- **Two Completely Isolated Portals**: Dedicated session scopes, separate authentication filters (`StudentAuthFilter` vs `AuthFilter`), cross-portal 403 barriers, and independent remember-me cookies with SHA-256 token hashing.
- **Device-Aware Adaptive UI**:
  - **Phone (≤ 640px)**: Bottom sticky thumb bar, 4-step step-by-step grievance wizard, camera capture (`capture="environment"`), client-side Canvas auto-compression (max 1600px edge), PWA manifest + service worker (`sw.js`) with offline notice.
  - **Tablet (641px – 1024px)**: Adaptive 2-column card grid, collapsible sticky filters, expandable status dossiers.
  - **Laptop (≥ 1025px)**: Top pinned stationery bar, master-detail ledger grid with live detail panes, full keyboard shortcuts.
  - **Admin Laptop-Only Enforcement**: Blocks smartphone User-Agents on server (`AdminDeviceGuardFilter`) with HTTP 403 mahogany wood notice, plus CSS desktop check overlay on viewport resize.
- **Absolute Anonymity Protection**: Anonymous complaints logically decouple `student_id` during all admin docket views, detail screens, CSV export dumps, and real-time SSE broadcasts.
- **Targeted Server-Sent Events (SSE)**: Scoped async pub/sub channels (`student-{id}`, `complaint-{trackingId}`, `admin-feed`) with keep-alive heartbeats and automatic browser reconnects.
- **Enterprise Security**: BCrypt password hashing, session fixation regeneration, in-memory sliding-window brute-force lockout (5 attempts → 5-minute lockout), cryptographic CSRF token validation on every mutating POST, and strict HTTP security headers (`X-Frame-Options`, `X-Content-Type-Options`, `Content-Security-Policy`).

---

## 🛠️ Technology Stack

| Layer | Technology | Details |
|---|---|---|
| **Language & SDK** | Java 17 / 21 LTS | OpenJDK 21 compatible |
| **Web Container** | Jakarta Servlet 6.0 | Apache Tomcat 10.1 (embedded runner & standard WAR) |
| **View Layer** | JSP 3.1 & JSTL 3.0 | Jakarta Taglibs with XML escaping (`<c:out>`) |
| **Database** | MySQL 8.x / Embedded H2 | Auto-initialized via `DatabaseInitializer` |
| **Connection Pool** | HikariCP 5.1.0 | Fast, thread-safe connection pooling |
| **Security** | jBCrypt 0.4 | Salted BCrypt password hashing + SHA-256 tokens |
| **Real-time** | Server-Sent Events (SSE) | Jakarta `AsyncContext` with scoped channel pub/sub |
| **Offline & PWA** | Web App Manifest + Service Worker | `manifest.json`, `sw.js`, `offline.html` |
| **Client Compression** | HTML5 Canvas API | High-resolution image downsizing (< 1600px) |
| **Testing** | JUnit 5 Jupiter + PowerShell | 16 unit tests + 29 end-to-end smoke tests |
| **Styling** | Handcrafted CSS | Custom design tokens, zero external CSS dependencies |

---

## 🚀 Quick Start (One Command Run)

### Prerequisites
1. **JDK 17 or 21** (`java -version`)
2. **Apache Maven 3.8+** (`mvn -version`)

### 1. Launch with Zero-Config Default
The application is self-bootstrapping with an automatic schema runner that provisions all tables, 5 pre-configured student accounts, 2 staff administrators, 25 sample complaints, 6 academic departments, and reviews on startup:

```bash
mvn compile exec:java
```

Or using the compiled classpath runner:
```bash
java -cp "target/classes;target/complaint-system/WEB-INF/lib/*" com.college.complaint.AppRunner
```

The application will start immediately at:
👉 **`http://localhost:8080/`**

---

## 🔑 Login Credentials

### Student Portal (`/student/login`)
All student accounts are pre-seeded with the password: **`Student@123`**

| Roll Number | Full Name | Email Address | Department | Year |
|---|---|---|---|---|
| `22CS101` | Aarav Sharma | `student1@campus.edu` | Computer Science | 3rd Year |
| `22EC205` | Priya Patel | `student2@campus.edu` | Electronics & Comm. | 3rd Year |
| `23ME042` | Rohan Kulkarni | `student3@campus.edu` | Mechanical Engg. | 2nd Year |
| `24CV018` | Ananya Rao | `student4@campus.edu` | Civil Engineering | 1st Year |
| `21IT089` | Vikram Verma | `student5@campus.edu` | Information Tech. | 4th Year |

### Staff Admin Portal (`/admin/login`)
*Accessible only on laptop/desktop viewports (≥ 1024px).*

| Role | Username | Password | Access Scope |
|---|---|---|---|
| **Super Administrator** | `superadmin` | `Admin@12345` | Global oversight, all departments, status updates |
| **Hostel Warden Admin** | `warden_hostel` | `Admin@12345` | Hostel & Facilities grievance docket |

---

## 📱 Device Experience & Portal Layouts

| Device Class | Viewport | Layout Strategy | Key Capabilities |
|---|---|---|---|
| **Student Phone** | ≤ 640px | Thumb-driven bottom navigation | 4-step wizard, camera capture, Canvas compression, draft autosave, PWA offline notice. |
| **Student Tablet** | 641px – 1024px | 2-column adaptive grid | Responsive cards, quick filter drawer, full touch-friendly targets. |
| **Student Laptop** | ≥ 1025px | Pinned top stationery nav | Master-detail ledger, live ticket preview, keyboard hotkeys (`N` = New Complaint, `/` = Search). |
| **Staff Admin** | ≥ 1024px | Desktop Register & Docket Ledger | Manila folder desks, real-time incoming SSE ticker, audit history stamps, CSV export. |
| **Admin Mobile Intercept** | < 1024px | Blocked with Mahogany desk notice | Server-side `AdminDeviceGuardFilter` (HTTP 403) and CSS overlay preventing mobile admin usage. |

---

## 🗄️ Database Configuration (Using MySQL 8)

To connect the application to an external MySQL 8 instance:

1. Create a database and grant access:
   ```sql
   CREATE DATABASE college_complaint_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'campus_user'@'localhost' IDENTIFIED BY 'Campus@123';
   GRANT ALL PRIVILEGES ON college_complaint_db.* TO 'campus_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. Either export environment variables:
   ```bash
   export DB_DRIVER="com.mysql.cj.jdbc.Driver"
   export DB_URL="jdbc:mysql://localhost:3306/college_complaint_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
   export DB_USER="campus_user"
   export DB_PASSWORD="Campus@123"
   ```

3. Or configure `src/main/resources/config.properties`:
   ```properties
   db.driver=com.mysql.cj.jdbc.Driver
   db.url=jdbc:mysql://localhost:3306/college_complaint_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   db.user=campus_user
   db.password=Campus@123
   ```

`DatabaseInitializer` detects database dialect, creates missing tables (`students`, `complaints`, `departments`, `feedback`, `remember_tokens`, `password_resets`, `notifications`), and applies seed data.

---

## 🧪 Testing & Verification

### 1. Run JUnit 5 Unit Tests
```bash
mvn clean test
```
*Executes all 16 test cases covering StudentDAO, ComplaintDAO, PasswordUtil, rate limiting, and input validation with 0 failures.*

### 2. Run Comprehensive Smoke Test
With the application running on port 8080:
```powershell
powershell -ExecutionPolicy Bypass -File "scripts/smoke-test.ps1"
```
*Executes 29 automated end-to-end assertions with 0 failures:*
- Public landing page, notice board, and tracking lookups (200 OK)
- Strict HTTP security headers (`CSP`, `X-Frame-Options`, `X-Content-Type-Options`)
- Unauthenticated 302 redirects for `/student/home` and `/admin/dashboard`
- Cryptographic CSRF enforcement on all POST requests
- Student registration, duplicate email rejection, and student login
- SHA-256 hashed Remember-Me cookie persistence
- Cross-portal 403 access barriers (Student accessing admin URL, Admin accessing student URL)
- Mobile User-Agent rejection from `/admin/*` via `AdminDeviceGuardFilter`
- Anonymous complaint submission with full student masking on Admin Docket & CSV exports
- Cross-student data isolation (Student A cannot view Student B's complaints)
- Admin status transition triggering real-time student in-app notification
- Password reset token generation, expiration checks, and password updates

---

## 📸 Interface Screenshots

All 12 design-verified screenshots captured at authentic device viewports are located in `./screenshots/`:

| Screenshot File | Viewport | Description |
|---|---|---|
| `student_mobile_login.png` | 390 × 844 | Student mobile phone login with warm stationery styling & remember-me |
| `student_mobile_home.png` | 390 × 844 | Student mobile dashboard showing greeting, quick actions & bottom nav bar |
| `student_mobile_form.png` | 390 × 844 | 4-step mobile complaint wizard with progress bar & photo attachment |
| `student_mobile_complaints.png` | 390 × 844 | Mobile grievance list with status stamps & sticky thumb bar |
| `student_tablet_home.png` | 768 × 1024 | Tablet adaptive 2-column cards layout with responsive navigation |
| `student_desktop_form.png` | 1440 × 900 | Laptop 2-column complaint desk with live railway ticket receipt preview |
| `student_desktop_complaints.png` | 1440 × 900 | Laptop master-detail grievance ledger with keyboard shortcuts |
| `admin_mobile_laptop_notice.png` | 390 × 844 | Mahogany desk notice: *"STAFF DESK IS FOR LAPTOPS"* blocking phone access |
| `admin_dashboard_desktop.png` | 1440 × 950 | Admin manila folder dashboard with live incoming SSE ticker |
| `admin_complaints_desktop.png` | 1440 × 950 | Admin complaints register book with multi-filters and CSV export |
| `admin_detail_desktop.png` | 1440 × 1100 | Admin case dossier with status rubber stamps & audit trail |
| `admin_feedback_desktop.png` | 1440 × 950 | Admin campus feedback ledger with rating stars & sentiment cards |

---

## ☁️ Deploying to Render (Free Tier with External MySQL)

The application is containerized with a production-tuned multi-stage `Dockerfile` (`maven:3.9.9-eclipse-temurin-21-alpine` → `eclipse-temurin:21-jre-alpine`) configured for Render's 512 MB memory constraint, proxy TLS termination, ephemeral disk, and zero-downtime health checking.

### 1. Free External MySQL Providers
Because Render Web Services have ephemeral local filesystems and Render's managed PostgreSQL doesn't host MySQL natively, use one of the following free, highly reliable cloud MySQL databases:

#### Option A: TiDB Cloud Serverless (Recommended — 5 GiB Free Forever)
- **Sign Up**: [tidbcloud.com](https://tidbcloud.com) (no credit card required).
- **Create Cluster**: Select **Serverless** → Choose region matching your Render service (e.g. AWS `us-east-1` or `ap-southeast-1`).
- **Connection Details**: In the TiDB Cloud console, click **Connect** → choose **General** / **Java**.
- **JDBC URL Format**:
  ```
  jdbc:mysql://<host>:4000/<dbname>?sslMode=VERIFY_IDENTITY&serverTimezone=UTC&allowPublicKeyRetrieval=true
  ```

#### Option B: Aiven for MySQL (Free Trial / Credits)
- **Sign Up**: [aiven.io](https://aiven.io).
- **Create Service**: Select **MySQL** → Choose Cloud & Region (matching Render location).
- **Connection Details**: Copy Service URI from dashboard.
- **JDBC URL Format**:
  ```
  jdbc:mysql://<host>:<port>/<dbname>?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true
  ```

---

### 2. Environment Variables Configuration

Configure these environment variables in your Render Web Service dashboard under **Environment**:

| Variable | Required | Default / Recommended | Description |
|---|---|---|---|
| `PORT` | Auto | `8080` (Render sets automatically) | Port the embedded Tomcat container listens on |
| `APP_ENV` | **Yes** | `production` | Enables production security checks, HSTS, and fail-fast DB validation |
| `DB_URL` | **Yes** | `jdbc:mysql://...` | Full JDBC connection string to external MySQL/TiDB database |
| `DB_USER` | **Yes** | `campus_user` | Database user with CRUD and DDL permissions |
| `DB_PASSWORD` | **Yes** | *(your secure password)* | Database password |
| `DB_POOL_SIZE` | No | `5` | HikariCP pool size (capped at 5 to keep memory well under 512 MB) |
| `STORAGE_MODE` | **Yes** | `db` | Persists grievance attachments inside MySQL (`complaint_attachments`) to survive container restarts |
| `SEED_DEMO_DATA` | No | `true` (demo) or `false` (clean) | If `true`, seeds sample students, admins, and complaints on first boot |
| `ADMIN_INITIAL_PASSWORD` | No | *(Auto-generated 16-char random if unset)* | Password for initial `superadmin` account |

---

### 3. Step-by-Step Render Deployment Guide

#### Step 1: Initialize Git and Push to GitHub
Open your terminal in the project directory:
```bash
git init
git add .
git commit -m "feat: production-ready college complaint system for Render"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo-name>.git
git push -u origin main
```

#### Step 2: Provision Your Cloud Database
1. Create your database cluster on **TiDB Cloud** or **Aiven**.
2. Note your hostname, port, database name, username, and password.
3. *Note*: You do **not** need to manually run `schema.sql`. The application's `DatabaseInitializer` will automatically detect the database and run all table migrations and index creations on first boot.

#### Step 3: Deploy on Render
1. Log in to [render.com](https://render.com) and click **New +** → **Web Service**.
2. Connect your GitHub repository.
3. Configure the service settings:
   - **Name**: `college-complaint-system`
   - **Region**: Same region as your database (e.g. `Frankfurt`, `Oregon`, `Singapore`)
   - **Environment**: `Docker`
   - **Branch**: `main`
   - **Plan**: `Free`
4. Under **Health Check Path**, enter:
   ```
   /healthz
   ```
5. Expand **Advanced** → **Add Environment Variable** and add the variables listed above (`APP_ENV`, `DB_URL`, `DB_USER`, `DB_PASSWORD`, `STORAGE_MODE`, `SEED_DEMO_DATA`).
6. Click **Create Web Service**.

#### Step 4: Verification & Smoke Test
1. Watch the Render build logs. The multi-stage build will compile the WAR, extract it, and start `com.college.complaint.AppRunner`.
2. Once deployed, open your live Render URL:
   - Public Board: `https://<your-service>.onrender.com/`
   - Health Check: `https://<your-service>.onrender.com/healthz` (returns `{"status":"UP"}`)
   - DB Readiness: `https://<your-service>.onrender.com/healthz/db` (returns `{"status":"UP","database":"OK"}`)
   - Student Portal: `https://<your-service>.onrender.com/student/login`
   - Admin Staff Desk: `https://<your-service>.onrender.com/admin/login` (access via laptop)

---

### 4. Cold-Start & Memory Behavior on Free Tier
- **Spin Down on Idle**: Render's free tier spins down the web service after 15 minutes of inactivity.
- **Cold-Start Duration**: On the first request after spinning down, the container wakes up in ~50-60 seconds. The bundled Service Worker and offline page (`offline.html`) inform users: *"The board is waking up. Give it a minute."* with an auto-refresh countdown.
- **512 MB RAM Optimization**: The JVM is tuned with `-Xms128m -Xmx320m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError`, keeping total container memory around 190–240 MB even under 50 concurrent connections, preventing out-of-memory container restarts.

---

## 📦 Building Deployable WAR

To build a standard standalone WAR file for deployment onto Apache Tomcat 10.1:
```bash
mvn clean package
```
The output WAR is generated at:
`target/complaint-system.war`
