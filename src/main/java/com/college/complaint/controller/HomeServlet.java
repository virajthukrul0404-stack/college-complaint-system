package com.college.complaint.controller;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.model.Complaint;
import com.college.complaint.model.Department;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int totalComplaints = complaintDAO.getTotalCount();
        int resolvedComplaints = complaintDAO.getResolvedCount();
        double avgResolutionHours = complaintDAO.getAverageResolutionHours();

        Map<String, Integer> statusCounts = complaintDAO.countByStatus();
        int inProgress = statusCounts.getOrDefault("In Progress", 0) + statusCounts.getOrDefault("Under Review", 0);

        List<Complaint> publicWall = complaintDAO.findPublicResolved(6);
        List<Department> departments = departmentDAO.findAll();

        req.setAttribute("totalComplaints", totalComplaints);
        req.setAttribute("resolvedComplaints", resolvedComplaints);
        req.setAttribute("avgResolutionHours", avgResolutionHours);
        req.setAttribute("inProgressComplaints", inProgress);
        req.setAttribute("publicWall", publicWall);
        req.setAttribute("departments", departments);

        req.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(req, resp);
    }
}
