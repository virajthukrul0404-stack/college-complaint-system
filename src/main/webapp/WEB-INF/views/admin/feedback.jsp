<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>Student Feedback Reviews - Office Desk</title>
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
          <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-left: 0.5rem;">[FEEDBACK REVIEWS]</span>
        </div>
      </div>
      <div style="display: flex; align-items: center; gap: 1.25rem; font-family: var(--font-mono); font-size: 0.85rem;">
        <span>Staff: <strong><c:out value="${sessionScope.admin.fullName}"/></strong></span>
        <a href="${pageContext.request.contextPath}/home" target="_blank" class="btn btn-secondary btn-sm">Public Board ↗</a>
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
          <a href="${pageContext.request.contextPath}/admin/feedback" class="folder-tab-link active">
            <span>⭐ Student Feedback</span>
          </a>
        </li>
        <c:if test="${sessionScope.admin.superAdmin}">
          <li>
            <a href="${pageContext.request.contextPath}/admin/manage" class="folder-tab-link">
              <span>⚙ Admin Accounts</span>
            </a>
          </li>
        </c:if>
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
          <h1 class="desk-title">Student Quality Reviews</h1>
          <p style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-ink-muted);">
            Departmental satisfaction scores, star distributions, and student suggestions.
          </p>
        </div>
      </div>

      <!-- Ratings Analytics Row -->
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin-bottom: 2.5rem;">

        <!-- Department Averages -->
        <div class="paper-card" style="background: #FFF; padding: 1.75rem;">
          <h3 style="font-size: 1.25rem; margin-bottom: 1.25rem;">Average Score by Department</h3>
          <div style="display: flex; flex-direction: column; gap: 0.85rem; font-family: var(--font-mono); font-size: 0.9rem;">
            <c:forEach items="${deptAverages}" var="entry">
              <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px dashed rgba(26, 25, 22, 0.12); padding-bottom: 0.4rem;">
                <span><c:out value="${entry.key}"/></span>
                <span style="font-weight: 800; color: var(--color-board-green); font-size: 1.1rem;">
                  <c:out value="${entry.value}"/> ★
                </span>
              </div>
            </c:forEach>
          </div>
        </div>

        <!-- Rating Distribution -->
        <div class="paper-card" style="background: #FFF; padding: 1.75rem;">
          <h3 style="font-size: 1.25rem; margin-bottom: 1.25rem;">Star Rating Distribution</h3>
          <div style="display: flex; flex-direction: column; gap: 0.75rem; font-family: var(--font-mono); font-size: 0.85rem;">
            <c:forEach items="${ratingDist}" var="dist">
              <div style="display: flex; align-items: center; gap: 1rem;">
                <span style="width: 45px; font-weight: 700;">${dist.key} ★</span>
                <div style="flex: 1; height: 18px; background: var(--color-paper); border: 1px solid var(--color-ink); border-radius: 2px; overflow: hidden;">
                  <div style="height: 100%; width: ${totalCount > 0 ? (dist.value * 100.0 / totalCount) : 0}%; background: var(--color-highway-yellow);"></div>
                </div>
                <span style="width: 30px; text-align: right; font-weight: 800;">${dist.value}</span>
              </div>
            </c:forEach>
          </div>
        </div>

      </div>

      <!-- Feedback List Ledger Table -->
      <div class="ledger-wrapper">
        <table class="ledger-table">
          <thead>
            <tr>
              <th class="col-margin"></th>
              <th>Date</th>
              <th>Department</th>
              <th>Category</th>
              <th>Rating</th>
              <th>Student Comment</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty feedbackList}">
                <tr>
                  <td class="col-margin"></td>
                  <td colspan="5" style="text-align: center; padding: 2.5rem; font-family: var(--font-hand); font-size: 1.4rem;">
                    No feedback entries submitted yet.
                  </td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach items="${feedbackList}" var="fb">
                  <tr>
                    <td class="col-margin"></td>
                    <td class="mono" style="font-size: 0.75rem; white-space: nowrap;">
                      <c:out value="${fn:substring(fb.createdAt, 0, 10)}"/>
                    </td>
                    <td style="font-weight: 600;"><c:out value="${fb.departmentName}"/></td>
                    <td><c:out value="${fb.category}"/></td>
                    <td>
                      <span style="font-family: var(--font-mono); font-weight: 800; color: var(--color-signal-red);">
                        <c:out value="${fb.rating}"/> ★
                      </span>
                    </td>
                    <td style="max-width: 450px;">
                      "<c:out value="${fb.comment}"/>"
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>

    </main>

  </div>

</body>
</html>
