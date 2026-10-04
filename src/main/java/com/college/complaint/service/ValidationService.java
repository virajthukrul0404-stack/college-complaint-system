package com.college.complaint.service;

import com.college.complaint.model.Complaint;
import com.college.complaint.model.Feedback;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidationService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static final List<String> ALLOWED_CATEGORIES = Arrays.asList(
            "Hostel", "Library", "Canteen", "Academics", "Infrastructure",
            "Transport", "Ragging/Harassment", "IT/WiFi", "Other"
    );

    public static final List<String> ALLOWED_PRIORITIES = Arrays.asList("Low", "Medium", "High");

    public static final List<String> ALLOWED_STATUSES = Arrays.asList(
            "Submitted", "Under Review", "In Progress", "Resolved", "Rejected"
    );

    public static Map<String, String> validateComplaint(Complaint c) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (c.getStudentName() == null || c.getStudentName().trim().length() < 2) {
            errors.put("studentName", "Student name must be at least 2 characters long.");
        } else if (c.getStudentName().trim().length() > 100) {
            errors.put("studentName", "Student name cannot exceed 100 characters.");
        }

        if (c.getRollNumber() == null || c.getRollNumber().trim().length() < 2) {
            errors.put("rollNumber", "Roll number is required (at least 2 characters).");
        } else if (c.getRollNumber().trim().length() > 50) {
            errors.put("rollNumber", "Roll number cannot exceed 50 characters.");
        }

        if (c.getEmail() == null || !EMAIL_PATTERN.matcher(c.getEmail().trim()).matches()) {
            errors.put("email", "Please provide a valid email address.");
        }

        if (c.getDepartmentId() <= 0) {
            errors.put("departmentId", "Please select a valid department.");
        }

        if (c.getCategory() == null || !ALLOWED_CATEGORIES.contains(c.getCategory().trim())) {
            errors.put("category", "Please select a valid category from the list.");
        }

        if (c.getPriority() == null || !ALLOWED_PRIORITIES.contains(c.getPriority().trim())) {
            errors.put("priority", "Priority must be Low, Medium, or High.");
        }

        if (c.getSubject() == null || c.getSubject().trim().length() < 5) {
            errors.put("subject", "Subject must be at least 5 characters.");
        } else if (c.getSubject().trim().length() > 200) {
            errors.put("subject", "Subject cannot exceed 200 characters.");
        }

        if (c.getDescription() == null || c.getDescription().trim().length() < 10) {
            errors.put("description", "Description must provide details (at least 10 characters).");
        } else if (c.getDescription().trim().length() > 4000) {
            errors.put("description", "Description cannot exceed 4000 characters.");
        }

        return errors;
    }

    public static Map<String, String> validateFeedback(Feedback f) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (f.getDepartmentId() <= 0) {
            errors.put("departmentId", "Please select a department.");
        }

        if (f.getCategory() == null || f.getCategory().trim().isEmpty()) {
            errors.put("category", "Please select a category.");
        }

        if (f.getRating() < 1 || f.getRating() > 5) {
            errors.put("rating", "Rating must be between 1 and 5 stars.");
        }

        if (f.getComment() == null || f.getComment().trim().length() < 5) {
            errors.put("comment", "Comment must be at least 5 characters long.");
        } else if (f.getComment().trim().length() > 2000) {
            errors.put("comment", "Comment cannot exceed 2000 characters.");
        }

        return errors;
    }

    public static Map<String, String> validatePasswordChange(String currentPassword, String newPassword, String confirmPassword) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (currentPassword == null || currentPassword.isEmpty()) {
            errors.put("currentPassword", "Current password is required.");
        }
        if (newPassword == null || newPassword.length() < 8) {
            errors.put("newPassword", "New password must be at least 8 characters long.");
        } else if (!Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).+$").matcher(newPassword).matches()) {
            errors.put("newPassword", "Password must contain at least one letter and one number.");
        }
        if (confirmPassword == null || !confirmPassword.equals(newPassword)) {
            errors.put("confirmPassword", "New passwords do not match.");
        }
        return errors;
    }

    public static Map<String, String> validateStudentRegistration(
            String fullName, String rollNo, String email, int departmentId,
            int yearOfStudy, String mobile, String password, String confirmPassword) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (fullName == null || fullName.trim().length() < 2) {
            errors.put("fullName", "Full name must be at least 2 characters.");
        } else if (fullName.trim().length() > 100) {
            errors.put("fullName", "Full name cannot exceed 100 characters.");
        }

        if (rollNo == null || rollNo.trim().length() < 2) {
            errors.put("rollNo", "Roll number is required (at least 2 characters).");
        } else if (rollNo.trim().length() > 50) {
            errors.put("rollNo", "Roll number cannot exceed 50 characters.");
        }

        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            errors.put("email", "Please provide a valid college email address.");
        }

        if (departmentId <= 0) {
            errors.put("departmentId", "Please select your department.");
        }

        if (yearOfStudy < 1 || yearOfStudy > 5) {
            errors.put("yearOfStudy", "Please select a valid year of study (1 to 5).");
        }

        if (mobile != null && !mobile.trim().isEmpty()) {
            String cleanMobile = mobile.trim().replaceAll("[\\s\\-\\(\\)]", "");
            if (!cleanMobile.matches("^[+]?\\d{10,15}$")) {
                errors.put("mobile", "Please enter a valid 10-digit mobile number.");
            }
        }

        if (password == null || password.length() < 8) {
            errors.put("password", "Password must be at least 8 characters long.");
        } else if (!Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).+$").matcher(password).matches()) {
            errors.put("password", "Password must contain at least one letter and one number.");
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            errors.put("confirmPassword", "Passwords do not match.");
        }

        return errors;
    }

    public static Map<String, String> validateStudentProfile(String fullName, String mobile, int departmentId) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (fullName == null || fullName.trim().length() < 2) {
            errors.put("fullName", "Full name must be at least 2 characters.");
        } else if (fullName.trim().length() > 100) {
            errors.put("fullName", "Full name cannot exceed 100 characters.");
        }
        if (departmentId <= 0) {
            errors.put("departmentId", "Please select a valid department.");
        }
        if (mobile != null && !mobile.trim().isEmpty()) {
            String cleanMobile = mobile.trim().replaceAll("[\\s\\-\\(\\)]", "");
            if (!cleanMobile.matches("^[+]?\\d{10,15}$")) {
                errors.put("mobile", "Please enter a valid mobile number.");
            }
        }
        return errors;
    }
}
