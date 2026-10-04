<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Enter Reset Code • Campus Notice Board</title>
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
    .reset-slip {
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

  <div class="reset-slip">
    <span class="notice-tag">Verification Desk</span>
    <h1 style="font-family: var(--font-display); font-size: 1.6rem; margin: 0.25rem 0 1rem 0;">
      Verify Code & New Password
    </h1>

    <c:if test="${param.sent eq 'true'}">
      <div class="alert alert-info" style="margin-bottom: 1.25rem;">
        ℹ A 6-digit verification code has been dispatched. (For this course evaluation demo, check the server console output!).
      </div>
    </c:if>

    <c:if test="${not empty error}">
      <div class="alert alert-danger" style="margin-bottom: 1.25rem;">
        <c:out value="${error}"/>
      </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/student/reset-password" method="POST">
      <input type="hidden" name="csrfToken" value="${csrfToken}">

      <div class="form-group" style="margin-bottom: 1rem;">
        <label for="email" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
          College Email Address
        </label>
        <input type="email"
               id="email"
               name="email"
               class="input-mobile"
               value="<c:out value='${email}'/>"
               required
               style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
      </div>

      <div class="form-group" style="margin-bottom: 1rem;">
        <label for="code" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
          One-Time 6-Digit Code
        </label>
        <input type="text"
               id="code"
               name="code"
               class="input-mobile"
               required
               maxlength="6"
               inputmode="numeric"
               autocomplete="one-time-code"
               placeholder="123456"
               style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono); font-size: 1.2rem; letter-spacing: 0.3em; text-align: center;" />
      </div>

      <div class="form-group" style="margin-bottom: 1rem;">
        <label for="newPassword" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
          New Password (min 8 chars, letter+number)
        </label>
        <input type="password"
               id="newPassword"
               name="newPassword"
               class="input-mobile"
               required
               autocomplete="new-password"
               style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
      </div>

      <div class="form-group" style="margin-bottom: 1.5rem;">
        <label for="confirmPassword" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
          Confirm New Password
        </label>
        <input type="password"
               id="confirmPassword"
               name="confirmPassword"
               class="input-mobile"
               required
               autocomplete="new-password"
               style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
      </div>

      <button type="submit" class="btn btn-primary" style="width: 100%; min-height: 48px;">
        Update Password & Revoke Old Sessions ➔
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
