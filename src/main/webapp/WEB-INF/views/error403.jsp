<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>403 - Restricted Area | Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
</head>
<body style="display: flex; align-items: center; justify-content: center; min-height: 100vh; text-align: center; padding: 2rem;">
  <div class="paper-card" style="max-width: 500px; padding: 3rem 2rem; background: var(--color-paper-light);">
    <div class="tape-top"></div>
    <span class="stamp stamp-rejected" style="font-size: 1.2rem; margin-bottom: 1.5rem;">ACCESS RESTRICTED (403)</span>
    <h1 style="font-size: 2rem; margin: 1rem 0;">Authorized Personnel Only.</h1>
    <p style="font-family: var(--font-mono); font-size: 0.9rem; color: var(--color-ink-muted); margin-bottom: 2rem;">
      <c:out value="${empty errorMessage ? 'You do not have administrative clearance to access this registry section.' : errorMessage}"/>
    </p>
    <div style="display: flex; justify-content: center; gap: 1rem;">
      <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Public Board</a>
      <a href="${pageContext.request.contextPath}/admin/login" class="btn btn-primary">Staff Login</a>
    </div>
  </div>
</body>
</html>
