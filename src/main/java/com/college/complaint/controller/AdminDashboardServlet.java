package com.college.complaint.controller;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.model.Complaint;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = "/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Integer> statusCounts = complaintDAO.countByStatus();
        Map<String, Integer> deptCounts = complaintDAO.countByDepartment();
        Map<String, Integer> categoryCounts = complaintDAO.countByCategory();
        Map<String, Integer> priorityCounts = complaintDAO.countByPriority();
        Map<String, Integer> trend = complaintDAO.getTrendData(30);

        int total = complaintDAO.getTotalCount();
        int resolved = complaintDAO.getResolvedCount();
        double avgHours = complaintDAO.getAverageResolutionHours();
        List<Complaint> recent = complaintDAO.findRecent(8);

        // Convert trend to JSON arrays for Chart.js
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : trend.entrySet()) {
            labels.add("\"" + entry.getKey() + "\"");
            values.add(entry.getValue());
        }

        req.setAttribute("statusCounts", statusCounts);
        req.setAttribute("deptCounts", deptCounts);
        req.setAttribute("categoryCounts", categoryCounts);
        req.setAttribute("priorityCounts", priorityCounts);
        req.setAttribute("trendLabelsJson", "[" + String.join(",", labels) + "]");
        req.setAttribute("trendValuesJson", values.toString());
        req.setAttribute("totalComplaints", total);
        req.setAttribute("resolvedComplaints", resolved);
        req.setAttribute("avgResolutionHours", avgHours);
        req.setAttribute("recentComplaints", recent);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
