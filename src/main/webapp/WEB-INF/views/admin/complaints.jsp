<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>Complaints Ledger - Office Desk</title>
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
          <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-left: 0.5rem;">[OFFICIAL USE ONLY]</span>
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

    <!-- Main Desk Ledger Content -->
    <main class="admin-main">

      <div class="desk-header">
        <div>
          <h1 class="desk-title">Master Complaints Register</h1>
          <p style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-ink-muted);">
            Official ledger of student grievances. Showing <c:out value="${complaints.size()}"/> of <c:out value="${totalCount}"/> records.
          </p>
        </div>

        <div>
          <!-- Export CSV with current filters -->
          <a href="${pageContext.request.contextPath}/admin/export?search=${search}&status=${selectedStatus}&departmentId=${selectedDepartmentId}&category=${selectedCategory}&priority=${selectedPriority}&startDate=${startDate}&endDate=${endDate}&sortBy=${sortBy}&sortOrder=${sortOrder}" 
             class="btn btn-secondary btn-sm">
            📥 Export Filtered CSV
          </a>
        </div>
      </div>

      <c:if test="${param.deleted eq 'true'}">
        <div class="alert alert-success" style="font-size: 0.85rem;">
          Complaint soft-deleted successfully and archived from the active register.
        </div>
      </c:if>

      <!-- Search & Multi-Filter Bar -->
      <div class="filter-bar">
        <form action="${pageContext.request.contextPath}/admin/complaints" method="GET">
          <div class="filter-grid">
            
            <!-- Search Text -->
            <div style="grid-column: span 2;">
              <label class="form-label" style="font-size: 1rem;">Search Keyword</label>
              <input type="text" name="search" value="<c:out value='${search}'/>" 
                     placeholder="ID, subject, roll no, name..." class="input-ruled mono">
            </div>

            <!-- Department Filter -->
            <div>
              <label class="form-label" style="font-size: 1rem;">Department</label>
              <select name="departmentId" class="input-ruled">
                <option value="">All Departments</option>
                <c:forEach items="${departments}" var="dept">
                  <option value="${dept.id}" ${selectedDepartmentId == dept.id ? 'selected' : ''}>
                    <c:out value="${dept.code}"/> - <c:out value="${dept.name}"/>
                  </option>
                </c:forEach>
              </select>
            </div>

            <!-- Status Filter -->
            <div>
              <label class="form-label" style="font-size: 1rem;">Status</label>
              <select name="status" class="input-ruled">
                <option value="">All Statuses</option>
                <c:forEach items="${statuses}" var="st">
                  <option value="${st}" ${selectedStatus eq st ? 'selected' : ''}><c:out value="${st}"/></option>
                </c:forEach>
              </select>
            </div>

            <!-- Priority Filter -->
            <div>
              <label class="form-label" style="font-size: 1rem;">Priority</label>
              <select name="priority" class="input-ruled">
                <option value="">All Priorities</option>
                <c:forEach items="${priorities}" var="pr">
                  <option value="${pr}" ${selectedPriority eq pr ? 'selected' : ''}><c:out value="${pr}"/></option>
                </c:forEach>
              </select>
            </div>

            <!-- Category Filter -->
            <div>
              <label class="form-label" style="font-size: 1rem;">Category</label>
              <select name="category" class="input-ruled">
                <option value="">All Categories</option>
                <c:forEach items="${categories}" var="cat">
                  <option value="${cat}" ${selectedCategory eq cat ? 'selected' : ''}><c:out value="${cat}"/></option>
                </c:forEach>
              </select>
            </div>

            <!-- Date Range -->
            <div>
              <label class="form-label" style="font-size: 1rem;">From Date</label>
              <input type="date" name="startDate" value="<c:out value='${startDate}'/>" class="input-ruled mono">
            </div>

            <div>
              <label class="form-label" style="font-size: 1rem;">To Date</label>
              <input type="date" name="endDate" value="<c:out value='${endDate}'/>" class="input-ruled mono">
            </div>

            <!-- Sort By -->
            <div>
              <label class="form-label" style="font-size: 1rem;">Sort Order</label>
              <select name="sortBy" class="input-ruled">
                <option value="created_at" ${sortBy eq 'created_at' ? 'selected' : ''}>Date Filed</option>
                <option value="priority" ${sortBy eq 'priority' ? 'selected' : ''}>Priority</option>
                <option value="status" ${sortBy eq 'status' ? 'selected' : ''}>Status</option>
                <option value="trackingId" ${sortBy eq 'trackingId' ? 'selected' : ''}>Tracking ID</option>
              </select>
            </div>

            <!-- Action Buttons -->
            <div style="display: flex; gap: 0.5rem;">
              <button type="submit" class="btn btn-primary btn-sm" style="flex: 1;">Filter</button>
              <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-secondary btn-sm">Reset</a>
            </div>

          </div>
        </form>
      </div>

      <!-- Ledger Table -->
      <div class="ledger-wrapper">
        <table class="ledger-table">
          <thead>
            <tr>
              <th class="col-margin"></th>
              <th>Tracking ID</th>
              <th>Date Filed</th>
              <th>Complainant</th>
              <th>Department</th>
              <th>Category</th>
              <th>Priority</th>
              <th>Subject</th>
              <th>Status Stamp</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty complaints}">
                <tr>
                  <td class="col-margin"></td>
                  <td colspan="9" style="text-align: center; padding: 3rem; font-family: var(--font-hand); font-size: 1.5rem; color: var(--color-ink-muted);">
                    No complaints match the specified search or filter criteria in this register.
                  </td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach items="${complaints}" var="c">
                  <tr>
                    <td class="col-margin"></td>
                    <td class="mono" style="font-weight: 700; color: var(--color-signal-red);">
                      <c:out value="${c.trackingId}"/>
                    </td>
                    <td class="mono" style="font-size: 0.75rem; white-space: nowrap;">
                      <c:out value="${fn:substring(c.createdAt, 0, 10)}"/>
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${c.anonymous}">
                          <span style="font-style: italic; color: var(--color-ink-muted);">Anonymous</span>
                        </c:when>
                        <c:otherwise>
                          <c:out value="${c.studentName}"/><br>
                          <span class="mono" style="font-size: 0.7rem; color: var(--color-ink-muted);"><c:out value="${c.rollNumber}"/></span>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td><c:out value="${c.departmentCode}"/></td>
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
                    <td style="max-width: 220px; font-weight: 500;">
                      <c:out value="${c.subject}"/>
                    </td>
                    <td>
                      <span class="stamp stamp-${fn:toLowerCase(fn:replace(c.status, ' ', '-'))}" style="font-size: 0.7rem; padding: 0.15rem 0.5rem;">
                        <c:out value="${c.status}"/>
                      </span>
                    </td>
                    <td>
                      <a href="${pageContext.request.contextPath}/admin/complaints/detail?id=${c.id}" class="btn btn-secondary btn-sm" style="padding: 0.25rem 0.6rem; font-size: 0.75rem;">
                        Inspect ➔
                      </a>
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <c:if test="${totalPages > 1}">
        <div class="pagination">
          <div>
            Page <strong><c:out value="${currentPage}"/></strong> of <strong><c:out value="${totalPages}"/></strong> (<c:out value="${totalCount}"/> total entries)
          </div>
          <div class="page-buttons">
            <c:if test="${currentPage > 1}">
              <a href="${pageContext.request.contextPath}/admin/complaints?page=${currentPage - 1}&search=${search}&status=${selectedStatus}&departmentId=${selectedDepartmentId}&category=${selectedCategory}&priority=${selectedPriority}&startDate=${startDate}&endDate=${endDate}&sortBy=${sortBy}&sortOrder=${sortOrder}" 
                 class="page-btn">← Prev</a>
            </c:if>

            <c:forEach begin="1" end="${totalPages}" var="p">
              <c:if test="${p == 1 || p == totalPages || (p >= currentPage - 2 && p <= currentPage + 2)}">
                <a href="${pageContext.request.contextPath}/admin/complaints?page=${p}&search=${search}&status=${selectedStatus}&departmentId=${selectedDepartmentId}&category=${selectedCategory}&priority=${selectedPriority}&startDate=${startDate}&endDate=${endDate}&sortBy=${sortBy}&sortOrder=${sortOrder}" 
                   class="page-btn ${p == currentPage ? 'active' : ''}">
                  ${p}
                </a>
              </c:if>
            </c:forEach>

            <c:if test="${currentPage < totalPages}">
              <a href="${pageContext.request.contextPath}/admin/complaints?page=${currentPage + 1}&search=${search}&status=${selectedStatus}&departmentId=${selectedDepartmentId}&category=${selectedCategory}&priority=${selectedPriority}&startDate=${startDate}&endDate=${endDate}&sortBy=${sortBy}&sortOrder=${sortOrder}" 
                 class="page-btn">Next →</a>
            </c:if>
          </div>
        </div>
      </c:if>

    </main>

  </div>

</body>
</html>
