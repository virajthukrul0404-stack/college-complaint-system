package com.college.complaint.filter;

import com.college.complaint.dao.RememberTokenDAO;
import com.college.complaint.dao.StudentDAO;
import com.college.complaint.dao.impl.RememberTokenDAOImpl;
import com.college.complaint.dao.impl.StudentDAOImpl;
import com.college.complaint.model.RememberToken;
import com.college.complaint.model.Student;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@WebFilter(filterName = "StudentAuthFilter", urlPatterns = "/student/*", asyncSupported = true)
public class StudentAuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(StudentAuthFilter.class);
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final RememberTokenDAO rememberTokenDAO = new RememberTokenDAOImpl();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Allow public student authentication routes
        if (path.equals("/student/login") ||
            path.equals("/student/register") ||
            path.equals("/student/forgot-password") ||
            path.equals("/student/reset-password")) {

            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("studentUser") != null &&
                (path.equals("/student/login") || path.equals("/student/register"))) {
                res.sendRedirect(req.getContextPath() + "/student/home");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // Allow logout to proceed
        if (path.equals("/student/logout")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Student student = (session != null) ? (Student) session.getAttribute("studentUser") : null;

        // If not in session, attempt remember-me authentication via secure cookie
        if (student == null) {
            String rememberRawToken = getCookieValue(req, "REMEMBER_STUDENT");
            if (rememberRawToken != null && !rememberRawToken.trim().isEmpty()) {
                String tokenHash = hashToken(rememberRawToken);
                RememberToken remToken = rememberTokenDAO.findByTokenHash(tokenHash);
                if (remToken != null) {
                    student = studentDAO.findById(remToken.getStudentId());
                    if (student != null) {
                        if (session == null) {
                            session = req.getSession(true);
                        }
                        session.setAttribute("studentUser", student);
                        session.setAttribute("student", student);
                        session.setMaxInactiveInterval(14 * 24 * 60 * 60); // 14 days for remembered sessions
                        studentDAO.updateLastLogin(student.getId());
                        logger.info("Student {} authenticated via remember-me token", student.getRollNo());
                    }
                }
            }
        }

        // If authenticated as student, proceed
        if (student != null) {
            chain.doFilter(request, response);
            return;
        }

        // Check if admin is trying to access student portal
        if (session != null && (session.getAttribute("adminUser") != null || session.getAttribute("admin") != null)) {
            logger.warn("Admin session attempted to access student path: {}", path);
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin sessions cannot access student accounts.");
            return;
        }

        // Unauthenticated access
        logger.debug("Unauthenticated attempt to access student path: {}", path);
        res.sendRedirect(req.getContextPath() + "/student/login?redirect=" + req.getContextPath() + path);
    }

    private String getCookieValue(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (name.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    public static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    @Override
    public void destroy() {}
}
