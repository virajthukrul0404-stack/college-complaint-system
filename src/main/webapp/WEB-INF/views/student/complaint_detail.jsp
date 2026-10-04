<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="#1F5D4A">
  <title>Docket <c:out value="${complaint.trackingId}"/> • Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
  <style>
    .docket-paper {
      background: var(--color-card);
      border: var(--border-thick);
      box-shadow: var(--shadow-hard-lg);
      padding: 2rem;
      position: relative;
      margin-bottom: 2rem;
    }
    .timeline-node {
      position: relative;
      padding-left: 2rem;
      margin-bottom: 1.5rem;
      border-left: 2px dashed var(--color-ink-muted);
    }
    .timeline-dot-stamp {
      position: absolute;
      left: -8px;
      top: 0;
      width: 14px;
      height: 14px;
      border-radius: 50%;
      background: var(--color-board-green);
      border: 2px solid var(--color-ink);
    }
  </style>
</head>
<body>

  <div class="student-shell">

    <!-- Desktop Header -->
    <%@ include file="nav_desktop.jspf" %>

    <!-- Mobile Header -->
    <header class="site-header" style="display: block; border-bottom: var(--border-thick);">
      <div class="container" style="display: flex; justify-content: space-between; align-items: center; padding: 0.6rem 1rem;">
        <a href="${pageContext.request.contextPath}/student/complaints" class="mono" style="font-size: 0.85rem; font-weight: 700; color: var(--color-ink); text-decoration: none;">
          ← My Complaints
        </a>
        <span class="mono" style="font-weight: 800; font-size: 0.95rem; color: var(--color-pen-blue);"><c:out value="${complaint.trackingId}"/></span>
      </div>
    </header>

    <main class="slip-container" style="max-width: 860px;">

      <div class="docket-paper">
        <div class="tape-pin" style="width: 120px;"></div>

        <!-- Live Connection Pill -->
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; background: var(--color-paper-light); padding: 0.5rem 0.75rem; border: var(--border-thin);">
          <div style="display: flex; align-items: center; gap: 0.5rem; font-family: var(--font-mono); font-size: 0.75rem;">
            <span id="sse-indicator" style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: var(--color-board-green);"></span>
            <span>LIVE TRACKING ACTIVE</span>
          </div>
          <div class="mono" style="font-size: 0.75rem; color: var(--color-ink-muted);">
            FILED: <c:out value="${complaint.createdAt}"/>
          </div>
        </div>

        <!-- Header: Tracking ID & Stamp -->
        <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem; margin-bottom: 1.25rem;">
          <div>
            <span class="notice-tag">Grievance Docket</span>
            <h1 class="mono" style="font-size: 1.6rem; font-weight: 900; color: var(--color-signal-red); margin: 0.25rem 0;">
              <c:out value="${complaint.trackingId}"/>
            </h1>
            <div style="font-family: var(--font-display); font-size: 1.35rem; font-weight: 800; margin-top: 0.35rem;">
              <c:out value="${complaint.subject}"/>
            </div>
          </div>

          <div id="live-stamp-container" style="text-align: right;">
            <c:choose>
              <c:when test="${complaint.status eq 'Resolved'}">
                <span class="stamp stamp-resolved stamp-animate" style="font-size: 1.3rem; padding: 0.5rem 1.25rem;">RESOLVED</span>
              </c:when>
              <c:when test="${complaint.status eq 'In Progress'}">
                <span class="stamp stamp-inprogress stamp-animate" style="font-size: 1.3rem; padding: 0.5rem 1.25rem;">IN PROGRESS</span>
              </c:when>
              <c:when test="${complaint.status eq 'Under Review'}">
                <span class="stamp stamp-review stamp-animate" style="font-size: 1.3rem; padding: 0.5rem 1.25rem;">UNDER REVIEW</span>
              </c:when>
              <c:when test="${complaint.status eq 'Rejected'}">
                <span class="stamp stamp-rejected stamp-animate" style="font-size: 1.3rem; padding: 0.5rem 1.25rem;">REJECTED</span>
              </c:when>
              <c:otherwise>
                <span class="stamp stamp-submitted stamp-animate" style="font-size: 1.3rem; padding: 0.5rem 1.25rem;">SUBMITTED</span>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <!-- Privacy Status Note -->
        <c:if test="${complaint.anonymous}">
          <div style="background: var(--color-highway-yellow-light); border: 1px dashed var(--color-ink); padding: 0.75rem 1rem; margin-bottom: 1.25rem; font-family: var(--font-mono); font-size: 0.82rem;">
            🔒 <strong>Anonymous Submission:</strong> Your name, roll number, and email are strictly hidden from campus staff and department admins.
          </div>
        </c:if>

        <!-- Metadata Grid -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap: 1rem; margin-bottom: 1.5rem; background: var(--color-paper-light); padding: 1rem; border: var(--border-thin); font-family: var(--font-mono); font-size: 0.85rem;">
          <div><span style="color: var(--color-ink-muted);">DEPARTMENT:</span><br><strong><c:out value="${complaint.departmentName}"/></strong></div>
          <div><span style="color: var(--color-ink-muted);">CATEGORY:</span><br><strong><c:out value="${complaint.category}"/></strong></div>
          <div><span style="color: var(--color-ink-muted);">PRIORITY:</span><br><strong><c:out value="${complaint.priority}"/></strong></div>
          <div><span style="color: var(--color-ink-muted);">ASSIGNED TO:</span><br><strong><c:out value="${not empty complaint.assignedTo ? complaint.assignedTo : 'Pending Assignment'}"/></strong></div>
        </div>

        <!-- Description -->
        <div style="margin-bottom: 1.5rem;">
          <h3 style="font-family: var(--font-mono); font-size: 0.85rem; text-transform: uppercase; margin-bottom: 0.5rem;">
            Detailed Statement
          </h3>
          <div style="background: white; border: 1px solid var(--color-ink-faint); padding: 1rem; font-size: 0.95rem; line-height: 1.55; white-space: pre-wrap;">
            <c:out value="${complaint.description}"/>
          </div>
        </div>

        <!-- Attached Evidence -->
        <c:if test="${not empty complaint.attachmentPath}">
          <div style="margin-bottom: 1.5rem; background: var(--color-paper-light); border: var(--border-thin); padding: 1rem;">
            <h4 style="font-family: var(--font-mono); font-size: 0.85rem; text-transform: uppercase; margin-top: 0; margin-bottom: 0.5rem;">
              Attached Photographic Evidence:
            </h4>
            <div style="margin-bottom: 0.75rem;">
              <img src="${pageContext.request.contextPath}/${complaint.attachmentPath}" alt="Complaint Evidence" style="max-width: 100%; max-height: 280px; border: var(--border-thick); border-radius: var(--radius-sm); object-fit: contain;">
            </div>
            <a href="${pageContext.request.contextPath}/${complaint.attachmentPath}" target="_blank" class="btn btn-secondary btn-sm" style="font-family: var(--font-mono); font-size: 0.8rem;">
              Open Full Resolution ↗
            </a>
          </div>
        </c:if>

        <!-- Official Resolution Remark -->
        <div id="live-remark-box" style="${not empty complaint.publicRemark ? '' : 'display:none;'} margin-bottom: 1.5rem; background: var(--color-board-green-light); border-left: 4px solid var(--color-board-green); padding: 1rem;">
          <h4 style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-board-green); margin: 0 0 0.4rem 0;">
            Official Staff Resolution Note:
          </h4>
          <p id="live-remark-text" style="font-size: 0.92rem; line-height: 1.4; margin: 0;">
            <c:out value="${complaint.publicRemark}"/>
          </p>
        </div>

        <!-- Audit Timeline -->
        <div style="margin-top: 2rem;">
          <h3 style="font-family: var(--font-mono); font-size: 0.85rem; text-transform: uppercase; margin-bottom: 1rem; border-bottom: 1px dashed var(--color-ink-faint); padding-bottom: 0.4rem;">
            Resolution Timeline & Updates
          </h3>

          <div id="timeline-container">
            <c:forEach var="log" items="${statusLogs}">
              <div class="timeline-node">
                <div class="timeline-dot-stamp"></div>
                <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted);">
                  <c:out value="${log.createdAt}"/> • Action by <c:out value="${log.changedBy}"/>
                </div>
                <div style="font-weight: 800; font-size: 0.95rem; margin: 0.2rem 0;">
                  <c:out value="${log.newStatus}"/>
                </div>
                <c:if test="${not empty log.remark}">
                  <div style="font-size: 0.85rem; font-style: italic; color: var(--color-ink);">
                    "<c:out value="${log.remark}"/>"
                  </div>
                </c:if>
              </div>
            </c:forEach>
          </div>
        </div>

        <!-- Back & Receipt buttons -->
        <div style="margin-top: 2rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
          <a href="${pageContext.request.contextPath}/student/complaints" class="btn btn-secondary btn-sm">
            ← Return to Slips
          </a>
          <a href="${pageContext.request.contextPath}/complaint/receipt?id=${complaint.trackingId}" class="btn btn-primary btn-sm" target="_blank">
            Print Official Ticket Receipt ➔
          </a>
        </div>
      </div>

    </main>

    <!-- Mobile Bottom Navigation -->
    <%@ include file="nav_mobile.jspf" %>

  </div>

  <!-- Real-time Scoped SSE Stream for this Complaint -->
  <script>
    const trackingId = '${complaint.trackingId}';
    let sseSource = null;

    function connectSSE() {
      if (!window.EventSource || !trackingId) return;

      if (sseSource) {
        sseSource.close();
      }

      sseSource = new EventSource('${pageContext.request.contextPath}/events?channel=complaint-' + trackingId);

      sseSource.onopen = function () {
        document.getElementById('sse-indicator').style.backgroundColor = 'var(--color-board-green)';
      };

      sseSource.addEventListener('statusUpdate', function (e) {
        try {
          const data = JSON.parse(e.data);
          // Update Stamp
          const stampBox = document.getElementById('live-stamp-container');
          if (stampBox) {
            let stampClass = 'stamp-submitted';
            if (data.status === 'Resolved') stampClass = 'stamp-resolved';
            else if (data.status === 'In Progress') stampClass = 'stamp-inprogress';
            else if (data.status === 'Under Review') stampClass = 'stamp-review';
            else if (data.status === 'Rejected') stampClass = 'stamp-rejected';

            stampBox.innerHTML = '<span class="stamp ' + stampClass + ' stamp-animate" style="font-size: 1.3rem; padding: 0.5rem 1.25rem;">' + data.status.toUpperCase() + '</span>';
          }

          // Update Remark
          if (data.remark && data.remark.trim().length > 0) {
            const remarkBox = document.getElementById('live-remark-box');
            const remarkText = document.getElementById('live-remark-text');
            if (remarkBox && remarkText) {
              remarkText.textContent = data.remark;
              remarkBox.style.display = 'block';
            }
          }

          // Add to timeline
          const timeline = document.getElementById('timeline-container');
          if (timeline) {
            const item = document.createElement('div');
            item.className = 'timeline-node';
            item.innerHTML = `
              <div class="timeline-dot-stamp" style="background: var(--color-highway-yellow);"></div>
              <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted);">` + data.updatedAt + ` • Just now</div>
              <div style="font-weight: 800; font-size: 0.95rem; margin: 0.2rem 0;">` + data.status + `</div>
              <div style="font-size: 0.85rem; font-style: italic; color: var(--color-ink);">"` + (data.remark || '') + `"</div>
            `;
            timeline.insertBefore(item, timeline.firstChild);
          }
        } catch (err) {
          console.error('Error handling SSE update', err);
        }
      } );

      sseSource.onerror = function () {
        document.getElementById('sse-indicator').style.backgroundColor = 'var(--color-signal-red)';
      };
    }

    window.reconnectEventStream = connectSSE;
    document.addEventListener('DOMContentLoaded', connectSSE);

    document.addEventListener('visibilitychange', function () {
      if (document.visibilityState === 'visible') {
        if (!sseSource || sseSource.readyState === EventSource.CLOSED) {
          connectSSE();
        }
      }
    });

    window.addEventListener('online', function () {
      connectSSE();
    });
  </script>

</body>
</html>
