<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>Admin Management - Office Desk</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body>

  <%@ include file="device_guard.jspf" %>

  <!-- Top Header -->
  <header class="site-header">
    <div class="container header-inner" style="max-width: 100%; padding: 0.75rem 2rem;">
      <div style="display: flex; align-items: center; gap: 1rem;">
        <div class="brand-seal" style="background: var(--color-ink);">AD</div>
        <div>
          <span style="font-family: var(--font-display); font-size: 1.15rem; font-weight: 800;">Campus Administration Desk</span>
          <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-left: 0.5rem;">[SECURITY & USERS]</span>
        </div>
      </div>
      <div style="display: flex; align-items: center; gap: 1.25rem; font-family: var(--font-mono); font-size: 0.85rem;">
        <span>Staff: <strong><c:out value="${sessionScope.admin.fullName}"/></strong></span>
        <a href="${pageContext.request.contextPath}/admin/logout" class="btn btn-danger btn-sm">Logout</a>
      </div>
    </div>
  </header>

  <div class="admin-shell">

    <!-- Sidebar -->
    <aside class="admin-sidebar">
      <ul class="folder-tabs">
        <li>
          <a href="${pageContext.request.contextPath}/admin/dashboard" class="folder-tab-link">
            <span>📁 Overview Desk</span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/admin/complaints" class="folder-tab-link">
            <span>📑 Complaints Ledger</span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/admin/feedback" class="folder-tab-link">
            <span>⭐ Student Feedback</span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/admin/manage" class="folder-tab-link active">
            <span>⚙ Admin Accounts</span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/admin/export" class="folder-tab-link" target="_blank">
            <span>📥 Export CSV</span>
          </a>
        </li>
      </ul>
    </aside>

    <main class="admin-main">

      <div class="desk-header">
        <div>
          <h1 class="desk-title">Staff Credentials & Administration</h1>
          <p style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-ink-muted);">
            Manage administrator accounts, roles, and credential security.
          </p>
        </div>
      </div>

      <!-- Success Alert -->
      <c:if test="${not empty successMessage}">
        <div class="alert alert-success">
          <strong>Success:</strong> <c:out value="${successMessage}"/>
        </div>
      </c:if>

      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin-bottom: 2.5rem;">

        <!-- 1. Change Password Form -->
        <div class="paper-card" style="background: var(--color-card);">
          <h3 style="font-size: 1.3rem; margin-bottom: 1.25rem;">Change My Password</h3>

          <form action="${pageContext.request.contextPath}/admin/manage" method="POST">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" name="action" value="changePassword">

            <div class="form-group">
              <label class="form-label" style="font-size: 1rem;">Current Password</label>
              <input type="password" name="currentPassword" class="input-ruled mono" required>
              <c:if test="${not empty pwdErrors.currentPassword}">
                <span class="inline-error"><c:out value="${pwdErrors.currentPassword}"/></span>
              </c:if>
            </div>

            <div class="form-group">
              <label class="form-label" style="font-size: 1rem;">New Password (Min 8 characters)</label>
              <input type="password" name="newPassword" class="input-ruled mono" required>
              <c:if test="${not empty pwdErrors.newPassword}">
                <span class="inline-error"><c:out value="${pwdErrors.newPassword}"/></span>
              </c:if>
            </div>

            <div class="form-group">
              <label class="form-label" style="font-size: 1rem;">Confirm New Password</label>
              <input type="password" name="confirmPassword" class="input-ruled mono" required>
              <c:if test="${not empty pwdErrors.confirmPassword}">
                <span class="inline-error"><c:out value="${pwdErrors.confirmPassword}"/></span>
              </c:if>
            </div>

            <button type="submit" class="btn btn-primary">Update Password</button>
          </form>
        </div>

        <!-- 2. Create Administrator (Super Admin Only) -->
        <c:choose>
          <c:when test="${sessionScope.admin.superAdmin}">
            <div class="paper-card" style="background: var(--color-card);">
              <h3 style="font-size: 1.3rem; margin-bottom: 1.25rem;">Register New Administrator</h3>

              <c:if test="${not empty createError}">
                <div class="alert alert-danger" style="font-size: 0.8rem; margin-bottom: 1rem;">
                  <c:out value="${createError}"/>
                </div>
              </c:if>

              <form action="${pageContext.request.contextPath}/admin/manage" method="POST">
                <input type="hidden" name="csrfToken" value="${csrfToken}">
                <input type="hidden" name="action" value="createAdmin">

                <div class="form-group">
                  <label class="form-label" style="font-size: 1rem;">Username</label>
                  <input type="text" name="newUsername" placeholder="e.g. hod_civil" class="input-ruled mono" required>
                </div>

                <div class="form-group">
                  <label class="form-label" style="font-size: 1rem;">Full Name</label>
                  <input type="text" name="newFullName" placeholder="e.g. Dr. Ramesh Kumar" class="input-ruled" required>
                </div>

                <div class="form-group">
                  <label class="form-label" style="font-size: 1rem;">Official Email</label>
                  <input type="email" name="newEmail" placeholder="e.g. ramesh.k@campus.edu" class="input-ruled mono" required>
                </div>

                <div class="form-group">
                  <label class="form-label" style="font-size: 1rem;">Role</label>
                  <select name="newRole" class="input-ruled">
                    <option value="ADMIN">Regular Department Administrator</option>
                    <option value="SUPER_ADMIN">Super Administrator (Full Privileges)</option>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label" style="font-size: 1rem;">Initial Password</label>
                  <input type="password" name="tempPassword" placeholder="Min 8 characters" class="input-ruled mono" required>
                </div>

                <button type="submit" class="btn btn-secondary">Create Administrator Account</button>
              </form>
            </div>
          </c:when>
          <c:otherwise>
            <div class="paper-card" style="background: var(--color-paper-light); display: flex; align-items: center; justify-content: center; text-align: center;">
              <div>
                <span class="handwritten" style="font-size: 1.5rem;">Access Restricted</span>
                <p style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-ink-muted); margin-top: 0.5rem;">
                  Only Super Administrators can create or provision new staff accounts.
                </p>
              </div>
            </div>
          </c:otherwise>
        </c:choose>

      </div>

      <!-- Current Administrators Table -->
      <h3 style="font-size: 1.3rem; margin-bottom: 1rem;">Registered Campus Administrators</h3>
      <div class="ledger-wrapper">
        <table class="ledger-table">
          <thead>
            <tr>
              <th class="col-margin"></th>
              <th>Username</th>
              <th>Full Name</th>
              <th>Official Email</th>
              <th>Role</th>
              <th>Created Date</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${adminList}" var="adm">
              <tr>
                <td class="col-margin"></td>
                <td class="mono" style="font-weight: 700;"><c:out value="${adm.username}"/></td>
                <td><c:out value="${adm.fullName}"/></td>
                <td class="mono"><c:out value="${adm.email}"/></td>
                <td>
                  <span class="stamp ${adm.superAdmin ? 'stamp-submitted' : 'stamp-under-review'}" style="font-size: 0.65rem; padding: 0.15rem 0.4rem;">
                    <c:out value="${adm.role}"/>
                  </span>
                </td>
                <td class="mono" style="font-size: 0.75rem;"><c:out value="${adm.createdAt}"/></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

    </main>

  </div>

</body>
</html>
