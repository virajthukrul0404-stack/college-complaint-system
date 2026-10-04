package com.college.complaint.controller.student;

import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.dao.StudentDAO;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.dao.impl.StudentDAOImpl;
import com.college.complaint.model.Student;
import com.college.complaint.service.ValidationService;
import com.college.complaint.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "StudentRegisterServlet", urlPatterns = "/student/register")
public class StudentRegisterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentRegisterServlet.class);
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("departments", departmentDAO.findAll());
        req.getRequestDispatcher("/WEB-INF/views/student/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("fullName");
        String rollNo = req.getParameter("rollNo");
        String email = req.getParameter("email");
        String deptParam = req.getParameter("departmentId");
        String yearParam = req.getParameter("yearOfStudy");
        String mobile = req.getParameter("mobile");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        int departmentId = 0;
        try {
            if (deptParam != null) departmentId = Integer.parseInt(deptParam.trim());
        } catch (NumberFormatException ignored) {}

        int yearOfStudy = 0;
        try {
            if (yearParam != null) yearOfStudy = Integer.parseInt(yearParam.trim());
        } catch (NumberFormatException ignored) {}

        Map<String, String> errors = ValidationService.validateStudentRegistration(
                fullName, rollNo, email, departmentId, yearOfStudy, mobile, password, confirmPassword
        );

        if (rollNo != null && !rollNo.trim().isEmpty() && studentDAO.isRollNoTaken(rollNo.trim(), null)) {
            errors.put("rollNo", "This roll number is already registered. Please sign in or use forgot password.");
        }

        if (email != null && !email.trim().isEmpty() && studentDAO.isEmailTaken(email.trim(), null)) {
            errors.put("email", "This email address is already registered.");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("fullName", fullName);
            req.setAttribute("rollNo", rollNo);
            req.setAttribute("email", email);
            req.setAttribute("departmentId", departmentId);
            req.setAttribute("yearOfStudy", yearOfStudy);
            req.setAttribute("mobile", mobile);
            req.setAttribute("departments", departmentDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/student/register.jsp").forward(req, resp);
            return;
        }

        Student student = new Student();
        student.setFullName(fullName.trim());
        student.setRollNo(rollNo.trim().toUpperCase());
        student.setEmail(email.trim().toLowerCase());
        student.setDepartmentId(departmentId);
        student.setYearOfStudy(yearOfStudy);
        student.setMobile(mobile != null ? mobile.trim() : null);
        student.setPasswordHash(PasswordUtil.hashPassword(password));

        boolean created = studentDAO.create(student);
        if (!created) {
            req.setAttribute("error", "Unable to create account due to a database error. Please try again.");
            req.setAttribute("departments", departmentDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/student/register.jsp").forward(req, resp);
            return;
        }

        logger.info("Student {} registered successfully with roll no {}", student.getFullName(), student.getRollNo());
        resp.sendRedirect(req.getContextPath() + "/student/login?registered=true");
    }
}
