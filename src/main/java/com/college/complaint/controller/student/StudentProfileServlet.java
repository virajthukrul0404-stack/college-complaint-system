package com.college.complaint.controller.student;

import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.dao.RememberTokenDAO;
import com.college.complaint.dao.StudentDAO;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.dao.impl.RememberTokenDAOImpl;
import com.college.complaint.dao.impl.StudentDAOImpl;
import com.college.complaint.model.Student;
import com.college.complaint.service.ValidationService;
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
import java.util.Map;

@WebServlet(name = "StudentProfileServlet", urlPatterns = "/student/profile")
public class StudentProfileServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentProfileServlet.class);
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();
    private final RememberTokenDAO rememberTokenDAO = new RememberTokenDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        // Fresh fetch from DB
        Student fresh = studentDAO.findById(student.getId());
        if (fresh != null) {
            session.setAttribute("studentUser", fresh);
            session.setAttribute("student", fresh);
            student = fresh;
        }

        req.setAttribute("student", student);
        req.setAttribute("departments", departmentDAO.findAll());
        req.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        String action = req.getParameter("action");

        if ("updateProfile".equalsIgnoreCase(action)) {
            String fullName = req.getParameter("fullName");
            String mobile = req.getParameter("mobile");
            String deptParam = req.getParameter("departmentId");

            int departmentId = student.getDepartmentId();
            try {
                if (deptParam != null) departmentId = Integer.parseInt(deptParam.trim());
            } catch (NumberFormatException ignored) {}

            Map<String, String> errors = ValidationService.validateStudentProfile(fullName, mobile, departmentId);
            if (!errors.isEmpty()) {
                req.setAttribute("profileErrors", errors);
                req.setAttribute("student", student);
                req.setAttribute("departments", departmentDAO.findAll());
                req.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(req, resp);
                return;
            }

            studentDAO.updateProfile(student.getId(), fullName.trim(), mobile, departmentId);
            Student updated = studentDAO.findById(student.getId());
            session.setAttribute("studentUser", updated);
            session.setAttribute("student", updated);

            req.setAttribute("profileSuccess", "Your profile details have been successfully updated.");
            req.setAttribute("student", updated);
            req.setAttribute("departments", departmentDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(req, resp);

        } else if ("changePassword".equalsIgnoreCase(action)) {
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            Map<String, String> errors = ValidationService.validatePasswordChange(currentPassword, newPassword, confirmPassword);

            if (!PasswordUtil.checkPassword(currentPassword, student.getPasswordHash())) {
                errors.put("currentPassword", "Incorrect current password.");
            }

            if (!errors.isEmpty()) {
                req.setAttribute("passwordErrors", errors);
                req.setAttribute("student", student);
                req.setAttribute("departments", departmentDAO.findAll());
                req.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(req, resp);
                return;
            }

            String newHash = PasswordUtil.hashPassword(newPassword);
            studentDAO.updatePassword(student.getId(), newHash);
            student.setPasswordHash(newHash);
            session.setAttribute("studentUser", student);
            session.setAttribute("student", student);

            // Revoke remembered devices on password change
            rememberTokenDAO.deleteByStudentId(student.getId());

            req.setAttribute("passwordSuccess", "Your password was changed successfully. Active sessions on other devices have been revoked.");
            req.setAttribute("student", student);
            req.setAttribute("departments", departmentDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/student/profile");
        }
    }
}
