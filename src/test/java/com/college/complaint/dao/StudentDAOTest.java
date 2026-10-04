package com.college.complaint.dao;

import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.NotificationDAOImpl;
import com.college.complaint.dao.impl.RememberTokenDAOImpl;
import com.college.complaint.dao.impl.StudentDAOImpl;
import com.college.complaint.model.Complaint;
import com.college.complaint.model.Notification;
import com.college.complaint.model.RememberToken;
import com.college.complaint.model.Student;
import com.college.complaint.service.TrackingIdService;
import com.college.complaint.util.DatabaseInitializer;
import com.college.complaint.util.PasswordUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StudentDAOTest {

    private static StudentDAO studentDAO;
    private static ComplaintDAO complaintDAO;
    private static NotificationDAO notificationDAO;
    private static RememberTokenDAO rememberTokenDAO;

    @BeforeAll
    public static void setup() {
        DatabaseInitializer.initialize();
        studentDAO = new StudentDAOImpl();
        complaintDAO = new ComplaintDAOImpl();
        notificationDAO = new NotificationDAOImpl();
        rememberTokenDAO = new RememberTokenDAOImpl();
    }

    @Test
    public void testDemoStudentsLoaded() {
        Student s1 = studentDAO.findByEmail("student1@campus.edu");
        assertNotNull(s1, "student1 should be present from seed data");
        assertEquals("22CS101", s1.getRollNo());
        assertTrue(PasswordUtil.checkPassword("Student@123", s1.getPasswordHash()));

        Student s2 = studentDAO.findByRollNo("23IT042");
        assertNotNull(s2);
        assertEquals("student2@campus.edu", s2.getEmail());
    }

    @Test
    public void testStudentRegistrationAndLookup() {
        Student s = new Student();
        String roll = "TEST" + (System.currentTimeMillis() % 100000);
        String email = "test" + (System.currentTimeMillis() % 100000) + "@campus.edu";
        s.setRollNo(roll);
        s.setEmail(email);
        s.setFullName("Unit Tester");
        s.setDepartmentId(1);
        s.setYearOfStudy(2);
        s.setMobile("9123456789");
        s.setPasswordHash(PasswordUtil.hashPassword("Password@123"));

        boolean created = studentDAO.create(s);
        assertTrue(created);
        assertTrue(s.getId() > 0);

        Student byEmail = studentDAO.findByEmail(email);
        assertNotNull(byEmail);
        assertEquals(roll, byEmail.getRollNo());

        // Update profile
        boolean updated = studentDAO.updateProfile(byEmail.getId(), "Updated Unit Tester", "9998887776", 1);
        assertTrue(updated);

        Student fetched = studentDAO.findById(byEmail.getId());
        assertEquals("Updated Unit Tester", fetched.getFullName());
    }

    @Test
    public void testStrictStudentOwnershipIsolation() {
        Student s1 = studentDAO.findByEmail("student1@campus.edu");
        assertNotNull(s1);

        // Create a complaint specifically owned by student1
        Complaint c = new Complaint();
        String tid = TrackingIdService.generateTrackingId();
        c.setTrackingId(tid);
        c.setStudentId(s1.getId());
        c.setStudentName(s1.getFullName());
        c.setRollNumber(s1.getRollNo());
        c.setEmail(s1.getEmail());
        c.setDepartmentId(s1.getDepartmentId());
        c.setCategory("Hostel");
        c.setPriority("High");
        c.setSubject("Isolated Room Repair");
        c.setDescription("Only student 1 should be able to view this complaint.");
        c.setAnonymous(false);
        c.setStatus("Submitted");
        c.setPublic(false);

        boolean created = complaintDAO.create(c);
        assertTrue(created);

        // Student 1 can access it
        Complaint s1Owned = complaintDAO.findByIdAndStudentId(c.getId(), s1.getId());
        assertNotNull(s1Owned, "Student 1 must be able to access own complaint");

        // Another student (student 2, ID != s1.getId()) CANNOT access it
        int fakeOtherStudentId = s1.getId() + 9999;
        Complaint s2Access = complaintDAO.findByIdAndStudentId(c.getId(), fakeOtherStudentId);
        assertNull(s2Access, "Cross-student access must return null (strict SQL isolation)");

        // Tracking ID query with ownership check
        assertNotNull(complaintDAO.findByTrackingIdAndStudentId(tid, s1.getId()));
        assertNull(complaintDAO.findByTrackingIdAndStudentId(tid, fakeOtherStudentId));

        // Listing complaints for student1
        List<Complaint> s1List = complaintDAO.findByStudentId(s1.getId(), null, null, 0, 50);
        assertTrue(s1List.stream().anyMatch(item -> item.getTrackingId().equals(tid)));

        // Status counts for student1
        Map<String, Integer> counts = complaintDAO.countByStatusForStudent(s1.getId());
        assertNotNull(counts);
        assertTrue(counts.getOrDefault("Submitted", 0) >= 1);
    }

    @Test
    public void testNotificationsAndRememberToken() {
        Student s1 = studentDAO.findByEmail("student1@campus.edu");
        assertNotNull(s1);

        // Notification creation
        Notification notif = new Notification();
        notif.setStudentId(s1.getId());
        notif.setMessage("Your complaint status has changed to In Progress.");
        notif.setRead(false);
        boolean nCreated = notificationDAO.create(notif);
        assertTrue(nCreated);
        assertTrue(notif.getId() > 0);

        List<Notification> list = notificationDAO.findByStudentId(s1.getId(), 10);
        assertFalse(list.isEmpty());
        assertTrue(notificationDAO.countUnreadByStudentId(s1.getId()) >= 1);

        boolean marked = notificationDAO.markAllAsRead(s1.getId());
        assertTrue(marked);
        assertEquals(0, notificationDAO.countUnreadByStudentId(s1.getId()));

        // Remember token persistence and lookup
        String tokenHash = "token_hash_" + System.currentTimeMillis();
        RememberToken token = new RememberToken(
                s1.getId(),
                tokenHash,
                new Timestamp(System.currentTimeMillis() + 86400000L * 30),
                "JUnit Browser"
        );

        boolean tSaved = rememberTokenDAO.saveToken(token);
        assertTrue(tSaved);

        RememberToken foundToken = rememberTokenDAO.findByTokenHash(tokenHash);
        assertNotNull(foundToken);
        assertEquals(s1.getId(), foundToken.getStudentId());

        // Cleanup token
        rememberTokenDAO.deleteByTokenHash(tokenHash);
        assertNull(rememberTokenDAO.findByTokenHash(tokenHash));
    }
}
