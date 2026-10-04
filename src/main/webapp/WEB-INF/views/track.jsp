<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>Track Grievance Slip - Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/board.css">
</head>
<body>

  <!-- Site Header -->
  <header class="site-header">
    <div class="container header-inner">
      <a href="${pageContext.request.contextPath}/home" class="brand-stamp">
        <div class="brand-seal">NB</div>
        <div class="brand-title">
          <span class="title">Campus Notice Board</span>
          <span class="subtitle">Official Grievance Registry</span>
        </div>
      </a>
      <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/home" class="nav-link">The Board</a></li>
        <li><a href="${pageContext.request.contextPath}/complaint/new" class="nav-link">File Complaint</a></li>
        <li><a href="${pageContext.request.contextPath}/complaint/track" class="nav-link active">Track Slip</a></li>
        <li><a href="${pageContext.request.contextPath}/feedback" class="nav-link">Feedback</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-admin-btn">Office Desk ➔</a></li>
      </ul>
    </div>
  </header>

  <main class="container" style="padding: 3rem 1.5rem; max-width: 860px;">

    <!-- Track Lookup Header & Search Form -->
    <div style="margin-bottom: 2rem;">
      <span class="handwritten" style="font-size: 1.5rem;">Live Verification Desk</span>
      <h1 style="font-size: 2.4rem;">Track Complaint Slip</h1>
      <p style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-ink-muted);">
        Enter your tracking slip ID and registered email to inspect the official timeline.
      </p>
    </div>

    <!-- Search Box Paper Slip -->
    <div class="paper-card" style="margin-bottom: 2.5rem; background: var(--color-paper-light);">
      <form action="${pageContext.request.contextPath}/complaint/track" method="GET" style="display: flex; flex-wrap: wrap; gap: 1rem; align-items: flex-end;">
        <div style="flex: 1; min-width: 220px;">
          <label class="form-label" style="font-size: 1.1rem;">Tracking Slip ID</label>
          <input type="text" name="trackingId" value="<c:out value='${trackingId}'/>" 
                 placeholder="e.g. CMP-2026-00001" class="input-ruled mono" required>
        </div>
        <div style="flex: 1; min-width: 220px;">
          <label class="form-label" style="font-size: 1.1rem;">Registered Contact Email</label>
          <input type="email" name="email" value="<c:out value='${email}'/>" 
                 placeholder="e.g. student@campus.edu" class="input-ruled mono" required>
        </div>
        <div>
          <button type="submit" class="btn btn-primary">Find Slip</button>
        </div>
      </form>
    </div>

    <!-- Error Alert -->
    <c:if test="${not empty error}">
      <div class="alert alert-danger" role="alert">
        <strong>Verification Failed:</strong> <c:out value="${error}"/>
      </div>
    </c:if>

    <!-- Complaint Details & Real-Time Timeline -->
    <c:if test="${not empty complaint}">
      
      <!-- Live SSE Connection Pill & ARIA Live Region -->
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
        <div id="livePill" style="display: inline-flex; align-items: center; gap: 0.5rem; font-family: var(--font-mono); font-size: 0.75rem; background: #FFF; border: 1px solid var(--color-ink); padding: 0.25rem 0.65rem; border-radius: 20px; box-shadow: 1px 2px 0 var(--color-ink);">
          <span style="width: 8px; height: 8px; background: #22C55E; border-radius: 50%; display: inline-block;"></span>
          <span id="liveStatusText">Live SSE Connected (Waiting for updates)</span>
        </div>
        <a href="${pageContext.request.contextPath}/complaint/receipt?id=${complaint.trackingId}" class="btn btn-secondary btn-sm" target="_blank">
          View Printable Ticket ➔
        </a>
      </div>
      <div id="ariaLiveAlert" class="sr-only" aria-live="polite" style="position: absolute; width: 1px; height: 1px; overflow: hidden; clip: rect(1px, 1px, 1px, 1px);"></div>

      <!-- Main Complaint Inspection Card -->
      <div class="paper-card ruled-lines" style="background-color: var(--color-paper-light); padding: 2rem; margin-bottom: 2rem; position: relative;">
        <div class="tape-top"></div>

        <div style="display: flex; flex-wrap: wrap; justify-content: space-between; align-items: flex-start; gap: 1rem; border-bottom: 2px dashed var(--color-ink-faint); padding-bottom: 1.5rem; margin-bottom: 1.5rem;">
          <div>
            <span class="mono" style="font-size: 1.3rem; font-weight: 800; color: var(--color-signal-red);">
              <c:out value="${complaint.trackingId}"/>
            </span>
            <h2 style="font-size: 1.7rem; margin-top: 0.25rem;"><c:out value="${complaint.subject}"/></h2>
            <div style="font-family: var(--font-mono); font-size: 0.8rem; color: var(--color-ink-muted); margin-top: 0.25rem;">
              <span>Filed on <c:out value="${complaint.createdAt}"/></span> • 
              <span><c:out value="${complaint.departmentName}"/></span> • 
              <span><c:out value="${complaint.category}"/></span>
            </div>
          </div>

          <!-- Prominent Live Rubber Stamp -->
          <div style="text-align: right;">
            <div id="liveStamp" class="stamp stamp-lg stamp-${fn:toLowerCase(fn:replace(complaint.status, ' ', '-'))}">
              <c:out value="${fn:toUpperCase(complaint.status)}"/>
            </div>
          </div>
        </div>

        <!-- Description -->
        <div style="margin-bottom: 1.5rem;">
          <h4 style="font-family: var(--font-hand); font-size: 1.3rem; color: var(--color-pen-blue); margin-bottom: 0.25rem;">Grievance Statement:</h4>
          <p style="font-size: 0.95rem; line-height: 1.6; white-space: pre-wrap;"><c:out value="${complaint.description}"/></p>
        </div>

        <!-- Attachment if present -->
        <c:if test="${not empty complaint.attachmentPath}">
          <div style="margin-top: 1rem; padding: 1rem; background: var(--color-paper); border: 1px dashed var(--color-ink); border-radius: var(--radius-sm);">
            <strong style="font-family: var(--font-mono); font-size: 0.8rem; text-transform: uppercase;">Attached Evidence:</strong>
            <div style="margin-top: 0.5rem;">
              <a href="${pageContext.request.contextPath}/${complaint.attachmentPath}" target="_blank" style="font-family: var(--font-mono); font-size: 0.85rem;">
                📎 View Attached Image (${complaint.attachmentPath})
              </a>
            </div>
          </div>
        </c:if>

        <!-- Public Remark Callout -->
        <div id="publicRemarkBox" style="margin-top: 1.5rem; ${empty complaint.publicRemark ? 'display: none;' : ''}">
          <div style="background-color: var(--color-highway-yellow-light); border: 2px solid var(--color-highway-yellow); padding: 1rem 1.25rem; border-radius: var(--radius-sm);">
            <strong style="font-family: var(--font-mono); font-size: 0.8rem; text-transform: uppercase; color: #9A6700;">Latest Administrator Remark:</strong>
            <p id="publicRemarkText" style="font-size: 0.95rem; margin-top: 0.25rem; font-weight: 500;">
              <c:out value="${complaint.publicRemark}"/>
            </p>
          </div>
        </div>

      </div>

      <!-- Live Status Timeline -->
      <section>
        <div style="border-bottom: 2px dashed var(--color-ink-faint); padding-bottom: 0.5rem; margin-bottom: 1rem;">
          <h3 style="font-size: 1.4rem;">Official Status History Log</h3>
          <p style="font-family: var(--font-mono); font-size: 0.8rem; color: var(--color-ink-muted);">
            Timeline updates live via Server-Sent Events whenever administrators post remarks.
          </p>
        </div>

        <div id="timelineContainer" class="timeline">
          <c:forEach items="${logs}" var="log" varStatus="loop">
            <div class="timeline-step ${loop.last ? 'active' : 'completed'}">
              <div class="timeline-dot"><c:out value="${loop.count}"/></div>
              <div class="timeline-content">
                <div class="timeline-title">
                  <span>Status: <strong style="color: var(--color-signal-red);"><c:out value="${log.newStatus}"/></strong></span>
                  <span class="timeline-time"><c:out value="${log.createdAt}"/></span>
                </div>
                <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-top: 0.2rem;">
                  Updated by: <c:out value="${log.changedBy}"/>
                </div>
                <c:if test="${not empty log.remark}">
                  <div class="timeline-remark">
                    "<c:out value="${log.remark}"/>"
                  </div>
                </c:if>
              </div>
            </div>
          </c:forEach>
        </div>
      </section>

      <!-- Real-Time SSE Listener Script -->
      <script type="module">
        import { initLiveStream } from "${pageContext.request.contextPath}/assets/js/live.js";
        import { applyStamp } from "${pageContext.request.contextPath}/assets/js/stamp.js";

        const channel = "${sseChannel}";
        const trackingId = "${complaint.trackingId}";

        initLiveStream(channel, {
          onConnect: () => {
            const statusText = document.getElementById("liveStatusText");
            if (statusText) statusText.textContent = "Live SSE Connected (Syncing in real-time)";
          },
          onStatusUpdate: (data) => {
            if (data.trackingId === trackingId) {
              // 1. Update Rubber Stamp with Thunk animation
              const stampElem = document.getElementById("liveStamp");
              if (stampElem) {
                applyStamp(stampElem, data.status);
              }

              // 2. Update Public Remark box if present
              if (data.remark && data.remark.trim().length > 0) {
                const remarkBox = document.getElementById("publicRemarkBox");
                const remarkText = document.getElementById("publicRemarkText");
                if (remarkBox && remarkText) {
                  remarkText.textContent = data.remark;
                  remarkBox.style.display = "block";
                }
              }

              // 3. Append to Timeline
              const timeline = document.getElementById("timelineContainer");
              if (timeline) {
                // Remove active class from previous items
                document.querySelectorAll(".timeline-step").forEach(s => s.classList.remove("active"));
                const count = document.querySelectorAll(".timeline-step").length + 1;

                const step = document.createElement("div");
                step.className = "timeline-step active";
                step.innerHTML = `
                  <div class="timeline-dot">\${count}</div>
                  <div class="timeline-content" style="border: 2px solid var(--color-signal-red);">
                    <div class="timeline-title">
                      <span>Status: <strong style="color: var(--color-signal-red);">\${data.status}</strong></span>
                      <span class="timeline-time">\${data.updatedAt || 'Just now'}</span>
                    </div>
                    <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-top: 0.2rem;">
                      Updated live by: Administrator
                    </div>
                    \${data.remark ? `<div class="timeline-remark">"\${data.remark}"</div>` : ''}
                  </div>
                `;
                timeline.appendChild(step);
              }

              // 4. Announce via ARIA live region
              const aria = document.getElementById("ariaLiveAlert");
              if (aria) {
                aria.textContent = "Complaint status changed to " + data.status + (data.remark ? " with remark: " + data.remark : "");
              }
            }
          }
        });
      </script>

    </c:if>

  </main>

  <footer class="site-footer">
    <div class="container footer-inner">
      <div>Campus Complaint & Feedback Registry • Advanced Java</div>
      <div class="footer-stamp">LIVE TRACKING CONSOLE</div>
    </div>
  </footer>

</body>
</html>
