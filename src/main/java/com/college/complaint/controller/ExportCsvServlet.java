package com.college.complaint.controller;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.model.Complaint;
import com.college.complaint.service.CsvExporter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "ExportCsvServlet", urlPatterns = "/admin/export")
public class ExportCsvServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("search");
        String status = req.getParameter("status");
        String deptParam = req.getParameter("departmentId");
        String category = req.getParameter("category");
        String priority = req.getParameter("priority");
        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");
        String sortBy = req.getParameter("sortBy");
        String sortOrder = req.getParameter("sortOrder");

        Integer departmentId = null;
        try {
            if (deptParam != null && !deptParam.trim().isEmpty() && !"-1".equals(deptParam)) {
                departmentId = Integer.parseInt(deptParam.trim());
            }
        } catch (NumberFormatException ignored) {}

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "created_at";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "DESC";
        }

        // Fetch all matching records (up to 10,000)
        List<Complaint> complaints = complaintDAO.searchAndFilter(
                search, status, departmentId, category, priority, startDate, endDate,
                sortBy, sortOrder, 0, 10000
        );

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String filename = "campus_complaints_" + dateStr + ".csv";

        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        CsvExporter.exportComplaintsToCsv(complaints, resp.getWriter());
    }
}
