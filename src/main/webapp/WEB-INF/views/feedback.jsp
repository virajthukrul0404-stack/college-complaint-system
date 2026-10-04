<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>Student Feedback - Campus Notice Board</title>
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
        <li><a href="${pageContext.request.contextPath}/complaint/track" class="nav-link">Track Slip</a></li>
        <li><a href="${pageContext.request.contextPath}/feedback" class="nav-link active">Feedback</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-admin-btn">Office Desk ➔</a></li>
      </ul>
    </div>
  </header>

  <main class="container" style="padding: 3rem 1.5rem; max-width: 960px;">

    <div style="margin-bottom: 2rem;">
      <span class="handwritten" style="font-size: 1.5rem;">Academic & Campus Quality</span>
      <h1 style="font-size: 2.4rem;">Student Feedback Box</h1>
      <p style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-ink-muted);">
        Rate college departments, labs, and campus amenities. Stored independently of individual complaints.
      </p>
    </div>

    <!-- Success Message -->
    <c:if test="${param.success eq 'true'}">
      <div class="alert alert-success">
        <strong>Feedback Pinned!</strong> Thank you for your review. Your ratings help improve campus facilities.
      </div>
    </c:if>

    <!-- General Error Banner -->
    <c:if test="${not empty errors.general}">
      <div class="alert alert-danger">
        <strong>Error:</strong> <c:out value="${errors.general}"/>
      </div>
    </c:if>

    <div style="display: grid; grid-template-columns: 1.2fr 0.8fr; gap: 2rem; align-items: flex-start;">

      <!-- Feedback Form Card -->
      <div class="paper-card ruled-lines exercise-margin" style="background-color: var(--color-paper-light); padding: 2rem; position: relative;">
        <div class="tape-top"></div>

        <form action="${pageContext.request.contextPath}/feedback" method="POST">
          <input type="hidden" name="csrfToken" value="${csrfToken}">

          <div class="form-group">
            <label for="departmentId" class="form-label">1. Department / Facility</label>
            <select id="departmentId" name="departmentId" class="input-ruled" required>
              <option value="">-- Choose Department --</option>
              <c:forEach items="${departments}" var="dept">
                <option value="${dept.id}" ${feedback.departmentId == dept.id ? 'selected' : ''}>
                  <c:out value="${dept.name}"/> (<c:out value="${dept.code}"/>)
                </option>
              </c:forEach>
            </select>
            <c:if test="${not empty errors.departmentId}">
              <span class="inline-error"><c:out value="${errors.departmentId}"/></span>
            </c:if>
          </div>

          <div class="form-group">
            <label for="category" class="form-label">2. Category</label>
            <input type="text" id="category" name="category" class="input-ruled" 
                   value="<c:out value='${feedback.category}'/>" placeholder="e.g. Lab Facilities, Library, Teaching, Canteen" required>
            <c:if test="${not empty errors.category}">
              <span class="inline-error"><c:out value="${errors.category}"/></span>
            </c:if>
          </div>

          <!-- 1 to 5 Star Rating Chips -->
          <div class="form-group">
            <label class="form-label">3. Overall Rating (1 to 5 Stars)</label>
            <div style="display: flex; gap: 0.75rem; margin-top: 0.5rem;">
              <c:forEach begin="1" end="5" var="r">
                <label style="flex: 1; border: var(--border-thick); padding: 0.5rem; text-align: center; border-radius: var(--radius-sm); background: #FFF; cursor: pointer; box-shadow: 2px 2px 0 var(--color-ink); font-family: var(--font-mono); font-weight: 700;">
                  <input type="radio" name="rating" value="${r}" ${feedback.rating == r || (empty feedback.rating && r == 5) ? 'checked' : ''} style="margin-right: 2px;">
                  <span>${r} ★</span>
                </label>
              </c:forEach>
            </div>
            <c:if test="${not empty errors.rating}">
              <span class="inline-error"><c:out value="${errors.rating}"/></span>
            </c:if>
          </div>

          <div class="form-group">
            <label for="comment" class="form-label">4. Your Observations & Comments</label>
            <textarea id="comment" name="comment" rows="4" class="input-ruled" 
                      placeholder="What is working well? What needs improvement?" required><c:out value="${feedback.comment}"/></textarea>
            <c:if test="${not empty errors.comment}">
              <span class="inline-error"><c:out value="${errors.comment}"/></span>
            </c:if>
          </div>

          <div style="margin-top: 2rem;">
            <button type="submit" class="btn btn-primary btn-block">
              Submit Review Slip
            </button>
          </div>
        </form>
      </div>

      <!-- Department Rating Summary Slips -->
      <div>
        <div class="paper-card" style="margin-bottom: 1.5rem; background: var(--color-card);">
          <h3 style="font-size: 1.25rem; border-bottom: 2px dashed var(--color-ink-faint); padding-bottom: 0.5rem; margin-bottom: 1rem;">
            Campus Rating Pulse
          </h3>
          <div style="font-family: var(--font-mono); font-size: 2.2rem; font-weight: 800; color: var(--color-signal-red);">
            <c:out value="${overallAvg}"/> <span style="font-size: 1.2rem; color: #1A1916;">/ 5.0 ★</span>
          </div>
          <div style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted); margin-bottom: 1.25rem;">
            Overall Campus Satisfaction Score
          </div>

          <div style="display: flex; flex-direction: column; gap: 0.75rem; font-family: var(--font-mono); font-size: 0.85rem;">
            <c:forEach items="${deptAverages}" var="entry">
              <div style="display: flex; justify-content: space-between; border-bottom: 1px dashed rgba(26, 25, 22, 0.15); padding-bottom: 0.35rem;">
                <span style="font-weight: 600;"><c:out value="${entry.key}"/></span>
                <span style="font-weight: 800; color: var(--color-pen-blue);"><c:out value="${entry.value}"/> ★</span>
              </div>
            </c:forEach>
          </div>
        </div>

        <!-- Recent Reviews Snippet -->
        <div style="font-family: var(--font-mono); font-size: 0.8rem; color: var(--color-ink-muted); margin-bottom: 0.5rem;">
          RECENT ANONYMOUS REVIEWS:
        </div>
        <c:forEach items="${recentFeedback}" var="rf" end="2">
          <div class="paper-card" style="margin-bottom: 0.75rem; padding: 1rem; font-size: 0.85rem; transform: rotate(-0.5deg);">
            <div style="display: flex; justify-content: space-between; font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-muted);">
              <strong><c:out value="${rf.departmentName}"/></strong>
              <span style="color: var(--color-signal-red); font-weight: 800;"><c:out value="${rf.rating}"/> ★</span>
            </div>
            <p style="margin-top: 0.35rem; font-style: italic;">"<c:out value="${rf.comment}"/>"</p>
          </div>
        </c:forEach>
      </div>

    </div>

  </main>

  <footer class="site-footer">
    <div class="container footer-inner">
      <div>Campus Complaint & Feedback Registry • Advanced Java</div>
      <div class="footer-stamp">STUDENT FEEDBACK DESK</div>
    </div>
  </footer>

</body>
</html>
