<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>Campus Notice Board & Complaint Registry</title>
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
        <li><a href="${pageContext.request.contextPath}/home" class="nav-link active">The Board</a></li>
        <li><a href="${pageContext.request.contextPath}/complaint/track" class="nav-link">Track Ticket</a></li>
        <c:choose>
          <c:when test="${not empty sessionScope.studentUser}">
            <li><a href="${pageContext.request.contextPath}/student/home" class="btn btn-primary btn-sm">My Desk (<c:out value="${sessionScope.studentUser.rollNo}"/>)</a></li>
          </c:when>
          <c:otherwise>
            <li><a href="${pageContext.request.contextPath}/student/login" class="btn btn-primary btn-sm">Student? Sign in ➔</a></li>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>
  </header>

  <!-- Green Notice Board Hero -->
  <section class="board-hero-wrapper">
    <div class="container hero-grid">
      <!-- Main Pinned Notice -->
      <div class="main-notice">
        <div class="tape-top"></div>
        <span class="notice-tag">Notice No. 2026/G-01</span>
        <h1 class="hero-headline">Something broken?<br><span class="highlight">Pin it up.</span></h1>
        <p class="hero-subhead">
          Fan screeching? Lab router down? Geyser tripping again? Don't wait for semester end. File your issue directly to campus maintenance and track live resolution stamps.
        </p>
        <div class="hero-actions">
          <c:choose>
            <c:when test="${not empty sessionScope.studentUser}">
              <a href="${pageContext.request.contextPath}/student/complaint/new" class="btn btn-primary">File a Complaint</a>
              <a href="${pageContext.request.contextPath}/student/home" class="btn btn-secondary">Open My Desk</a>
            </c:when>
            <c:otherwise>
              <a href="${pageContext.request.contextPath}/student/login?redirect=${pageContext.request.contextPath}/student/complaint/new" class="btn btn-primary">File a Complaint (Sign In)</a>
              <a href="${pageContext.request.contextPath}/complaint/track" class="btn btn-secondary">Track Ticket Slip</a>
            </c:otherwise>
          </c:choose>
          <div class="hero-arrow-hint">
            <svg width="40" height="24" viewBox="0 0 50 30" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <path d="M5 15 C 20 8, 30 25, 45 15 M 35 7 L 45 15 L 36 24" />
            </svg>
            <span>takes 60 secs</span>
          </div>
        </div>
      </div>

      <!-- Pinned Live Counters -->
      <div class="slips-stack">
        <div class="counter-slip">
          <svg class="push-pin" viewBox="0 0 24 24" fill="#E5481B">
            <path d="M14 4v5c0 1.12.37 2.16 1 3H9c.65-.86 1-1.9 1-3V4h4m3-2H7v2h1v5c0 1.66-1.34 3-3 3v2h7v7l1 1 1-1v-7h7v-2c-1.66 0-3-1.34-3-3V4h1V2z"/>
          </svg>
          <div class="slip-number"><c:out value="${totalComplaints}"/></div>
          <div class="slip-label">Total Slips Pinned</div>
        </div>

        <div class="counter-slip">
          <svg class="push-pin" viewBox="0 0 24 24" fill="#1F5D4A">
            <path d="M14 4v5c0 1.12.37 2.16 1 3H9c.65-.86 1-1.9 1-3V4h4m3-2H7v2h1v5c0 1.66-1.34 3-3 3v2h7v7l1 1 1-1v-7h7v-2c-1.66 0-3-1.34-3-3V4h1V2z"/>
          </svg>
          <div class="slip-number" style="color: var(--color-board-green);"><c:out value="${resolvedComplaints}"/></div>
          <div class="slip-label">Cases Resolved & Verified</div>
        </div>

        <div class="counter-slip">
          <svg class="push-pin" viewBox="0 0 24 24" fill="#26407A">
            <path d="M14 4v5c0 1.12.37 2.16 1 3H9c.65-.86 1-1.9 1-3V4h4m3-2H7v2h1v5c0 1.66-1.34 3-3 3v2h7v7l1 1 1-1v-7h7v-2c-1.66 0-3-1.34-3-3V4h1V2z"/>
          </svg>
          <div class="slip-number"><c:out value="${avgResolutionHours}"/> <span style="font-size: 1.1rem; font-weight: 600;">hrs</span></div>
          <div class="slip-label">Average Resolution Time</div>
        </div>
      </div>
    </div>
  </section>

  <!-- Quick Track Bar -->
  <section style="background-color: var(--color-paper-light); border-bottom: var(--border-thick); padding: 1.5rem 0;">
    <div class="container">
      <form action="${pageContext.request.contextPath}/complaint/track" method="GET" style="display: flex; flex-wrap: wrap; gap: 1rem; align-items: center; justify-content: space-between;">
        <div style="display: flex; align-items: center; gap: 0.75rem;">
          <span class="handwritten" style="font-size: 1.6rem;">Already pinned a slip?</span>
          <span style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-ink-muted);">Check live rubber stamp status ➔</span>
        </div>
        <div style="display: flex; flex-wrap: wrap; gap: 0.75rem; flex: 1; max-width: 600px;">
          <input type="text" name="trackingId" placeholder="Tracking ID (e.g. CMP-2026-00001)" class="input-ruled" style="flex: 1; min-width: 180px; background: #FFF; border: var(--border-thick); padding: 0.5rem 0.75rem;" required>
          <input type="email" name="email" placeholder="Your contact email" class="input-ruled" style="flex: 1; min-width: 180px; background: #FFF; border: var(--border-thick); padding: 0.5rem 0.75rem;" required>
          <button type="submit" class="btn btn-primary btn-sm">Track Slip</button>
        </div>
      </form>
    </div>
  </section>

  <!-- Public Notice Wall (Resolved Cases) -->
  <main class="container section-wall">
    <div class="section-header">
      <div>
        <h2 class="section-title">Resolved Cases on the Board</h2>
        <p style="font-family: var(--font-mono); font-size: 0.85rem; color: var(--color-ink-muted); margin-top: 0.25rem;">
          Anonymized slips marked resolved by campus administrators to build transparency.
        </p>
      </div>
      <a href="${pageContext.request.contextPath}/complaint/new" class="btn btn-secondary btn-sm">+ Pin New Slip</a>
    </div>

    <c:choose>
      <c:when test="${empty publicWall}">
        <div class="paper-card" style="text-align: center; padding: 3rem;">
          <p class="handwritten" style="font-size: 2rem; color: var(--color-ink-muted);">Nothing pinned here yet. Suspiciously peaceful.</p>
        </div>
      </c:when>
      <c:otherwise>
        <div class="wall-grid">
          <c:forEach items="${publicWall}" var="c">
            <article class="wall-card">
              <div class="tape-top"></div>
              <div>
                <div class="wall-meta">
                  <span class="mono" style="font-weight: 700; color: var(--color-signal-red);"><c:out value="${c.trackingId}"/></span>
                  <span><c:out value="${c.category}"/> • <c:out value="${c.departmentName}"/></span>
                </div>
                <h3 class="wall-subject"><c:out value="${c.subject}"/></h3>
                <p class="wall-desc"><c:out value="${c.description}"/></p>
              </div>

              <div>
                <c:if test="${not empty c.publicRemark}">
                  <div class="wall-resolution">
                    <strong>Action Taken:</strong> <c:out value="${c.publicRemark}"/>
                  </div>
                </c:if>
                <div style="margin-top: 1rem; display: flex; justify-content: space-between; align-items: center;">
                  <span class="stamp stamp-resolved">RESOLVED</span>
                  <span class="handwritten" style="font-size: 1.1rem;"><c:out value="${c.displayName}"/></span>
                </div>
              </div>
            </article>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </main>

  <!-- Footer -->
  <footer class="site-footer">
    <div class="container footer-inner">
      <div>
        <strong>Campus Complaint & Feedback Registry</strong> — Advanced Java Mini Project
      </div>
      <div class="footer-stamp">
        SYSTEM TIME: 2026 • STATUS: OPERATIONAL
      </div>
      <div>
        <a href="${pageContext.request.contextPath}/admin/login" style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--color-ink-faint); text-decoration: none;">Staff / Admin Portal ➔</a>
      </div>
    </div>
  </footer>

</body>
</html>
