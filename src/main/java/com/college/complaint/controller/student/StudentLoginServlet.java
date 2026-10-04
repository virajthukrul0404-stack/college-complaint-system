package com.college.complaint.controller.student;

import com.college.complaint.dao.RememberTokenDAO;
import com.college.complaint.dao.StudentDAO;
import com.college.complaint.dao.impl.RememberTokenDAOImpl;
import com.college.complaint.dao.impl.StudentDAOImpl;
import com.college.complaint.filter.StudentAuthFilter;
import com.college.complaint.model.RememberToken;
import com.college.complaint.model.Student;
import com.college.complaint.service.RateLimiter;
import com.college.complaint.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HexFormat;

@WebServlet(name = "StudentLoginServlet", urlPatterns = "/student/login")
public class StudentLoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentLoginServlet.class);
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final RememberTokenDAO rememberTokenDAO = new RememberTokenDAOImpl();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("studentUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/student/home");
            return;
        }

        String redirect = req.getParameter("redirect");
        req.setAttribute("redirect", redirect);
        req.getRequestDispatcher("/WEB-INF/views/student/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String identifier = req.getParameter("identifier"); // roll number or email
        String password = req.getParameter("password");
        String rememberMe = req.getParameter("rememberMe");
        String redirect = req.getParameter("redirect");
        String clientIp = com.college.complaint.util.HttpUtil.getClientIp(req);

        if (identifier == null || identifier.trim().isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("error", "Roll number / email and password are required.");
            req.setAttribute("identifier", identifier);
            req.setAttribute("redirect", redirect);
            req.getRequestDispatcher("/WEB-INF/views/student/login.jsp").forward(req, resp);
            return;
        }

        identifier = identifier.trim();

        // 1. Rate limiter check
        if (RateLimiter.isLocked(clientIp, identifier)) {
            long remainingSec = RateLimiter.getRemainingLockSeconds(clientIp, identifier);
            long minutes = (remainingSec + 59) / 60;
            logger.warn("Student login locked out for identifier {} from IP {}", identifier, clientIp);
            req.setAttribute("error", "Too many failed attempts. Login locked for " + minutes + " minute(s).");
            req.setAttribute("identifier", identifier);
            req.setAttribute("redirect", redirect);
            req.getRequestDispatcher("/WEB-INF/views/student/login.jsp").forward(req, resp);
            return;
        }

        // 2. Authenticate
        Student student = studentDAO.findByIdentifier(identifier);
        boolean authenticated = false;

        if (student != null) {
            authenticated = PasswordUtil.checkPassword(password, student.getPasswordHash());
        }

        if (!authenticated) {
            RateLimiter.recordFailedAttempt(clientIp, identifier);
            int failed = RateLimiter.getFailedAttempts(clientIp, identifier);
            int remaining = Math.max(0, 5 - failed);

            String errorMsg = "Invalid roll number, email, or password.";
            if (remaining > 0 && remaining <= 3) {
                errorMsg += " " + remaining + " attempt(s) remaining before temporary lockout.";
            } else if (RateLimiter.isLocked(clientIp, identifier)) {
                errorMsg = "Account temporarily locked for 5 minutes due to 5 failed attempts.";
            }

            req.setAttribute("error", errorMsg);
            req.setAttribute("identifier", identifier);
            req.setAttribute("redirect", redirect);
            req.getRequestDispatcher("/WEB-INF/views/student/login.jsp").forward(req, resp);
            return;
        }

        // 3. Success: Reset rate limiter
        RateLimiter.recordSuccess(clientIp, identifier);

        // 4. Session fixation protection: invalidate old session, create new
        HttpSession oldSession = req.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = req.getSession(true);
        newSession.setAttribute("studentUser", student);
        newSession.setAttribute("student", student);
        byte[] csrfBytes = new byte[32];
        secureRandom.nextBytes(csrfBytes);
        String sessionCsrf = Base64.getUrlEncoder().withoutPadding().encodeToString(csrfBytes);
        newSession.setAttribute("CSRF_TOKEN", sessionCsrf);
        newSession.setAttribute("csrfToken", sessionCsrf);

        // 5. Remember-me token
        if ("true".equalsIgnoreCase(rememberMe) || "on".equalsIgnoreCase(rememberMe)) {
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);
            String rawToken = HexFormat.of().formatHex(randomBytes);
            String tokenHash = StudentAuthFilter.hashToken(rawToken);

            long expireMillis = System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000);
            Timestamp expiresAt = new Timestamp(expireMillis);
            String userAgent = req.getHeader("User-Agent");

            rememberTokenDAO.saveToken(new RememberToken(student.getId(), tokenHash, expiresAt, userAgent));

            Cookie cookie = new Cookie("REMEMBER_STUDENT", rawToken);
            cookie.setHttpOnly(true);
            if (com.college.complaint.util.HttpUtil.isSecureRequest(req) || com.college.complaint.util.AppConfig.isProduction()) {
                cookie.setSecure(true);
            }
            cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            cookie.setMaxAge(14 * 24 * 60 * 60);
            cookie.setAttribute("SameSite", "Lax");
            resp.addCookie(cookie);

            newSession.setMaxInactiveInterval(14 * 24 * 60 * 60);
        } else {
            newSession.setMaxInactiveInterval(30 * 60); // 30 minutes idle timeout
        }

        studentDAO.updateLastLogin(student.getId());
        logger.info("Student '{}' ({}) logged in successfully from IP {}", student.getFullName(), student.getRollNo(), clientIp);

        if (redirect != null && !redirect.trim().isEmpty() && redirect.startsWith(req.getContextPath() + "/student")) {
            resp.sendRedirect(redirect);
        } else {
            resp.sendRedirect(req.getContextPath() + "/student/home");
        }
    }
}
