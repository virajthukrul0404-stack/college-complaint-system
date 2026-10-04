package com.college.complaint.controller;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.StatusLogDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.StatusLogDAOImpl;
import com.college.complaint.model.Complaint;
import com.college.complaint.model.StatusLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "TrackComplaintServlet", urlPatterns = "/complaint/track")
public class TrackComplaintServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final StatusLogDAO statusLogDAO = new StatusLogDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String trackingId = req.getParameter("trackingId");
        String email = req.getParameter("email");

        if (trackingId != null && !trackingId.trim().isEmpty()) {
            trackingId = trackingId.trim();

            if (email == null || email.trim().isEmpty()) {
                req.setAttribute("error", "Please provide your registered contact email for verification.");
                req.setAttribute("trackingId", trackingId);
            } else {
                Complaint complaint = complaintDAO.findByTrackingIdAndEmail(trackingId, email.trim());
                if (complaint != null) {
                    List<StatusLog> logs = statusLogDAO.findByComplaintId(complaint.getId());
                    req.setAttribute("complaint", complaint);
                    req.setAttribute("logs", logs);
                    req.setAttribute("sseChannel", "complaint-" + complaint.getTrackingId());
                } else {
                    req.setAttribute("error", "No active complaint found matching Tracking ID '" + trackingId + "' and email '" + email.trim() + "'.");
                    req.setAttribute("trackingId", trackingId);
                    req.setAttribute("email", email);
                }
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/track.jsp").forward(req, resp);
    }
}
