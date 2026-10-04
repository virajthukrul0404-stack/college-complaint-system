package com.college.complaint.controller;

import com.college.complaint.dao.FeedbackDAO;
import com.college.complaint.dao.impl.FeedbackDAOImpl;
import com.college.complaint.model.Feedback;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminFeedbackServlet", urlPatterns = "/admin/feedback")
public class AdminFeedbackServlet extends HttpServlet {

    private final FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
    private static final int PAGE_SIZE = 15;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pageParam = req.getParameter("page");
        int page = 1;
        try {
            if (pageParam != null) {
                page = Math.max(1, Integer.parseInt(pageParam.trim()));
            }
        } catch (NumberFormatException ignored) {}

        int totalCount = feedbackDAO.countAll();
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        int offset = (page - 1) * PAGE_SIZE;
        List<Feedback> feedbackList = feedbackDAO.findAll(offset, PAGE_SIZE);
        Map<String, Double> deptAverages = feedbackDAO.getAverageRatingByDepartment();
        Map<Integer, Integer> ratingDist = feedbackDAO.getRatingDistribution();
        double overallAvg = feedbackDAO.getOverallAverageRating();

        req.setAttribute("feedbackList", feedbackList);
        req.setAttribute("deptAverages", deptAverages);
        req.setAttribute("ratingDist", ratingDist);
        req.setAttribute("overallAvg", overallAvg);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalCount", totalCount);

        req.getRequestDispatcher("/WEB-INF/views/admin/feedback.jsp").forward(req, resp);
    }
}
