<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Forgot Password • Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
  <style>
    body {
      background-color: var(--color-paper);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 100dvh;
      padding: 1.5rem;
    }
    .forgot-slip {
      background: var(--color-card);
      border: var(--border-thick);
      box-shadow: var(--shadow-hard-lg);
      padding: 2.25rem 2rem;
      max-width: 440px;
      width: 100%;
      position: relative;
    }
  </style>
</head>
<body>

  <div class="forgot-slip">
    <span class="notice-tag">Security Desk</span>
    <h1 style="font-family: var(--font-display); font-size: 1.6rem; margin: 0.25rem 0 1rem 0;">
      Reset Access Code
    </h1>

    <p style="color: var(--color-ink-muted); font-size: 0.9rem; line-height: 1.4; margin-bottom: 1.25rem;">
      Enter your verified roll number and registered college email address. A one-time 6-digit verification code will be generated (valid for 10 minutes).
    </p>

    <c:if test="${not empty error}">
      <div class="alert alert-danger" style="margin-bottom: 1.25rem;">
        <c:out value="${error}"/>
      </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/student/forgot-password" method="POST">
      <input type="hidden" name="csrfToken" value="${csrfToken}">

      <div class="form-group" style="margin-bottom: 1rem;">
        <label for="rollNo" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
          Roll Number
        </label>
        <input type="text"
               id="rollNo"
               name="rollNo"
               class="input-mobile"
               value="<c:out value='${rollNo}'/>"
               required
               placeholder="e.g. 22CS101"
               style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono); text-transform: uppercase;" />
      </div>

      <div class="form-group" style="margin-bottom: 1.5rem;">
        <label for="email" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
          Registered College Email
        </label>
        <input type="email"
               id="email"
               name="email"
               class="input-mobile"
               value="<c:out value='${email}'/>"
               required
               placeholder="e.g. student@campus.edu"
               style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
      </div>

      <button type="submit" class="btn btn-primary" style="width: 100%; min-height: 48px;">
        Dispatch 6-Digit Code ➔
      </button>
    </form>

    <div style="margin-top: 1.5rem; text-align: center;">
      <a href="${pageContext.request.contextPath}/student/login" style="font-size: 0.85rem; font-family: var(--font-mono); color: var(--color-ink-muted);">
        ← Return to Sign In
      </a>
    </div>
  </div>

</body>
</html>
