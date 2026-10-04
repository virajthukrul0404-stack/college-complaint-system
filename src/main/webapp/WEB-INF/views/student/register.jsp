<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="#1F5D4A">
  <link rel="manifest" href="${pageContext.request.contextPath}/manifest.json">
  <title>Student Registration • Campus Notice Board</title>
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
      padding: 2rem 1rem;
    }
    .register-slip {
      background: var(--color-card);
      border: var(--border-thick);
      box-shadow: var(--shadow-hard-lg);
      padding: 2.25rem 2rem;
      max-width: 540px;
      width: 100%;
      position: relative;
    }
    .tape-top-reg {
      position: absolute;
      top: -12px;
      left: 50%;
      transform: translateX(-50%) rotate(1deg);
      width: 110px;
      height: 24px;
      background: rgba(244, 196, 48, 0.7);
      border: 1px dashed rgba(26, 25, 22, 0.4);
    }
    .strength-bar-bg {
      height: 6px;
      background: #e0d8cb;
      border: 1px solid var(--color-ink-faint);
      margin-top: 6px;
      border-radius: 3px;
      overflow: hidden;
    }
    .strength-bar-fill {
      height: 100%;
      width: 0%;
      transition: width 0.2s ease, background-color 0.2s ease;
    }
  </style>
