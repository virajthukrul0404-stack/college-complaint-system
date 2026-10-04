<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="#1F5D4A">
  <title>Notifications • Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
  <style>
    .notif-card {
      background: var(--color-card);
      border: var(--border-thick);
      box-shadow: var(--shadow-hard);
      padding: 1rem 1.25rem;
      margin-bottom: 0.75rem;
      position: relative;
    }
    .notif-card.unread {
      background: var(--color-highway-yellow-light);
      border-left: 6px solid var(--color-signal-red);
    }
  </style>
</head>
<body>

  <div class="student-shell">

    <!-- Desktop Navigation -->
    <%@ include file="nav_desktop.jspf" %>

    <!-- Mobile Header -->
    <header class="site-header" style="display: block; border-bottom: var(--border-thick);">
      <div class="container" style="display: flex; justify-content: space-between; align-items: center; padding: 0.6rem 1rem;">
        <span style="font-family: var(--font-display); font-weight: 800; font-size: 1.1rem;">Grievance Alerts</span>
        <c:if test="${unreadCount > 0}">
          <a href="${pageContext.request.contextPath}/student/notifications?action=readAll" class="mono" style="font-size: 0.75rem; color: var(--color-pen-blue); font-weight: 700;">
            Mark all read
          </a>
        </c:if>
      </div>
    </header>

    <main class="slip-container" style="max-width: 760px;">

      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
        <div>
          <h1 style="font-family: var(--font-display); font-size: 1.5rem; margin: 0;">
            Notifications Inbox
          </h1>
          <p style="color: var(--color-ink-muted); font-size: 0.88rem; margin: 0.2rem 0 0 0;">
            Live status progression and resolution updates for your filed grievances.
          </p>
        </div>
        <c:if test="${unreadCount > 0}">
          <form action="${pageContext.request.contextPath}/student/notifications" method="POST" style="margin: 0;">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" name="action" value="markAllRead">
            <button type="submit" class="btn btn-secondary btn-sm">
              ✓ Mark All Read (${unreadCount})
            </button>
          </form>
        </c:if>
      </div>

      <c:choose>
        <c:when test="${empty notifications}">
          <div class="pinned-slip" style="text-align: center; padding: 3rem 1rem;">
            <p style="color: var(--color-ink-muted); font-size: 1rem; margin: 0;">
              No notifications yet. You will receive updates here as staff acts on your complaints.
            </p>
          </div>
        </c:when>
        <c:otherwise>
          <c:forEach var="n" items="${notifications}">
            <div class="notif-card ${n.read ? '' : 'unread'}">
              <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 0.75rem;">
                <div style="flex: 1;">
                  <div style="font-size: 0.95rem; font-weight: ${n.read ? '500' : '700'}; line-height: 1.4;">
                    <c:out value="${n.message}"/>
                  </div>
                  <div class="mono" style="font-size: 0.72rem; color: var(--color-ink-muted); margin-top: 4px;">
                    <c:out value="${n.createdAt}"/>
                  </div>
                </div>

                <div style="display: flex; align-items: center; gap: 0.5rem;">
                  <c:if test="${not empty n.complaintId}">
                    <a href="${pageContext.request.contextPath}/student/complaints/detail?id=${n.complaintId}" class="btn btn-secondary btn-sm" style="font-size: 0.75rem; padding: 0.2rem 0.5rem;">
                      View Docket ➔
                    </a>
                  </c:if>
                  <c:if test="${not n.read}">
                    <form action="${pageContext.request.contextPath}/student/notifications" method="POST" style="margin: 0;">
                      <input type="hidden" name="csrfToken" value="${csrfToken}">
                      <input type="hidden" name="action" value="markRead">
                      <input type="hidden" name="id" value="${n.id}">
                      <button type="submit" class="btn btn-secondary btn-sm" style="font-size: 0.72rem; padding: 0.2rem 0.5rem;" title="Mark as read">
                        ✓
                      </button>
                    </form>
                  </c:if>
                </div>
              </div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>

    </main>

    <!-- Mobile Bottom Navigation -->
    <%@ include file="nav_mobile.jspf" %>

  </div>

</body>
</html>
