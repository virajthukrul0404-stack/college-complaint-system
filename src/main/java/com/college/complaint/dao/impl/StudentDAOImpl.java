package com.college.complaint.dao.impl;

import com.college.complaint.dao.StudentDAO;
import com.college.complaint.model.Student;
import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class StudentDAOImpl implements StudentDAO {

    private static final Logger logger = LoggerFactory.getLogger(StudentDAOImpl.class);

    private static final String BASE_SELECT =
            "SELECT s.id, s.roll_no, s.email, s.full_name, s.department_id, s.year_of_study, " +
            "s.mobile, s.password_hash, s.created_at, s.last_login_at, " +
            "d.name AS department_name, d.code AS department_code " +
            "FROM students s " +
            "LEFT JOIN departments d ON s.department_id = d.id ";

    @Override
    public boolean create(Student student) {
        String sql = "INSERT INTO students (roll_no, email, full_name, department_id, year_of_study, mobile, password_hash) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, student.getRollNo().trim().toUpperCase());
            ps.setString(2, student.getEmail().trim().toLowerCase());
            ps.setString(3, student.getFullName().trim());
            ps.setInt(4, student.getDepartmentId());
            ps.setInt(5, student.getYearOfStudy());
            ps.setString(6, student.getMobile() != null ? student.getMobile().trim() : null);
            ps.setString(7, student.getPasswordHash());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("Error creating student: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Student findById(int id) {
        String sql = BASE_SELECT + "WHERE s.id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            logger.error("Error finding student by id {}: {}", id, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Student findByEmail(String email) {
        if (email == null) return null;
        String sql = BASE_SELECT + "WHERE LOWER(s.email) = LOWER(?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            logger.error("Error finding student by email {}: {}", email, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Student findByRollNo(String rollNo) {
        if (rollNo == null) return null;
        String sql = BASE_SELECT + "WHERE UPPER(s.roll_no) = UPPER(?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            logger.error("Error finding student by rollNo {}: {}", rollNo, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Student findByIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) return null;
        String trimmed = identifier.trim();
        String sql = BASE_SELECT + "WHERE UPPER(s.roll_no) = UPPER(?) OR LOWER(s.email) = LOWER(?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trimmed);
            ps.setString(2, trimmed);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            logger.error("Error finding student by identifier {}: {}", identifier, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean updateProfile(int id, String fullName, String mobile, int departmentId) {
        String sql = "UPDATE students SET full_name = ?, mobile = ?, department_id = ? WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            ps.setString(2, mobile != null ? mobile.trim() : null);
            ps.setInt(3, departmentId);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error updating profile for student {}: {}", id, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean updatePassword(int id, String passwordHash) {
        String sql = "UPDATE students SET password_hash = ? WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error updating password for student {}: {}", id, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean updateLastLogin(int id) {
        String sql = "UPDATE students SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error updating last login for student {}: {}", id, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean isRollNoTaken(String rollNo, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM students WHERE UPPER(roll_no) = UPPER(?) " +
                     (excludeId != null ? "AND id != ?" : "");
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim());
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            logger.error("Error checking roll_no {}: {}", rollNo, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean isEmailTaken(String email, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM students WHERE LOWER(email) = LOWER(?) " +
                     (excludeId != null ? "AND id != ?" : "");
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            logger.error("Error checking email {}: {}", email, e.getMessage(), e);
        }
        return false;
    }

    private Student mapRow(ResultSet rs) throws Exception {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setRollNo(rs.getString("roll_no"));
        s.setEmail(rs.getString("email"));
        s.setFullName(rs.getString("full_name"));
        s.setDepartmentId(rs.getInt("department_id"));
        s.setDepartmentName(rs.getString("department_name"));
        s.setDepartmentCode(rs.getString("department_code"));
        s.setYearOfStudy(rs.getInt("year_of_study"));
        s.setMobile(rs.getString("mobile"));
        s.setPasswordHash(rs.getString("password_hash"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setLastLoginAt(rs.getTimestamp("last_login_at"));
        return s;
    }
}
