<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="context-path" content="${pageContext.request.contextPath}">
  <title>Ticket Receipt - <c:out value="${complaint.trackingId}"/></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/board.css">
  <style>
    @media print {
      body { background: #FFF !important; }
      .site-header, .site-footer, .no-print { display: none !important; }
      .receipt-wrapper { margin: 0 auto !important; box-shadow: none !important; }
      .railway-ticket { box-shadow: none !important; border: 2px solid #000 !important; }
    }
  </style>
</head>
<body>

  <!-- Site Header -->
  <header class="site-header no-print">
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
        <li><a href="${pageContext.request.contextPath}/complaint/track?trackingId=${complaint.trackingId}&email=${complaint.email}" class="nav-link active">Track Slip</a></li>
        <li><a href="${pageContext.request.contextPath}/feedback" class="nav-link">Feedback</a></li>
      </ul>
    </div>
  </header>

  <main class="container" style="padding: 2.5rem 1.5rem;">

    <div class="no-print" style="max-width: 620px; margin: 0 auto 1.5rem; display: flex; justify-content: space-between; align-items: center;">
      <div>
        <span class="handwritten" style="font-size: 1.5rem; color: var(--color-board-green);">Slip pinned successfully!</span>
        <h2 style="font-size: 1.6rem;">Official Lodgement Receipt</h2>
      </div>
      <div style="display: flex; gap: 0.5rem;">
        <button id="printBtn" class="btn btn-secondary btn-sm">🖨 Print Ticket</button>
        <a href="${pageContext.request.contextPath}/complaint/track?trackingId=${complaint.trackingId}&email=${complaint.email}" class="btn btn-primary btn-sm">Track Live ➔</a>
      </div>
    </div>

    <!-- Railway/Bus Ticket Receipt -->
    <div class="receipt-wrapper">
      <article class="railway-ticket">

        <!-- Ticket Header -->
        <div class="ticket-header">
          <div class="ticket-board-name">CAMPUS GRIEVANCE TICKET</div>
          <div class="ticket-sub">BOARD DESK • ACKNOWLEDGEMENT SLIP • CONFIDENTIAL</div>
        </div>

        <!-- Big Highlighted Tracking ID -->
        <div style="margin-bottom: 1.5rem;">
          <div class="ticket-tracking-large">
            <c:out value="${complaint.trackingId}"/>
          </div>
        </div>

        <!-- Ticket Body Details -->
        <div class="ticket-body-grid">
          <div class="ticket-field">
            <div class="label">Date & Time Filed</div>
            <div class="val"><c:out value="${complaint.createdAt}"/></div>
          </div>

          <div class="ticket-field">
            <div class="label">Current Status</div>
            <div class="val">
              <span class="stamp stamp-submitted" style="font-size: 0.8rem; padding: 0.2rem 0.6rem;">
                <c:out value="${complaint.status}"/>
              </span>
            </div>
          </div>

          <div class="ticket-field">
            <div class="label">Department</div>
            <div class="val"><c:out value="${complaint.departmentName}"/></div>
          </div>

          <div class="ticket-field">
            <div class="label">Category & Priority</div>
            <div class="val"><c:out value="${complaint.category}"/> • <c:out value="${complaint.priority}"/></div>
          </div>

          <div class="ticket-field" style="grid-column: span 2;">
            <div class="label">Complainant Identity</div>
            <div class="val">
              <c:choose>
                <c:when test="${complaint.anonymous}">
                  <em>Anonymous Student (Identity Masked)</em>
                </c:when>
                <c:otherwise>
                  <c:out value="${complaint.studentName}"/> (Roll: <c:out value="${complaint.rollNumber}"/>)
                </c:otherwise>
              </c:choose>
            </div>
          </div>

          <div class="ticket-field" style="grid-column: span 2;">
            <div class="label">Registered Email for Alerts</div>
            <div class="val"><c:out value="${complaint.email}"/></div>
          </div>

          <div class="ticket-field" style="grid-column: span 2;">
            <div class="label">Subject</div>
            <div class="val" style="font-family: var(--font-body); font-weight: 600;"><c:out value="${complaint.subject}"/></div>
          </div>
        </div>

        <!-- SVG Barcode Box -->
        <div class="ticket-barcode-box">
          <div id="barcodeContainer"></div>
          <span style="font-size: 0.75rem; letter-spacing: 0.2em; color: var(--color-ink-muted);">
            * <c:out value="${complaint.trackingId}"/> *
          </span>
          <p style="font-size: 0.7rem; color: var(--color-ink-muted); text-align: center; margin-top: 0.5rem;">
            Keep this tracking slip safe. You can track real-time rubber stamp updates anytime using your tracking ID and email.
          </p>
        </div>

      </article>
    </div>

  </main>

  <footer class="site-footer no-print">
    <div class="container footer-inner">
      <div>Campus Complaint & Feedback Registry • Advanced Java</div>
      <div class="footer-stamp">OFFICIAL TICKET RECEIPT</div>
    </div>
  </footer>

  <script type="module">
    import { generateSvgBarcode, setupPrintButton } from "${pageContext.request.contextPath}/assets/js/ui.js";
    generateSvgBarcode("barcodeContainer", "${complaint.trackingId}");
    setupPrintButton("printBtn");
  </script>
</body>
</html>
