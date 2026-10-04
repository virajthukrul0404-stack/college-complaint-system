package com.college.complaint.controller;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.model.Complaint;
import com.college.complaint.model.Department;
import com.college.complaint.service.ValidationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminComplaintsServlet", urlPatterns = "/admin/complaints")
public class AdminComplaintsServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();
    private static final int DEFAULT_PAGE_SIZE = 10;

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
        String pageParam = req.getParameter("page");

        Integer departmentId = null;
        try {
            if (deptParam != null && !deptParam.trim().isEmpty() && !"-1".equals(deptParam)) {
                departmentId = Integer.parseInt(deptParam.trim());
            }
        } catch (NumberFormatException ignored) {}

        int page = 1;
        try {
            if (pageParam != null) {
                page = Math.max(1, Integer.parseInt(pageParam.trim()));
            }
        } catch (NumberFormatException ignored) {}

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "created_at";
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "DESC";
        }

        int offset = (page - 1) * DEFAULT_PAGE_SIZE;

        int totalCount = complaintDAO.countSearchAndFilter(search, status, departmentId, category, priority, startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalCount / DEFAULT_PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        List<Complaint> complaints = complaintDAO.searchAndFilter(
                search, status, departmentId, category, priority, startDate, endDate,
                sortBy, sortOrder, offset, DEFAULT_PAGE_SIZE
        );

        List<Department> departments = departmentDAO.findAll();

        req.setAttribute("complaints", complaints);
        req.setAttribute("departments", departments);
        req.setAttribute("categories", ValidationService.ALLOWED_CATEGORIES);
        req.setAttribute("priorities", ValidationService.ALLOWED_PRIORITIES);
        req.setAttribute("statuses", ValidationService.ALLOWED_STATUSES);

        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("pageSize", DEFAULT_PAGE_SIZE);

        // Keep filter values in request for inputs
        req.setAttribute("search", search);
        req.setAttribute("selectedStatus", status);
        req.setAttribute("selectedDepartmentId", departmentId);
        req.setAttribute("selectedCategory", category);
        req.setAttribute("selectedPriority", priority);
        req.setAttribute("startDate", startDate);
        req.setAttribute("endDate", endDate);
        req.setAttribute("sortBy", sortBy);
        req.setAttribute("sortOrder", sortOrder);

        req.getRequestDispatcher("/WEB-INF/views/admin/complaints.jsp").forward(req, resp);
    }
}
