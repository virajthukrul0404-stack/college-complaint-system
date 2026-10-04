<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="theme-color" content="#1F5D4A">
  <title>Student Feedback • Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/device.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/student.css">
  <style>
    .star-rating-row {
      display: flex;
      flex-direction: row-reverse;
      justify-content: flex-end;
      gap: 0.5rem;
    }
    .star-rating-row input {
      display: none;
    }
    .star-rating-row label {
      font-size: 2rem;
      color: #d1c8b9;
      cursor: pointer;
      min-width: 44px;
      min-height: 44px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: color 0.15s ease;
    }
    .star-rating-row input:checked ~ label,
    .star-rating-row label:hover,
    .star-rating-row label:hover ~ label {
      color: #E5A812;
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
        <span style="font-family: var(--font-display); font-weight: 800; font-size: 1.1rem;">Campus Feedback</span>
        <a href="${pageContext.request.contextPath}/student/home" class="mono" style="font-size: 0.8rem;">Desk ➔</a>
      </div>
    </header>

    <main class="slip-container" style="max-width: 800px;">

      <c:if test="${param.submitted eq 'true'}">
        <div class="alert alert-success" style="margin-bottom: 1.25rem;">
          ✓ Thank you! Your feedback has been pinned to the departmental review ledger.
        </div>
      </c:if>

      <c:if test="${not empty errors}">
        <div class="alert alert-danger" style="margin-bottom: 1.25rem;">
          <ul style="margin: 0; padding-left: 1.25rem;">
            <c:forEach var="err" items="${errors}">
              <li><c:out value="${err.value}"/></li>
            </c:forEach>
          </ul>
        </div>
      </c:if>

      <!-- Feedback Submission Form -->
      <div class="pinned-slip" style="padding: 1.75rem; margin-bottom: 2rem;">
        <div class="tape-pin"></div>
        <span class="notice-tag">Student Voice</span>
        <h1 style="font-family: var(--font-display); font-size: 1.5rem; margin: 0.25rem 0 0.5rem 0;">
          Departmental Feedback Slip
        </h1>
        <p style="color: var(--color-ink-muted); font-size: 0.88rem; margin-bottom: 1.5rem;">
          Rate campus labs, academic facilities, library timings, or general administration.
        </p>

        <form action="${pageContext.request.contextPath}/student/feedback" method="POST">
          <input type="hidden" name="csrfToken" value="${csrfToken}">

          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.25rem;">
            <div class="form-group">
              <label for="departmentId" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
                Department *
              </label>
              <select id="departmentId" name="departmentId" class="input-mobile" required style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body);">
                <c:forEach var="d" items="${departments}">
                  <option value="${d.id}" ${student.departmentId == d.id ? 'selected' : ''}>
                    <c:out value="${d.name}"/>
                  </option>
                </c:forEach>
              </select>
            </div>

            <div class="form-group">
              <label for="category" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
                Facility Area *
              </label>
              <select id="category" name="category" class="input-mobile" required style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body);">
                <option value="Lab Facilities">Lab Facilities & Hardware</option>
                <option value="Faculty Guidance">Faculty & Tutorials</option>
                <option value="WiFi & Network">WiFi & Network Infrastructure</option>
                <option value="Library Timings">Library & Study Halls</option>
                <option value="Cafeteria Quality">Cafeteria & Mess</option>
                <option value="Campus Cleanliness">Campus Cleanliness</option>
                <option value="Administrative Office">Administration & Certificates</option>
                <option value="Other">Other</option>
              </select>
            </div>
          </div>

          <div class="form-group" style="margin-bottom: 1.25rem;">
            <label style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase; display: block; margin-bottom: 0.4rem;">
              Overall Rating (1 to 5 Stars) *
            </label>
            <div class="star-rating-row">
              <input type="radio" id="star5" name="rating" value="5" />
              <label for="star5" title="5 stars">★</label>
              <input type="radio" id="star4" name="rating" value="4" />
              <label for="star4" title="4 stars">★</label>
              <input type="radio" id="star3" name="rating" value="3" checked />
              <label for="star3" title="3 stars">★</label>
              <input type="radio" id="star2" name="rating" value="2" />
              <label for="star2" title="2 stars">★</label>
              <input type="radio" id="star1" name="rating" value="1" />
              <label for="star1" title="1 star">★</label>
            </div>
          </div>

          <div class="form-group" style="margin-bottom: 1.5rem;">
            <label for="comment" style="font-family: var(--font-mono); font-weight: 700; font-size: 0.8rem; text-transform: uppercase;">
              Constructive Review & Comments *
            </label>
            <textarea id="comment"
                      name="comment"
                      class="input-mobile"
                      rows="4"
                      required
                      placeholder="Share what worked well and what could be improved..."
                      style="width: 100%; padding: 0.75rem; border: var(--border-thin); background: var(--color-paper-light); font-family: var(--font-body);"></textarea>
          </div>

          <button type="submit" class="btn btn-primary" style="min-height: 48px; width: 100%;">
            Submit Departmental Feedback ➔
          </button>
        </form>
      </div>

      <!-- Past Feedback Reviews by this Student -->
      <c:if test="${not empty pastFeedbacks}">
        <h2 style="font-family: var(--font-display); font-size: 1.25rem; margin-bottom: 0.75rem;">
          My Submitted Feedback History
        </h2>
        <div style="display: flex; flex-direction: column; gap: 0.75rem; margin-bottom: 2rem;">
          <c:forEach var="fb" items="${pastFeedbacks}">
            <div class="pinned-slip" style="padding: 1rem 1.25rem; margin-bottom: 0;">
              <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 0.25rem;">
                <strong style="font-size: 0.95rem;"><c:out value="${fb.departmentName}"/> • <c:out value="${fb.category}"/></strong>
                <span style="color: #E5A812; font-size: 1.1rem; letter-spacing: 2px;">
                  <c:forEach begin="1" end="${fb.rating}">★</c:forEach>
                </span>
              </div>
              <p style="margin: 0.25rem 0 0.5rem 0; font-size: 0.88rem; color: var(--color-ink); line-height: 1.4;">
                "<c:out value="${fb.comment}"/>"
              </p>
              <div class="mono" style="font-size: 0.72rem; color: var(--color-ink-muted);">
                <c:out value="${fb.createdAt}"/>
              </div>
            </div>
          </c:forEach>
        </div>
      </c:if>

    </main>

    <!-- Mobile Bottom Navigation -->
    <%@ include file="nav_mobile.jspf" %>

  </div>

</body>
</html>
