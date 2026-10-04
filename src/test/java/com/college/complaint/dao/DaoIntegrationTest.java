package com.college.complaint.dao;

import com.college.complaint.dao.impl.AdminDAOImpl;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.dao.impl.FeedbackDAOImpl;
import com.college.complaint.dao.impl.StatusLogDAOImpl;
import com.college.complaint.model.Admin;
import com.college.complaint.model.Complaint;
import com.college.complaint.model.Department;
import com.college.complaint.model.Feedback;
import com.college.complaint.model.StatusLog;
import com.college.complaint.service.TrackingIdService;
import com.college.complaint.util.DatabaseInitializer;
import com.college.complaint.util.PasswordUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DaoIntegrationTest {

    private static DepartmentDAO departmentDAO;
    private static AdminDAO adminDAO;
    private static ComplaintDAO complaintDAO;
    private static StatusLogDAO statusLogDAO;
    private static FeedbackDAO feedbackDAO;

    @BeforeAll
    public static void setup() {
        DatabaseInitializer.initialize();
        departmentDAO = new DepartmentDAOImpl();
        adminDAO = new AdminDAOImpl();
        complaintDAO = new ComplaintDAOImpl();
        statusLogDAO = new StatusLogDAOImpl();
        feedbackDAO = new FeedbackDAOImpl();
    }

    @Test
    public void testDepartmentsLoaded() {
        List<Department> departments = departmentDAO.findAll();
        assertNotNull(departments);
        assertTrue(departments.size() >= 6, "Expected at least 6 seeded departments");
        Department cse = departmentDAO.findByCode("CSE");
        assertNotNull(cse);
        assertEquals("Computer Science & Engineering", cse.getName());
    }

    @Test
    public void testSuperadminLoginCredentials() {
        Admin admin = adminDAO.findByUsername("superadmin");
        assertNotNull(admin, "Superadmin should exist in database");
        assertTrue(admin.isSuperAdmin());
        assertTrue(PasswordUtil.checkPassword("Admin@12345", admin.getPasswordHash()));
        assertFalse(PasswordUtil.checkPassword("wrongpassword", admin.getPasswordHash()));
    }

    @Test
    public void testComplaintCreationAndRetrieval() {
        Complaint c = new Complaint();
        String tid = TrackingIdService.generateTrackingId();
        c.setTrackingId(tid);
        c.setStudentName("Test Student");
        c.setRollNumber("22CS999");
        c.setEmail("test.student@campus.edu");
        c.setDepartmentId(1);
        c.setCategory("IT/WiFi");
        c.setPriority("High");
        c.setSubject("Integration Test Complaint");
        c.setDescription("Testing the full DAO persistence cycle with JUnit 5.");
        c.setAnonymous(false);
        c.setStatus("Submitted");
        c.setPublic(false);

        boolean created = complaintDAO.create(c);
        assertTrue(created, "Complaint should be inserted successfully");
        assertTrue(c.getId() > 0, "Generated ID should be populated");

        Complaint fetched = complaintDAO.findByTrackingId(tid);
        assertNotNull(fetched);
        assertEquals("Test Student", fetched.getStudentName());
        assertEquals("Computer Science & Engineering", fetched.getDepartmentName());

        // Update status
        boolean updated = complaintDAO.updateStatus(c.getId(), "In Progress", "superadmin", "Investigating issue");
        assertTrue(updated);

        List<StatusLog> logs = statusLogDAO.findByComplaintId(c.getId());
        assertTrue(logs.size() >= 2, "Should have initial log and status update log");
        assertEquals("In Progress", logs.get(logs.size() - 1).getNewStatus());
    }

    @Test
    public void testComplaintAnalytics() {
        Map<String, Integer> statusCounts = complaintDAO.countByStatus();
        assertNotNull(statusCounts);
        assertTrue(statusCounts.containsKey("Submitted"));
        assertTrue(statusCounts.containsKey("Resolved"));

        Map<String, Integer> deptCounts = complaintDAO.countByDepartment();
        assertNotNull(deptCounts);
        assertFalse(deptCounts.isEmpty());

        Map<String, Integer> trend = complaintDAO.getTrendData(30);
        assertNotNull(trend);
        assertEquals(30, trend.size(), "Should produce 30 daily buckets");
    }

    @Test
    public void testFeedbackOperations() {
        Feedback f = new Feedback();
        f.setDepartmentId(1);
        f.setCategory("Lab Facilities");
        f.setRating(5);
        f.setComment("Excellent hardware in Turing Lab!");

        boolean created = feedbackDAO.create(f);
        assertTrue(created);

        List<Feedback> all = feedbackDAO.findAll(0, 10);
        assertNotNull(all);
        assertFalse(all.isEmpty());

        Map<String, Double> deptAverages = feedbackDAO.getAverageRatingByDepartment();
        assertNotNull(deptAverages);
        assertTrue(deptAverages.containsKey("Computer Science & Engineering"));

        Map<Integer, Integer> distribution = feedbackDAO.getRatingDistribution();
        assertNotNull(distribution);
        assertEquals(5, distribution.size());
    }
}
