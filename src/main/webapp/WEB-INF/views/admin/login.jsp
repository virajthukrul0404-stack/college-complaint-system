<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>Administration Login - Office Desk</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body style="background-color: #E2DAC8; display: flex; align-items: center; justify-content: center; min-height: 100vh;">

  <%@ include file="device_guard.jspf" %>

  <div class="admin-desktop-only-content" style="width: 100%; max-width: 440px; padding: 1.5rem;">

    <div style="text-align: center; margin-bottom: 1.5rem;">
      <div class="brand-seal" style="margin: 0 auto 0.75rem; width: 50px; height: 50px; font-size: 1.25rem;">AD</div>
      <h1 style="font-size: 2rem;">Administration Desk</h1>
      <p style="font-family: var(--font-mono); font-size: 0.8rem; color: var(--color-ink-muted);">
        Official Staff Access & Grievance Ledger
      </p>
    </div>

    <!-- Logged out notice -->
    <c:if test="${param.loggedOut eq 'true'}">
      <div class="alert alert-success" style="font-size: 0.8rem;">
        You have been logged out securely.
      </div>
    </c:if>

    <!-- Error Banner -->
    <c:if test="${not empty error}">
      <div class="alert alert-danger" style="font-size: 0.85rem;">
        <strong>Access Denied:</strong> <c:out value="${error}"/>
      </div>
    </c:if>

    <!-- Manila Folder Login Docket -->
    <div class="paper-card" style="background-color: #FBF7EE; padding: 2.25rem; border-width: 2px; position: relative;">
      <div class="tape-top"></div>

      <form action="${pageContext.request.contextPath}/admin/login" method="POST">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <c:if test="${not empty redirect}">
          <input type="hidden" name="redirect" value="<c:out value='${redirect}'/>">
        </c:if>

        <div class="form-group">
          <label for="username" class="form-label" style="font-size: 1.15rem;">Staff Username</label>
          <input type="text" id="username" name="username" class="input-ruled mono" 
                 value="<c:out value='${username}'/>" placeholder="e.g. superadmin" required autofocus>
        </div>

        <div class="form-group" style="margin-bottom: 2rem;">
          <label for="password" class="form-label" style="font-size: 1.15rem;">Password</label>
          <input type="password" id="password" name="password" class="input-ruled mono" 
                 placeholder="••••••••••••" required>
        </div>

        <button type="submit" class="btn btn-primary btn-block">
          Authenticate & Open Desk ➔
        </button>

      </form>

      <div style="margin-top: 1.75rem; padding-top: 1.25rem; border-top: 1px dashed var(--color-ink-faint); font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); text-align: center;">
        <div>DEFAULT SYSTEM LOGIN:</div>
        <div style="font-weight: 700; color: var(--color-ink); margin-top: 0.2rem;">
          superadmin / Admin@12345
        </div>
      </div>
    </div>

    <div style="text-align: center; margin-top: 1.5rem;">
      <a href="${pageContext.request.contextPath}/home" style="font-family: var(--font-mono); font-size: 0.8rem; color: var(--color-ink-muted);">
        ← Return to Public Notice Board
      </a>
    </div>

  </div>

</body>
</html>
