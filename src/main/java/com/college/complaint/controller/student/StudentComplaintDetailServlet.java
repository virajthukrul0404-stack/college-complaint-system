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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "StudentComplaintDetailServlet", urlPatterns = {"/student/complaints/detail", "/student/complaint/detail"})
public class StudentComplaintDetailServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentComplaintDetailServlet.class);
    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final StatusLogDAO statusLogDAO = new StatusLogDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        String idParam = req.getParameter("id");
        String trackingId = req.getParameter("trackingId");

        Complaint complaint = null;

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam.trim());
                // Strict ownership query in DAO
                complaint = complaintDAO.findByIdAndStudentId(id, student.getId());
                if (complaint == null) {
                    // Check if it exists for someone else
                    Complaint other = complaintDAO.findById(id);
                    if (other != null) {
                        logger.warn("Cross-student access attempt: Student {} tried accessing complaint {} owned by student {}",
                                student.getId(), id, other.getStudentId());
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. You do not own this complaint docket.");
                        return;
                    }
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Complaint docket not found.");
                    return;
                }
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid complaint identifier.");
                return;
            }
        } else if (trackingId != null && !trackingId.trim().isEmpty()) {
            complaint = complaintDAO.findByTrackingIdAndStudentId(trackingId.trim(), student.getId());
            if (complaint == null) {
                Complaint other = complaintDAO.findByTrackingId(trackingId.trim());
                if (other != null) {
                    logger.warn("Cross-student access attempt by tracking ID: Student {} tried accessing {}",
                            student.getId(), trackingId);
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. You do not own this complaint docket.");
                    return;
                }
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Complaint docket not found.");
                return;
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/student/complaints");
            return;
        }

        List<StatusLog> statusLogs = statusLogDAO.findByComplaintId(complaint.getId());

        req.setAttribute("complaint", complaint);
        req.setAttribute("statusLogs", statusLogs);
        req.getRequestDispatcher("/WEB-INF/views/student/complaint_detail.jsp").forward(req, resp);
    }
}
