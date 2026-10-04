package com.college.complaint.filter;

import com.college.complaint.model.Admin;
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

@WebFilter(filterName = "AuthFilter", urlPatterns = "/admin/*", asyncSupported = true)
public class AuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Allow login page and login action
        if (path.equals("/admin/login")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Admin admin = (session != null) ? (Admin) session.getAttribute("adminUser") : null;
        if (admin == null && session != null) {
            admin = (Admin) session.getAttribute("admin");
        }

        if (admin == null) {
            if (session != null && session.getAttribute("studentUser") != null) {
                logger.warn("Student account attempted unauthorized access to admin path: {}", path);
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Student sessions cannot access staff administration.");
                return;
            }
            logger.debug("Unauthenticated attempt to access admin path: {}", path);
            res.sendRedirect(req.getContextPath() + "/admin/login?redirect=" + req.getContextPath() + path);
            return;
        }

        // Check superadmin permissions if accessing /admin/manage
        if (path.startsWith("/admin/manage") && !admin.isSuperAdmin()) {
            logger.warn("Unauthorized admin {} attempted to access superadmin area: {}", admin.getUsername(), path);
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Super Administrator privileges required.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
