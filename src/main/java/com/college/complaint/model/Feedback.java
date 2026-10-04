package com.college.complaint.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Integer studentId;
    private int departmentId;
    private String departmentName;
    private String category;
    private int rating; // 1 to 5
    private String comment;
    private Timestamp createdAt;

    public Feedback() {}

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Feedback(int id, int departmentId, String departmentName, String category, int rating, String comment, Timestamp createdAt) {
        this.id = id;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.category = category;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
