<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>Admin Desk - Grievance Overview & Analytics</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
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
          <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-left: 0.5rem;">[OFFICIAL USE ONLY]</span>
        </div>
      </div>
      <div style="display: flex; align-items: center; gap: 1.25rem; font-family: var(--font-mono); font-size: 0.85rem;">
        <span>Staff: <strong><c:out value="${sessionScope.admin.fullName}"/></strong> (<c:out value="${sessionScope.admin.role}"/>)</span>
        <a href="${pageContext.request.contextPath}/home" target="_blank" class="btn btn-secondary btn-sm">Public Board ↗</a>
        <a href="${pageContext.request.contextPath}/admin/logout" class="btn btn-danger btn-sm">Logout</a>
      </div>
    </div>
  </header>

  <div class="admin-shell">

    <!-- Manila Folder Tabs Sidebar -->
    <aside class="admin-sidebar">
      <ul class="folder-tabs">
        <li>
          <a href="${pageContext.request.contextPath}/admin/dashboard" class="folder-tab-link active">
            <span>📁 Overview Desk</span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/admin/complaints" class="folder-tab-link">
            <span>📑 Complaints Ledger</span>
            <span class="badge" id="sidebarBadge"><c:out value="${totalComplaints}"/></span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/admin/feedback" class="folder-tab-link">
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

    <!-- Main Desk Content -->
    <main class="admin-main">

      <div class="desk-header">
        <div>
          <h1 class="desk-title">Grievance Overview Desk</h1>
          <p style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-ink-muted);">
            Real-time complaint telemetry, status distribution, and 30-day resolution trends.
          </p>
        </div>

        <div style="display: flex; align-items: center; gap: 1rem;">
          <div id="liveFeedStatus" style="display: flex; align-items: center; gap: 0.4rem; font-family: var(--font-mono); font-size: 0.75rem; background: #FFF; border: 1px solid var(--color-ink); padding: 0.3rem 0.75rem; border-radius: 20px;">
            <span style="width: 8px; height: 8px; background: #22C55E; border-radius: 50%;"></span>
            <span>Live Feed Connected</span>
          </div>
          <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-primary btn-sm">Open Full Ledger ➔</a>
        </div>
      </div>

      <!-- Ledger Metric Cards -->
      <div class="metrics-row">
        <div class="metric-card">
          <div class="metric-label">Total Registered</div>
          <div class="metric-val" id="totalCounter"><c:out value="${totalComplaints}"/></div>
          <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-top: 0.4rem;">
            All historical complaints
          </div>
        </div>

        <div class="metric-card metric-urgent">
          <div class="metric-label" style="color: var(--color-signal-red);">High Priority (Urgent)</div>
          <div class="metric-val"><c:out value="${priorityCounts['High']}"/></div>
          <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-signal-red); margin-top: 0.4rem;">
            Requires immediate inspection
          </div>
        </div>

        <div class="metric-card">
          <div class="metric-label">Under Action</div>
          <div class="metric-val" style="color: #C05621;">
            <c:out value="${statusCounts['In Progress'] + statusCounts['Under Review']}"/>
          </div>
          <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-top: 0.4rem;">
            Under Review or In Progress
          </div>
        </div>

        <div class="metric-card">
          <div class="metric-label" style="color: var(--color-board-green);">Resolved & Verified</div>
          <div class="metric-val" style="color: var(--color-board-green);"><c:out value="${resolvedComplaints}"/></div>
          <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-top: 0.4rem;">
            Avg: <c:out value="${avgResolutionHours}"/> hours to resolve
          </div>
        </div>
      </div>

      <!-- Charts & Department Pulse Grid -->
      <div style="display: grid; grid-template-columns: 1.6fr 1fr; gap: 2rem; margin-bottom: 2.5rem;">
        
        <!-- 30-Day Trend Inked Chart -->
        <div class="paper-card" style="background: #FFF; padding: 1.75rem;">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
            <h3 style="font-size: 1.25rem;">30-Day Filing Influx Trend</h3>
            <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted);">DAILY COMPLAINTS</span>
          </div>
          <div style="height: 240px; position: relative;">
            <canvas id="trendChart"></canvas>
          </div>
        </div>

        <!-- Breakdown by Department -->
        <div class="paper-card" style="background: #FFF; padding: 1.75rem;">
          <h3 style="font-size: 1.25rem; margin-bottom: 1.25rem;">By Department</h3>
          <div style="display: flex; flex-direction: column; gap: 0.75rem; font-family: var(--font-mono); font-size: 0.85rem;">
            <c:forEach items="${deptCounts}" var="dept">
              <div style="display: flex; justify-content: space-between; border-bottom: 1px dashed rgba(26, 25, 22, 0.12); padding-bottom: 0.35rem;">
                <span><c:out value="${dept.key}"/></span>
                <strong><c:out value="${dept.value}"/></strong>
              </div>
            </c:forEach>
          </div>
        </div>

      </div>

      <!-- Live Recent Complaints Register -->
      <div style="margin-bottom: 2rem;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
          <div>
            <h2 style="font-size: 1.45rem;">Newest Incoming Complaints</h2>
            <p style="font-family: var(--font-mono); font-size: 0.8rem; color: var(--color-ink-muted);">
              Updates in real-time as students submit new slips.
            </p>
          </div>
          <span class="handwritten" style="font-size: 1.3rem; color: var(--color-signal-red);">Auto-syncing via SSE ➔</span>
        </div>

        <div class="ledger-wrapper">
          <table class="ledger-table" id="recentTable">
            <thead>
              <tr>
                <th class="col-margin"></th>
                <th>Tracking ID</th>
                <th>Subject</th>
                <th>Department</th>
                <th>Category</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody id="recentTableBody">
              <c:forEach items="${recentComplaints}" var="c">
                <tr id="row-${c.trackingId}">
                  <td class="col-margin"></td>
                  <td class="mono" style="font-weight: 700; color: var(--color-signal-red);">
                    <c:out value="${c.trackingId}"/>
                  </td>
                  <td style="font-weight: 600;"><c:out value="${c.subject}"/></td>
                  <td><c:out value="${c.departmentName}"/></td>
                  <td><c:out value="${c.category}"/></td>
                  <td>
                    <c:choose>
                      <c:when test="${c.priority eq 'High'}">
                        <span class="urgent-flag">URGENT</span>
                      </c:when>
                      <c:otherwise>
                        <c:out value="${c.priority}"/>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <span class="stamp stamp-${fn:toLowerCase(fn:replace(c.status, ' ', '-'))}" style="font-size: 0.7rem; padding: 0.15rem 0.5rem;">
                      <c:out value="${c.status}"/>
                    </span>
                  </td>
                  <td>
                    <a href="${pageContext.request.contextPath}/admin/complaints/detail?id=${c.id}" class="btn btn-secondary btn-sm" style="padding: 0.2rem 0.5rem; font-size: 0.7rem;">
                      Inspect ➔
                    </a>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>

    </main>

  </div>

  <script type="module">
    import { renderTrendChart } from "${pageContext.request.contextPath}/assets/js/charts.js";
    import { initLiveStream } from "${pageContext.request.contextPath}/assets/js/live.js";

    // 1. Render Flat Inked Trend Chart
    const labels = ${trendLabelsJson};
    const values = ${trendValuesJson};
    renderTrendChart("trendChart", labels, values);

    // 2. Real-Time Admin SSE Feed
    initLiveStream("admin-feed", {
      onNewComplaint: (data) => {
        // Increment Counter
        const counter = document.getElementById("totalCounter");
        if (counter) {
          const current = parseInt(counter.textContent) || 0;
          counter.textContent = current + 1;
        }
        const badge = document.getElementById("sidebarBadge");
        if (badge) {
          const current = parseInt(badge.textContent) || 0;
          badge.textContent = current + 1;
        }

        // Prepend new row to table with yellow highlighter wipe
        const tbody = document.getElementById("recentTableBody");
        if (tbody) {
          const tr = document.createElement("tr");
          tr.id = "row-" + data.trackingId;
          tr.className = "row-highlight-new";
          tr.innerHTML = `
            <td class="col-margin"></td>
            <td class="mono" style="font-weight: 700; color: var(--color-signal-red);">\${data.trackingId}</td>
            <td style="font-weight: 600;">\${data.subject}</td>
            <td>\${data.department}</td>
            <td>\${data.category}</td>
            <td>\${data.priority === 'High' ? '<span class="urgent-flag">URGENT</span>' : data.priority}</td>
            <td><span class="stamp stamp-submitted" style="font-size: 0.7rem; padding: 0.15rem 0.5rem;">SUBMITTED</span></td>
            <td><a href="${pageContext.request.contextPath}/admin/complaints?search=\${data.trackingId}" class="btn btn-secondary btn-sm" style="padding: 0.2rem 0.5rem; font-size: 0.7rem;">Inspect ➔</a></td>
          `;
          tbody.insertBefore(tr, tbody.firstChild);
        }
      },
      onStatusUpdate: (data) => {
        const row = document.getElementById("row-" + data.trackingId);
        if (row) {
          const statusCell = row.cells[6];
          if (statusCell) {
            const normalized = data.status.toLowerCase().replace(/\\s+/g, '-');
            statusCell.innerHTML = `<span class="stamp stamp-\${normalized}" style="font-size: 0.7rem; padding: 0.15rem 0.5rem;">\${data.status}</span>`;
          }
        }
      }
    });
  </script>

</body>
</html>
