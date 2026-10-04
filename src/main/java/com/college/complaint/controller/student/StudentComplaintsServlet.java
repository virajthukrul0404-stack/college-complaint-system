package com.college.complaint.controller.student;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.StatusLogDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.StatusLogDAOImpl;
import com.college.complaint.model.Complaint;
import com.college.complaint.model.StatusLog;
import com.college.complaint.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StudentComplaintsServlet", urlPatterns = "/student/complaints")
public class StudentComplaintsServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final StatusLogDAO statusLogDAO = new StatusLogDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        String status = req.getParameter("status");
        String search = req.getParameter("search");
        String pageParam = req.getParameter("page");
        String selectedIdParam = req.getParameter("id");

        int page = 1;
        try {
            if (pageParam != null) page = Math.max(1, Integer.parseInt(pageParam.trim()));
        } catch (NumberFormatException ignored) {}

        int limit = 20;
        int offset = (page - 1) * limit;

        List<Complaint> complaints = complaintDAO.findByStudentId(student.getId(), status, search, offset, limit);
        int totalComplaints = complaintDAO.countByStudentId(student.getId(), status, search);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalComplaints / limit));

        Map<String, Integer> statusCounts = complaintDAO.countByStatusForStudent(student.getId());

        // For desktop master-detail: if selectedIdParam is present, load detail with ownership check
        Complaint selectedComplaint = null;
        List<StatusLog> selectedLogs = null;
        if (selectedIdParam != null && !selectedIdParam.trim().isEmpty()) {
            try {
                int selectedId = Integer.parseInt(selectedIdParam.trim());
                // Enforce student ownership in query
                selectedComplaint = complaintDAO.findByIdAndStudentId(selectedId, student.getId());
                if (selectedComplaint != null) {
                    selectedLogs = statusLogDAO.findByComplaintId(selectedComplaint.getId());
                }
            } catch (NumberFormatException ignored) {}
        } else if (!complaints.isEmpty()) {
            // Default first complaint as selected for desktop master-detail
            selectedComplaint = complaints.get(0);
            selectedLogs = statusLogDAO.findByComplaintId(selectedComplaint.getId());
        }

        req.setAttribute("complaints", complaints);
        req.setAttribute("totalComplaints", totalComplaints);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("status", status);
        req.setAttribute("search", search);
        req.setAttribute("statusCounts", statusCounts);
        req.setAttribute("selectedComplaint", selectedComplaint);
        req.setAttribute("selectedLogs", selectedLogs);

        req.getRequestDispatcher("/WEB-INF/views/student/complaints.jsp").forward(req, resp);
    }
}
