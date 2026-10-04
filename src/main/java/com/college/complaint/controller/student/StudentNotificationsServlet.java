package com.college.complaint.controller.student;

import com.college.complaint.dao.NotificationDAO;
import com.college.complaint.dao.impl.NotificationDAOImpl;
import com.college.complaint.model.Notification;
import com.college.complaint.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "StudentNotificationsServlet", urlPatterns = "/student/notifications")
public class StudentNotificationsServlet extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        String action = req.getParameter("action");
        if ("readAll".equalsIgnoreCase(action)) {
            notificationDAO.markAllAsRead(student.getId());
            resp.sendRedirect(req.getContextPath() + "/student/notifications");
            return;
        }

        List<Notification> notifications = notificationDAO.findByStudentId(student.getId(), 50);
        int unreadCount = notificationDAO.countUnreadByStudentId(student.getId());

        req.setAttribute("notifications", notifications);
        req.setAttribute("unreadCount", unreadCount);
        req.getRequestDispatcher("/WEB-INF/views/student/notifications.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        String action = req.getParameter("action");
        String idParam = req.getParameter("id");

        if ("markRead".equalsIgnoreCase(action) && idParam != null) {
            try {
                int id = Integer.parseInt(idParam.trim());
                notificationDAO.markAsRead(id, student.getId());
            } catch (NumberFormatException ignored) {}
        } else if ("markAllRead".equalsIgnoreCase(action)) {
            notificationDAO.markAllAsRead(student.getId());
        }

        resp.sendRedirect(req.getContextPath() + "/student/notifications");
    }
}
