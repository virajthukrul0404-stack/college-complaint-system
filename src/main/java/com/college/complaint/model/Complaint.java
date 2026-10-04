package com.college.complaint.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String trackingId;
    private Integer studentId;
    private String studentName;
    private String rollNumber;
    private String email;
    private int departmentId;
    private String departmentName; // Joined from departments table
    private String departmentCode; // Joined from departments table
    private String category;
    private String priority; // 'Low', 'Medium', 'High'
    private String subject;
    private String description;
    private boolean isAnonymous;
    private String status; // 'Submitted', 'Under Review', 'In Progress', 'Resolved', 'Rejected'
    private String attachmentPath;
    private String internalNotes;
    private String publicRemark;
    private String assignedTo;
    private boolean isPublic;
    private Timestamp deletedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Complaint() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDisplayEmail() {
        if (isAnonymous) {
            return "[Hidden - Anonymous Submission]";
        }
        return email;
    }

    /**
     * Display name respecting anonymity: if anonymous, returns "Anonymous Student" for public/admin display
     */
    public String getDisplayName() {
        if (isAnonymous) {
            return "Anonymous Student";
        }
        return studentName;
    }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getDisplayRollNumber() {
        if (isAnonymous) {
            return "Hidden";
        }
        return rollNumber;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }

    public String getPublicRemark() { return publicRemark; }
    public void setPublicRemark(String publicRemark) { this.publicRemark = publicRemark; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public boolean isPublic() { return isPublic; }
    public boolean isPublicCase() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public Timestamp getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Timestamp deletedAt) { this.deletedAt = deletedAt; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