</head>
<body>

  <div class="register-slip">
    <div class="tape-top-reg"></div>

    <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.25rem;">
      <div>
        <span class="notice-tag">Student Enrollment</span>
        <h1 style="font-family: var(--font-display); font-size: 1.75rem; margin-top: 0.25rem;">
          Register Account
        </h1>
      </div>
      <div class="stamp stamp-submitted" style="font-size: 0.75rem; padding: 2px 6px;">
        NEW ADMISSION
      </div>
    </div>

    <c:if test="${not empty errors}">
      <div class="alert alert-danger" style="margin-bottom: 1.25rem;">
        <strong>Please correct the following:</strong>
        <ul style="margin: 0.5rem 0 0 1.25rem; padding: 0;">
          <c:forEach var="err" items="${errors}">
            <li><c:out value="${err.value}"/></li>
          </c:forEach>
        </ul>
      </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/student/register" method="POST">
      <input type="hidden" name="csrfToken" value="${csrfToken}">

      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
        <div class="form-group" style="grid-column: span 2;">
          <label for="fullName" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
            Full Legal Name *
          </label>
          <input type="text"
                 id="fullName"
                 name="fullName"
                 class="input-mobile"
                 value="<c:out value='${fullName}'/>"
                 required
                 autocomplete="name"
                 placeholder="e.g. Aarav Sharma"
                 style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body);" />
        </div>

        <div class="form-group">
          <label for="rollNo" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
            Roll Number *
          </label>
          <input type="text"
                 id="rollNo"
                 name="rollNo"
                 class="input-mobile"
                 value="<c:out value='${rollNo}'/>"
                 required
                 autocomplete="off"
                 placeholder="e.g. 23CS042"
                 style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono); text-transform: uppercase;" />
        </div>

        <div class="form-group">
          <label for="mobile" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
            Mobile (Optional)
          </label>
          <input type="tel"
                 id="mobile"
                 name="mobile"
                 class="input-mobile"
                 value="<c:out value='${mobile}'/>"
                 inputmode="tel"
                 autocomplete="tel"
                 placeholder="e.g. 9876543210"
                 style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
        </div>
      </div>

      <div class="form-group" style="margin-bottom: 1rem;">
        <label for="email" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
          College Email Address *
        </label>
        <input type="email"
               id="email"
               name="email"
               class="input-mobile"
               value="<c:out value='${email}'/>"
               required
               inputmode="email"
               autocomplete="email"
               placeholder="e.g. rollno@campus.edu"
               style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
      </div>

      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
        <div class="form-group">
          <label for="departmentId" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
            Department *
          </label>
          <select id="departmentId" name="departmentId" class="input-mobile" required style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body);">
            <option value="">-- Choose Dept --</option>
            <c:forEach var="d" items="${departments}">
              <option value="${d.id}" ${departmentId == d.id ? 'selected' : ''}>
                <c:out value="${d.name}"/> (<c:out value="${d.code}"/>)
              </option>
            </c:forEach>
          </select>
        </div>

        <div class="form-group">
          <label for="yearOfStudy" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
            Year of Study *
          </label>
          <select id="yearOfStudy" name="yearOfStudy" class="input-mobile" required style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body);">
            <option value="">-- Year --</option>
            <option value="1" ${yearOfStudy == 1 ? 'selected' : ''}>1st Year (Freshman)</option>
            <option value="2" ${yearOfStudy == 2 ? 'selected' : ''}>2nd Year (Sophomore)</option>
            <option value="3" ${yearOfStudy == 3 ? 'selected' : ''}>3rd Year (Junior)</option>
            <option value="4" ${yearOfStudy == 4 ? 'selected' : ''}>4th Year (Senior)</option>
            <option value="5" ${yearOfStudy == 5 ? 'selected' : ''}>5th Year (Dual/PG)</option>
          </select>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem;">
        <div class="form-group">
          <label for="reg-password" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
            Password *
          </label>
          <input type="password"
                 id="reg-password"
                 name="password"
                 class="input-mobile"
                 required
                 autocomplete="new-password"
                 placeholder="Min 8 chars, letter+num"
                 style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
          <div class="strength-bar-bg">
            <div id="strength-fill" class="strength-bar-fill"></div>
          </div>
          <span id="strength-text" style="font-family: var(--font-mono); font-size: 0.72rem; color: var(--color-ink-muted); display: block; margin-top: 3px;">
            At least 8 chars with letter & number
          </span>
        </div>

        <div class="form-group">
          <label for="confirmPassword" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
            Confirm Password *
          </label>
          <input type="password"
                 id="confirmPassword"
                 name="confirmPassword"
                 class="input-mobile"
                 required
                 autocomplete="new-password"
                 placeholder="Retype password"
                 style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono);" />
        </div>
      </div>

      <button type="submit" class="btn btn-primary" style="width: 100%; min-height: 48px; font-size: 1rem;">
        Complete Registration ➔
      </button>
    </form>

    <div style="margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px dashed var(--color-ink-faint); text-align: center; font-size: 0.88rem;">
      Already registered?
      <a href="${pageContext.request.contextPath}/student/login" style="font-weight: 700; color: var(--color-board-green); text-decoration: underline;">
        Sign in here
      </a>
    </div>
  </div>

  <script>
    const pwdInput = document.getElementById('reg-password');
    const fill = document.getElementById('strength-fill');
    const label = document.getElementById('strength-text');

    if (pwdInput && fill && label) {
      pwdInput.addEventListener('input', function () {
        const val = pwdInput.value;
        let score = 0;
        if (val.length >= 8) score++;
        if (/[A-Za-z]/.test(val)) score++;
        if (/\d/.test(val)) score++;
        if (/[^A-Za-z0-9]/.test(val)) score++;

        if (val.length === 0) {
          fill.style.width = '0%';
          label.textContent = 'At least 8 chars with letter & number';
        } else if (score <= 1) {
          fill.style.width = '25%';
          fill.style.backgroundColor = 'var(--color-signal-red)';
          label.textContent = 'Weak (add numbers & length)';
        } else if (score === 2) {
          fill.style.width = '50%';
          fill.style.backgroundColor = 'var(--color-highway-yellow)';
          label.textContent = 'Fair';
        } else if (score === 3) {
          fill.style.width = '75%';
          fill.style.backgroundColor = 'var(--color-pen-blue)';
          label.textContent = 'Good (valid)';
        } else {
          fill.style.width = '100%';
          fill.style.backgroundColor = 'var(--color-board-green)';
          label.textContent = 'Strong password';
        }
      });
    }
  </script>
</body>
</html>
