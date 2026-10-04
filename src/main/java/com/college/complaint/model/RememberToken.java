package com.college.complaint.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class RememberToken implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int studentId;
    private String tokenHash;
    private Timestamp expiresAt;
    private String userAgent;
    private Timestamp createdAt;

    public RememberToken() {}

    public RememberToken(int studentId, String tokenHash, Timestamp expiresAt, String userAgent) {
        this.studentId = studentId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.userAgent = userAgent;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public Timestamp getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Timestamp expiresAt) { this.expiresAt = expiresAt; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
