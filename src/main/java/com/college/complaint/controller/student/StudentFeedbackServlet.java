package com.college.complaint.controller.student;

import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.dao.FeedbackDAO;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.dao.impl.FeedbackDAOImpl;
import com.college.complaint.model.Feedback;
import com.college.complaint.model.Student;
import com.college.complaint.service.ValidationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StudentFeedbackServlet", urlPatterns = "/student/feedback")
public class StudentFeedbackServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentFeedbackServlet.class);
    private final FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        List<Feedback> pastFeedbacks = feedbackDAO.findByStudentId(student.getId(), 0, 10);

        req.setAttribute("student", student);
        req.setAttribute("departments", departmentDAO.findAll());
        req.setAttribute("pastFeedbacks", pastFeedbacks);
        req.getRequestDispatcher("/WEB-INF/views/student/feedback.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        String deptParam = req.getParameter("departmentId");
        String category = req.getParameter("category");
        String ratingParam = req.getParameter("rating");
        String comment = req.getParameter("comment");

        int departmentId = 0;
        try {
            if (deptParam != null) departmentId = Integer.parseInt(deptParam.trim());
        } catch (NumberFormatException ignored) {}

        int rating = 0;
        try {
            if (ratingParam != null) rating = Integer.parseInt(ratingParam.trim());
        } catch (NumberFormatException ignored) {}

        Feedback feedback = new Feedback();
        feedback.setStudentId(student.getId());
        feedback.setDepartmentId(departmentId);
        feedback.setCategory(category != null ? category.trim() : "");
        feedback.setRating(rating);
        feedback.setComment(comment != null ? comment.trim() : "");

        Map<String, String> errors = ValidationService.validateFeedback(feedback);

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("feedback", feedback);
            req.setAttribute("student", student);
            req.setAttribute("departments", departmentDAO.findAll());
            req.setAttribute("pastFeedbacks", feedbackDAO.findByStudentId(student.getId(), 0, 10));
            req.getRequestDispatcher("/WEB-INF/views/student/feedback.jsp").forward(req, resp);
            return;
        }

        boolean created = feedbackDAO.create(feedback);
        if (!created) {
            req.setAttribute("systemError", "Failed to submit feedback due to a database error.");
            req.setAttribute("feedback", feedback);
            req.setAttribute("student", student);
            req.setAttribute("departments", departmentDAO.findAll());
            req.setAttribute("pastFeedbacks", feedbackDAO.findByStudentId(student.getId(), 0, 10));
            req.getRequestDispatcher("/WEB-INF/views/student/feedback.jsp").forward(req, resp);
            return;
        }

        logger.info("Feedback submitted by student {} for department {}", student.getRollNo(), departmentId);
        resp.sendRedirect(req.getContextPath() + "/student/feedback?submitted=true");
    }
}
