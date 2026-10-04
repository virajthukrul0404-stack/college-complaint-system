<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Staff Desk Requires Laptop/Desktop • Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
  <style>
    body {
      margin: 0;
      padding: 0;
      background-color: #2D1810; /* Dark administrative mahogany desk */
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100dvh;
    }
  </style>
</head>
<body>

  <div class="admin-mobile-notice-shell">
    <div class="admin-mobile-notice-card">
      <div class="tape-pin" style="width: 100px;"></div>

      <div class="admin-mobile-stamp">
        STAFF DESK IS FOR LAPTOPS
      </div>

      <h1 style="font-family: var(--font-display); font-size: 1.5rem; margin-bottom: 0.75rem; color: var(--color-ink);">
        Administrative Ledger Restricted
      </h1>

      <p style="color: var(--color-ink-muted); font-size: 0.95rem; line-height: 1.5; margin-bottom: 1.5rem;">
        The campus grievance management ledger, multi-column triage desk, and audit reports are designed exclusively for laptop and desktop screens (minimum width 1024px).
      </p>

      <div style="background: var(--color-paper-light); border: 1px dashed var(--color-ink); padding: 0.75rem; margin-bottom: 1.5rem; font-family: var(--font-mono); font-size: 0.8rem;">
        Please open this administrative link on a laptop or desktop workstation.
      </div>

      <div style="display: flex; flex-direction: column; gap: 0.75rem;">
        <a href="${pageContext.request.contextPath}/home" class="btn btn-primary" style="min-height: 48px; text-decoration: none;">
          Go to Student Portal ➔
        </a>

        <c:if test="${not empty sessionScope.admin or not empty sessionScope.adminUser}">
          <a href="${pageContext.request.contextPath}/admin/logout" class="btn btn-secondary" style="min-height: 48px; text-decoration: none; color: var(--color-signal-red);">
            Sign Out of Staff Account
          </a>
        </c:if>

        <a href="${pageContext.request.contextPath}/admin/login?view=desktop" class="mono" style="font-size: 0.75rem; color: var(--color-ink-faint); margin-top: 0.5rem;">
          [Force Desktop View for Testing]
        </a>
      </div>
    </div>
  </div>

</body>
</html>
