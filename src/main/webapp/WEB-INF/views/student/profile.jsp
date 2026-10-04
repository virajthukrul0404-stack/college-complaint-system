<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="#1F5D4A">
  <title>Student Profile • Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
</head>
<body>

  <div class="student-shell">

    <!-- Desktop Navigation -->
    <%@ include file="nav_desktop.jspf" %>

    <!-- Mobile Header -->
    <header class="site-header" style="display: block; border-bottom: var(--border-thick);">
      <div class="container" style="display: flex; justify-content: space-between; align-items: center; padding: 0.6rem 1rem;">
        <span style="font-family: var(--font-display); font-weight: 800; font-size: 1.1rem;">My Student Profile</span>
        <a href="${pageContext.request.contextPath}/student/logout" class="btn btn-secondary btn-sm">Sign Out</a>
      </div>
    </header>

    <main class="slip-container" style="max-width: 780px;">

      <c:if test="${not empty profileSuccess}">
        <div class="alert alert-success" style="margin-bottom: 1.25rem;">
          <c:out value="${profileSuccess}"/>
        </div>
      </c:if>

      <c:if test="${not empty passwordSuccess}">
        <div class="alert alert-success" style="margin-bottom: 1.25rem;">
          <c:out value="${passwordSuccess}"/>
        </div>
      </c:if>

      <!-- Profile Details Slip -->
      <div class="pinned-slip" style="padding: 1.75rem; margin-bottom: 1.5rem;">
        <div class="tape-pin"></div>
        <span class="notice-tag">Student Identity</span>
        <h2 style="font-family: var(--font-display); font-size: 1.4rem; margin: 0.25rem 0 1rem 0;">
          Profile Information
        </h2>

        <c:if test="${not empty profileErrors}">
          <div class="alert alert-danger" style="margin-bottom: 1rem;">
            <ul style="margin: 0; padding-left: 1.25rem;">
              <c:forEach var="err" items="${profileErrors}">
                <li><c:out value="${err.value}"/></li>
              </c:forEach>
            </ul>
          </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/student/profile" method="POST">
          <input type="hidden" name="csrfToken" value="${csrfToken}">
          <input type="hidden" name="action" value="updateProfile">

          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
            <div class="form-group">
              <label style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
                Roll Number (Fixed)
              </label>
              <input type="text"
                     value="<c:out value='${student.rollNo}'/>"
                     disabled
                     style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: #e8e2d5; font-family: var(--font-mono); color: var(--color-ink-muted);" />
            </div>

            <div class="form-group">
              <label style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
                Email Address (Fixed)
              </label>
              <input type="email"
                     value="<c:out value='${student.email}'/>"
                     disabled
                     style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: #e8e2d5; font-family: var(--font-mono); color: var(--color-ink-muted);" />
            </div>
          </div>

          <div class="form-group" style="margin-bottom: 1rem;">
            <label for="fullName" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
              Full Legal Name *
            </label>
            <input type="text"
                   id="fullName"
                   name="fullName"
                   class="input-mobile"
                   value="<c:out value='${student.fullName}'/>"
                   required
                   style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body);" />
          </div>

          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem;">
            <div class="form-group">
              <label for="mobile" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
                Mobile Number
              </label>
              <input type="tel"
                     id="mobile"
                     name="mobile"
                     class="input-mobile"
                     value="<c:out value='${student.mobile}'/>"
                     placeholder="e.g. 9876543210"
                     style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
            </div>

            <div class="form-group">
              <label for="departmentId" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
                Department *
              </label>
              <select id="departmentId" name="departmentId" class="input-mobile" required style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body);">
                <c:forEach var="d" items="${departments}">
                  <option value="${d.id}" ${student.departmentId == d.id ? 'selected' : ''}>
                    <c:out value="${d.name}"/>
                  </option>
                </c:forEach>
              </select>
            </div>
          </div>

          <button type="submit" class="btn btn-primary" style="min-height: 48px;">
            Save Profile Changes
          </button>
        </form>
      </div>

      <!-- Change Password Slip -->
      <div class="pinned-slip" style="padding: 1.75rem;">
        <span class="notice-tag">Security & Credentials</span>
        <h2 style="font-family: var(--font-display); font-size: 1.4rem; margin: 0.25rem 0 1rem 0;">
          Change Password
        </h2>

        <c:if test="${not empty passwordErrors}">
          <div class="alert alert-danger" style="margin-bottom: 1rem;">
            <ul style="margin: 0; padding-left: 1.25rem;">
              <c:forEach var="err" items="${passwordErrors}">
                <li><c:out value="${err.value}"/></li>
              </c:forEach>
            </ul>
          </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/student/profile" method="POST">
          <input type="hidden" name="csrfToken" value="${csrfToken}">
          <input type="hidden" name="action" value="changePassword">

          <div class="form-group" style="margin-bottom: 1rem;">
            <label for="currentPassword" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
              Current Password *
            </label>
            <input type="password"
                   id="currentPassword"
                   name="currentPassword"
                   class="input-mobile"
                   required
                   autocomplete="current-password"
                   style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
          </div>

          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem;">
            <div class="form-group">
              <label for="newPassword" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
                New Password (min 8 chars) *
              </label>
              <input type="password"
                     id="newPassword"
                     name="newPassword"
                     class="input-mobile"
                     required
                     autocomplete="new-password"
                     style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
            </div>

            <div class="form-group">
              <label for="confirmPassword" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
                Confirm New Password *
              </label>
              <input type="password"
                     id="confirmPassword"
                     name="confirmPassword"
                     class="input-mobile"
                     required
                     autocomplete="new-password"
                     style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
            </div>
          </div>

          <button type="submit" class="btn btn-secondary" style="min-height: 48px;">
            Update Password & Revoke Other Sessions
          </button>
        </form>
      </div>

    </main>

    <!-- Mobile Bottom Navigation -->
    <%@ include file="nav_mobile.jspf" %>

  </div>

</body>
</html>
