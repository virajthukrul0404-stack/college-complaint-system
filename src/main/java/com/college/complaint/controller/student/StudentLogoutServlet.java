package com.college.complaint.controller.student;

import com.college.complaint.dao.RememberTokenDAO;
import com.college.complaint.dao.impl.RememberTokenDAOImpl;
import com.college.complaint.filter.StudentAuthFilter;
import com.college.complaint.model.Student;
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

@WebServlet(name = "StudentLogoutServlet", urlPatterns = "/student/logout")
public class StudentLogoutServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentLogoutServlet.class);
    private final RememberTokenDAO rememberTokenDAO = new RememberTokenDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Student student = (Student) session.getAttribute("studentUser");
            if (student != null) {
                logger.info("Student {} logged out", student.getRollNo());
                rememberTokenDAO.deleteByStudentId(student.getId());
            }
            session.invalidate();
        }

        // Clear remember-me cookie
        Cookie cookie = new Cookie("REMEMBER_STUDENT", "");
        cookie.setHttpOnly(true);
        if (com.college.complaint.util.HttpUtil.isSecureRequest(req) || com.college.complaint.util.AppConfig.isProduction()) {
            cookie.setSecure(true);
        }
        cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        resp.addCookie(cookie);

        resp.sendRedirect(req.getContextPath() + "/student/login?loggedOut=true");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
