package com.college.complaint.controller;

import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.dao.FeedbackDAO;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.dao.impl.FeedbackDAOImpl;
import com.college.complaint.model.Department;
import com.college.complaint.model.Feedback;
import com.college.complaint.service.ValidationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "FeedbackServlet", urlPatterns = "/feedback")
public class FeedbackServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();
    private final FeedbackDAO feedbackDAO = new FeedbackDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        loadData(req);
        req.getRequestDispatcher("/WEB-INF/views/feedback.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String deptParam = req.getParameter("departmentId");
        String category = req.getParameter("category");
        String ratingParam = req.getParameter("rating");
        String comment = req.getParameter("comment");

        int departmentId = 0;
        int rating = 0;
        try {
            if (deptParam != null) departmentId = Integer.parseInt(deptParam.trim());
        } catch (NumberFormatException ignored) {}
        try {
            if (ratingParam != null) rating = Integer.parseInt(ratingParam.trim());
        } catch (NumberFormatException ignored) {}

        Feedback fb = new Feedback();
        fb.setDepartmentId(departmentId);
        fb.setCategory(category != null ? category.trim() : "");
        fb.setRating(rating);
        fb.setComment(comment != null ? comment.trim() : "");

        Map<String, String> errors = ValidationService.validateFeedback(fb);

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("feedback", fb);
            loadData(req);
            req.getRequestDispatcher("/WEB-INF/views/feedback.jsp").forward(req, resp);
            return;
        }

        boolean saved = feedbackDAO.create(fb);
        if (saved) {
            resp.sendRedirect(req.getContextPath() + "/feedback?success=true");
        } else {
            errors.put("general", "Could not save your feedback due to a database error. Please try again.");
            req.setAttribute("errors", errors);
            req.setAttribute("feedback", fb);
            loadData(req);
            req.getRequestDispatcher("/WEB-INF/views/feedback.jsp").forward(req, resp);
        }
    }

    private void loadData(HttpServletRequest req) {
        List<Department> departments = departmentDAO.findAll();
        List<Feedback> recentFeedback = feedbackDAO.findAll(0, 10);
        Map<String, Double> deptAverages = feedbackDAO.getAverageRatingByDepartment();
        double overallAvg = feedbackDAO.getOverallAverageRating();

        req.setAttribute("departments", departments);
        req.setAttribute("recentFeedback", recentFeedback);
        req.setAttribute("deptAverages", deptAverages);
        req.setAttribute("overallAvg", overallAvg);
    }
}
