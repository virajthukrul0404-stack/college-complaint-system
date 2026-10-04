package com.college.complaint.service;

import com.college.complaint.model.Complaint;
import com.college.complaint.model.Feedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityAndValidationTest {

    @BeforeEach
    public void reset() {
        RateLimiter.resetAll();
    }

    @Test
    public void testRateLimiterLockoutAfterFiveAttempts() {
        String ip = "192.168.1.100";
        String user = "admin";

        assertFalse(RateLimiter.isLocked(ip, user), "Initially should not be locked");

        for (int i = 0; i < 4; i++) {
            RateLimiter.recordFailedAttempt(ip, user);
            assertFalse(RateLimiter.isLocked(ip, user), "Should not be locked after attempt " + (i + 1));
        }

        // 5th attempt triggers lockout
        RateLimiter.recordFailedAttempt(ip, user);
        assertTrue(RateLimiter.isLocked(ip, user), "Must be locked after 5 failed attempts");
        assertTrue(RateLimiter.getRemainingLockSeconds(ip, user) > 0);

        // Different user from same IP should not be locked
        assertFalse(RateLimiter.isLocked(ip, "otheruser"));

        // Different IP for same user should not be locked
        assertFalse(RateLimiter.isLocked("192.168.1.101", user));
    }

    @Test
    public void testComplaintValidationErrors() {
        Complaint c = new Complaint(); // Empty fields
        Map<String, String> errors = ValidationService.validateComplaint(c);

        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("studentName"));
        assertTrue(errors.containsKey("rollNumber"));
        assertTrue(errors.containsKey("email"));
        assertTrue(errors.containsKey("subject"));
        assertTrue(errors.containsKey("description"));

        // Valid complaint
        c.setStudentName("Rohan Roy");
        c.setRollNumber("22CS101");
        c.setEmail("rohan.roy@campus.edu");
        c.setDepartmentId(1);
        c.setCategory("IT/WiFi");
        c.setPriority("High");
        c.setSubject("Lab Switch Power Outage");
        c.setDescription("Main network switch lost power in building block 3.");

        Map<String, String> validErrors = ValidationService.validateComplaint(c);
        assertTrue(validErrors.isEmpty(), "Valid complaint should have 0 validation errors");
    }

    @Test
    public void testFeedbackValidation() {
        Feedback f = new Feedback();
        Map<String, String> errors = ValidationService.validateFeedback(f);
        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("departmentId"));
        assertTrue(errors.containsKey("rating"));

        f.setDepartmentId(2);
        f.setCategory("Curriculum");
        f.setRating(5);
        f.setComment("Course coverage of distributed databases is top notch.");

        Map<String, String> noErrors = ValidationService.validateFeedback(f);
        assertTrue(noErrors.isEmpty());
    }

    @Test
    public void testStudentRegistrationValidation() {
        Map<String, String> errors = ValidationService.validateStudentRegistration(
                "", "", "", 0, 0, "", "weak", "mismatch"
        );
        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("rollNo"));
        assertTrue(errors.containsKey("email"));
        assertTrue(errors.containsKey("fullName"));
        assertTrue(errors.containsKey("departmentId"));
        assertTrue(errors.containsKey("yearOfStudy"));
        assertTrue(errors.containsKey("password"));

        // Valid student input
        Map<String, String> valid = ValidationService.validateStudentRegistration(
                "Rohan Roy", "22CS101", "student@campus.edu", 1, 3, "9876543210", "Strong@123", "Strong@123"
        );
        assertTrue(valid.isEmpty(), "Valid registration should have no errors");
    }

    @Test
    public void testDeviceClassification() {
        String iPhone = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_5 like Mac OS X) AppleWebKit/605.1.15 Mobile/15E148";
        String android = "Mozilla/5.0 (Linux; Android 13; SM-S918B) AppleWebKit/537.36 Mobile Safari/537.36";
        String iPad = "Mozilla/5.0 (iPad; CPU OS 16_5 like Mac OS X) AppleWebKit/605.1.15";
        String desktopMac = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        String desktopWin = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

        assertEquals("mobile", com.college.complaint.filter.DeviceDetectionFilter.classifyUserAgent(iPhone));
        assertEquals("mobile", com.college.complaint.filter.DeviceDetectionFilter.classifyUserAgent(android));
        assertEquals("tablet", com.college.complaint.filter.DeviceDetectionFilter.classifyUserAgent(iPad));
        assertEquals("desktop", com.college.complaint.filter.DeviceDetectionFilter.classifyUserAgent(desktopMac));
        assertEquals("desktop", com.college.complaint.filter.DeviceDetectionFilter.classifyUserAgent(desktopWin));
    }

    @Test
    public void testCsvExportAnonymityAndFormulaSanitization() throws java.io.IOException {
        Complaint anon = new Complaint();
        anon.setTrackingId("CMP-TEST-001");
        anon.setStudentName("Secret Student");
        anon.setRollNumber("22CS999");
        anon.setEmail("secret@campus.edu");
        anon.setAnonymous(true);
        anon.setDepartmentName("CSE");
        anon.setCategory("Hostel");
        anon.setPriority("Normal");
        anon.setStatus("Submitted");
        anon.setSubject("=CMD|' /C calc'!A0"); // Attempted CSV formula injection
        anon.setDescription("Room issue");

        java.io.StringWriter sw = new java.io.StringWriter();
        CsvExporter.exportComplaintsToCsv(java.util.Collections.singletonList(anon), sw);
        String csv = sw.toString();

        assertFalse(csv.contains("Secret Student"), "Anonymous student name must never appear in CSV export");
        assertFalse(csv.contains("22CS999"), "Anonymous roll number must never appear in CSV export");
        assertFalse(csv.contains("secret@campus.edu"), "Anonymous email must never appear in CSV export");
        assertTrue(csv.contains("\"Anonymous Student\""));
        assertTrue(csv.contains("\"Hidden\""));
        // Formula injection should be escaped with leading single quote
        assertTrue(csv.contains("\"'=CMD|' /C calc'!A0\""));
    }
}
