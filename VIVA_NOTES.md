# Advanced Java Viva Examination Questions & Answers
## College Complaint & Feedback Management System

This guide prepares you for your viva / oral examination by covering the foundational Java, Servlet, JDBC, security, and architectural questions related to this project.

---

### Q1: What is the MVC architecture, and how is it implemented in this project?
**Answer:**
MVC stands for Model-View-Controller:
- **Model**: Represents the business data and persistence layer. In this project, POJO classes (`Complaint`, `Admin`, `Department`, `Feedback`, `StatusLog`) represent entities, and DAO classes (`ComplaintDAOImpl`, etc.) handle JDBC database operations.
- **View**: Represents the user presentation layer. In our project, this is implemented using **JSP** (Jakarta Server Pages) and **JSTL** tag libraries, styled with custom CSS. Views only display data passed by the controller and contain no direct database queries.
- **Controller**: Coordinates between Model and View. Jakarta Servlets (`HomeServlet`, `ComplaintSubmitServlet`, `AdminComplaintsServlet`, etc.) receive HTTP requests, validate input via `ValidationService`, call DAOs, put model attributes into the request, and forward to the corresponding JSP.

---

### Q2: Explain the Servlet Lifecycle and the methods involved.
**Answer:**
A servlet lifecycle is managed by the servlet container (Apache Tomcat):
1. **Loading and Instantiation**: The container loads the servlet class and instantiates it.
2. **Initialization (`init(ServletConfig config)`)**: Called once when the servlet is first created. Used to initialize resources or read configuration parameters.
3. **Request Handling (`service(ServletRequest req, ServletResponse res)`)**: Called on every client request. In `HttpServlet`, the `service()` method dispatches the call to `doGet()`, `doPost()`, `doPut()`, or `doDelete()` based on the HTTP method.
4. **Destruction (`destroy()`)**: Called once when the application stops or the container undeploys the servlet. Used to release database connections, threads, or open files.

---

### Q3: What is the difference between `Statement` and `PreparedStatement` in JDBC? Why did we use `PreparedStatement` everywhere?
**Answer:**
- **PreparedStatement** precompiles the SQL query template on the database server. Dynamic parameters are bound using placeholders (`?`).
- **Statement** compiles and executes raw SQL strings directly every time.
- **Key Reasons for using `PreparedStatement`**:
  1. **Security (SQL Injection Prevention)**: Parameter values are automatically escaped by the JDBC driver and treated strictly as literal data, preventing malicious SQL code injection.
  2. **Performance**: Precompiled SQL statements can be cached and reused by the database query execution plan.
  3. **Type Safety**: Provides typed setter methods (`setInt()`, `setString()`, `setTimestamp()`), avoiding manual quote escaping or date format conversion.

---

### Q4: What is a Connection Pool, and why is HikariCP used instead of `DriverManager.getConnection()`?
**Answer:**
- Opening a new physical database connection via `DriverManager.getConnection()` involves TCP handshakes, authentication, and resource allocation on both client and server, which takes 50–200 ms per request.
- A **Connection Pool** (like **HikariCP**) maintains a pool of pre-allocated, established physical connections in memory. When a servlet needs a connection, it borrows one instantly in sub-milliseconds (`pool.getConnection()`) and returns it back to the pool upon calling `conn.close()`.
- HikariCP is recognized as the fastest, lightweight, and most reliable connection pool library in the Java ecosystem.

---

### Q5: How is session management implemented, and how did we prevent Session Fixation attacks?
**Answer:**
- Session management is implemented using `HttpServletRequest.getSession(true)` which creates or retrieves an `HttpSession`. The container tracks sessions using a secure HTTP cookie named `CAMPUS_SESSION_ID` with the `HttpOnly` flag enabled.
- **Session Fixation Prevention**: When an administrator logs in, any existing unauthenticated session is explicitly destroyed using `oldSession.invalidate()`, and a fresh, authenticated session is created using `request.getSession(true)`. This ensures an attacker cannot force a known session ID onto a victim before login.

---

