package com.college.complaint.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class PasswordReset implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int studentId;
    private String codeHash;
    private Timestamp expiresAt;
    private int attempts;
    private boolean used;
    private Timestamp createdAt;

    public PasswordReset() {}

    public PasswordReset(int studentId, String codeHash, Timestamp expiresAt) {
        this.studentId = studentId;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
        this.attempts = 0;
        this.used = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getCodeHash() { return codeHash; }
    public void setCodeHash(String codeHash) { this.codeHash = codeHash; }

    public Timestamp getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Timestamp expiresAt) { this.expiresAt = expiresAt; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
