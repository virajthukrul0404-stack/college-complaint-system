<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>404 - Slip Missing | Campus Notice Board</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tokens.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
</head>
<body style="display: flex; align-items: center; justify-content: center; min-height: 100vh; text-align: center; padding: 2rem;">
  <div class="paper-card" style="max-width: 500px; padding: 3rem 2rem; background: var(--color-paper-light);">
    <div class="tape-top"></div>
    <span class="stamp stamp-rejected" style="font-size: 1.2rem; margin-bottom: 1.5rem;">SLIP NOT FOUND (404)</span>
    <h1 style="font-size: 2rem; margin: 1rem 0;">This slip flew off the board.</h1>
    <p style="font-family: var(--font-mono); font-size: 0.9rem; color: var(--color-ink-muted); margin-bottom: 2rem;">
      <c:out value="${empty errorMessage ? 'The page or grievance record you are looking for does not exist or may have been unpinned.' : errorMessage}"/>
    </p>
    <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Return to Notice Board</a>
  </div>
</body>
</html>
