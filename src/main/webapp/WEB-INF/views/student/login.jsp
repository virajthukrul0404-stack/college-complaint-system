<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="#1F5D4A">
  <link rel="manifest" href="${pageContext.request.contextPath}/manifest.json">
  <title>Student Sign In • Campus Notice Board</title>
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
    .login-slip {
      background: var(--color-card);
      border: var(--border-thick);
      box-shadow: var(--shadow-hard-lg);
      padding: 2.25rem 2rem;
      max-width: 440px;
      width: 100%;
      position: relative;
    }
    .tape-corner {
      position: absolute;
      top: -12px;
      left: 24px;
      width: 90px;
      height: 24px;
      background: rgba(244, 196, 48, 0.7);
      border: 1px dashed rgba(26, 25, 22, 0.4);
      transform: rotate(-3deg);
    }
    .pwd-wrapper {
      position: relative;
      display: flex;
      align-items: center;
    }
    .pwd-toggle-btn {
      position: absolute;
      right: 8px;
      background: transparent;
      border: none;
      font-family: var(--font-mono);
      font-size: 0.8rem;
      font-weight: 700;
      color: var(--color-ink-muted);
      cursor: pointer;
      min-width: 48px;
      min-height: 48px; /* Touch target >= 48px */
      display: flex;
      align-items: center;
      justify-content: center;
    }
  </style>
</head>
<body>

  <div class="login-slip">
    <div class="tape-corner"></div>

    <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.25rem;">
      <div>
        <span class="notice-tag">Student Desk</span>
        <h1 style="font-family: var(--font-display); font-size: 1.75rem; margin-top: 0.25rem; line-height: 1.15;">
          Sign in to the board
        </h1>
      </div>
      <div class="stamp stamp-submitted" style="transform: rotate(4deg); font-size: 0.75rem; padding: 2px 6px;">
        STUDENT
      </div>
    </div>

    <p style="font-family: var(--font-hand); font-size: 1.2rem; color: var(--color-pen-blue); margin-bottom: 1.5rem; line-height: 1.3;">
      "Check your complaint slips, track live repairs, and share campus feedback."
    </p>

    <c:if test="${param.registered eq 'true'}">
      <div class="alert alert-success" style="margin-bottom: 1.25rem;">
        ✓ Your student account has been created. Please sign in below.
      </div>
    </c:if>

    <c:if test="${param.resetSuccess eq 'true'}">
      <div class="alert alert-success" style="margin-bottom: 1.25rem;">
        ✓ Password updated successfully. Please log in with your new password.
      </div>
    </c:if>

    <c:if test="${param.loggedOut eq 'true'}">
      <div class="alert alert-info" style="margin-bottom: 1.25rem;">
        You have safely signed out of your session.
      </div>
    </c:if>

    <c:if test="${param.notice eq 'login_required'}">
      <div class="alert alert-info" style="margin-bottom: 1.25rem;">
        Please sign in with your student account to lodge a new complaint.
      </div>
    </c:if>

    <c:if test="${not empty error}">
      <div class="alert alert-danger" style="margin-bottom: 1.25rem;">
        <c:out value="${error}"/>
      </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/student/login" method="POST">
      <input type="hidden" name="csrfToken" value="${csrfToken}">
      <input type="hidden" name="redirect" value="<c:out value='${redirect}'/>">

      <div class="form-group" style="margin-bottom: 1rem;">
        <label for="identifier" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
          Roll Number or College Email
        </label>
        <input type="text"
               id="identifier"
               name="identifier"
               class="input-mobile"
               value="<c:out value='${identifier}'/>"
               required
               autocomplete="username"
               autocapitalize="characters"
               placeholder="e.g. 22CS101 or name@campus.edu"
               style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
      </div>

      <div class="form-group" style="margin-bottom: 1rem;">
        <div style="display: flex; justify-content: space-between; align-items: baseline;">
          <label for="password" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.82rem; text-transform: uppercase;">
            Password
          </label>
          <a href="${pageContext.request.contextPath}/student/forgot-password" style="font-size: 0.78rem; font-family: var(--font-mono); color: var(--color-ink-muted);">
            Forgot code?
          </a>
        </div>
        <div class="pwd-wrapper">
          <input type="password"
                 id="password"
                 name="password"
                 class="input-mobile"
                 required
                 autocomplete="current-password"
                 placeholder="••••••••"
                 style="width: 100%; padding: 0.75rem 3.5rem 0.75rem 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
          <button type="button" class="pwd-toggle-btn" id="btn-toggle-pwd" aria-label="Toggle password view">SHOW</button>
        </div>
      </div>

      <div style="display: flex; align-items: center; gap: 0.6rem; margin-bottom: 1.5rem;">
        <input type="checkbox" id="rememberMe" name="rememberMe" value="true" style="width: 20px; height: 20px; accent-color: var(--color-board-green); cursor: pointer;" />
        <label for="rememberMe" style="font-size: 0.88rem; font-weight: 500; cursor: pointer; user-select: none;">
          Stay signed in on this device
        </label>
      </div>

      <button type="submit" class="btn btn-primary" style="width: 100%; min-height: 48px; font-size: 1rem;">
        Sign In to Portal ➔
      </button>
    </form>

    <div style="margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px dashed var(--color-ink-faint); text-align: center; font-size: 0.88rem;">
      New student here?
      <a href="${pageContext.request.contextPath}/student/register" style="font-weight: 700; color: var(--color-board-green); text-decoration: underline;">
        Register your account
      </a>
    </div>

    <div style="margin-top: 0.75rem; text-align: center;">
      <a href="${pageContext.request.contextPath}/home" style="font-size: 0.8rem; font-family: var(--font-mono); color: var(--color-ink-muted);">
        ← Back to Notice Board
      </a>
    </div>
  </div>

  <script>
    const pwdInput = document.getElementById('password');
    const toggleBtn = document.getElementById('btn-toggle-pwd');
    if (toggleBtn && pwdInput) {
      toggleBtn.addEventListener('click', function () {
        if (pwdInput.type === 'password') {
          pwdInput.type = 'text';
          toggleBtn.textContent = 'HIDE';
        } else {
          pwdInput.type = 'password';
          toggleBtn.textContent = 'SHOW';
        }
      });
    }
  </script>
</body>
</html>
