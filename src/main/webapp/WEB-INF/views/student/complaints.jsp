<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="#1F5D4A">
  <title>My Complaints Register • Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
  <style>
    .complaint-card-slip {
      background: var(--color-card);
      border: var(--border-thick);
      box-shadow: var(--shadow-hard);
      padding: 1rem 1.2rem;
      margin-bottom: 1rem;
      position: relative;
      cursor: pointer;
      transition: all 0.15s ease;
      text-decoration: none;
      display: block;
      color: var(--color-ink);
    }
    .complaint-card-slip:hover {
      box-shadow: 2px 2px 0 var(--color-ink);
      transform: translate(2px, 2px);
    }
    .complaint-card-slip.selected-docket {
      border-color: var(--color-board-green);
      background: var(--color-board-green-light);
    }
    /* Timeline list inside detail panel */
    .timeline-item {
      position: relative;
      padding-left: 1.5rem;
      margin-bottom: 1rem;
      border-left: 2px dashed var(--color-ink-muted);
    }
    .timeline-dot {
      position: absolute;
      left: -6px;
      top: 2px;
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background: var(--color-board-green);
      border: 1px solid var(--color-ink);
    }
  </style>
</head>
<body>

  <div class="student-shell">

    <!-- Desktop Navigation Header -->
    <%@ include file="nav_desktop.jspf" %>

    <!-- Mobile Header -->
    <header class="site-header" style="display: block; border-bottom: var(--border-thick);">
      <div class="container" style="display: flex; justify-content: space-between; align-items: center; padding: 0.6rem 1rem;">
        <span style="font-family: var(--font-display); font-weight: 800; font-size: 1.1rem;">My Grievance Slips</span>
        <a href="${pageContext.request.contextPath}/student/complaint/new" class="btn btn-primary btn-sm">+ New</a>
      </div>
    </header>

    <main class="slip-container">

      <!-- Search & Filters Toolbar -->
      <div class="pinned-slip" style="padding: 1rem; margin-bottom: 1.5rem;">
        <form action="${pageContext.request.contextPath}/student/complaints" method="GET" style="display: flex; gap: 0.75rem; flex-wrap: wrap; align-items: center;">
          <div style="flex: 1; min-width: 220px; position: relative;">
            <input type="text"
                   id="search-complaints-box"
                   name="search"
                   class="input-mobile"
                   value="<c:out value='${search}'/>"
                   placeholder="Search by tracking ID, subject, category... [/]"
                   style="width: 100%; padding: 0.65rem 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-mono); font-size: 0.88rem;" />
          </div>

          <div style="min-width: 160px;">
            <select name="status" class="input-mobile" onchange="this.form.submit()" style="width: 100%; padding: 0.65rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body); font-size: 0.88rem;">
              <option value="">All Statuses (${totalComplaints})</option>
              <option value="Submitted" ${status eq 'Submitted' ? 'selected' : ''}>Submitted</option>
              <option value="Under Review" ${status eq 'Under Review' ? 'selected' : ''}>Under Review</option>
              <option value="In Progress" ${status eq 'In Progress' ? 'selected' : ''}>In Progress</option>
              <option value="Resolved" ${status eq 'Resolved' ? 'selected' : ''}>Resolved</option>
              <option value="Rejected" ${status eq 'Rejected' ? 'selected' : ''}>Rejected</option>
            </select>
          </div>

          <button type="submit" class="btn btn-primary btn-sm" style="min-height: 44px; padding: 0 1.25rem;">
            Search
          </button>

          <c:if test="${not empty search or not empty status}">
            <a href="${pageContext.request.contextPath}/student/complaints" class="btn btn-secondary btn-sm" style="min-height: 44px; display: inline-flex; align-items: center;">
              Clear
            </a>
          </c:if>
        </form>
      </div>

      <!-- Master-Detail Layout on Desktop (>= 1025px) / Stacked List on Mobile -->
      <div class="master-detail-shell">

        <!-- Column 1: Complaints Scroll List / Grid -->
        <div class="complaints-scroll-column">
          <c:choose>
            <c:when test="${empty complaints}">
              <div class="pinned-slip" style="text-align: center; padding: 3rem 1rem;">
                <p style="color: var(--color-ink-muted); font-size: 1rem; margin-bottom: 1rem;">
                  No complaints found matching your criteria.
                </p>
                <a href="${pageContext.request.contextPath}/student/complaint/new" class="btn btn-primary">
                  Pin a Complaint Slip ➔
                </a>
              </div>
            </c:when>
            <c:otherwise>
              <c:forEach var="c" items="${complaints}">
                <a href="${pageContext.request.contextPath}/student/complaints?id=${c.id}&status=<c:out value='${status}'/>&search=<c:out value='${search}'/>"
                   class="complaint-card-slip ${selectedComplaint != null and selectedComplaint.id == c.id ? 'selected-docket' : ''}">
                  <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.35rem;">
                    <span class="mono" style="font-weight: 700; font-size: 0.85rem; color: var(--color-pen-blue);">
                      <c:out value="${c.trackingId}"/>
                    </span>
                    <c:choose>
                      <c:when test="${c.status eq 'Resolved'}">
                        <span class="stamp stamp-resolved" style="font-size: 0.68rem; padding: 1px 5px;">RESOLVED</span>
                      </c:when>
                      <c:when test="${c.status eq 'In Progress'}">
                        <span class="stamp stamp-inprogress" style="font-size: 0.68rem; padding: 1px 5px;">IN PROGRESS</span>
                      </c:when>
                      <c:when test="${c.status eq 'Under Review'}">
                        <span class="stamp stamp-review" style="font-size: 0.68rem; padding: 1px 5px;">UNDER REVIEW</span>
                      </c:when>
                      <c:when test="${c.status eq 'Rejected'}">
                        <span class="stamp stamp-rejected" style="font-size: 0.68rem; padding: 1px 5px;">REJECTED</span>
                      </c:when>
                      <c:otherwise>
                        <span class="stamp stamp-submitted" style="font-size: 0.68rem; padding: 1px 5px;">SUBMITTED</span>
                      </c:otherwise>
                    </c:choose>
                  </div>

                  <h3 style="font-family: var(--font-display); font-size: 1.08rem; margin: 0.25rem 0 0.4rem 0; line-height: 1.25;">
                    <c:out value="${c.subject}"/>
                  </h3>

                  <div style="display: flex; justify-content: space-between; align-items: center; font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted);">
                    <span><c:out value="${c.category}"/> • <c:out value="${c.priority}"/></span>
                    <span><c:out value="${c.createdAt}"/></span>
                  </div>

                  <!-- On mobile, direct link to full detail page -->
                  <div style="margin-top: 0.5rem; text-align: right; display: block;" class="mobile-only-link">
                    <span class="mono" style="font-size: 0.75rem; font-weight: 700; color: var(--color-board-green);">Open full docket ➔</span>
                  </div>
                </a>
              </c:forEach>
            </c:otherwise>
          </c:choose>

          <!-- Pagination -->
          <c:if test="${totalPages > 1}">
            <div style="display: flex; justify-content: center; gap: 0.5rem; margin: 1.5rem 0;">
              <c:forEach begin="1" end="${totalPages}" var="p">
                <a href="${pageContext.request.contextPath}/student/complaints?page=${p}&status=<c:out value='${status}'/>&search=<c:out value='${search}'/>"
                   class="btn ${currentPage == p ? 'btn-primary' : 'btn-secondary'} btn-sm">
                  ${p}
                </a>
              </c:forEach>
            </div>
          </c:if>
        </div>

        <!-- Column 2: Desktop Detail Panel (Side-by-Side Master-Detail on >= 1025px) -->
        <div class="complaint-detail-column" style="display: none;">
          <c:if test="${not empty selectedComplaint}">
            <div class="pinned-slip" style="padding: 1.75rem; position: relative;">
              <div class="tape-pin"></div>

              <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem; border-bottom: 1px dashed var(--color-ink-faint); padding-bottom: 0.75rem;">
                <div>
                  <span class="notice-tag">Selected Docket</span>
                  <div class="mono" style="font-size: 1.2rem; font-weight: 800; color: var(--color-pen-blue); margin-top: 4px;">
                    <c:out value="${selectedComplaint.trackingId}"/>
                  </div>
                </div>
                <div style="text-align: right;">
                  <c:choose>
                    <c:when test="${selectedComplaint.status eq 'Resolved'}">
                      <span class="stamp stamp-resolved">RESOLVED</span>
                    </c:when>
                    <c:when test="${selectedComplaint.status eq 'In Progress'}">
                      <span class="stamp stamp-inprogress">IN PROGRESS</span>
                    </c:when>
                    <c:when test="${selectedComplaint.status eq 'Under Review'}">
                      <span class="stamp stamp-review">UNDER REVIEW</span>
                    </c:when>
                    <c:when test="${selectedComplaint.status eq 'Rejected'}">
                      <span class="stamp stamp-rejected">REJECTED</span>
                    </c:when>
                    <c:otherwise>
                      <span class="stamp stamp-submitted">SUBMITTED</span>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>

              <h2 style="font-family: var(--font-display); font-size: 1.35rem; margin-bottom: 0.75rem;">
                <c:out value="${selectedComplaint.subject}"/>
              </h2>

              <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; margin-bottom: 1.25rem; font-family: var(--font-mono); font-size: 0.8rem; background: var(--color-paper-light); padding: 0.75rem; border: var(--border-thin);">
                <div><strong>Category:</strong> <c:out value="${selectedComplaint.category}"/></div>
                <div><strong>Priority:</strong> <c:out value="${selectedComplaint.priority}"/></div>
                <div><strong>Department:</strong> <c:out value="${selectedComplaint.departmentName}"/></div>
                <div><strong>Visibility:</strong> <c:out value="${selectedComplaint.anonymous ? 'Anonymous (Identity Masked)' : 'Standard Student'}"/></div>
              </div>

              <div style="margin-bottom: 1.25rem;">
                <strong style="font-family: var(--font-mono); font-size: 0.82rem; text-transform: uppercase;">Problem Description:</strong>
                <p style="white-space: pre-wrap; font-size: 0.92rem; line-height: 1.5; margin-top: 0.4rem; padding: 0.75rem; background: white; border: 1px solid var(--color-ink-faint);">
                  <c:out value="${selectedComplaint.description}"/>
                </p>
              </div>

              <c:if test="${not empty selectedComplaint.publicRemark}">
                <div style="margin-bottom: 1.25rem; padding: 0.75rem; background: var(--color-board-green-light); border-left: 4px solid var(--color-board-green);">
                  <strong style="font-family: var(--font-mono); font-size: 0.82rem; color: var(--color-board-green);">Official Staff Resolution Note:</strong>
                  <p style="margin-top: 0.35rem; font-size: 0.9rem; line-height: 1.4;">
                    <c:out value="${selectedComplaint.publicRemark}"/>
                  </p>
                </div>
              </c:if>

              <!-- Audit Timeline -->
              <h4 style="font-family: var(--font-mono); font-size: 0.82rem; text-transform: uppercase; margin-bottom: 0.75rem;">
                Audit Trail & Status History
              </h4>
              <div style="margin-top: 0.5rem;">
                <c:forEach var="log" items="${selectedLogs}">
                  <div class="timeline-item">
                    <div class="timeline-dot"></div>
                    <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted);">
                      <c:out value="${log.createdAt}"/> • by <c:out value="${log.changedBy}"/>
                    </div>
                    <div style="font-weight: 700; font-size: 0.88rem; margin: 2px 0;">
                      <c:out value="${log.newStatus}"/>
                    </div>
                    <c:if test="${not empty log.remark}">
                      <div style="font-size: 0.82rem; color: var(--color-ink); font-style: italic;">
                        "<c:out value="${log.remark}"/>"
                      </div>
                    </c:if>
                  </div>
                </c:forEach>
              </div>

              <div style="margin-top: 1.5rem; text-align: right;">
                <a href="${pageContext.request.contextPath}/student/complaints/detail?id=${selectedComplaint.id}" class="btn btn-secondary btn-sm">
                  Open Standalone Tracker ➔
                </a>
              </div>
            </div>
          </c:if>
        </div>

      </div>

    </main>

    <!-- Mobile Bottom Navigation -->
    <%@ include file="nav_mobile.jspf" %>

  </div>

  <style>
    @media (min-width: 1025px) {
      .complaint-detail-column {
        display: block !important;
      }
      .mobile-only-link {
        display: none !important;
      }
    }
  </style>

</body>
</html>
