package com.college.complaint.dao;

import com.college.complaint.model.RememberToken;

public interface RememberTokenDAO {
    boolean saveToken(RememberToken token);
    RememberToken findByTokenHash(String tokenHash);
    boolean deleteByStudentId(int studentId);
    boolean deleteByTokenHash(String tokenHash);
    void cleanExpiredTokens();
}
