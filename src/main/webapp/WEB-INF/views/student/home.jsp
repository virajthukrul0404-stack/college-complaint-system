<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="#1F5D4A">
  <link rel="manifest" href="${pageContext.request.contextPath}/manifest.json">
  <title>Student Home Desk • Campus Notice Board</title>
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

    <!-- Mobile Top Header -->
    <header class="site-header" style="display: block; border-bottom: var(--border-thick);">
      <div class="container" style="display: flex; justify-content: space-between; align-items: center; padding: 0.6rem 1rem;">
        <div style="display: flex; align-items: center; gap: 0.5rem;">
          <div class="brand-seal" style="background: var(--color-board-green); width: 32px; height: 32px; font-size: 0.85rem;">ST</div>
          <div>
            <div style="font-family: var(--font-display); font-weight: 800; font-size: 1.05rem; line-height: 1;">Student Desk</div>
            <div style="font-family: var(--font-mono); font-size: 0.72rem; color: var(--color-ink-muted);"><c:out value="${student.rollNo}"/> • Year <c:out value="${student.yearOfStudy}"/></div>
          </div>
        </div>
        <div style="display: flex; gap: 0.5rem; align-items: center;">
          <a href="${pageContext.request.contextPath}/student/logout" class="btn btn-secondary btn-sm" style="font-size: 0.75rem; padding: 0.25rem 0.6rem;">Sign Out</a>
        </div>
      </div>
    </header>

    <!-- Main Desk Content -->
    <main class="slip-container">

      <!-- Greeting Slip -->
      <div class="pinned-slip">
        <div class="tape-pin"></div>
        <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 0.75rem;">
          <div>
            <span class="notice-tag">Welcome Back</span>
            <h1 style="font-family: var(--font-display); font-size: 1.6rem; margin: 0.25rem 0;">
              Hello, <c:out value="${student.fullName}"/>
            </h1>
            <p style="color: var(--color-ink-muted); font-size: 0.92rem; margin: 0;">
              <c:out value="${student.departmentName}"/> • Roll: <span class="mono"><c:out value="${student.rollNo}"/></span>
            </p>
          </div>
          <div style="display: flex; gap: 0.6rem; flex-wrap: wrap;">
            <a href="${pageContext.request.contextPath}/student/complaint/new" class="btn btn-primary" style="font-size: 0.9rem;">
              + File a Complaint
            </a>
            <a href="${pageContext.request.contextPath}/student/feedback" class="btn btn-secondary" style="font-size: 0.9rem;">
              ⭐ Give Feedback
            </a>
          </div>
        </div>
      </div>

      <!-- Live Activity Strip (SSE Connection Indicator) -->
      <div id="live-activity-bar" style="background: var(--color-paper-light); border: var(--border-thin); padding: 0.6rem 1rem; margin-bottom: 1.25rem; display: flex; align-items: center; justify-content: space-between; font-family: var(--font-mono); font-size: 0.8rem;">
        <div style="display: flex; align-items: center; gap: 0.5rem;">
          <span id="sse-pulse-dot" style="display: inline-block; width: 10px; height: 10px; border-radius: 50%; background: var(--color-board-green); border: 1px solid var(--color-ink);"></span>
          <span id="live-status-text">LIVE ACTIVITY FEED • CONNECTED</span>
        </div>
        <div id="live-update-msg" style="color: var(--color-pen-blue); font-weight: 700;">
          All systems monitored
        </div>
      </div>

      <!-- Status Breakdown Ledger Slips -->
      <h2 style="font-family: var(--font-display); font-size: 1.25rem; margin-bottom: 0.75rem;">
        My Grievance Overview
      </h2>
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 0.75rem; margin-bottom: 1.5rem;">
        <div class="counter-slip" style="padding: 1rem; text-align: center;">
          <div class="slip-number" style="font-size: 1.75rem;"><c:out value="${statusCounts['Submitted']}"/></div>
          <div class="slip-label" style="font-size: 0.75rem;">Submitted</div>
        </div>
        <div class="counter-slip" style="padding: 1rem; text-align: center;">
          <div class="slip-number" style="font-size: 1.75rem; color: var(--color-pen-blue);"><c:out value="${statusCounts['Under Review']}"/></div>
          <div class="slip-label" style="font-size: 0.75rem;">Under Review</div>
        </div>
        <div class="counter-slip" style="padding: 1rem; text-align: center;">
          <div class="slip-number" style="font-size: 1.75rem; color: var(--color-highway-yellow);"><c:out value="${statusCounts['In Progress']}"/></div>
          <div class="slip-label" style="font-size: 0.75rem;">In Progress</div>
        </div>
        <div class="counter-slip" style="padding: 1rem; text-align: center;">
          <div class="slip-number" style="font-size: 1.75rem; color: var(--color-board-green);"><c:out value="${statusCounts['Resolved']}"/></div>
          <div class="slip-label" style="font-size: 0.75rem;">Resolved</div>
        </div>
      </div>

      <!-- Recent Complaints List -->
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem;">
        <h2 style="font-family: var(--font-display); font-size: 1.25rem; margin: 0;">
          Recent Complaint Slips
        </h2>
        <a href="${pageContext.request.contextPath}/student/complaints" class="mono" style="font-size: 0.85rem; font-weight: 700; color: var(--color-pen-blue);">
          View All Slips (${totalComplaints}) ➔
        </a>
      </div>

      <c:choose>
        <c:when test="${empty recentComplaints}">
          <div class="pinned-slip" style="text-align: center; padding: 2.5rem 1rem;">
            <p style="color: var(--color-ink-muted); font-size: 1rem; margin-bottom: 1rem;">
              You have not pinned any complaint slips yet. Everything seems to be running smoothly!
            </p>
            <a href="${pageContext.request.contextPath}/student/complaint/new" class="btn btn-primary">
              Pin Your First Issue ➔
            </a>
          </div>
        </c:when>
        <c:otherwise>
          <div style="display: flex; flex-direction: column; gap: 0.85rem; margin-bottom: 2rem;">
            <c:forEach var="c" items="${recentComplaints}">
              <div class="pinned-slip" style="margin-bottom: 0; padding: 1.1rem 1.25rem;">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 0.5rem; flex-wrap: wrap;">
                  <div>
                    <span class="mono" style="font-size: 0.82rem; font-weight: 700; color: var(--color-pen-blue);">
                      <c:out value="${c.trackingId}"/>
                    </span>
                    •
                    <span style="font-family: var(--font-mono); font-size: 0.78rem; color: var(--color-ink-muted);">
                      <c:out value="${c.category}"/>
                    </span>
                    <c:if test="${c.anonymous}">
                      <span class="stamp" style="font-size: 0.65rem; padding: 1px 4px; border-color: var(--color-pen-blue); color: var(--color-pen-blue);">ANONYMOUS</span>
                    </c:if>
                    <h3 style="font-family: var(--font-display); font-size: 1.15rem; margin: 0.35rem 0;">
                      <a href="${pageContext.request.contextPath}/student/complaints/detail?id=${c.id}" style="color: var(--color-ink); text-decoration: none;">
                        <c:out value="${c.subject}"/>
                      </a>
                    </h3>
                  </div>

                  <div style="text-align: right;">
                    <c:choose>
                      <c:when test="${c.status eq 'Resolved'}">
                        <span class="stamp stamp-resolved">RESOLVED</span>
                      </c:when>
                      <c:when test="${c.status eq 'In Progress'}">
                        <span class="stamp stamp-inprogress">IN PROGRESS</span>
                      </c:when>
                      <c:when test="${c.status eq 'Under Review'}">
                        <span class="stamp stamp-review">UNDER REVIEW</span>
                      </c:when>
                      <c:when test="${c.status eq 'Rejected'}">
                        <span class="stamp stamp-rejected">REJECTED</span>
                      </c:when>
                      <c:otherwise>
                        <span class="stamp stamp-submitted">SUBMITTED</span>
                      </c:otherwise>
                    </c:choose>
                    <div style="font-family: var(--font-mono); font-size: 0.72rem; color: var(--color-ink-muted); margin-top: 4px;">
                      <c:out value="${c.createdAt}"/>
                    </div>
                  </div>
                </div>

                <c:if test="${not empty c.publicRemark}">
                  <div style="margin-top: 0.75rem; padding: 0.5rem 0.75rem; background: var(--color-paper-light); border-left: 3px solid var(--color-board-green); font-size: 0.85rem;">
                    <strong>Office Note:</strong> <c:out value="${c.publicRemark}"/>
                  </div>
                </c:if>

                <div style="margin-top: 0.75rem; display: flex; justify-content: flex-end;">
                  <a href="${pageContext.request.contextPath}/student/complaints/detail?id=${c.id}" class="btn btn-secondary btn-sm" style="font-size: 0.78rem;">
                    View Live Timeline ➔
                  </a>
                </div>
              </div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>

    </main>

    <!-- Mobile Bottom Navigation -->
    <%@ include file="nav_mobile.jspf" %>

  </div>

  <!-- Real-time Scoped SSE Stream for this Student -->
  <script>
    const studentId = '${student.id}';
    let eventSource = null;

    function initEventStream() {
      if (!window.EventSource || !studentId) return;

      if (eventSource) {
        eventSource.close();
      }

      eventSource = new EventSource('${pageContext.request.contextPath}/events?channel=student-' + studentId);

      eventSource.onopen = function () {
        document.getElementById('sse-pulse-dot').style.backgroundColor = 'var(--color-board-green)';
        document.getElementById('live-status-text').textContent = 'LIVE STREAM • ACTIVE';
      };

      eventSource.addEventListener('statusUpdate', function (e) {
        try {
          const data = JSON.parse(e.data);
          document.getElementById('live-update-msg').textContent = '⚡ Docket ' + data.trackingId + ' moved to ' + data.status;
          document.getElementById('live-activity-bar').style.backgroundColor = 'var(--color-highway-yellow-light)';
        } catch (err) {
          console.error('Error parsing live status update', err);
        }
      });

      eventSource.addEventListener('notification', function (e) {
        try {
          const data = JSON.parse(e.data);
          document.getElementById('live-update-msg').textContent = '🔔 ' + data.message;
        } catch (err) {}
      });

      eventSource.onerror = function () {
        document.getElementById('sse-pulse-dot').style.backgroundColor = 'var(--color-signal-red)';
        document.getElementById('live-status-text').textContent = 'STREAM RECONNECTING...';
      };
    }

    window.reconnectEventStream = initEventStream;
    document.addEventListener('DOMContentLoaded', initEventStream);

    document.addEventListener('visibilitychange', function () {
      if (document.visibilityState === 'visible') {
        if (!eventSource || eventSource.readyState === EventSource.CLOSED) {
          initEventStream();
        }
      }
    });

    window.addEventListener('online', function () {
      initEventStream();
    });
  </script>
</body>
</html>
