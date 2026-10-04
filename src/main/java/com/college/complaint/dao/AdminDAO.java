package com.college.complaint.dao;

import com.college.complaint.model.Admin;
import java.util.List;

public interface AdminDAO {
    Admin findByUsername(String username);
    Admin findById(int id);
    List<Admin> findAll();
    boolean createAdmin(Admin admin);
    boolean updatePassword(int adminId, String newPasswordHash);
    void recordLoginAttempt(String ipAddress, String username, boolean success);
    int getFailedAttemptsCount(String ipAddress, String username, int windowMinutes);
    void clearLoginAttempts(String ipAddress, String username);
}
