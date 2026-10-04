package com.college.complaint.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@WebFilter(filterName = "CsrfFilter", urlPatterns = "/*", asyncSupported = true)
public class CsrfFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CsrfFilter.class);
    public static final String CSRF_SESSION_ATTR = "CSRF_TOKEN";
    public static final String CSRF_PARAM_NAME = "csrfToken";
    public static final String CSRF_HEADER_NAME = "X-CSRF-Token";

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Skip static resources
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.startsWith("/assets/") || path.startsWith("/uploads/") || path.equals("/favicon.ico")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(true);
        String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);
        if (sessionToken == null) {
            byte[] bytes = new byte[32];
            secureRandom.nextBytes(bytes);
            sessionToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(CSRF_SESSION_ATTR, sessionToken);
        }
        session.setAttribute("csrfToken", sessionToken);

        // Make token available to views/JSPs
        req.setAttribute(CSRF_PARAM_NAME, sessionToken);

        String method = req.getMethod().toUpperCase();
        if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
            String requestToken = req.getParameter(CSRF_PARAM_NAME);
            if (requestToken == null || requestToken.isEmpty()) {
                requestToken = req.getHeader(CSRF_HEADER_NAME);
            }

            if (requestToken == null || !requestToken.equals(sessionToken)) {
                logger.warn("CSRF token validation failed for path: {} from IP: {}", path, req.getRemoteAddr());
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token. Request rejected for security.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
