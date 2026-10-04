<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="#1F5D4A">
  <link rel="manifest" href="${pageContext.request.contextPath}/manifest.json">
  <title>Lodge Complaint • Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
  <style>
    .ruled-paper-slip {
      background: var(--color-card);
      border: var(--border-thick);
      box-shadow: var(--shadow-hard-lg);
      padding: 2rem;
      position: relative;
    }
    .receipt-preview-slip {
      background: var(--color-paper-light);
      border: var(--border-thick);
      box-shadow: var(--shadow-hard);
      padding: 1.5rem;
      position: sticky;
      top: 90px;
    }
  </style>
</head>
<body>

  <div class="student-shell">

    <!-- Desktop Header -->
    <%@ include file="nav_desktop.jspf" %>

    <!-- Mobile Header -->
    <header class="site-header" style="display: block; border-bottom: var(--border-thick);">
      <div class="container" style="display: flex; justify-content: space-between; align-items: center; padding: 0.6rem 1rem;">
        <a href="${pageContext.request.contextPath}/student/home" class="mono" style="font-size: 0.85rem; font-weight: 700; color: var(--color-ink); text-decoration: none;">
          ← Student Desk
        </a>
        <span style="font-family: var(--font-display); font-weight: 800; font-size: 1rem;">File Grievance</span>
      </div>
    </header>

    <main class="slip-container" style="max-width: 1200px;">

      <!-- Draft Restored Banner -->
      <div id="draft-restored-banner" style="display: none; background: var(--color-highway-yellow-light); border: var(--border-thin); padding: 0.6rem 1rem; margin-bottom: 1rem; font-family: var(--font-mono); font-size: 0.82rem;">
        📝 Draft restored from this device. You can edit and submit.
      </div>

      <c:if test="${not empty errors}">
        <div class="alert alert-danger" style="margin-bottom: 1.25rem;">
          <strong>Please address the following errors:</strong>
          <ul style="margin: 0.4rem 0 0 1.25rem; padding: 0;">
            <c:forEach var="err" items="${errors}">
              <li><c:out value="${err.value}"/></li>
            </c:forEach>
          </ul>
        </div>
      </c:if>

      <c:if test="${not empty systemError}">
        <div class="alert alert-danger" style="margin-bottom: 1.25rem;">
          <c:out value="${systemError}"/>
        </div>
      </c:if>

      <!-- Hidden Holder for Student Name for Live Receipt -->
      <input type="hidden" id="student-name-holder" value="<c:out value='${student.fullName}'/>">

      <div class="desktop-two-col">

        <!-- Form Column (Left on Desktop, Full on Mobile) -->
        <div class="ruled-paper-slip">
          <div class="tape-pin"></div>

          <!-- Pre-filled & Locked Complainant Identification -->
          <div style="background: var(--color-paper-light); border: var(--border-thin); padding: 0.75rem 1rem; margin-bottom: 1.5rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.5rem;">
            <div>
              <span style="font-family: var(--font-mono); font-size: 0.72rem; color: var(--color-ink-muted); text-transform: uppercase;">
                Authenticated Student:
              </span><br>
              <strong><c:out value="${student.fullName}"/></strong> (<span class="mono"><c:out value="${student.rollNo}"/></span>)
            </div>
            <div style="font-family: var(--font-mono); font-size: 0.78rem; color: var(--color-ink-muted);">
              Dept: <c:out value="${student.departmentName}"/>
            </div>
          </div>

          <!-- Step Indicator (Visible on Mobile only) -->
          <div class="step-indicator">
            <div id="step-dot-1" class="step-dot active">1</div>
            <div class="step-line"></div>
            <div id="step-dot-2" class="step-dot">2</div>
            <div class="step-line"></div>
            <div id="step-dot-3" class="step-dot">3</div>
            <div class="step-line"></div>
            <div id="step-dot-4" class="step-dot">4</div>
          </div>

          <form id="complaint-form" action="${pageContext.request.contextPath}/student/complaint/new" method="POST" enctype="multipart/form-data">
            <input type="hidden" name="csrfToken" value="${csrfToken}">

            <!-- STEP 1: What Happened (Category + Priority) -->
            <div id="step-panel-1" class="step-panel active">
              <h2 style="font-family: var(--font-display); font-size: 1.35rem; margin-bottom: 0.5rem;">
                Step 1: Category & Priority
              </h2>
              <p style="color: var(--color-ink-muted); font-size: 0.88rem; margin-bottom: 1.25rem;">
                Where does the issue occur and how urgent is it?
              </p>

              <div class="form-group" style="margin-bottom: 1.25rem;">
                <label for="field-category" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
                  Grievance Category *
                </label>
                <select id="field-category" name="category" class="input-mobile" required style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body);">
                  <option value="">-- Choose Category --</option>
                  <c:forEach var="cat" items="${categories}">
                    <option value="${cat}" ${complaint.category eq cat ? 'selected' : ''}>${cat}</option>
                  </c:forEach>
                </select>
              </div>

              <div class="form-group" style="margin-bottom: 1.25rem;">
                <label style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase; display: block; margin-bottom: 0.5rem;">
                  Priority Level *
                </label>
                <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
                  <label class="btn btn-secondary" style="flex: 1; min-height: 48px; display: flex; align-items: center; justify-content: center; gap: 0.4rem; cursor: pointer;">
                    <input type="radio" name="priority" value="Low" ${complaint.priority eq 'Low' ? 'checked' : ''} style="width: 18px; height: 18px;" />
                    <span>Low</span>
                  </label>
                  <label class="btn btn-secondary" style="flex: 1; min-height: 48px; display: flex; align-items: center; justify-content: center; gap: 0.4rem; cursor: pointer;">
                    <input type="radio" name="priority" value="Medium" ${empty complaint.priority or complaint.priority eq 'Medium' ? 'checked' : ''} style="width: 18px; height: 18px;" />
                    <span>Medium</span>
                  </label>
                  <label class="btn btn-secondary" style="flex: 1; min-height: 48px; display: flex; align-items: center; justify-content: center; gap: 0.4rem; cursor: pointer; color: var(--color-signal-red);">
                    <input type="radio" name="priority" value="High" ${complaint.priority eq 'High' ? 'checked' : ''} style="width: 18px; height: 18px;" />
                    <span>High (Urgent)</span>
                  </label>
                </div>
              </div>

              <div class="mobile-step-nav" style="margin-top: 1.5rem; text-align: right;">
                <button type="button" id="btn-step-1-next" class="btn btn-primary" style="min-height: 48px;">
                  Continue to Details ➔
                </button>
              </div>
            </div>

            <!-- STEP 2: Details (Subject + Description) -->
            <div id="step-panel-2" class="step-panel">
              <h2 style="font-family: var(--font-display); font-size: 1.35rem; margin-bottom: 0.5rem;">
                Step 2: Grievance Details
              </h2>
              <p style="color: var(--color-ink-muted); font-size: 0.88rem; margin-bottom: 1.25rem;">
                Describe the specific issue clearly so maintenance staff can resolve it quickly.
              </p>

              <div class="form-group" style="margin-bottom: 1.25rem;">
                <label for="field-subject" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
                  Summary / Headline *
                </label>
                <input type="text"
                       id="field-subject"
                       name="subject"
                       class="input-mobile"
                       value="<c:out value='${complaint.subject}'/>"
                       required
                       placeholder="e.g. WiFi router down in Lab 3 (Turing Block)"
                       style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body);" />
              </div>

              <div class="form-group" style="margin-bottom: 1.25rem;">
                <label for="field-description" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
                  Detailed Description *
                </label>
                <textarea id="field-description"
                          name="description"
                          class="input-mobile"
                          rows="6"
                          required
                          placeholder="Provide room numbers, equipment IDs, frequency of issue, or safety risks..."
                          style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body); resize: vertical;"><c:out value="${complaint.description}"/></textarea>
              </div>

              <div class="mobile-step-nav" style="margin-top: 1.5rem; display: flex; justify-content: space-between;">
                <button type="button" id="btn-step-2-back" class="btn btn-secondary" style="min-height: 48px;">
                  ← Back
                </button>
                <button type="button" id="btn-step-2-next" class="btn btn-primary" style="min-height: 48px;">
                  Attach Photo & Privacy ➔
                </button>
              </div>
            </div>

            <!-- STEP 3: Photo + Anonymity -->
            <div id="step-panel-3" class="step-panel">
              <h2 style="font-family: var(--font-display); font-size: 1.35rem; margin-bottom: 0.5rem;">
                Step 3: Evidence & Anonymity
              </h2>
              <p style="color: var(--color-ink-muted); font-size: 0.88rem; margin-bottom: 1.25rem;">
                Attach a photo from camera or files. You may also mask your identity from staff.
              </p>

              <!-- Photo Upload Zone -->
              <div class="form-group" style="margin-bottom: 1.5rem;">
                <label style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase; display: block; margin-bottom: 0.4rem;">
                  Photo Evidence (Optional, max 2MB)
                </label>

                <!-- Drop & File Zone -->
                <div id="file-drop-target" class="file-drop-zone">
                  <p style="margin: 0 0 0.5rem 0; font-family: var(--font-mono); font-size: 0.85rem;">
                    📁 Drop photo onto this board, or snap with camera
                  </p>
                  <input type="file"
                         id="file-attachment"
                         name="attachment"
                         accept="image/jpeg,image/png,image/webp"
                         capture="environment"
                         style="display: none;" />
                  <label for="file-attachment" class="camera-attach-btn">
                    📷 Take Photo / Pick File
                  </label>
                  <div id="image-preview-area"></div>
                </div>
              </div>

              <!-- Anonymity Checkbox -->
              <div style="background: var(--color-paper-light); border: var(--border-thin); padding: 1rem; margin-bottom: 1.25rem;">
                <div style="display: flex; align-items: flex-start; gap: 0.75rem;">
                  <input type="checkbox"
                         id="field-anonymous"
                         name="isAnonymous"
                         value="true"
                         ${complaint.anonymous ? 'checked' : ''}
                         style="width: 22px; height: 22px; margin-top: 2px; accent-color: var(--color-board-green); cursor: pointer;" />
                  <div>
                    <label for="field-anonymous" style="font-weight: 700; font-size: 0.95rem; cursor: pointer; display: block;">
                      Submit Anonymously
                    </label>
                    <span style="font-size: 0.82rem; color: var(--color-ink-muted); line-height: 1.3; display: block; margin-top: 2px;">
                      Your name, roll number, and email will be hidden from staff and admins. The slip is still linked to your own student portal so you can track it under My Complaints.
                    </span>
                  </div>
                </div>
              </div>

              <!-- Public Wall Checkbox -->
              <div style="display: flex; align-items: center; gap: 0.6rem; margin-bottom: 1rem;">
                <input type="checkbox" id="field-public" name="isPublic" value="true" ${complaint.publicCase ? 'checked' : ''} style="width: 18px; height: 18px; accent-color: var(--color-board-green);" />
                <label for="field-public" style="font-size: 0.88rem;">
                  Allow display on Public Notice Board once resolved
                </label>
              </div>

              <div class="mobile-step-nav" style="margin-top: 1.5rem; display: flex; justify-content: space-between;">
                <button type="button" id="btn-step-3-back" class="btn btn-secondary" style="min-height: 48px;">
                  ← Back
                </button>
                <button type="button" id="btn-step-3-next" class="btn btn-primary" style="min-height: 48px;">
                  Review Slip ➔
                </button>
              </div>
            </div>

            <!-- STEP 4: Review + Submit (Mobile) -->
            <div id="step-panel-4" class="step-panel">
              <h2 style="font-family: var(--font-display); font-size: 1.35rem; margin-bottom: 0.5rem;">
                Step 4: Final Review
              </h2>
              <p style="color: var(--color-ink-muted); font-size: 0.88rem; margin-bottom: 1.25rem;">
                Confirm your details before officially pinning the slip to the registry.
              </p>

              <div style="background: var(--color-paper-light); border: var(--border-thin); padding: 1rem; margin-bottom: 1.5rem; font-family: var(--font-mono); font-size: 0.85rem; line-height: 1.6;">
                <div><strong>Category:</strong> <span id="rev-category">General</span></div>
                <div><strong>Priority:</strong> <span id="rev-priority">Medium</span></div>
                <div><strong>Headline:</strong> <span id="rev-subject"></span></div>
                <div><strong>Anonymous:</strong> <span id="rev-anon">No</span></div>
                <div style="margin-top: 0.5rem;">
                  <strong>Description Preview:</strong>
                  <div id="rev-description" style="font-size: 0.8rem; background: white; padding: 0.5rem; border: 1px solid var(--color-ink-faint); margin-top: 4px;"></div>
                </div>
              </div>

              <div class="mobile-step-nav" style="margin-top: 1.5rem; display: flex; justify-content: space-between;">
                <button type="button" id="btn-step-4-back" class="btn btn-secondary" style="min-height: 48px;">
                  ← Edit
                </button>
                <button type="submit" class="btn btn-primary" style="min-height: 48px; background: var(--color-board-green);">
                  Pin to Board & Generate Ticket ➔
                </button>
              </div>
            </div>

            <!-- Desktop Submit Row (Visible only on desktop 1025px+) -->
            <div class="desktop-submit-row" style="display: none; margin-top: 2rem; border-top: 2px dashed var(--color-ink-faint); padding-top: 1.5rem;">
              <button type="submit" class="btn btn-primary" style="min-height: 52px; font-size: 1.05rem; padding: 0 2rem;">
                Pin Complaint to Board & Print Ticket ➔
              </button>
            </div>

          </form>
        </div>

        <!-- Live Receipt Preview Column (Desktop only >= 1025px) -->
        <div class="receipt-preview-slip" style="display: none;">
          <div class="tape-pin" style="width: 80px;"></div>
          <div style="display: flex; justify-content: space-between; align-items: baseline; border-bottom: 2px dashed var(--color-ink); padding-bottom: 0.5rem; margin-bottom: 1rem;">
            <span class="mono" style="font-weight: 800; font-size: 0.85rem; letter-spacing: 0.05em;">LIVE RECEIPT PREVIEW</span>
            <span class="stamp stamp-submitted" style="font-size: 0.65rem; padding: 1px 4px;">DRAFT</span>
          </div>

          <div class="mono" style="font-size: 0.75rem; color: var(--color-ink-muted); margin-bottom: 0.5rem;">
            TRACKING NO: <span style="color: var(--color-signal-red); font-weight: 800;">CMP-2026-AUTO</span>
          </div>

          <h3 id="receipt-preview-subject" style="font-family: var(--font-display); font-size: 1.15rem; margin: 0.5rem 0 1rem 0;">
            [Subject headline will appear here]
          </h3>

          <div style="font-family: var(--font-mono); font-size: 0.78rem; line-height: 1.8; border-top: 1px dashed var(--color-ink-faint); border-bottom: 1px dashed var(--color-ink-faint); padding: 0.75rem 0; margin-bottom: 1rem;">
            <div>CATEGORY: <strong id="receipt-preview-category">General</strong></div>
            <div>PRIORITY: <strong id="receipt-preview-priority">Medium</strong></div>
            <div>COMPLAINANT: <span id="receipt-preview-complainant"><c:out value="${student.fullName}"/></span></div>
            <div>DEPARTMENT: <c:out value="${student.departmentName}"/></div>
          </div>

          <div style="text-align: center; margin-top: 1.25rem;">
            <svg width="180" height="42" viewBox="0 0 180 42" style="max-width: 100%;">
              <rect x="0" y="0" width="3" height="36" fill="#1A1916"/>
              <rect x="6" y="0" width="6" height="36" fill="#1A1916"/>
              <rect x="15" y="0" width="2" height="36" fill="#1A1916"/>
              <rect x="20" y="0" width="4" height="36" fill="#1A1916"/>
              <rect x="28" y="0" width="2" height="36" fill="#1A1916"/>
              <rect x="34" y="0" width="5" height="36" fill="#1A1916"/>
              <rect x="44" y="0" width="2" height="36" fill="#1A1916"/>
              <rect x="50" y="0" width="7" height="36" fill="#1A1916"/>
              <rect x="62" y="0" width="3" height="36" fill="#1A1916"/>
              <rect x="70" y="0" width="2" height="36" fill="#1A1916"/>
              <rect x="76" y="0" width="6" height="36" fill="#1A1916"/>
              <rect x="86" y="0" width="4" height="36" fill="#1A1916"/>
              <rect x="94" y="0" width="2" height="36" fill="#1A1916"/>
              <rect x="100" y="0" width="8" height="36" fill="#1A1916"/>
              <rect x="112" y="0" width="3" height="36" fill="#1A1916"/>
              <rect x="120" y="0" width="5" height="36" fill="#1A1916"/>
              <rect x="130" y="0" width="2" height="36" fill="#1A1916"/>
              <rect x="136" y="0" width="6" height="36" fill="#1A1916"/>
              <rect x="146" y="0" width="3" height="36" fill="#1A1916"/>
              <rect x="154" y="0" width="2" height="36" fill="#1A1916"/>
              <rect x="160" y="0" width="5" height="36" fill="#1A1916"/>
              <rect x="170" y="0" width="4" height="36" fill="#1A1916"/>
            </svg>
            <div class="mono" style="font-size: 0.65rem; color: var(--color-ink-muted); margin-top: 2px;">
              SECURE RAILWAY TICKET PROTOCOL
            </div>
          </div>
        </div>

      </div>

    </main>

    <!-- Mobile Bottom Navigation -->
    <%@ include file="nav_mobile.jspf" %>

  </div>

  <style>
    @media (min-width: 1025px) {
      .receipt-preview-slip {
        display: block !important;
      }
    }
  </style>

  <script src="${pageContext.request.contextPath}/assets/js/student-form.js"></script>

</body>
</html>
