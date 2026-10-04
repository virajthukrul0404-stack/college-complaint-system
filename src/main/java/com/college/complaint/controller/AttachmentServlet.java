package com.college.complaint.controller;

import com.college.complaint.dao.AttachmentDAO;
import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.impl.AttachmentDAOImpl;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.model.Admin;
import com.college.complaint.model.Attachment;
import com.college.complaint.model.Complaint;
import com.college.complaint.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/complaint/attachment")
public class AttachmentServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentServlet.class);
    private final AttachmentDAO attachmentDAO = new AttachmentDAOImpl();
    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String complaintIdParam = req.getParameter("complaintId");
        String trackingIdParam = req.getParameter("trackingId");

        Attachment attachment = null;
        Complaint complaint = null;

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                attachment = attachmentDAO.findById(Long.parseLong(idParam.trim()));
                if (attachment != null) {
                    complaint = complaintDAO.findById(attachment.getComplaintId());
                }
            } catch (NumberFormatException ignored) {}
        } else if (complaintIdParam != null && !complaintIdParam.trim().isEmpty()) {
            try {
                int cId = Integer.parseInt(complaintIdParam.trim());
                complaint = complaintDAO.findById(cId);
                attachment = attachmentDAO.findByComplaintId(cId);
            } catch (NumberFormatException ignored) {}
        } else if (trackingIdParam != null && !trackingIdParam.trim().isEmpty()) {
            complaint = complaintDAO.findByTrackingId(trackingIdParam.trim());
            if (complaint != null) {
                attachment = attachmentDAO.findByComplaintId(complaint.getId());
            }
        }

        if (complaint == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Attachment not found.");
            return;
        }

        // Security & Ownership Checks
        HttpSession session = req.getSession(false);
        Student studentUser = session != null ? (Student) session.getAttribute("studentUser") : null;
        Admin adminUser = session != null ? (Admin) session.getAttribute("adminUser") : null;

        boolean isOwner = (studentUser != null && complaint.getStudentId() != null &&
                studentUser.getId() == complaint.getStudentId());
        boolean isAdmin = (adminUser != null);
        boolean isPublicComplaint = complaint.isPublic();

        // Check if user is currently tracking this complaint via tracking session
        String trackedId = session != null ? (String) session.getAttribute("TRACKED_COMPLAINT_ID") : null;
        boolean isTrackingAuthorized = (trackedId != null && trackedId.equalsIgnoreCase(complaint.getTrackingId()));

        if (!isAdmin && !isOwner && !isPublicComplaint && !isTrackingAuthorized) {
            logger.warn("Unauthorized attachment access attempt for complaint {}", complaint.getTrackingId());
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. You cannot view this attachment.");
            return;
        }

        // 1. If attachment is in database
        if (attachment != null && attachment.getData() != null) {
            resp.setContentType(attachment.getFileType());
            resp.setHeader("X-Content-Type-Options", "nosniff");
            resp.setHeader("Content-Disposition", "inline; filename=\"" + sanitizeFilename(attachment.getFileName()) + "\"");
            resp.setHeader("Cache-Control", "private, max-age=86400");
            resp.setContentLength(attachment.getData().length);

            try (OutputStream out = resp.getOutputStream()) {
                out.write(attachment.getData());
                out.flush();
            }
            return;
        }

        // 2. Fallback: If stored on disk (local development mode)
        String path = complaint.getAttachmentPath();
        if (path != null && !path.trim().isEmpty()) {
            File file = new File(getServletContext().getRealPath("/"), path);
            if (!file.exists()) {
                file = new File(path);
            }
            if (file.exists() && file.isFile()) {
                String mime = getServletContext().getMimeType(file.getName());
                if (mime == null) mime = "image/jpeg";
                resp.setContentType(mime);
                resp.setHeader("X-Content-Type-Options", "nosniff");
                resp.setHeader("Content-Disposition", "inline; filename=\"" + sanitizeFilename(file.getName()) + "\"");
                resp.setHeader("Cache-Control", "private, max-age=86400");
                resp.setContentLengthLong(file.length());

                try (FileInputStream fis = new FileInputStream(file);
                     OutputStream out = resp.getOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.flush();
                }
                return;
            }
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Attachment file data not available.");
    }

    private String sanitizeFilename(String name) {
        if (name == null) return "attachment.jpg";
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
