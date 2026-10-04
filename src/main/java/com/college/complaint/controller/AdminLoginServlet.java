package com.college.complaint.controller;

import com.college.complaint.dao.AdminDAO;
import com.college.complaint.dao.impl.AdminDAOImpl;
import com.college.complaint.model.Admin;
import com.college.complaint.service.RateLimiter;
import com.college.complaint.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(name = "AdminLoginServlet", urlPatterns = "/admin/login")
public class AdminLoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminLoginServlet.class);
    private final AdminDAO adminDAO = new AdminDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("admin") != null) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        String redirect = req.getParameter("redirect");
        req.setAttribute("redirect", redirect);
        req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");
        String clientIp = com.college.complaint.util.HttpUtil.getClientIp(req);

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("error", "Username and password are required.");
            req.setAttribute("username", username);
            req.setAttribute("redirect", redirect);
            req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
            return;
        }

        username = username.trim();

        // 1. Check Rate Limiter
        if (RateLimiter.isLocked(clientIp, username)) {
            long remainingSec = RateLimiter.getRemainingLockSeconds(clientIp, username);
            long minutes = (remainingSec + 59) / 60;
            logger.warn("Login attempt blocked by rate limiter for user {} from IP {}", username, clientIp);
            req.setAttribute("error", "Too many failed attempts. Account locked for " + minutes + " minute(s).");
            req.setAttribute("username", username);
            req.setAttribute("redirect", redirect);
            req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
            return;
        }

        // 2. Fetch Admin
        Admin admin = adminDAO.findByUsername(username);
        boolean authenticated = false;

        if (admin != null) {
            authenticated = PasswordUtil.checkPassword(password, admin.getPasswordHash());
        }

        // Record attempt in database and in-memory rate limiter
        adminDAO.recordLoginAttempt(clientIp, username, authenticated);

        if (!authenticated) {
            RateLimiter.recordFailedAttempt(clientIp, username);
            int failedCount = adminDAO.getFailedAttemptsCount(clientIp, username, 5);
            int remaining = Math.max(0, 5 - failedCount);

            String errorMsg = "Invalid username or password.";
            if (remaining > 0 && remaining <= 3) {
                errorMsg += " " + remaining + " attempt(s) remaining before temporary lockout.";
            } else if (RateLimiter.isLocked(clientIp, username)) {
                errorMsg = "Account temporarily locked for 5 minutes due to 5 failed attempts.";
            }

            req.setAttribute("error", errorMsg);
            req.setAttribute("username", username);
            req.setAttribute("redirect", redirect);
            req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
            return;
        }

        // 3. Success: Reset rate limiter
        RateLimiter.recordSuccess(clientIp, username);

        // 4. Session fixation protection: invalidate existing session, create new
        HttpSession oldSession = req.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = req.getSession(true);
        newSession.setAttribute("adminUser", admin);
        newSession.setAttribute("admin", admin);
        newSession.setMaxInactiveInterval(30 * 60); // 30 minutes timeout

        logger.info("Admin '{}' logged in successfully from IP {}", username, clientIp);

        if (redirect != null && !redirect.trim().isEmpty() && redirect.startsWith(req.getContextPath() + "/admin")) {
            resp.sendRedirect(redirect);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        }
    }
}
