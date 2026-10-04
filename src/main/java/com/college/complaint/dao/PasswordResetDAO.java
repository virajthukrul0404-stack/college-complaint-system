package com.college.complaint.dao;

import com.college.complaint.model.PasswordReset;

public interface PasswordResetDAO {
    boolean saveResetCode(PasswordReset reset);
    PasswordReset findActiveByStudentId(int studentId);
    boolean incrementAttempts(int id);
    boolean markUsed(int id);
    void invalidateOldCodes(int studentId);
}
