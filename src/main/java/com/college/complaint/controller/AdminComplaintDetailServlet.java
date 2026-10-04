package com.college.complaint.controller;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.dao.StatusLogDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.dao.impl.StatusLogDAOImpl;
import com.college.complaint.model.Admin;
import com.college.complaint.model.Complaint;
import com.college.complaint.model.Department;
import com.college.complaint.model.StatusLog;
import com.college.complaint.service.EventBroadcaster;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "AdminComplaintDetailServlet", urlPatterns = "/admin/complaints/detail")
public class AdminComplaintDetailServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminComplaintDetailServlet.class);
    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final StatusLogDAO statusLogDAO = new StatusLogDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/complaints");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/complaints");
            return;
        }

        Complaint complaint = complaintDAO.findById(id);
        if (complaint == null) {
            req.setAttribute("errorMessage", "Complaint #" + id + " not found or has been deleted.");
            req.getRequestDispatcher("/WEB-INF/views/error404.jsp").forward(req, resp);
            return;
        }

        List<StatusLog> logs = statusLogDAO.findByComplaintId(id);
        List<Department> departments = departmentDAO.findAll();

        req.setAttribute("complaint", complaint);
        req.setAttribute("logs", logs);
        req.setAttribute("departments", departments);
        req.setAttribute("statuses", ValidationService.ALLOWED_STATUSES);

        req.getRequestDispatcher("/WEB-INF/views/admin/complaint_detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String action = req.getParameter("action");

        int id = 0;
        try {
            if (idParam != null) id = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException ignored) {}

        Complaint complaint = complaintDAO.findById(id);
        if (complaint == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/complaints");
            return;
        }

        HttpSession session = req.getSession(false);
        Admin admin = (session != null) ? (Admin) session.getAttribute("adminUser") : null;
        if (admin == null && session != null) {
            admin = (Admin) session.getAttribute("admin");
        }
        String changedBy = (admin != null) ? admin.getUsername() : "Admin";

        com.college.complaint.dao.NotificationDAO notificationDAO = new com.college.complaint.dao.impl.NotificationDAOImpl();

        if ("updateStatus".equals(action)) {
            String newStatus = req.getParameter("status");
            String remark = req.getParameter("remark");
            if (newStatus != null && ValidationService.ALLOWED_STATUSES.contains(newStatus.trim())) {
                complaintDAO.updateStatus(id, newStatus.trim(), changedBy, remark);
                if (remark != null && !remark.trim().isEmpty()) {
                    complaintDAO.updatePublicRemark(id, remark.trim());
                }

                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Integer studentId = complaint.getStudentId();
                if (studentId != null && studentId > 0) {
                    String notifMsg = "Your complaint " + complaint.getTrackingId() + " (" + complaint.getSubject() + ") moved to " + newStatus.trim() + ".";
                    com.college.complaint.model.Notification notif = new com.college.complaint.model.Notification(0, studentId, complaint.getId(), notifMsg, false, null);
                    notificationDAO.create(notif);
                    EventBroadcaster.broadcastStudentNotification(studentId, notifMsg, now);
                }

                // Broadcast scoped SSE status update to student channel, ticket tracker & admin dashboard
                EventBroadcaster.broadcastStudentStatusUpdate(studentId, complaint.getTrackingId(), newStatus.trim(), remark, now);
                logger.info("Status of {} updated to {} by {}", complaint.getTrackingId(), newStatus, changedBy);
            }
        } else if ("updateNotes".equals(action)) {
            String internalNotes = req.getParameter("internalNotes");
            complaintDAO.updateInternalNotes(id, internalNotes != null ? internalNotes.trim() : "");
        } else if ("updateRemark".equals(action)) {
            String publicRemark = req.getParameter("publicRemark");
            complaintDAO.updatePublicRemark(id, publicRemark != null ? publicRemark.trim() : "");
        } else if ("assignStaff".equals(action)) {
            String staff = req.getParameter("assignedTo");
            complaintDAO.assignStaff(id, staff != null ? staff.trim() : "");
        } else if ("togglePublic".equals(action)) {
            boolean isPublic = "true".equalsIgnoreCase(req.getParameter("isPublic"));
            complaintDAO.setPublic(id, isPublic);
        } else if ("delete".equals(action)) {
            complaintDAO.softDelete(id);
            logger.info("Complaint {} soft deleted by {}", complaint.getTrackingId(), changedBy);
            resp.sendRedirect(req.getContextPath() + "/admin/complaints?deleted=true");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/complaints/detail?id=" + id + "&updated=true");
    }
}
