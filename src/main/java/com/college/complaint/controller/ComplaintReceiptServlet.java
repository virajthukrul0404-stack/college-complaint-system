package com.college.complaint.controller;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.model.Complaint;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ComplaintReceiptServlet", urlPatterns = "/complaint/receipt")
public class ComplaintReceiptServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String trackingId = req.getParameter("id");
        if (trackingId == null || trackingId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        Complaint complaint = complaintDAO.findByTrackingId(trackingId.trim());
        if (complaint == null) {
            req.setAttribute("errorMessage", "Complaint not found for tracking slip ID: " + trackingId);
            req.getRequestDispatcher("/WEB-INF/views/error404.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("complaint", complaint);
        req.getRequestDispatcher("/WEB-INF/views/complaint_receipt.jsp").forward(req, resp);
    }
}
