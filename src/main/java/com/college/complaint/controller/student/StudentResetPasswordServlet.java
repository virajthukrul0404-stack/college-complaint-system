package com.college.complaint.controller.student;

import com.college.complaint.dao.PasswordResetDAO;
import com.college.complaint.dao.RememberTokenDAO;
import com.college.complaint.dao.StudentDAO;
import com.college.complaint.dao.impl.PasswordResetDAOImpl;
import com.college.complaint.dao.impl.RememberTokenDAOImpl;
import com.college.complaint.dao.impl.StudentDAOImpl;
import com.college.complaint.filter.StudentAuthFilter;
import com.college.complaint.model.PasswordReset;
import com.college.complaint.model.Student;
import com.college.complaint.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Pattern;

@WebServlet(name = "StudentResetPasswordServlet", urlPatterns = "/student/reset-password")
public class StudentResetPasswordServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentResetPasswordServlet.class);
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final PasswordResetDAO passwordResetDAO = new PasswordResetDAOImpl();
    private final RememberTokenDAO rememberTokenDAO = new RememberTokenDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        req.setAttribute("email", email);
        req.getRequestDispatcher("/WEB-INF/views/student/reset_password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String code = req.getParameter("code");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (email == null || email.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            req.setAttribute("error", "Email and 6-digit reset code are required.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/student/reset_password.jsp").forward(req, resp);
            return;
        }

        email = email.trim().toLowerCase();
        code = code.trim();

        if (newPassword == null || newPassword.length() < 8) {
            req.setAttribute("error", "New password must be at least 8 characters long.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/student/reset_password.jsp").forward(req, resp);
            return;
        } else if (!Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).+$").matcher(newPassword).matches()) {
            req.setAttribute("error", "Password must contain at least one letter and one number.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/student/reset_password.jsp").forward(req, resp);
            return;
        }

        if (confirmPassword == null || !confirmPassword.equals(newPassword)) {
            req.setAttribute("error", "Passwords do not match.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/student/reset_password.jsp").forward(req, resp);
            return;
        }

        Student student = studentDAO.findByEmail(email);
        if (student == null) {
            req.setAttribute("error", "No account found matching this email.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/student/reset_password.jsp").forward(req, resp);
            return;
        }

        PasswordReset reset = passwordResetDAO.findActiveByStudentId(student.getId());
        if (reset == null) {
            req.setAttribute("error", "No active password reset request found or code has expired. Please request a new code.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/student/reset_password.jsp").forward(req, resp);
            return;
        }

        passwordResetDAO.incrementAttempts(reset.getId());

        String inputCodeHash = StudentAuthFilter.hashToken(code);
        if (!inputCodeHash.equals(reset.getCodeHash())) {
            int currentAttempts = reset.getAttempts() + 1;
            int remaining = Math.max(0, 5 - currentAttempts);
            String err = "Invalid 6-digit verification code.";
            if (remaining > 0) {
                err += " " + remaining + " attempt(s) remaining.";
            } else {
                err += " Maximum attempts exceeded. Code has been invalidated. Please request a new one.";
                passwordResetDAO.markUsed(reset.getId());
            }
            req.setAttribute("error", err);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/student/reset_password.jsp").forward(req, resp);
            return;
        }

        // Code matches!
        passwordResetDAO.markUsed(reset.getId());
        studentDAO.updatePassword(student.getId(), PasswordUtil.hashPassword(newPassword));
        // Revoke all remember tokens on password change
        rememberTokenDAO.deleteByStudentId(student.getId());

        logger.info("Password successfully reset for student {}", student.getRollNo());
        resp.sendRedirect(req.getContextPath() + "/student/login?resetSuccess=true");
    }
}
