package com.college.complaint.controller.student;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.NotificationDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.NotificationDAOImpl;
import com.college.complaint.model.Complaint;
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
import java.util.Map;

@WebServlet(name = "StudentHomeServlet", urlPatterns = "/student/home")
public class StudentHomeServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        Map<String, Integer> statusCounts = complaintDAO.countByStatusForStudent(student.getId());
        List<Complaint> recentComplaints = complaintDAO.findByStudentId(student.getId(), null, null, 0, 5);
        int totalComplaints = complaintDAO.countByStudentId(student.getId(), null, null);
        int unreadNotifs = notificationDAO.countUnreadByStudentId(student.getId());
        List<Notification> recentNotifs = notificationDAO.findByStudentId(student.getId(), 5);

        req.setAttribute("student", student);
        req.setAttribute("statusCounts", statusCounts);
        req.setAttribute("recentComplaints", recentComplaints);
        req.setAttribute("totalComplaints", totalComplaints);
        req.setAttribute("unreadNotificationsCount", unreadNotifs);
        req.setAttribute("recentNotifications", recentNotifs);

        req.getRequestDispatcher("/WEB-INF/views/student/home.jsp").forward(req, resp);
    }
}
