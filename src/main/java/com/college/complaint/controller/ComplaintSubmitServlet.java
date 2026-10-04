package com.college.complaint.controller;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.model.Complaint;
import com.college.complaint.model.Department;
import com.college.complaint.service.EventBroadcaster;
import com.college.complaint.service.TrackingIdService;
import com.college.complaint.service.ValidationService;
import com.college.complaint.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ComplaintSubmitServlet", urlPatterns = "/complaint/new")
@MultipartConfig(
        maxFileSize = 2 * 1024 * 1024,      // 2 MB
        maxRequestSize = 5 * 1024 * 1024,   // 5 MB
        fileSizeThreshold = 512 * 1024      // 512 KB
)
public class ComplaintSubmitServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintSubmitServlet.class);
    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        jakarta.servlet.http.HttpSession session = req.getSession(false);
        com.college.complaint.model.Student student = (session != null) ? (com.college.complaint.model.Student) session.getAttribute("studentUser") : null;
        if (student != null) {
            resp.sendRedirect(req.getContextPath() + "/student/complaint/new");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/student/login?redirect=" + req.getContextPath() + "/student/complaint/new&notice=login_required");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        jakarta.servlet.http.HttpSession session = req.getSession(false);
        com.college.complaint.model.Student student = (session != null) ? (com.college.complaint.model.Student) session.getAttribute("studentUser") : null;
        if (student != null) {
            resp.sendRedirect(req.getContextPath() + "/student/complaint/new");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/student/login?redirect=" + req.getContextPath() + "/student/complaint/new&notice=login_required");
    }
}