### Q6: What is a Servlet Filter, and how is it used in this application?
**Answer:**
A Servlet Filter (`jakarta.servlet.Filter`) is a pluggable interceptor that pre-processes requests before they reach a servlet and post-processes responses before they return to the client.
Our project implements four dedicated filters:
1. `EncodingFilter`: Enforces UTF-8 character encoding on both request and response.
2. `SecurityHeadersFilter`: Appends HTTP headers (`X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, CSP) to defend against clickjacking and MIME sniffing.
3. `CsrfFilter`: Generates and validates cryptographic CSRF tokens on all POST/mutation requests.
4. `AuthFilter`: Protects all `/admin/*` routes, redirecting unauthenticated users to `/admin/login` and verifying `SUPER_ADMIN` privileges for account creation.

---

### Q7: How do Server-Sent Events (SSE) work in Java, and why use SSE over WebSockets here?
**Answer:**
- **Server-Sent Events (SSE)** is an HTTP standard (`text/event-stream`) allowing a server to push real-time events to client browsers over a persistent HTTP connection.
- In Jakarta Servlet 6, it is implemented using an asynchronous servlet (`asyncSupported = true`) via `request.startAsync()`. The `AsyncContext` holds the open HTTP connection without blocking container request worker threads.
- **Why SSE for this project?**
  1. Our use case (complaint status updates and new complaint notifications) is primarily **unidirectional** (server to client).
  2. SSE works over standard HTTP/1.1 and HTTP/2 without protocol upgrades, passing through standard firewalls and reverse proxies without special configuration.
  3. The browser's native `EventSource` API handles automatic reconnection and reconnection backoff natively.

---

### Q8: What is CSRF (Cross-Site Request Forgery), and how does the application defend against it?
**Answer:**
- CSRF is an attack where a malicious website tricks a logged-in user's browser into executing unwanted actions (e.g. submitting a complaint or changing admin passwords) on a trusted application where the user is currently authenticated.
- **Defense**: We implemented the **Synchronizer Token Pattern** via `CsrfFilter`:
  1. A cryptographically random token (`SecureRandom`, Base64 encoded) is generated and stored in the user's session.
  2. Every HTML `<form>` includes this token as a hidden field `<input type="hidden" name="csrfToken" value="${csrfToken}">`.
  3. On state-changing HTTP methods (POST, PUT, DELETE), `CsrfFilter` compares the form parameter with the session token. If the token is missing or mismatched, the request is immediately rejected with HTTP `403 Forbidden`.

---

### Q9: Why is BCrypt used for passwords instead of MD5 or SHA-256?
**Answer:**
- Fast hashing algorithms like MD5 or plain SHA-256 can compute billions of hashes per second on modern GPUs, making them vulnerable to brute-force dictionary and rainbow table attacks.
- **BCrypt** is an adaptive, salted cryptographic hash function based on the Blowfish cipher:
  1. It includes a built-in random salt, ensuring identical passwords generate completely different hashes.
  2. It has an adjustable **work factor / cost parameter** (we use log rounds = 12), which intentionally slows down hash computation to resist GPU brute-forcing.

---

### Q10: How does the application prevent XSS (Cross-Site Scripting)?
**Answer:**
- **Contextual Output Escaping**: All dynamic user-submitted content (subjects, descriptions, remarks, student names) is rendered in JSP using JSTL `<c:out value="${...}"/>` or `fn:escapeXml()`. This automatically converts dangerous HTML characters (`<`, `>`, `&`, `"`, `'`) into safe XML entities (`&lt;`, `&gt;`), rendering them strictly as text rather than executable scripts.
- **Content Security Policy (CSP)**: The `SecurityHeadersFilter` injects a CSP header restricting script execution to approved origins.

---

### Q11: What is the DAO (Data Access Object) Pattern, and what are its benefits?
**Answer:**
The DAO pattern abstracts and encapsulates all access to the data source.
- We define interfaces (`ComplaintDAO`, `AdminDAO`, `DepartmentDAO`, `FeedbackDAO`, `StatusLogDAO`) specifying business data operations (`findById`, `searchAndFilter`, `updateStatus`).
- We provide concrete implementations (`ComplaintDAOImpl`) using JDBC.
- **Benefits**:
  1. **Separation of Concerns**: Controllers are decoupled from database queries and SQL syntax.
  2. **Maintainability**: If database schema or query logic changes, only the DAO implementation needs modification.
  3. **Testability**: DAO interfaces allow straightforward mocking during automated testing.

---

### Q12: How is Rate Limiting implemented to stop brute-force admin logins?
**Answer:**
- We implemented an in-memory sliding-window rate limiter in `RateLimiter.java` and recorded attempts in the `login_attempts` table.
- Failed attempts are tracked per composite key `(IP Address + Username)`.
- If 5 failed attempts occur within a 5-minute sliding window, the account is temporarily locked for 5 minutes (`lockedUntil = now + 5 minutes`). Any subsequent attempt during this lockout is rejected before hitting database credentials.
- A successful login clears the attempt history for that key.

---

### Q13: What is the purpose of `DatabaseInitializer` and how does auto-seeding work?
**Answer:**
- `DatabaseInitializer` inspects the database metadata on application startup.
- If the required tables (`complaints`, `departments`, `admins`) do not exist, it automatically reads and executes `db/schema.sql` to construct the tables, indexes, and foreign keys.
- It then executes `db/seed.sql` to populate sample departments, realistic complaints, and the default super administrator account.
- This ensures zero-configuration onboarding: an evaluator can run the application on an empty database without running manual SQL scripts.

---

### Q14: How does the application handle safe file uploads?
**Answer:**
File uploads are processed using Jakarta Servlet's `@MultipartConfig` and `FileUploadUtil`:
1. **Size Validation**: Upload size is strictly limited to 2 MB (`MAX_FILE_SIZE = 2 * 1024 * 1024`).
2. **Content-Type & Extension Checking**: Validates both the MIME type (`image/jpeg`, `image/png`, `image/webp`) and file extension to prevent executable files (`.jsp`, `.exe`, `.sh`) from being uploaded.
3. **Randomized Filenames**: Files are saved with a randomly generated `UUID` (e.g. `3f1a2b...png`) to prevent directory traversal and file overwrite attacks.

---

### Q15: What is the difference between `RequestDispatcher.forward()` and `HttpServletResponse.sendRedirect()`?
**Answer:**
- **`forward()`**: Server-side dispatch. The request and response objects are passed to another resource (e.g. JSP) on the server without informing the browser. The browser URL does not change, and only one HTTP round-trip occurs.
- **`sendRedirect()`**: Client-side redirect. The server returns an HTTP 302 redirect response with a `Location` header. The browser initiates a brand-new HTTP GET request to the new URL. The URL in the address bar changes, and two HTTP round-trips occur.
- In this project:
  - We use **`forward()`** when rendering views from controllers (e.g. forwarding to `index.jsp` with request attributes).
  - We use **`sendRedirect()`** after successful POST actions (Post-Redirect-Get pattern) to prevent duplicate form submissions if the user refreshes the browser.

---

### Q16: How are Student and Admin logins completely separated and isolated in this system?
**Answer:**
- **Separate Endpoints & Visual Themes**: Students log in at `/student/login` (designed with the warm paper student register aesthetic), while staff administrators log in at `/admin/login` (designed as an official administrative manila desk).
- **Separate Session Keys**: Students are stored under `session.getAttribute("studentUser")`, while staff admins are stored under `session.getAttribute("adminUser")`.
- **Dedicated Guard Filters**:
  - `StudentAuthFilter` guards `/student/*`. If an admin attempts to access `/student/*`, the filter explicitly rejects with `403 Forbidden`.
  - `AuthFilter` guards `/admin/*`. If an authenticated student attempts to access `/admin/*`, it immediately aborts with `403 Forbidden`.
- **Independent Rate Limiting & Remember Tokens**: Failed login counters and persistent remember-me tokens are strictly scoped to user types (`STUDENT` vs `ADMIN`).

---

### Q17: How is strict data isolation enforced so students cannot view each other's grievances?
**Answer:**
- **SQL-Level Enforcement**: In `ComplaintDAOImpl`, queries never rely solely on primary keys (`WHERE id = ?`). All student queries enforce `WHERE id = ? AND student_id = ?` or `WHERE tracking_id = ? AND student_id = ?`.
- **Controller Verification**: In `StudentComplaintDetailServlet`, if a complaint ID exists in the database but belongs to a different student, the servlet logs an unauthorized cross-student access attempt and immediately returns `403 Forbidden` (`resp.sendError(SC_FORBIDDEN)`).
- **Public vs Private Scoping**: Only complaints that are explicitly marked as resolved and approved for the public board (`is_public = TRUE AND status = 'Resolved'`) are displayed on `/home`. All ongoing or unapproved grievances are strictly visible only to their creator.

---

### Q18: How does the system guarantee absolute anonymity for students choosing anonymous submission?
**Answer:**
- **Ownership without Exposure**: The database maintains `student_id` as a foreign key so the student can track their complaint in their personal portal under "My Complaints" and receive in-app notifications.
- **Admin Docket Masking**: In `AdminComplaintDetailServlet` and `admin/complaint_detail.jsp`, the complainant field is masked as `"Anonymous Student"`, and the student's email is replaced with `"[Hidden - Anonymous Submission]"`.
- **CSV Audit Export Masking**: In `CsvExporter.java`, anonymous complaint rows output `"Anonymous Student"` for name and `"Hidden"` for roll number and email.
- **Real-Time Feed Masking**: Live SSE broadcasts dispatched to `admin-feed` strip all identifiable student metadata, broadcasting only the complaint category, department, tracking ID, and timestamp.

---

### Q19: How does the Device-Aware adaptive interface work without relying on client-only frameworks?
**Answer:**
- **Server-Side User-Agent Classification**: `DeviceDetectionFilter` inspects the HTTP `User-Agent` header using regular expressions (`TABLET_PATTERN`, `MOBILE_PATTERN`) and classifies the client as `"mobile"`, `"tablet"`, or `"desktop"`.
- **Request Attribute Injection**: The filter attaches `req.setAttribute("deviceClass", deviceClass)` and boolean flags (`isMobile`, `isDesktop`, `isTablet`) to the request.
- **Adaptive JSP Rendering**: JSPs use JSTL conditional tags (`<c:choose>`, `<c:if test="${isMobile}">`) to render device-tailored structures (e.g. mobile bottom navigation `nav_mobile.jspf` vs desktop top bar `nav_desktop.jspf`).
- **Responsive CSS Layering**: `device.css` and `student.css` pair with the server detection to provide touch target optimizations (>= 48px, `touch-action: manipulation`), input zoom prevention (16px base font size on mobile inputs), and CSS media query fallbacks.

---

### Q20: What is the purpose of `AdminDeviceGuardFilter` and why is admin restricted to laptops?
**Answer:**
- **Design Rationale**: The administrative portal features dense, multi-column triage ledgers, complex date-range filters, Chart.js analytics graphs, and split-pane case dossier editors that require at least a 1024px viewport to manage complaints effectively without information clipping or cramped layouts.
- **Filter Guard**: `AdminDeviceGuardFilter` intercepts any mobile device attempting to access `/admin/*` and forwards it to `WEB-INF/views/admin/laptop_only.jsp`, presenting a full-screen mahogany desk notice: *"STAFF DESK IS FOR LAPTOPS"*.
- **Client-Side Media Query Fallback**: If an administrator on a laptop resizes their browser window below 1024px, `.admin-laptop-notice-overlay` activates via CSS media queries (`@media (max-width: 1023px)`) to prompt them to widen their browser window.

---

### Q21: How are Server-Sent Events (SSE) authorized and scoped to individual student channels?
**Answer:**
- **Channel Architecture**: `EventStreamServlet` manages channel subscriptions using Jakarta Servlet asynchronous processing (`req.startAsync()`).
- **Authorization Guard**: When a client requests `GET /events?channel=...`:
  - `student-{id}`: The servlet checks `session.getAttribute("studentUser")`. If the session is missing or the student ID does not match the channel, access is rejected with `403 Forbidden`.
  - `complaint-{trackingId}`: Public tracking channel; available for anyone holding the valid unique tracking ticket.
  - `admin-feed`: Requires `session.getAttribute("adminUser")`. Unauthorized users are rejected with `403 Forbidden`.
- **Targeted Notification Dispatch**: When an administrator updates a complaint's status in `AdminComplaintDetailServlet`, `EventBroadcaster.broadcastStudentStatusUpdate(studentId, ...)` pushes the event exclusively to that specific student's open SSE connection.

---

### Q22: How does the "Remember Me" persistent authentication mechanism work securely?
**Answer:**
- **Cryptographic Token Generation**: When a student checks "Remember Me for 14 days", `StudentLoginServlet` generates 32 cryptographically secure random bytes via `SecureRandom` and hex-encodes them into a raw token.
- **SHA-256 Database Hashing**: The database table `remember_tokens` stores a salted SHA-256 hash of the token along with `student_id`, `expires_at` (14 days), and `user_agent`. The raw token is NEVER stored in plaintext in the database.
- **Secure Cookie**: The raw token is issued to the browser in an `HttpOnly`, `SameSite=Lax` cookie named `REMEMBER_STUDENT`.
- **Auto-Login**: On subsequent visits, `StudentAuthFilter` reads `REMEMBER_STUDENT`, hashes the value, matches it in `remember_tokens`, verifies expiration, and reconstructs the session transparently.

---

### Q23: How does client-side Canvas image compression and Service Worker offline caching operate?
**Answer:**
- **Canvas Compression**: In `student-form.js`, when a student snaps a photo with `capture="environment"`, an `HTMLImageElement` is loaded and drawn onto an HTML5 `<canvas>`. If the image dimensions exceed 1600px, it is scaled down proportionally and exported via `canvas.toBlob(..., "image/jpeg", 0.85)` before form submission, reducing 10MB mobile camera photos to ~400KB while preserving legibility.
- **Service Worker (`sw.js`)**: A Progressive Web App (PWA) service worker intercepts fetch requests. Critical static assets (`tokens.css`, `base.css`, `student.css`, `manifest.json`) are cached on install (`cache.addAll`).
- **Offline Fallback**: If the student loses internet connection on campus, navigation requests gracefully fall back to `offline.html`, and complaint drafts are automatically stored in browser `localStorage` to prevent lost input.


---

### Q24: What is a Docker Multi-Stage Build and why did we use it for Render?
**Answer:**
- A *multi-stage build* separates the compilation environment from the runtime environment in a single `Dockerfile`.
- In Stage 1 (`build`), we use a full Maven JKK image (maven:3.9.9-eclipse-temurin-21-alpine) to resolve dependencies and build the application WAR.
- In Stage 2 (`runtime`), we copy *only* the compiled application classes and runtime JARs into a lean JRE image (`eclipse-temurin:21-jre-alpine`).
- **Benefits**:
  1. *Minimal Image Size*: Reduces Docker image size from ~800 MB down to ~220 MB by stripping out Maven, compilers, source code, and intermediate caches.
  2. *Security*: Source code and build tools are not present in the runtime container, and the application runs under an unprivileged user (`appuser:appgroup` UID110001) rather than root.

---

### Q25: What is the 12-Factor App methodology for configuration, and how does `AppConfig` implement it?
**Answer:**
- The 12-Factor App methodology (specifically Factor III: *Config*) requires strict separation of configuration from code, storing configuration in the runtime environment rather than hardcoding it into repository files.
- In this project, `AppConfig.java`:
  1. Inspects environment variables first (`System.getenv("DB_URL")`, `System.getenv("PORT")`, `System.getenv("APP_ENV")`).
  2. Falls back to `config.properties` for local zero-config development.
  3. Enforces *fail-fast validation*: In production (`APP_ENV=production`), the application halts immediately with clear diagnostic errors if required variables (`DB_URL`, `DB_USER`) are missing, preventing the container from booting in an inconsistent or vulnerable state.

---

### Q26: What is an ephemeral container filesystem, and why must file uploads be stored in MySQL BLOBs on Render?
**Answer:**
- Container instances on cloud platforms like Render are *ephemeral*; any files written to local disk (such as `./uploads/`) are erased whenever the container restarts, updates, or wakes up from an idle spin-down.
- **Solution (`STORAGE_MODE=db`)**:
  - We added a `complaint_attachments` table with a `data` `MEDIUMBLOB` column (capable of storing files up to 16 MB).
  - Uploaded files are validated for MIME type, magic bytes (JPEG/PNG/WEBP), and SHA-256 checksums, then saved directly into MySQL.
  - `AttachmentServlet` reads the binary blob from MySQL and streams it to the user's browser with `Content-Type`, `X-Content-Type-Options: nosniff`, and access control checks, ensuring attachments persist permanently across container reboots.

---

### Q27: How does a reverse proxy like Render affect client IP resolution and HTTPS security, and how did we handle it?
**Answer:**
- Cloud hosting providers use reverse proxies/edge routers to terminate TLS/SSL and route incoming traffic to internal container ports.
- **Challenges**:
  1. `request.getRemoteAddr()` returns the internal proxy IP rather than the student's real browser IP, which would cause the rate limiter to mistakenly lock out all campus users at once.
  2. `request.isSecure()` returns `false` because the leg from proxy to container is plain HTTP, preventing `Secure` cookie flags from being set.
- **Solutions**:
  1. **Tomcat `RemoteIpValve`**: Configured in `AppRunner.java` to read `X-Forwarded-For` and `X-Forwarded-Proto` (`https`), restoring the client's actual remote address and HTTPS scheme.
  2. **`HttpUtil.getClientIp()`**: Explicitly checks `X-Forwarded-For` and `X-Real-IP` headers to guarantee accurate IP tracking.
  3. **Strict-Transport-Security (HSTS)**: `SecurityHeadersFilter` automatically emits `Strict-Transport-Security: max-age=31536000; includeSubDomains` on HTTPS requests in production to enforce encrypted browser transport.

---

### Q28: Why do we have two health check endpoints (`/healthz` and `/healthz/db`), and what is the difference between liveness and readiness probes?
**Answer:**
- **`/healthz` (Liveness Probe)**: An extremely fast endpoint that returns HTTP 200 `{"status":"UP"}` without making any database queries. Orchestrators (Render, Kubernetes) query this to check if the JVM process and web server are alive and handling HTTP requests. If this fails, the container is restarted.
- **`/healthz/db` (Readiness Probe)**: Executes a lightweight `SELECT 1` query against the database connection pool. This verifies that the application is ready to process real business requests that require database connectivity.
- **Why Separate Them?** If the external database experiences a momentary hiccup or network latency, a combined health check would fail and cause the orchestrator to repeatedly kill and restart the container, worsening downtime rather than waiting for DB 
reconnection.

---

### Q29: What causes cold starts on Render's free tier, and how does the application handle memory constraints (512 MB) and cold-start UX?
**Answer:**
- **Cause**: To conserve cloud resources, Render's free tier spins down containers after 15 minutes of zero traffic. When a new HTTP request arrives, Render spins up the container, which takes ~50 seconds (cold start).
- **Memory Optimization for 512 MB**:
  1. Set JVM heap flags: `-Xms128m -Xmx320m -XX:MaxMetaspaceSize=128m`.
  2. Enabled **Serial Garbage Collector** (`-XX:+UseSerialGC`), which eliminates the heavy multi-threaded memory overhead of G1GC.
  3. Capped HikariCP pool size to 5 connections and Tomcat max threads to 50. Total container RSS stays at ~191 MB under load.
- **Cold-Start User Experience**:
  1. The Progressive Web App Service Worker (`sw.js`) intercepts timeout errors and serves a custom `offline.html` styled with the campus notice board aesthetic: *"The board is waking up. Give it a minute."*
  2. Features an automated 30-second countdown and automatic page reload so users are never greeted with a generic, broken browser error screen.
