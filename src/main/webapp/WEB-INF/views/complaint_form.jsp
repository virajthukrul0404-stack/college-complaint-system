<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>File a Complaint - Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/board.css">
</head>
<body>

  <!-- Site Header -->
  <header class="site-header">
    <div class="container header-inner">
      <a href="${pageContext.request.contextPath}/home" class="brand-stamp">
        <div class="brand-seal">NB</div>
        <div class="brand-title">
          <span class="title">Campus Notice Board</span>
          <span class="subtitle">Official Grievance Registry</span>
        </div>
      </a>
      <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/home" class="nav-link">The Board</a></li>
        <li><a href="${pageContext.request.contextPath}/complaint/new" class="nav-link active">File Complaint</a></li>
        <li><a href="${pageContext.request.contextPath}/complaint/track" class="nav-link">Track Slip</a></li>
        <li><a href="${pageContext.request.contextPath}/feedback" class="nav-link">Feedback</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-admin-btn">Office Desk ➔</a></li>
      </ul>
    </div>
  </header>

  <main class="container" style="padding: 3rem 1.5rem; max-width: 800px;">
    
    <div style="margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: flex-end;">
      <div>
        <span class="handwritten" style="font-size: 1.5rem;">Form Slip No. 42-B</span>
        <h1 style="font-size: 2.4rem;">Grievance Lodgement Slip</h1>
        <p style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-ink-muted);">
          Fill out the details below. Once submitted, a perforated tracking ticket will be generated for you.
        </p>
      </div>
      <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary btn-sm">Cancel</a>
    </div>

    <!-- General Error Banner -->
    <c:if test="${not empty errors.general}">
      <div class="alert alert-danger">
        <strong>Error:</strong> <c:out value="${errors.general}"/>
      </div>
    </c:if>

    <!-- Ruled Paper Complaint Slip -->
    <div class="paper-card complaint-slip ruled-lines exercise-margin" style="background-color: var(--color-paper-light); padding: 2.5rem; position: relative;">
      <div class="tape-top"></div>

      <form id="complaintForm" action="${pageContext.request.contextPath}/complaint/new" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="csrfToken" value="${csrfToken}">

        <!-- Row 1: Student Name & Roll Number -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
          <div class="form-group">
            <label for="studentName" class="form-label">1. Student Full Name</label>
            <input type="text" id="studentName" name="studentName" class="input-ruled" 
                   value="<c:out value='${complaint.studentName}'/>" placeholder="e.g. Aarav Sharma" required>
            <c:if test="${not empty errors.studentName}">
              <span class="inline-error"><c:out value="${errors.studentName}"/></span>
            </c:if>
          </div>

          <div class="form-group">
            <label for="rollNumber" class="form-label">2. College Roll No.</label>
            <input type="text" id="rollNumber" name="rollNumber" class="input-ruled mono" 
                   value="<c:out value='${complaint.rollNumber}'/>" placeholder="e.g. 22CS104" required>
            <c:if test="${not empty errors.rollNumber}">
              <span class="inline-error"><c:out value="${errors.rollNumber}"/></span>
            </c:if>
          </div>
        </div>

        <!-- Row 2: Contact Email -->
        <div class="form-group">
          <label for="email" class="form-label">3. Contact Email (Required for ticket status updates)</label>
          <input type="email" id="email" name="email" class="input-ruled mono" 
                 value="<c:out value='${complaint.email}'/>" placeholder="e.g. student@campus.edu" required>
          <div class="form-hint">Used to verify your identity when tracking your complaint slip.</div>
          <c:if test="${not empty errors.email}">
            <span class="inline-error"><c:out value="${errors.email}"/></span>
          </c:if>
        </div>

        <!-- Row 3: Department & Category -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
          <div class="form-group">
            <label for="departmentId" class="form-label">4. Department Concerned</label>
            <select id="departmentId" name="departmentId" class="input-ruled" required>
              <option value="">-- Select Department --</option>
              <c:forEach items="${departments}" var="dept">
                <option value="${dept.id}" ${complaint.departmentId == dept.id ? 'selected' : ''}>
                  <c:out value="${dept.name}"/> (<c:out value="${dept.code}"/>)
                </option>
              </c:forEach>
            </select>
            <c:if test="${not empty errors.departmentId}">
              <span class="inline-error"><c:out value="${errors.departmentId}"/></span>
            </c:if>
          </div>

          <div class="form-group">
            <label for="category" class="form-label">5. Issue Category</label>
            <select id="category" name="category" class="input-ruled" required>
              <option value="">-- Select Category --</option>
              <c:forEach items="${categories}" var="cat">
                <option value="${cat}" ${complaint.category eq cat ? 'selected' : ''}><c:out value="${cat}"/></option>
              </c:forEach>
            </select>
            <c:if test="${not empty errors.category}">
              <span class="inline-error"><c:out value="${errors.category}"/></span>
            </c:if>
          </div>
        </div>

        <!-- Row 4: Priority Selector (Sticky Note Chips) -->
        <div class="form-group">
          <label class="form-label">6. Priority Level</label>
          <div class="priority-chips">
            <div class="priority-chip chip-low">
              <input type="radio" id="prioLow" name="priority" value="Low" ${complaint.priority eq 'Low' ? 'checked' : ''}>
              <label for="prioLow">🟢 Low Priority</label>
            </div>
            <div class="priority-chip chip-medium">
              <input type="radio" id="prioMed" name="priority" value="Medium" ${empty complaint.priority || complaint.priority eq 'Medium' ? 'checked' : ''}>
              <label for="prioMed">🟡 Medium</label>
            </div>
            <div class="priority-chip chip-high">
              <input type="radio" id="prioHigh" name="priority" value="High" ${complaint.priority eq 'High' ? 'checked' : ''}>
              <label for="prioHigh">🔴 High (Urgent)</label>
            </div>
          </div>
          <c:if test="${not empty errors.priority}">
            <span class="inline-error"><c:out value="${errors.priority}"/></span>
          </c:if>
        </div>

        <!-- Row 5: Subject -->
        <div class="form-group">
          <label for="subject" class="form-label">7. Brief Subject Line</label>
          <input type="text" id="subject" name="subject" class="input-ruled" 
                 value="<c:out value='${complaint.subject}'/>" placeholder="e.g. WiFi router down in Lab 3" required>
          <c:if test="${not empty errors.subject}">
            <span class="inline-error"><c:out value="${errors.subject}"/></span>
          </c:if>
        </div>

        <!-- Row 6: Description -->
        <div class="form-group">
          <label for="description" class="form-label">8. Detailed Statement of Grievance</label>
          <textarea id="description" name="description" rows="5" class="input-ruled" 
                    placeholder="Provide specific location, equipment numbers, or timing..." required><c:out value="${complaint.description}"/></textarea>
          <c:if test="${not empty errors.description}">
            <span class="inline-error"><c:out value="${errors.description}"/></span>
          </c:if>
        </div>

        <!-- Row 7: Anonymity Toggle -->
        <div class="form-group" style="background: rgba(244, 196, 48, 0.15); padding: 1rem; border: 1px dashed var(--color-ink); border-radius: var(--radius-sm);">
          <label class="toggle-wrapper">
            <input type="checkbox" id="anonToggle" name="isAnonymous" class="toggle-checkbox" ${complaint.anonymous ? 'checked' : ''}>
            <span style="font-family: var(--font-display); font-weight: 700; font-size: 1.05rem;">Submit this grievance anonymously</span>
          </label>
          <p id="anonNote" style="font-family: var(--font-mono); font-size: 0.78rem; color: var(--color-ink-muted); margin-top: 0.5rem; margin-left: 2rem;">
            Your name and roll number will be masked as 'Anonymous Student' from administrator tables. Only your private email is stored to notify you of status changes.
          </p>
        </div>

        <!-- Row 8: Optional File Attachment -->
        <div class="form-group">
          <label for="attachmentInput" class="form-label">9. Evidence Photo / Document (Optional)</label>
          <input type="file" id="attachmentInput" name="attachment" accept=".jpg,.jpeg,.png,.webp" class="input-ruled" style="border: var(--border-thick); padding: 0.6rem; background: #FFF;">
          <div class="form-hint">Accepted formats: JPG, PNG, WEBP (Max size: 2 MB)</div>
          <span id="attachmentError" class="inline-error">
            <c:if test="${not empty errors.attachment}"><c:out value="${errors.attachment}"/></c:if>
          </span>
        </div>

        <!-- Perforated Tear-off Footer Section -->
        <div style="border-top: 2px dashed var(--color-ink); margin-top: 2rem; padding-top: 1.75rem; display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; gap: 1rem;">
          <div>
            <span class="handwritten" style="font-size: 1.4rem;">Tear along perforation below ➔</span>
          </div>
          <button type="submit" class="btn btn-primary btn-lg">
            <span>Stamp & Pin to Board</span>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
              <path d="M14 4v5c0 1.12.37 2.16 1 3H9c.65-.86 1-1.9 1-3V4h4m3-2H7v2h1v5c0 1.66-1.34 3-3 3v2h7v7l1 1 1-1v-7h7v-2c-1.66 0-3-1.34-3-3V4h1V2z"/>
            </svg>
          </button>
        </div>

      </form>
    </div>

  </main>

  <footer class="site-footer">
    <div class="container footer-inner">
      <div>Campus Complaint & Feedback Registry • Advanced Java</div>
      <div class="footer-stamp">NOTICE BOARD REGISTER</div>
    </div>
  </footer>

  <script type="module">
    import { setupComplaintForm } from "${pageContext.request.contextPath}/assets/js/form.js";
    setupComplaintForm();
  </script>
</body>
</html>
