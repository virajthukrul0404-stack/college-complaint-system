package com.college.complaint.dao;

import com.college.complaint.model.Student;

public interface StudentDAO {
    boolean create(Student student);
    Student findById(int id);
    Student findByEmail(String email);
    Student findByRollNo(String rollNo);
    Student findByIdentifier(String identifier);
    boolean updateProfile(int id, String fullName, String mobile, int departmentId);
    boolean updatePassword(int id, String passwordHash);
    boolean updateLastLogin(int id);
    boolean isRollNoTaken(String rollNo, Integer excludeId);
    boolean isEmailTaken(String email, Integer excludeId);
}
