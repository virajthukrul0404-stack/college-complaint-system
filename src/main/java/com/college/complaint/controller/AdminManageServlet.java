package com.college.complaint.controller;

import com.college.complaint.dao.AdminDAO;
import com.college.complaint.dao.impl.AdminDAOImpl;
import com.college.complaint.model.Admin;
import com.college.complaint.service.ValidationService;
import com.college.complaint.util.PasswordUtil;
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
import java.util.Map;

@WebServlet(name = "AdminManageServlet", urlPatterns = "/admin/manage")
public class AdminManageServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminManageServlet.class);
    private final AdminDAO adminDAO = new AdminDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        loadData(req);
        req.getRequestDispatcher("/WEB-INF/views/admin/manage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession(false);
        Admin currentAdmin = (session != null) ? (Admin) session.getAttribute("admin") : null;

        if (currentAdmin == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }

        if ("changePassword".equals(action)) {
            String currentPass = req.getParameter("currentPassword");
            String newPass = req.getParameter("newPassword");
            String confirmPass = req.getParameter("confirmPassword");

            Map<String, String> errors = ValidationService.validatePasswordChange(currentPass, newPass, confirmPass);

            // Re-fetch current admin to verify latest password
            Admin fresh = adminDAO.findById(currentAdmin.getId());
            if (fresh == null || !PasswordUtil.checkPassword(currentPass, fresh.getPasswordHash())) {
                errors.put("currentPassword", "Incorrect current password.");
            }

            if (!errors.isEmpty()) {
                req.setAttribute("pwdErrors", errors);
                loadData(req);
                req.getRequestDispatcher("/WEB-INF/views/admin/manage.jsp").forward(req, resp);
                return;
            }

            String hashed = PasswordUtil.hashPassword(newPass);
            adminDAO.updatePassword(currentAdmin.getId(), hashed);
            currentAdmin.setPasswordHash(hashed);
            req.setAttribute("successMessage", "Password updated successfully.");

        } else if ("createAdmin".equals(action)) {
            if (!currentAdmin.isSuperAdmin()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only Super Administrators can create new accounts.");
                return;
            }

            String newUsername = req.getParameter("newUsername");
            String newFullName = req.getParameter("newFullName");
            String newEmail = req.getParameter("newEmail");
            String newRole = req.getParameter("newRole");
            String tempPassword = req.getParameter("tempPassword");

            if (newUsername == null || newUsername.trim().length() < 3 ||
                tempPassword == null || tempPassword.length() < 8) {
                req.setAttribute("createError", "Username must be at least 3 chars and password at least 8 chars.");
                loadData(req);
                req.getRequestDispatcher("/WEB-INF/views/admin/manage.jsp").forward(req, resp);
                return;
            }

            if (adminDAO.findByUsername(newUsername.trim()) != null) {
                req.setAttribute("createError", "Username '" + newUsername.trim() + "' is already taken.");
                loadData(req);
                req.getRequestDispatcher("/WEB-INF/views/admin/manage.jsp").forward(req, resp);
                return;
            }

            Admin newAdmin = new Admin();
            newAdmin.setUsername(newUsername.trim());
            newAdmin.setFullName(newFullName != null ? newFullName.trim() : newUsername.trim());
            newAdmin.setEmail(newEmail != null ? newEmail.trim() : "staff@campus.edu");
            newAdmin.setRole("SUPER_ADMIN".equalsIgnoreCase(newRole) ? "SUPER_ADMIN" : "ADMIN");
            newAdmin.setPasswordHash(PasswordUtil.hashPassword(tempPassword));

            adminDAO.createAdmin(newAdmin);
            logger.info("Admin {} created new account: {}", currentAdmin.getUsername(), newAdmin.getUsername());
            req.setAttribute("successMessage", "New administrator '" + newAdmin.getUsername() + "' created successfully.");
        }

        loadData(req);
        req.getRequestDispatcher("/WEB-INF/views/admin/manage.jsp").forward(req, resp);
    }

    private void loadData(HttpServletRequest req) {
        List<Admin> admins = adminDAO.findAll();
        req.setAttribute("adminList", admins);
    }
}
