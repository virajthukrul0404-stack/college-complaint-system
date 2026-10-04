<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>Inspect Docket <c:out value="${complaint.trackingId}"/> - Office Desk</title>
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
          <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-left: 0.5rem;">[CASE DOSSIER]</span>
        </div>
      </div>
      <div style="display: flex; align-items: center; gap: 1.25rem; font-family: var(--font-mono); font-size: 0.85rem;">
        <span>Staff: <strong><c:out value="${sessionScope.admin.fullName}"/></strong></span>
        <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-secondary btn-sm">← Back to Ledger</a>
        <a href="${pageContext.request.contextPath}/admin/logout" class="btn btn-danger btn-sm">Logout</a>
      </div>
    </div>
  </header>

  <div class="admin-shell">

    <!-- Manila Folder Sidebar -->
    <aside class="admin-sidebar">
      <ul class="folder-tabs">
        <li>
          <a href="${pageContext.request.contextPath}/admin/dashboard" class="folder-tab-link">
            <span>📁 Overview Desk</span>
          </a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/admin/complaints" class="folder-tab-link active">
            <span>📑 Complaints Ledger</span>
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

    <main class="admin-main">

      <div class="desk-header">
        <div>
          <span class="mono" style="font-size: 1.1rem; font-weight: 800; color: var(--color-signal-red);">
            CASE DOSSIER: <c:out value="${complaint.trackingId}"/>
          </span>
          <h1 class="desk-title" style="margin-top: 0.2rem;"><c:out value="${complaint.subject}"/></h1>
        </div>

        <div style="display: flex; align-items: center; gap: 1rem;">
          <span class="stamp stamp-${fn:toLowerCase(fn:replace(complaint.status, ' ', '-'))} stamp-lg">
            <c:out value="${complaint.status}"/>
          </span>
        </div>
      </div>

      <c:if test="${param.updated eq 'true'}">
        <div class="alert alert-success" style="font-size: 0.85rem;">
          Docket record and audit logs updated successfully.
        </div>
      </c:if>

      <div class="detail-grid">

        <!-- Left Pane: Case File Record & Audit Log -->
        <div>
          
          <div class="detail-pane" style="margin-bottom: 2rem; background: #FFFDF9;">
            <div class="tape-top"></div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem 1.5rem; margin-bottom: 1.5rem; font-family: var(--font-mono); font-size: 0.85rem;">
              <div>
                <span style="color: var(--color-ink-muted); text-transform: uppercase; font-size: 0.75rem;">Complainant:</span><br>
                <strong>
                  <c:choose>
                    <c:when test="${complaint.anonymous}">
                      Anonymous Student
                    </c:when>
                    <c:otherwise>
                      <c:out value="${complaint.studentName}"/> (Roll: <c:out value="${complaint.rollNumber}"/>)
                    </c:otherwise>
                  </c:choose>
                </strong>
              </div>

              <div>
                <span style="color: var(--color-ink-muted); text-transform: uppercase; font-size: 0.75rem;">Contact Email:</span><br>
                <c:choose>
                  <c:when test="${complaint.anonymous}">
                    <span class="mono" style="color: var(--color-ink-faint);">[Hidden - Anonymous Submission]</span>
                  </c:when>
                  <c:otherwise>
                    <a href="mailto:${complaint.email}" class="mono"><c:out value="${complaint.email}"/></a>
                  </c:otherwise>
                </c:choose>
              </div>

              <div>
                <span style="color: var(--color-ink-muted); text-transform: uppercase; font-size: 0.75rem;">Department:</span><br>
                <strong><c:out value="${complaint.departmentName}"/> (<c:out value="${complaint.departmentCode}"/>)</strong>
              </div>

              <div>
                <span style="color: var(--color-ink-muted); text-transform: uppercase; font-size: 0.75rem;">Category & Priority:</span><br>
                <strong><c:out value="${complaint.category}"/></strong> • 
                <c:choose>
                  <c:when test="${complaint.priority eq 'High'}">
                    <span class="urgent-flag">URGENT</span>
                  </c:when>
                  <c:otherwise>
                    <c:out value="${complaint.priority}"/>
                  </c:otherwise>
                </c:choose>
              </div>

              <div>
                <span style="color: var(--color-ink-muted); text-transform: uppercase; font-size: 0.75rem;">Filing Date & Time:</span><br>
                <span class="mono"><c:out value="${complaint.createdAt}"/></span>
              </div>

              <div>
                <span style="color: var(--color-ink-muted); text-transform: uppercase; font-size: 0.75rem;">Assigned Staff:</span><br>
                <span class="mono"><strong><c:out value="${empty complaint.assignedTo ? 'Unassigned' : complaint.assignedTo}"/></strong></span>
              </div>
            </div>

            <!-- Grievance Statement -->
            <div style="border-top: 1px dashed var(--color-ink-faint); padding-top: 1.25rem; margin-top: 1.25rem;">
              <h4 style="font-family: var(--font-hand); font-size: 1.35rem; color: var(--color-pen-blue); margin-bottom: 0.5rem;">
                Official Complaint Narrative:
              </h4>
              <p style="font-size: 0.95rem; line-height: 1.6; white-space: pre-wrap; background: var(--color-paper-light); padding: 1rem; border: 1px solid rgba(26, 25, 22, 0.15); border-radius: var(--radius-sm);">
                <c:out value="${complaint.description}"/>
              </p>
            </div>

            <!-- Attachment Preview if available -->
            <c:if test="${not empty complaint.attachmentPath}">
              <div style="margin-top: 1.5rem; padding: 1rem; background: var(--color-paper); border: 1px dashed var(--color-ink); border-radius: var(--radius-sm);">
                <div style="font-family: var(--font-mono); font-size: 0.8rem; font-weight: 700; margin-bottom: 0.5rem;">
                  ATTACHED EVIDENCE FILE:
                </div>
                <div style="margin-bottom: 0.5rem;">
                  <img src="${pageContext.request.contextPath}/${complaint.attachmentPath}" alt="Evidence" style="max-width: 100%; max-height: 280px; border: var(--border-thick); border-radius: var(--radius-sm);">
                </div>
                <a href="${pageContext.request.contextPath}/${complaint.attachmentPath}" target="_blank" class="btn btn-secondary btn-sm">
                  Open Full Resolution Image ↗
                </a>
              </div>
            </c:if>

          </div>

          <!-- Chronological Audit Trail Logs -->
          <div class="detail-pane" style="background: var(--color-card);">
            <h3 style="font-size: 1.3rem; margin-bottom: 1rem;">Chronological Status Audit Trail</h3>
            <div class="timeline" style="margin: 0; padding-left: 2rem;">
              <c:forEach items="${logs}" var="log" varStatus="loop">
                <div class="timeline-step ${loop.last ? 'active' : 'completed'}">
                  <div class="timeline-dot"><c:out value="${loop.count}"/></div>
                  <div class="timeline-content">
                    <div class="timeline-title">
                      <span>Status: <strong style="color: var(--color-signal-red);"><c:out value="${log.newStatus}"/></strong></span>
                      <span class="timeline-time"><c:out value="${log.createdAt}"/></span>
                    </div>
                    <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-top: 0.2rem;">
                      By: <strong><c:out value="${log.changedBy}"/></strong>
                      <c:if test="${not empty log.oldStatus}"> (from <c:out value="${log.oldStatus}"/>)</c:if>
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
          </div>

        </div>

        <!-- Right Pane: Administrative Action Station -->
        <div>
          
          <!-- 1. Change Status & Dispatch Live Stamp -->
          <div class="detail-pane" style="margin-bottom: 1.5rem; background: var(--color-card);">
            <h3 style="font-size: 1.25rem; margin-bottom: 1rem;">Update Status & Broadcast</h3>
            <form action="${pageContext.request.contextPath}/admin/complaints/detail" method="POST">
              <input type="hidden" name="csrfToken" value="${csrfToken}">
              <input type="hidden" name="id" value="${complaint.id}">
              <input type="hidden" name="action" value="updateStatus">

              <div class="form-group">
                <label class="form-label" style="font-size: 1rem;">New Status</label>
                <select name="status" class="input-ruled" required>
                  <c:forEach items="${statuses}" var="st">
                    <option value="${st}" ${complaint.status eq st ? 'selected' : ''}><c:out value="${st}"/></option>
                  </c:forEach>
                </select>
              </div>

              <div class="form-group">
                <label class="form-label" style="font-size: 1rem;">Audit Log Remark</label>
                <textarea name="remark" rows="3" class="input-ruled" 
                          placeholder="e.g. Technician dispatched; part ordered from vendor..."></textarea>
                <div class="form-hint">This remark is broadcast live to the student tracking screen.</div>
              </div>

              <button type="submit" class="btn btn-primary btn-block">
                Stamp New Status & Notify Live ➔
              </button>
            </form>
          </div>

          <!-- 2. Staff Assignment -->
          <div class="detail-pane" style="margin-bottom: 1.5rem; background: var(--color-card);">
            <h3 style="font-size: 1.25rem; margin-bottom: 1rem;">Assign Officer / Technician</h3>
            <form action="${pageContext.request.contextPath}/admin/complaints/detail" method="POST">
              <input type="hidden" name="csrfToken" value="${csrfToken}">
              <input type="hidden" name="id" value="${complaint.id}">
              <input type="hidden" name="action" value="assignStaff">

              <div class="form-group">
                <input type="text" name="assignedTo" value="<c:out value='${complaint.assignedTo}'/>" 
                       placeholder="e.g. Prof. K. Rao / Electrician Dept." class="input-ruled" required>
              </div>

              <button type="submit" class="btn btn-secondary btn-sm btn-block">
                Assign Staff Member
              </button>
            </form>
          </div>

          <!-- 3. Internal Notes (Private to Administrators) -->
          <div class="detail-pane" style="margin-bottom: 1.5rem; background: var(--color-card);">
            <h3 style="font-size: 1.25rem; margin-bottom: 1rem;">Internal Confidential Notes</h3>
            <form action="${pageContext.request.contextPath}/admin/complaints/detail" method="POST">
              <input type="hidden" name="csrfToken" value="${csrfToken}">
              <input type="hidden" name="id" value="${complaint.id}">
              <input type="hidden" name="action" value="updateNotes">

              <div class="form-group">
                <textarea name="internalNotes" rows="3" class="input-ruled" 
                          placeholder="Private notes visible only to logged-in admins..."><c:out value="${complaint.internalNotes}"/></textarea>
              </div>

              <button type="submit" class="btn btn-secondary btn-sm btn-block">
                Save Internal Notes
              </button>
            </form>
          </div>

          <!-- 4. Public Wall Visibility Toggle -->
          <div class="detail-pane" style="margin-bottom: 1.5rem; background: var(--color-card);">
            <h3 style="font-size: 1.25rem; margin-bottom: 0.5rem;">Public Transparency Wall</h3>
            <p style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-bottom: 1rem;">
              Allow this resolved issue to be displayed on the public notice board as an anonymized case study.
            </p>
            <form action="${pageContext.request.contextPath}/admin/complaints/detail" method="POST">
              <input type="hidden" name="csrfToken" value="${csrfToken}">
              <input type="hidden" name="id" value="${complaint.id}">
              <input type="hidden" name="action" value="togglePublic">
              <input type="hidden" name="isPublic" value="${!complaint.publicCase}">

              <c:choose>
                <c:when test="${complaint.publicCase}">
                  <button type="submit" class="btn btn-secondary btn-sm btn-block" style="border-color: var(--color-board-green); color: var(--color-board-green);">
                    ✔ Currently Visible on Public Wall (Click to Hide)
                  </button>
                </c:when>
                <c:otherwise>
                  <button type="submit" class="btn btn-secondary btn-sm btn-block">
                    + Pin to Public Wall
                  </button>
                </c:otherwise>
              </c:choose>
            </form>
          </div>

          <!-- 5. Danger Zone: Soft Delete -->
          <div class="detail-pane" style="background: var(--color-signal-red-light); border-color: var(--color-signal-red);">
            <h3 style="font-size: 1.25rem; color: #901B00; margin-bottom: 0.5rem;">Archive Docket</h3>
            <p style="font-family: var(--font-mono); font-size: 0.75rem; color: #901B00; margin-bottom: 1rem;">
              Soft delete will archive this complaint from active search and tables while retaining audit integrity.
            </p>
            <form action="${pageContext.request.contextPath}/admin/complaints/detail" method="POST" 
                  onsubmit="return confirm('Are you sure you want to archive and soft-delete complaint ${complaint.trackingId}?');">
              <input type="hidden" name="csrfToken" value="${csrfToken}">
              <input type="hidden" name="id" value="${complaint.id}">
              <input type="hidden" name="action" value="delete">

              <button type="submit" class="btn btn-danger btn-sm btn-block">
                Archive & Soft-Delete Slip
              </button>
            </form>
          </div>

        </div>

      </div>

    </main>

  </div>

</body>
</html>
