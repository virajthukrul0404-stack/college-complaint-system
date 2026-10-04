package com.college.complaint.controller.student;

import com.college.complaint.dao.PasswordResetDAO;
import com.college.complaint.dao.StudentDAO;
import com.college.complaint.dao.impl.PasswordResetDAOImpl;
import com.college.complaint.dao.impl.StudentDAOImpl;
import com.college.complaint.filter.StudentAuthFilter;
import com.college.complaint.model.PasswordReset;
import com.college.complaint.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Timestamp;

@WebServlet(name = "StudentForgotPasswordServlet", urlPatterns = "/student/forgot-password")
public class StudentForgotPasswordServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentForgotPasswordServlet.class);
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final PasswordResetDAO passwordResetDAO = new PasswordResetDAOImpl();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/student/forgot_password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String rollNo = req.getParameter("rollNo");
        String email = req.getParameter("email");

        if (rollNo == null || rollNo.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Roll number and college email are required.");
            req.setAttribute("rollNo", rollNo);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/student/forgot_password.jsp").forward(req, resp);
            return;
        }

        rollNo = rollNo.trim().toUpperCase();
        email = email.trim().toLowerCase();

        Student student = studentDAO.findByEmail(email);
        if (student != null && rollNo.equalsIgnoreCase(student.getRollNo())) {
            // Generate 6-digit code
            int codeInt = secureRandom.nextInt(900000) + 100000;
            String resetCode = String.valueOf(codeInt);
            String codeHash = StudentAuthFilter.hashToken(resetCode);
            Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + 10 * 60 * 1000L); // 10 minutes

            passwordResetDAO.saveResetCode(new PasswordReset(student.getId(), codeHash, expiresAt));

            // Log securely to console log for demo evaluation
            System.out.println("========================================================================");
            System.out.println("[STUDENT PASSWORD RESET DISPATCH]");
            System.out.println("  Student: " + student.getFullName() + " | Roll: " + student.getRollNo());
            System.out.println("  Email:   " + student.getEmail());
            System.out.println("  >>> ONE-TIME 6-DIGIT CODE: " + resetCode + " <<<");
            System.out.println("  Valid for 10 minutes. Max 5 verification attempts.");
            System.out.println("========================================================================");
            logger.info("Password reset code generated and logged for student {}", student.getRollNo());
        } else {
            logger.warn("Password reset requested for unmatched student roll {} / email {}", rollNo, email);
        }

        // Always redirect to verification page to prevent enumeration
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        resp.sendRedirect(req.getContextPath() + "/student/reset-password?email=" + encodedEmail + "&sent=true");
    }
}
